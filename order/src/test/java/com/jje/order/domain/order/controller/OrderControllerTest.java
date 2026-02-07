package com.jje.order.domain.order.controller;

import com.jje.order.common.exception.EntityNotFoundException;
import com.jje.order.common.exception.InvalidOrderStateException;
import com.jje.order.domain.order.dto.OrderResponse;
import com.jje.order.domain.order.entity.OrderStatus;
import com.jje.order.domain.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders - 주문 생성 성공")
    void createOrder() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .orderNumber("test-uuid")
                .userId(1L)
                .totalAmount(BigDecimal.valueOf(30000))
                .usedPoints(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .items(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(orderService.create(any())).willReturn(response);

        String requestBody = """
                {
                    "userId": 1,
                    "items": [
                        {
                            "productId": 1,
                            "productName": "테스트 상품",
                            "price": 10000,
                            "quantity": 3
                        }
                    ],
                    "usedPoints": 0
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.orderNumber").value("test-uuid"));
    }

    @Test
    @DisplayName("POST /api/orders - 주문 상품 누락 시 400 에러")
    void createOrderWithoutItems() throws Exception {
        String requestBody = """
                {
                    "userId": 1,
                    "items": [],
                    "usedPoints": 0
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/orders/{id} - 존재하지 않는 주문 조회 시 404")
    void getOrderNotFound() throws Exception {
        given(orderService.getById(999L))
                .willThrow(new EntityNotFoundException("주문", 999L));

        mockMvc.perform(get("/api/orders/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/orders/{id}/cancel - 상태 충돌 시 409")
    void cancelOrderConflict() throws Exception {
        given(orderService.cancel(1L))
                .willThrow(new InvalidOrderStateException("주문 상태를 COMPLETED에서 CANCELLED(으)로 변경할 수 없습니다"));

        mockMvc.perform(post("/api/orders/1/cancel"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }
}
