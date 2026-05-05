package com.project.emprendia.entrepreneurship.service;

import com.project.emprendia.entrepreneurship.dto.CategoryRequest;
import com.project.emprendia.entrepreneurship.dto.CategoryResponse;

import java.util.List;

/**
 * Service interface for managing entrepreneurship categories.
 */
public interface CategoryService {

    /**
     * Get all categories
     */
    List<CategoryResponse> findAll();

    /**
     * Get category by ID
     */
    CategoryResponse findById(Long id);

    /**
     * Get category by name
     */
    CategoryResponse findByName(String name);

    /**
     * Create new category
     */
    CategoryResponse create(CategoryRequest request);

    /**
     * Update existing category
     */
    CategoryResponse update(Long id, CategoryRequest request);

    /**
     * Delete category
     */
    void delete(Long id);

    /**
     * Check if category exists by name
     */
    boolean existsByName(String name);
}
