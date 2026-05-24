package com.jewelryshop.service;

import com.jewelryshop.entity.CartItem;
import com.jewelryshop.entity.Product;
import com.jewelryshop.entity.User;
import com.jewelryshop.repository.CartRepository;
import com.jewelryshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public int getCartCount(Long userId) {
        return cartRepository.countByUserId(userId);
    }

    public void addToCart(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (!product.isActive()) {
            throw new RuntimeException("Sản phẩm không còn bán");
        }

        // Kiem tra ton kho
        Optional<CartItem> existing = cartRepository.findByUserIdAndProductId(userId, productId);
        int currentQty = existing.map(CartItem::getQuantity).orElse(0);
        int totalQty = currentQty + quantity;

        if (totalQty > product.getStockQuantity()) {
            throw new RuntimeException("Chỉ còn " + product.getStockQuantity() + " sản phẩm trong kho!");
        }

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartRepository.save(item);
        } else {
            User user = new User();
            user.setId(userId);
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cartRepository.save(newItem);
        }
    }

    public void updateQuantity(Long userId, Long itemId, int quantity) {
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền");
        }

        if (quantity <= 0) {
            cartRepository.delete(item);
            return;
        }

        // Kiem tra ton kho
        if (quantity > item.getProduct().getStockQuantity()) {
            throw new RuntimeException("Chỉ còn " + item.getProduct().getStockQuantity() + " sản phẩm trong kho!");
        }

        item.setQuantity(quantity);
        cartRepository.save(item);
    }

    public void removeItem(Long userId, Long itemId) {
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item"));
        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền");
        }
        cartRepository.delete(item);
    }

    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(Long userId) {
        return getCartItems(userId).stream()
                .map(item -> item.getProduct().getCurrentPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
