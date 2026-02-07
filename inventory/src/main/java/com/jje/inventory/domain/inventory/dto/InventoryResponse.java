package com.jje.inventory.domain.inventory.dto;

import com.jje.inventory.domain.inventory.entity.Inventory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InventoryResponse {

    private Long id;
    private Long productId;
    private int quantity;
    private int reservedQuantity;
    private int availableQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InventoryResponse from(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
