package com.jewelryshop.controller.admin;
import com.jewelryshop.enums.OrderStatus;
import com.jewelryshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    @Autowired private OrderService orderService;

    @GetMapping
    public String list(@RequestParam(required = false) String status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        if (status != null && !status.isBlank()) {
            OrderStatus st = OrderStatus.valueOf(status);
            model.addAttribute("orders", orderService.findByStatus(st, page, 15));
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("orders", orderService.findAllForAdmin(page, 15));
        }
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("currentPage", page);
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes ra) {
        try {
            orderService.updateStatus(id, OrderStatus.valueOf(status));
            ra.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}
