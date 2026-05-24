package com.jewelryshop.controller;

import com.jewelryshop.entity.Product;
import com.jewelryshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatApiController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleChatMessage(@RequestBody Map<String, String> payload) {
        String userMsg = payload.getOrDefault("message", "").trim();
        Map<String, Object> response = new HashMap<>();

        if (userMsg.isEmpty()) {
            response.put("reply", "Xin chào! Tôi là Trợ lý ảo LuxeJewel. Tôi có thể giúp gì cho bạn hôm nay?");
            return ResponseEntity.ok(response);
        }

        String lowerMsg = userMsg.toLowerCase();
        String reply;
        List<Map<String, Object>> suggestedProducts = new ArrayList<>();

        // 1. GREETING & GENERAL
        if (lowerMsg.contains("chào") || lowerMsg.contains("hello") || lowerMsg.contains("hi ") || lowerMsg.equals("hi") || lowerMsg.contains("bạn là ai")) {
            reply = "✨ **Xin chào quý khách!** Tôi là Trợ lý mua sắm thông minh của LuxeJewel. ✨<br><br>" +
                    "Tôi có thể hỗ trợ quý khách thực hiện các công việc sau một cách nhanh chóng:<br>" +
                    "• 🔍 **Tìm kiếm & tư vấn sản phẩm** (ví dụ: *\"Tư vấn nhẫn kim cương\"*, *\"Tìm dây chuyền\"*)<br>" +
                    "• 📏 **Hướng dẫn đo size nhẫn** tại nhà cực đơn giản.<br>" +
                    "• ✨ **Bí quyết làm sạch trang sức** vàng, bạc, kim cương.<br>" +
                    "• 🏷️ **Thông tin ưu đãi** và các chính sách vận chuyển, đổi trả của cửa hàng.<br><br>" +
                    "Quý khách đang quan tâm đến dòng sản phẩm nào để tôi hỗ trợ tư vấn chi tiết ạ?";
        }
        // 2. PRODUCT RECOMMENDATIONS & SEARCH
        else if (containsAny(lowerMsg, "nhẫn", "nhan", "dây chuyền", "day chuyen", "vòng cổ", "vong co", "bông tai", "khuyên tai", "bong tai", "khuyen tai", "vòng tay", "vong tay", "lắc chân", "lac chan", "kim cương", "kim cuong", "ruby", "ngọc trai", "ngoc trai", "vàng", "vang", "bạc", "bac", "emerald", "lục bảo", "mua gì", "tư vấn", "tu van")) {
            
            List<Product> allProducts = productRepository.findAll().stream()
                    .filter(Product::isActive)
                    .collect(Collectors.toList());

            List<Product> matched = new ArrayList<>();
            String categoryKeyword = "";

            if (containsAny(lowerMsg, "nhẫn", "nhan")) {
                matched = filterProducts(allProducts, "Nhẫn");
                categoryKeyword = "nhẫn";
            } else if (containsAny(lowerMsg, "dây chuyền", "day chuyen", "vòng cổ", "vong co")) {
                matched = filterProducts(allProducts, "Dây Chuyền");
                categoryKeyword = "dây chuyền";
            } else if (containsAny(lowerMsg, "bông tai", "khuyên tai", "bong tai", "khuyen tai")) {
                matched = filterProducts(allProducts, "Bông Tai");
                categoryKeyword = "bông tai";
            } else if (containsAny(lowerMsg, "vòng tay", "vong tay", "lắc tay", "lac tay")) {
                matched = filterProducts(allProducts, "Vòng Tay");
                categoryKeyword = "vòng tay";
            } else if (containsAny(lowerMsg, "lắc chân", "lac chan")) {
                matched = filterProducts(allProducts, "Lắc Chân");
                categoryKeyword = "lắc chân";
            }

            // Filter by materials/gems if category matches are low or if explicitly asked
            if (matched.isEmpty() || containsAny(lowerMsg, "kim cương", "kim cuong", "ruby", "ngọc trai", "ngoc trai", "lục bảo", "emerald", "vàng", "vang", "bạc", "bac")) {
                final List<Product> base = matched.isEmpty() ? allProducts : matched;
                if (containsAny(lowerMsg, "kim cương", "kim cuong")) {
                    matched = filterByKeyword(base, "kim cương");
                } else if (containsAny(lowerMsg, "ruby")) {
                    matched = filterByKeyword(base, "ruby");
                } else if (containsAny(lowerMsg, "ngọc trai", "ngoc trai")) {
                    matched = filterByKeyword(base, "ngọc trai");
                } else if (containsAny(lowerMsg, "lục bảo", "emerald")) {
                    matched = filterByKeyword(base, "emerald");
                } else if (containsAny(lowerMsg, "vàng", "vang")) {
                    matched = filterByKeyword(base, "vàng");
                } else if (containsAny(lowerMsg, "bạc", "bac")) {
                    matched = filterByKeyword(base, "bạc");
                }
            }

            if (!matched.isEmpty()) {
                // Limit to top 3 recommendations
                int limit = Math.min(matched.size(), 3);
                DecimalFormat df = new DecimalFormat("#,###");
                
                StringBuilder sb = new StringBuilder();
                sb.append("Dựa trên yêu cầu của quý khách, tôi đã chọn lọc ra **").append(limit).append(" tác phẩm tinh xảo và sang trọng nhất** phù hợp tuyệt đối:<br><br>");

                for (int i = 0; i < limit; i++) {
                    Product p = matched.get(i);
                    BigDecimal price = p.getCurrentPrice();
                    String priceStr = df.format(price) + "đ";

                    sb.append("🔹 **").append(p.getName()).append("**<br>");
                    if (p.getBrand() != null) {
                        sb.append("• Thương hiệu: *").append(p.getBrand()).append("* | ");
                    }
                    if (p.getMaterial() != null) {
                        sb.append("Chất liệu: *").append(p.getMaterial()).append("*<br>");
                    }
                    sb.append("• Giá hiện tại: <span class=\"text-gold fw-bold\">").append(priceStr).append("</span><br>");
                    sb.append("• Trạng thái: ").append(p.getStockQuantity() > 0 ? "<span class=\"text-success\">Còn hàng</span>" : "<span class=\"text-danger\">Tạm hết hàng</span>").append("<br>");
                    sb.append("<a href=\"/shop/").append(p.getId()).append("\" target=\"_blank\" class=\"btn btn-xs btn-gold mt-1 py-1 px-2 d-inline-block text-decoration-none\" style=\"font-size: 0.72rem;\"><i class=\"fas fa-eye me-1\"></i>Xem Chi Tiết</a><br><br>");

                    // Format structured metadata for UI dynamic product card display in chat
                    Map<String, Object> card = new HashMap<>();
                    card.put("id", p.getId());
                    card.put("name", p.getName());
                    card.put("price", priceStr);
                    card.put("image", p.getMainImage());
                    card.put("url", "/shop/" + p.getId());
                    suggestedProducts.add(card);
                }
                
                sb.append("Quý khách có muốn tôi tư vấn thêm về kích cỡ (size) hoặc cách phối hợp bộ trang sức này đi tiệc/quà tặng không ạ?");
                reply = sb.toString();
            } else {
                reply = "Dạ, hiện tại dòng sản phẩm quý khách tìm kiếm tạm thời chưa có mẫu mới cập nhật hoặc nằm ngoài danh mục có sẵn. <br><br>Quý khách có muốn tham khảo các mẫu **Nhẫn Kim Cương Vàng 18K** bán chạy nhất của cửa hàng hoặc tìm kiếm theo danh mục khác không ạ?";
            }
        }
        // 3. SIZE & MEASUREMENT HELP
        else if (containsAny(lowerMsg, "size", "kích cỡ", "kich co", "kích thước", "kich thuoc", "đo", "do size", "chọn nhẫn")) {
            reply = "📏 **Hướng dẫn đo size nhẫn tại nhà cực kỳ đơn giản và chuẩn xác:**<br><br>" +
                    "**👉 Bước 1**: Dùng một sợi chỉ mảnh hoặc một dải giấy nhỏ bản rộng khoảng 5mm.<br>" +
                    "**👉 Bước 2**: Quấn sát quanh ngón tay bạn muốn đeo nhẫn (quấn vừa tay, không quá chặt).<br>" +
                    "**👉 Bước 3**: Đánh dấu điểm giao nhau và dùng thước kẻ đo chiều dài dải giấy bằng mm.<br>" +
                    "**👉 Bước 4**: Lấy số đo mm vừa đo được (chu vi) chia cho **3.14** để ra đường kính lòng trong của nhẫn.<br><br>" +
                    "**BẢNG SIZE PHỔ BIẾN TẠI VIỆT NAM:**<br>" +
                    "• Chu vi 50 - 52mm: Size 10 - 12 (phù hợp đa số nữ Việt Nam)<br>" +
                    "• Chu vi 53 - 55mm: Size 13 - 15<br>" +
                    "• Chu vi 56 - 58mm: Size 16 - 18 (phù hợp nam giới)<br><br>" +
                    "Quý khách đã có số đo chu vi ngón tay chưa ạ? Hãy gửi cho tôi để tôi tư vấn chính xác size nhẫn nhé!";
        }
        // 4. CLEANING & CARE INSTRUCTIONS
        else if (containsAny(lowerMsg, "bảo quản", "bao quan", "làm sạch", "lam sach", "vệ sinh", "ve sinh", "rửa", "rua ", "sáng", "sang ")) {
            reply = "✨ **Bí quyết bảo quản trang sức luôn lấp lánh như mới tại nhà:**<br><br>" +
                    "**1. Đối với Trang Sức Vàng & Kim Cương:**<br>" +
                    "• Pha loãng vài giọt sữa tắm hoặc nước rửa chén nhẹ vào nước ấm.<br>" +
                    "• Ngâm trang sức khoảng 5-10 phút để làm mềm các vết bám bẩn.<br>" +
                    "• Dùng bàn chải đánh răng lông siêu mềm chà thật nhẹ nhàng quanh các ổ đá.<br>" +
                    "• Rửa lại bằng nước sạch và lau khô bằng khăn mềm không xơ.<br><br>" +
                    "**2. Đối với Trang Sức Bạc 925:**<br>" +
                    "• Bạc tự nhiên có thể bị xỉn màu do mồ hôi lưu huỳnh. Quý khách chỉ cần ngâm bạc trong hỗn hợp *Nước ấm + Giấm ăn + Muối tinh* trong 15 phút, bạc sẽ tự động sáng bóng trở lại như lúc mới mua!<br><br>" +
                    "⚠️ **Lưu ý quan trọng**: Hạn chế để trang sức tiếp xúc trực tiếp với nước hoa, mỹ phẩm và tháo ra khi vận động mạnh hoặc làm việc nhà quý khách nhé!";
        }
        // 5. COUPONS & PROMOTIONS
        else if (containsAny(lowerMsg, "khuyến mãi", "khuyen mai", "giảm giá", "giam gia", "coupon", "voucher", "ưu đãi", "uu dai", "mã", "ma ")) {
            reply = "🏷️ **Chương trình ưu đãi đặc quyền hiện tại của LuxeJewel:**<br><br>" +
                    "• 🚚 **Ưu đãi vận chuyển**: Miễn phí vận chuyển hỏa tốc toàn quốc cho tất cả các đơn hàng có giá trị từ **2,000,000đ** trở lên.<br>" +
                    "• 💝 **Mã Giảm Giá Active**: Quý khách có thể sử dụng các mã giảm giá đặc biệt khi thanh toán:<br>" +
                    "  - Nhập **`LUXE10`**: Giảm ngay 10% cho các sản phẩm trong bộ sưu tập Nhẫn và Dây chuyền mới.<br>" +
                    "  - Nhập **`GIAM500K`**: Giảm thẳng 500,000đ cho đơn hàng trang sức vàng/kim cương từ 10,000,000đ.<br><br>" +
                    "Quý khách có thể thử áp dụng mã ngay trong giỏ hàng để nhận chiết khấu trực tiếp nhé!";
        }
        // 6. STORE POLICIES (RETURNS, WARRANTY, SHIPPING)
        else if (containsAny(lowerMsg, "đổi trả", "doi tra", "bảo hành", "bao hanh", "vận chuyển", "van chuyen", "ship", "chính sách", "chinh sach")) {
            reply = "🛡️ **Cam kết vàng & Chính sách dịch vụ tại LuxeJewel:**<br><br>" +
                    "• 🛍️ **Chính sách đổi trả**: Hỗ trợ đổi trả miễn phí vô điều kiện trong vòng **30 ngày** kể từ ngày nhận hàng nếu phát sinh lỗi từ nhà sản xuất hoặc quý khách muốn đổi size, mẫu mã khác.<br>" +
                    "• 🔍 **Cam kết chất lượng**: Cam kết sản phẩm chính hãng 100%, trang sức kim cương và đá quý thiên nhiên đi kèm đầy đủ giấy kiểm định uy tín của GIA/DOJI.<br>" +
                    "• 🚚 **Vận chuyển**: Đơn hàng dưới 2 triệu có phí ship đồng giá toàn quốc là 30,000đ. Đơn trên 2 triệu được miễn phí giao tận nhà.<br>" +
                    "• 🔧 **Bảo hành trọn đời**: Miễn phí đánh bóng, siêu âm làm sạch trang sức trọn đời tại hệ thống cửa hàng LuxeJewel.<br><br>" +
                    "Quý khách hoàn toàn có thể an tâm khi mua sắm tại cửa hàng của chúng tôi!";
        }
        // 7. DEFAULT FALLBACK
        else {
            reply = "Dạ, tôi chưa hiểu rõ ý của quý khách lắm. Tôi là trợ lý ảo chuyên về **Trang sức LuxeJewel**.<br><br>" +
                    "Quý khách có thể thử hỏi tôi các câu hỏi như:<br>" +
                    "• *\"Tư vấn cho tôi nhẫn đính hôn\"*<br>" +
                    "• *\"Làm sao để đo kích cỡ size ngón tay?\"*<br>" +
                    "• *\"Chỉ tôi cách vệ sinh trang sức bạc\"*<br>" +
                    "• *\"Hiện tại shop đang có mã giảm giá nào?\"*<br><br>" +
                    "Rất hân hạnh được hỗ trợ và phục vụ quý khách hàng!";
        }

        response.put("reply", reply);
        response.put("suggestedProducts", suggestedProducts);
        return ResponseEntity.ok(response);
    }

    private boolean containsAny(String input, String... keywords) {
        for (String kw : keywords) {
            if (input.contains(kw)) return true;
        }
        return false;
    }

    private List<Product> filterProducts(List<Product> list, String categoryName) {
        return list.stream()
                .filter(p -> p.getCategory() != null && categoryName.equalsIgnoreCase(p.getCategory().getName()))
                .collect(Collectors.toList());
    }

    private List<Product> filterByKeyword(List<Product> list, String keyword) {
        return list.stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword) || 
                            (p.getMaterial() != null && p.getMaterial().toLowerCase().contains(keyword)) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword)))
                .collect(Collectors.toList());
    }
}
