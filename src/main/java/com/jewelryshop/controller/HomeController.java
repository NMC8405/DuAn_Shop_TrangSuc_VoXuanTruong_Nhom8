package com.jewelryshop.controller;

import com.jewelryshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("newArrivals", productService.findNewArrivals());
        model.addAttribute("popularProducts", productService.findPopular());
        model.addAttribute("saleProducts", productService.findOnSale());
        return "index";
    }
}
