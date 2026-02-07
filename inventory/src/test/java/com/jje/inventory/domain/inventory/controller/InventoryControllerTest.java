package com.jje.inventory.domain.inventory.controller;

import com.jje.inventory.common.exception.EntityNotFoundException;
import com.jje.inventory.domain.inventory.dto.InventoryCreateRequest;
import com.jje.inventory.domain.inventory.dto.InventoryResponse;
import com.jje.inventory.domain.inventory.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    @DisplayName("GET /api/inventories/product/{productId} - 재고 조회 성공")
    void getInventory() throws Exception {
        InventoryResponse response = InventoryResponse.builder()
                .id(1L)
                .productId(1L)
                .quantity(100)
                .reservedQuantity(10)
                .availableQuantity(90)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(inventoryService.getByProductId(1L)).willReturn(response);

        mockMvc.perform(get("/api/inventories/product/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.availableQuantity").value(90));
    }

    @Test
    @DisplayName("GET /api/inventories/product/{productId} - 존재하지 않는 재고 조회 시 404")
    void getInventoryNotFound() throws Exception {
        given(inventoryService.getByProductId(999L))
                .willThrow(new EntityNotFoundException("재고", "productId", 999L));

        mockMvc.perform(get("/api/inventories/product/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/inventories - 재고 생성 성공")
    void createInventory() throws Exception {
        InventoryResponse response = InventoryResponse.builder()
                .id(1L)
                .productId(1L)
                .quantity(100)
                .reservedQuantity(0)
                .availableQuantity(100)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(inventoryService.create(any(InventoryCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1, \"quantity\": 100}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(100));
    }
}
