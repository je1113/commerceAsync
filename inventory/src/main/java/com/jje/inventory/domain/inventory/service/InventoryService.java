package com.jje.inventory.domain.inventory.service;

import com.jje.inventory.common.exception.EntityNotFoundException;
import com.jje.inventory.domain.inventory.dto.InventoryCreateRequest;
import com.jje.inventory.domain.inventory.dto.InventoryResponse;
import com.jje.inventory.domain.inventory.dto.InventoryUpdateRequest;
import com.jje.inventory.domain.inventory.entity.Inventory;
import com.jje.inventory.domain.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse create(InventoryCreateRequest request) {
        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();

        inventory = inventoryRepository.save(inventory);
        log.info("[INVENTORY] 재고 생성 - productId={}, quantity={}", request.getProductId(), request.getQuantity());
        return InventoryResponse.from(inventory);
    }

    public InventoryResponse getByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("재고", "productId", productId));
        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse updateQuantity(Long productId, InventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("재고", "productId", productId));

        inventory.updateQuantity(request.getQuantity());
        log.info("[INVENTORY] 재고 수량 변경 - productId={}, quantity={}", productId, request.getQuantity());
        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse deductStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("재고", "productId", productId));

        inventory.deduct(quantity);
        log.info("[INVENTORY] 재고 차감 - productId={}, deducted={}, remaining={}", productId, quantity, inventory.getQuantity());
        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse restoreStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("재고", "productId", productId));

        inventory.restore(quantity);
        log.info("[INVENTORY] 재고 복구 - productId={}, restored={}, total={}", productId, quantity, inventory.getQuantity());
        return InventoryResponse.from(inventory);
    }
}
