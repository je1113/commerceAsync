package com.jje.order.domain.order.entity;

import com.jje.order.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private BigDecimal usedPoints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(String orderNumber, Long userId, BigDecimal totalAmount, BigDecimal usedPoints) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.usedPoints = usedPoints != null ? usedPoints : BigDecimal.ZERO;
        this.status = OrderStatus.PENDING;
    }

    public void addItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void changeStatus(OrderStatus newStatus) {
        if (!newStatus.canTransitionFrom(this.status)) {
            throw new IllegalStateException(
                    "주문 상태를 " + this.status + "에서 " + newStatus + "(으)로 변경할 수 없습니다");
        }
        this.status = newStatus;
    }
}
