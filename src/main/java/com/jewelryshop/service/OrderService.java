package com.jewelryshop.service;

import com.jewelryshop.entity.*;
import com.jewelryshop.enums.OrderStatus;
import com.jewelryshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CouponService couponService;

    public Order placeOrder(Long userId, String shippingName, String shippingPhone,
                            String shippingAddress, String note, String couponCode,
                            String paymentMethod) {
        // Lay gio hang
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        // Tinh tong tien
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getCurrentPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Ap dung coupon
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank()) {
            try {
                Coupon coupon = couponService.validate(couponCode, totalAmount);
                discountAmount = couponService.calculateDiscount(coupon, totalAmount);
                couponService.markUsed(couponCode);
            } catch (RuntimeException e) {
                throw new RuntimeException("Coupon lỗi: " + e.getMessage());
            }
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        // Tao don hang
        User user = new User(); user.setId(userId);
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .status(OrderStatus.PENDING)
                .shippingName(shippingName)
                .shippingPhone(shippingPhone)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod != null ? paymentMethod : "COD")
                .couponCode(couponCode != null && !couponCode.isBlank() ? couponCode.toUpperCase() : null)
                .note(note)
                .build();
        Order savedOrder = orderRepository.save(order);

        // Tao order items + tru ton kho
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Kiem tra ton kho lan cuoi
            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new RuntimeException("Sản phẩm \"" + product.getName() + "\" không đủ hàng!");
            }

            // Tru ton kho
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Tao order item
            OrderItem item = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getCurrentPrice())
                    .productName(product.getName())
                    .productImage(product.getMainImage())
                    .build();
            orderItemRepository.save(item);
        }

        // Xoa gio hang
        cartRepository.deleteByUserId(userId);

        return savedOrder;
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = findById(orderId);
        // Validate luong trang thai
        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        // Neu huy don -> hoan tra ton kho
        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        return orderRepository.save(order);
    }

    public Order cancelByUser(Long orderId, Long userId) {
        Order order = findById(orderId);

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền hủy đơn này!");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn ở trạng thái 'Chờ xác nhận'!");
        }

        order.setStatus(OrderStatus.CANCELLED);
        restoreStock(order);
        return orderRepository.save(order);
    }

    private void restoreStock(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        items.forEach(item -> {
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        });
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING   -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.SHIPPING  || next == OrderStatus.CANCELLED;
            case SHIPPING  -> next == OrderStatus.COMPLETED;
            default        -> false;
        };
        if (!valid) {
            throw new RuntimeException("Không thể chuyển từ " + current.getDisplayName() +
                    " sang " + next.getDisplayName());
        }
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng #" + id));
    }

    @Transactional(readOnly = true)
    public List<Order> findByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Page<Order> findAllForAdmin(int page, int size) {
        return orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Page<Order> findByStatus(OrderStatus status, int page, int size) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        BigDecimal rev = orderRepository.sumCompletedRevenue();
        return rev != null ? rev : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, BigDecimal> getMonthlyRevenueStats() {
        List<Order> completed = orderRepository.findByStatus(OrderStatus.COMPLETED);
        java.util.Map<String, BigDecimal> stats = new java.util.TreeMap<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");
        for (Order o : completed) {
            String month = o.getCreatedAt().format(formatter);
            stats.put(month, stats.getOrDefault(month, java.math.BigDecimal.ZERO).add(o.getFinalAmount()));
        }
        return stats;
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return orderRepository.count();
    }
}
