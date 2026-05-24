package com.jewelryshop.controller;

import com.jewelryshop.entity.User;
import com.jewelryshop.service.CartService;
import com.jewelryshop.service.CouponService;
import com.jewelryshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;
    @Autowired private UserService userService;
    @Autowired private CouponService couponService;

    @GetMapping
    public String cartPage(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("cartItems", cartService.getCartItems(user.getId()));
        model.addAttribute("cartTotal", cartService.getCartTotal(user.getId()));
        return "cart/cart";
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> getCartCount() {
        Map<String,Object> res = new HashMap<>();
        try {
            User user = userService.getCurrentUser();
            res.put("count", cartService.getCartCount(user.getId()));
        } catch (Exception e) { res.put("count", 0); }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        Map<String,Object> response = new HashMap<>();
        try {
            User user = userService.getCurrentUser();
            cartService.addToCart(user.getId(), productId, quantity);
            int count = cartService.getCartCount(user.getId());
            response.put("success", true);
            response.put("message", "Đã thêm vào giỏ hàng!");
            response.put("cartCount", count);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{itemId}")
    public String updateQuantity(@PathVariable Long itemId,
                                 @RequestParam int quantity,
                                 RedirectAttributes ra) {
        try {
            User user = userService.getCurrentUser();
            cartService.updateQuantity(user.getId(), itemId, quantity);
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{itemId}")
    public String removeItem(@PathVariable Long itemId, RedirectAttributes ra) {
        try {
            User user = userService.getCurrentUser();
            cartService.removeItem(user.getId(), itemId);
            ra.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/cart";
    }

    @PostMapping("/apply-coupon")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> applyCoupon(
            @RequestParam String couponCode,
            @RequestParam BigDecimal orderAmount) {
        Map<String,Object> response = new HashMap<>();
        try {
            var coupon = couponService.validate(couponCode, orderAmount);
            var discount = couponService.calculateDiscount(coupon, orderAmount);
            response.put("success", true);
            response.put("discount", discount);
            response.put("message", "Áp dụng thành công! Giảm " +
                    String.format("%,.0f", discount) + "đ");
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
