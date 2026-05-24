package com.jewelryshop.controller;

import com.jewelryshop.dto.CheckoutDto;
import com.jewelryshop.entity.User;
import com.jewelryshop.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired private CartService cartService;
    @Autowired private OrderService orderService;
    @Autowired private UserService userService;

    @GetMapping
    public String checkoutPage(Model model) {
        User user = userService.getCurrentUser();
        var cartItems = cartService.getCartItems(user.getId());
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(user.getId()));
        model.addAttribute("checkoutDto", new CheckoutDto(
                user.getFullName(), user.getPhone(), user.getAddress(), "COD", null, null));
        model.addAttribute("user", user);
        return "cart/checkout";
    }

    @PostMapping
    public String placeOrder(@Valid @ModelAttribute CheckoutDto dto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();

        if (result.hasErrors()) {
            model.addAttribute("cartItems", cartService.getCartItems(user.getId()));
            model.addAttribute("cartTotal", cartService.getCartTotal(user.getId()));
            model.addAttribute("user", user);
            return "cart/checkout";
        }

        try {
            var order = orderService.placeOrder(
                    user.getId(),
                    dto.getShippingName(),
                    dto.getShippingPhone(),
                    dto.getShippingAddress(),
                    dto.getNote(),
                    dto.getCouponCode(),
                    dto.getPaymentMethod());
            redirectAttributes.addFlashAttribute("success",
                    "Đặt hàng thành công! Mã đơn hàng: #" + order.getId());
            return "redirect:/user/orders/" + order.getId();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cartItems", cartService.getCartItems(user.getId()));
            model.addAttribute("cartTotal", cartService.getCartTotal(user.getId()));
            model.addAttribute("user", user);
            return "cart/checkout";
        }
    }
}
