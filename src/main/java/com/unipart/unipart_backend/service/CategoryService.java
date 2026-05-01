package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.CategoryRequest;
import com.unipart.unipart_backend.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    List<CategoryResponse> getAll();
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
}
