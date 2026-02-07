package com.jje.product.domain.category.service;

import com.jje.product.common.exception.EntityNotFoundException;
import com.jje.product.domain.category.dto.CategoryCreateRequest;
import com.jje.product.domain.category.dto.CategoryResponse;
import com.jje.product.domain.category.entity.Category;
import com.jje.product.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        int depth = 0;

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("상위 카테고리", request.getParentId()));
            depth = parent.getDepth() + 1;
        }

        Category category = Category.builder()
                .name(request.getName())
                .parentId(request.getParentId())
                .depth(depth)
                .build();

        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리", id));
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리", id));

        category.update(request.getName());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리", id));
        categoryRepository.delete(category);
    }
}
