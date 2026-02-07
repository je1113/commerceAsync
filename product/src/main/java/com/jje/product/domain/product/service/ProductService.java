package com.jje.product.domain.product.service;

import com.jje.product.common.exception.EntityNotFoundException;
import com.jje.product.domain.category.entity.Category;
import com.jje.product.domain.category.repository.CategoryRepository;
import com.jje.product.domain.product.dto.ProductCreateRequest;
import com.jje.product.domain.product.dto.ProductResponse;
import com.jje.product.domain.product.dto.ProductUpdateRequest;
import com.jje.product.domain.product.entity.Product;
import com.jje.product.domain.product.entity.ProductStatus;
import com.jje.product.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .build();

        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품", id));
        return ProductResponse.from(product);
    }

    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponse::from);
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리", request.getCategoryId()));

        product.update(request.getName(), request.getDescription(), request.getPrice(), category);
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품", id));

        product.changeStatus(ProductStatus.DISCONTINUED);
    }
}
