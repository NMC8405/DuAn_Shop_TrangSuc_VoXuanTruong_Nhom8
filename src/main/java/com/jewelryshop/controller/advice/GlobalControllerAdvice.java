package com.jewelryshop.controller.advice;

import com.jewelryshop.entity.Category;
import com.jewelryshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("globalCategories")
    public List<Category> getGlobalCategories() {
        return categoryService.findAllActive();
    }
}
