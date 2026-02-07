package com.jje.product.domain.product.service;

import com.jje.product.common.exception.EntityNotFoundException;
import com.jje.product.domain.category.entity.Category;
import com.jje.product.domain.category.repository.CategoryRepository;
import com.jje.product.domain.product.dto.ProductCreateRequest;
import com.jje.product.domain.product.dto.ProductResponse;
import com.jje.product.domain.product.entity.Product;
import com.jje.product.domain.product.entity.ProductStatus;
import com.jje.product.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("상품을 정상적으로 등록한다")
    void createProduct() {
        // given
        Category category = Category.builder()
                .name("의류")
                .depth(0)
                .build();

        Product product = Product.builder()
                .name("테스트 상품")
                .description("테스트 설명")
                .price(BigDecimal.valueOf(10000))
                .category(category)
                .build();

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(productRepository.save(any(Product.class))).willReturn(product);

        ProductCreateRequest request = createRequest("테스트 상품", "테스트 설명", BigDecimal.valueOf(10000), 1L);

        // when
        ProductResponse response = productService.create(request);

        // then
        assertThat(response.getName()).isEqualTo("테스트 상품");
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(response.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
    void getProductNotFound() {
        // given
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품 등록 시 예외가 발생한다")
    void createProductWithInvalidCategory() {
        // given
        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        ProductCreateRequest request = createRequest("테스트 상품", "설명", BigDecimal.valueOf(10000), 999L);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private ProductCreateRequest createRequest(String name, String description, BigDecimal price, Long categoryId) {
        try {
            ProductCreateRequest request = new ProductCreateRequest();
            var nameField = ProductCreateRequest.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(request, name);

            var descField = ProductCreateRequest.class.getDeclaredField("description");
            descField.setAccessible(true);
            descField.set(request, description);

            var priceField = ProductCreateRequest.class.getDeclaredField("price");
            priceField.setAccessible(true);
            priceField.set(request, price);

            var categoryIdField = ProductCreateRequest.class.getDeclaredField("categoryId");
            categoryIdField.setAccessible(true);
            categoryIdField.set(request, categoryId);

            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
