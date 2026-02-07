package com.jje.point.domain.point.controller;

import com.jje.point.common.exception.EntityNotFoundException;
import com.jje.point.domain.point.dto.PointResponse;
import com.jje.point.domain.point.service.PointService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointService pointService;

    @Test
    @DisplayName("GET /api/points/{userId} - 잔액 조회 성공 200")
    void getBalance200() throws Exception {
        PointResponse response = PointResponse.builder()
                .id(1L)
                .userId(1L)
                .balance(BigDecimal.valueOf(5000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(pointService.getBalance(1L)).willReturn(response);

        mockMvc.perform(get("/api/points/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.balance").value(5000));
    }

    @Test
    @DisplayName("GET /api/points/{userId} - 존재하지 않는 사용자 404")
    void getBalance404() throws Exception {
        given(pointService.getBalance(999L))
                .willThrow(new EntityNotFoundException("포인트", 999L));

        mockMvc.perform(get("/api/points/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/points/earn - 포인트 적립 201")
    void earn201() throws Exception {
        PointResponse response = PointResponse.builder()
                .id(1L)
                .userId(1L)
                .balance(BigDecimal.valueOf(1000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(pointService.earn(eq(1L), any(BigDecimal.class), any()))
                .willReturn(response);

        mockMvc.perform(post("/api/points/earn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": 1,
                                    "amount": 1000,
                                    "description": "구매 적립"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.balance").value(1000));
    }
}
