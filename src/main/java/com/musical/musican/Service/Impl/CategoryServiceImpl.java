package com.musical.musican.Service.Impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.musical.musican.Model.Entity.Category;
import com.musical.musican.Repository.CategoryRepository;
import com.musical.musican.Service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> getCategoryById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return categoryRepository.findById(id);
    }

    @Override
    public Category addCategory(Category category) throws IllegalArgumentException {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thể loại không được để trống!");
        }
        if (categoryRepository.existsByName(category.getName().trim())) {
            throw new IllegalArgumentException("Tên thể loại đã tồn tại!");
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Integer id, Category updatedCategory) throws IllegalArgumentException {
        if (id == null || updatedCategory == null || updatedCategory.getName() == null
                || updatedCategory.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin thể loại không hợp lệ!");
        }
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (!existingCategory.isPresent()) {
            throw new IllegalArgumentException("Thể loại không tồn tại!");
        }
        if (!existingCategory.get().getName().equals(updatedCategory.getName().trim()) &&
                categoryRepository.existsByName(updatedCategory.getName().trim())) {
            throw new IllegalArgumentException("Tên thể loại mới đã tồn tại!");
        }
        updatedCategory.setId(id);
        return categoryRepository.save(updatedCategory);
    }

    @Override
    public void deleteCategory(Integer id) throws IllegalArgumentException {
        if (id == null || !categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Thể loại không tồn tại!");
        }
        categoryRepository.deleteById(id);
    }

     @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}