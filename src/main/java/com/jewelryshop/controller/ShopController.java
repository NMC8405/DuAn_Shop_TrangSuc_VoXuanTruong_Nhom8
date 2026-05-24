package com.jewelryshop.controller;

import com.jewelryshop.entity.Product;
import com.jewelryshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private ReviewService reviewService;

    @GetMapping
    public String shop(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Product> products = productService.findWithFilters(
                keyword, categoryId, minPrice, maxPrice, brand, sort, page, 12);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("brand", brand);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop/products";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.incrementView(id);
        Double avgRating = reviewService.getAvgRating(id);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.findApprovedByProduct(id));
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("relatedProducts",
                productService.findWithFilters(null,
                        product.getCategory() != null ? product.getCategory().getId() : null,
                        null, null, null, "newest", 0, 4).getContent());
        return "shop/product-detail";
    }
}
