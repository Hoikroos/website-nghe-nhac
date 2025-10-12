package com.musical.musican.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.musical.musican.Model.Entity.Category;
import com.musical.musican.Repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category addCategory(Category category) throws IllegalArgumentException {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Tên thể loại đã tồn tại!");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, Category updatedCategory) throws IllegalArgumentException {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (!existingCategory.isPresent()) {
            throw new IllegalArgumentException("Thể loại không tồn tại!");
        }
        if (!existingCategory.get().getName().equals(updatedCategory.getName()) &&
                categoryRepository.existsByName(updatedCategory.getName())) {
            throw new IllegalArgumentException("Tên thể loại mới đã tồn tại!");
        }
        updatedCategory.setId(id);
        return categoryRepository.save(updatedCategory);
    }

    public void deleteCategory(Integer id) throws IllegalArgumentException {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Thể loại không tồn tại!");
        }
        categoryRepository.deleteById(id);
    }
}
