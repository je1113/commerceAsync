package com.jje.product.domain.product.controller;

import tools.jackson.databind.ObjectMapper;
import com.jje.product.common.exception.EntityNotFoundException;
import com.jje.product.domain.product.dto.ProductResponse;
import com.jje.product.domain.product.entity.ProductStatus;
import com.jje.product.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("POST /api/products - 상품 등록 성공")
    void createProduct() throws Exception {
        // given
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("테스트 상품")
                .description("테스트 설명")
                .price(BigDecimal.valueOf(10000))
                .status(ProductStatus.ACTIVE)
                .categoryId(1L)
                .categoryName("의류")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(productService.create(any())).willReturn(response);

        String requestBody = """
                {
                    "name": "테스트 상품",
                    "description": "테스트 설명",
                    "price": 10000,
                    "categoryId": 1
                }
                """;

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("테스트 상품"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/products - 상품명 누락 시 400 에러")
    void createProductWithoutName() throws Exception {
        String requestBody = """
                {
                    "description": "테스트 설명",
                    "price": 10000,
                    "categoryId": 1
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    @DisplayName("GET /api/products/{id} - 존재하지 않는 상품 조회 시 404")
    void getProductNotFound() throws Exception {
        given(productService.getById(999L))
                .willThrow(new EntityNotFoundException("상품", 999L));

        mockMvc.perform(get("/api/products/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("상품을(를) 찾을 수 없습니다. id=999"));
    }
}
