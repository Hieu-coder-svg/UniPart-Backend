package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.CategoryRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.CategoryResponse;
import com.unipart.unipart_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAll() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getAll())
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> create(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> update(@PathVariable Long id,
                                                @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
