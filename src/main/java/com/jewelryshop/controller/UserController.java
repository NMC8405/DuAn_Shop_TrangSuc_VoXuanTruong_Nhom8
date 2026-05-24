package com.jewelryshop.controller;

import com.jewelryshop.entity.User;
import com.jewelryshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private ReviewService reviewService;

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam String address,
                                RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        userService.updateProfile(user.getId(), fullName, phone, address);
        ra.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Mật khẩu mới không khớp!");
            return "redirect:/user/profile";
        }
        try {
            User user = userService.getCurrentUser();
            userService.changePassword(user.getId(), oldPassword, newPassword);
            ra.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("orders", orderService.findByUser(user.getId()));
        return "user/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        User user = userService.getCurrentUser();
        var order = orderService.findById(id);
        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/user/orders";
        }
        model.addAttribute("order", order);
        return "user/order-detail";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes ra) {
        try {
            User user = userService.getCurrentUser();
            orderService.cancelByUser(id, user.getId());
            ra.addFlashAttribute("success", "Đã hủy đơn hàng thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/orders/" + id;
    }

    @PostMapping("/reviews")
    public String addReview(@RequestParam Long productId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            RedirectAttributes ra) {
        try {
            User user = userService.getCurrentUser();
            reviewService.addReview(productId, user.getId(), rating, comment);
            ra.addFlashAttribute("success", "Đánh giá của bạn đã được gửi và đang chờ duyệt!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/shop/" + productId;
    }
}
