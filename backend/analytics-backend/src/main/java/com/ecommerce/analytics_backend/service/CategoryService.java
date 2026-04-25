package com.ecommerce.analytics_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.analytics_backend.model.Category;
import com.ecommerce.analytics_backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category createCategory(Category category) {
        // Business logic (e.g., validation) goes here before saving
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}