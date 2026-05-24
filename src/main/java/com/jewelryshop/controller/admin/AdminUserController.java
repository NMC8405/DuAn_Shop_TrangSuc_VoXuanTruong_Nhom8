package com.jewelryshop.controller.admin;
import com.jewelryshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    @Autowired private UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("user", new com.jewelryshop.entity.User());
        model.addAttribute("roles", com.jewelryshop.enums.Role.values());
        return "admin/users/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", com.jewelryshop.enums.Role.values());
        return "admin/users/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute com.jewelryshop.entity.User user, RedirectAttributes ra) {
        try {
            userService.save(user);
            ra.addFlashAttribute("success", "Lưu thông tin người dùng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.delete(id);
            ra.addFlashAttribute("success", "Đã xóa tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/toggle-lock")
    public String toggleLock(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleLock(id);
        ra.addFlashAttribute("success", "Đã cập nhật trạng thái tài khoản!");
        return "redirect:/admin/users";
    }
}
