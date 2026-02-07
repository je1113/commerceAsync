package com.jje.inventory.domain.inventory.controller;

import com.jje.inventory.domain.inventory.dto.InventoryCreateRequest;
import com.jje.inventory.domain.inventory.dto.InventoryResponse;
import com.jje.inventory.domain.inventory.dto.InventoryUpdateRequest;
import com.jje.inventory.domain.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody InventoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.create(request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProductId(productId));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> updateQuantity(@PathVariable Long productId,
                                                             @Valid @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateQuantity(productId, request));
    }

    @PostMapping("/product/{productId}/deduct")
    public ResponseEntity<InventoryResponse> deductStock(@PathVariable Long productId,
                                                          @RequestParam int quantity) {
        return ResponseEntity.ok(inventoryService.deductStock(productId, quantity));
    }

    @PostMapping("/product/{productId}/restore")
    public ResponseEntity<InventoryResponse> restoreStock(@PathVariable Long productId,
                                                           @RequestParam int quantity) {
        return ResponseEntity.ok(inventoryService.restoreStock(productId, quantity));
    }
}
