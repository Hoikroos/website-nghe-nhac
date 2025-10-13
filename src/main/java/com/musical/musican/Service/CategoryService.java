package com.musical.musican.Service;

import java.util.List;
import java.util.Optional;
import com.musical.musican.Model.Entity.Category;

public interface CategoryService {

    List<Category> getAllCategories();

    Optional<Category> getCategoryById(Integer id);

    Category addCategory(Category category) throws IllegalArgumentException;

    Category updateCategory(Integer id, Category updatedCategory) throws IllegalArgumentException;

    void deleteCategory(Integer id) throws IllegalArgumentException;
}