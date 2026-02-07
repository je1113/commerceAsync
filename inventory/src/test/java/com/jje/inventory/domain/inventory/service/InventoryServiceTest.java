package com.jje.inventory.domain.inventory.service;

import com.jje.inventory.common.exception.InsufficientStockException;
import com.jje.inventory.domain.inventory.dto.InventoryCreateRequest;
import com.jje.inventory.domain.inventory.dto.InventoryResponse;
import com.jje.inventory.domain.inventory.entity.Inventory;
import com.jje.inventory.domain.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Test
    @DisplayName("재고를 생성한다")
    void create() {
        // given
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(100)
                .build();

        given(inventoryRepository.save(any(Inventory.class))).willReturn(inventory);

        InventoryCreateRequest request = new InventoryCreateRequest();
        // InventoryCreateRequest에는 setter가 없으므로 리플렉션으로 설정
        setField(request, "productId", 1L);
        setField(request, "quantity", 100);

        // when
        InventoryResponse response = inventoryService.create(request);

        // then
        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getQuantity()).isEqualTo(100);
        assertThat(response.getReservedQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("재고를 차감한다")
    void deductStock() {
        // given
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(100)
                .build();

        given(inventoryRepository.findByProductId(1L)).willReturn(Optional.of(inventory));

        // when
        InventoryResponse response = inventoryService.deductStock(1L, 30);

        // then
        assertThat(response.getQuantity()).isEqualTo(70);
    }

    @Test
    @DisplayName("재고 부족 시 차감에 실패한다")
    void deductInsufficientStock() {
        // given
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(10)
                .build();

        given(inventoryRepository.findByProductId(1L)).willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() -> inventoryService.deductStock(1L, 50))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    @DisplayName("재고를 복구한다")
    void restoreStock() {
        // given
        Inventory inventory = Inventory.builder()
                .productId(1L)
                .quantity(70)
                .build();

        given(inventoryRepository.findByProductId(1L)).willReturn(Optional.of(inventory));

        // when
        InventoryResponse response = inventoryService.restoreStock(1L, 30);

        // then
        assertThat(response.getQuantity()).isEqualTo(100);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
