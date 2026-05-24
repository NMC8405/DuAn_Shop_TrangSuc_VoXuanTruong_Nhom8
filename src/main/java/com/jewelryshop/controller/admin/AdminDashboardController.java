package com.jewelryshop.controller.admin;

import com.jewelryshop.enums.OrderStatus;
import com.jewelryshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired private ProductService productService;
    @Autowired private OrderService orderService;
    @Autowired private UserService userService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        // Thong ke tong quan
        model.addAttribute("totalProducts",   productService.countActive());
        model.addAttribute("totalOrders",     orderService.countAll());
        model.addAttribute("totalUsers",      userService.countUsers());
        model.addAttribute("totalRevenue",    orderService.getTotalRevenue());
        model.addAttribute("monthlyRevenue",  orderService.getMonthlyRevenueStats());

        // Thong ke don hang theo trang thai
        model.addAttribute("pendingOrders",   orderService.countByStatus(OrderStatus.PENDING));
        model.addAttribute("confirmedOrders", orderService.countByStatus(OrderStatus.CONFIRMED));
        model.addAttribute("shippingOrders",  orderService.countByStatus(OrderStatus.SHIPPING));
        model.addAttribute("completedOrders", orderService.countByStatus(OrderStatus.COMPLETED));

        // San pham sap het hang (< 5)
        model.addAttribute("lowStockProducts", productService.findLowStock(5));
        model.addAttribute("lowStockCount",    productService.countLowStock(5));

        // Don hang moi nhat
        model.addAttribute("recentOrders", orderService.findAllForAdmin(0, 10).getContent());

        return "admin/dashboard";
    }
}
