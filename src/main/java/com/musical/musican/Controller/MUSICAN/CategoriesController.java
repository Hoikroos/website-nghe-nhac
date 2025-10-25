package com.musical.musican.Controller.MUSICAN;

import com.musical.musican.Model.Entity.Category;
import com.musical.musican.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("showModal", false);
        model.addAttribute("category", new Category());
        return "Musican/categories";
    }
    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("isEdit", false);
        model.addAttribute("showModal", true);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "Musican/categories";
    }
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("showModal", true);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "Musican/categories";
        }
        try {
            categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("message", "Thêm thể loại thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("showModal", true);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "Musican/categories";
        }
        return "redirect:/admin/categories";
    }
    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Integer id, Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (!category.isPresent()) {
            model.addAttribute("errorMessage", "Thể loại không tồn tại!");
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("showModal", false);
            model.addAttribute("category", new Category());
            return "Musican/categories";
        }
        model.addAttribute("category", category.get());
        model.addAttribute("isEdit", true);
        model.addAttribute("showModal", true);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "Musican/categories";
    }
    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("showModal", true);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "Musican/categories";
        }
        try {
            category.setId(id);
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thể loại thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("showModal", true);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "Musican/categories";
        }
        return "redirect:/admin/categories";
    }
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "Xóa thể loại thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}