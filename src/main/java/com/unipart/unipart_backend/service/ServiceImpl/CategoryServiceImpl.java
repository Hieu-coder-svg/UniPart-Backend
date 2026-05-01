package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.CategoryRequest;
import com.unipart.unipart_backend.dto.response.CategoryResponse;
import com.unipart.unipart_backend.entity.Category;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.repository.CategoryRepository;
import com.unipart.unipart_backend.repository.PostRepository;
import com.unipart.unipart_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    private CategoryResponse toDTO(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .categoryName(c.getCategoryName())
                .description(c.getDescription())
                .build();
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }
        Category category = Category.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .build();
        return toDTO(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (!category.getCategoryName().equals(request.getCategoryName())
                && categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        return toDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (postRepository.existsByCategoryId(id)) {
            throw new AppException(ErrorCode.CATEGORY_HAS_POSTS);
        }
        categoryRepository.deleteById(id);
    }
}
