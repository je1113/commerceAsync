package com.jje.inventory.domain.inventory.entity;

import com.jje.inventory.common.entity.BaseEntity;
import com.jje.inventory.common.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int reservedQuantity;

    @Builder
    public Inventory(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.reservedQuantity = 0;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void deduct(int qty) {
        if (this.quantity < qty) {
            throw new InsufficientStockException(this.productId, qty, this.quantity);
        }
        this.quantity -= qty;
    }

    public void restore(int qty) {
        this.quantity += qty;
    }

    public void reserve(int qty) {
        int available = this.quantity - this.reservedQuantity;
        if (available < qty) {
            throw new InsufficientStockException(this.productId, qty, available);
        }
        this.reservedQuantity += qty;
    }

    public void cancelReservation(int qty) {
        this.reservedQuantity -= qty;
        if (this.reservedQuantity < 0) {
            this.reservedQuantity = 0;
        }
    }

    public int getAvailableQuantity() {
        return this.quantity - this.reservedQuantity;
    }
}
