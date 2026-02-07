package com.jje.order.domain.order.service;

import com.jje.order.common.exception.EntityNotFoundException;
import com.jje.order.common.exception.InvalidOrderStateException;
import com.jje.order.domain.order.dto.OrderCreateRequest;
import com.jje.order.domain.order.dto.OrderResponse;
import com.jje.order.domain.order.entity.Order;
import com.jje.order.domain.order.entity.OrderItem;
import com.jje.order.domain.order.entity.OrderStatus;
import com.jje.order.domain.order.event.OrderCancelledEvent;
import com.jje.order.domain.order.event.OrderCreatedEvent;
import com.jje.order.domain.order.event.OrderEventPublisher;
import com.jje.order.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public OrderResponse create(OrderCreateRequest request) {
        // 1. 주문번호 생성 (멱등성 키)
        String orderNumber = UUID.randomUUID().toString();

        // 2. 총액 계산
        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 주문 엔티티 생성
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(request.getUserId())
                .totalAmount(totalAmount)
                .usedPoints(request.getUsedPoints())
                .build();

        // 4. 주문 항목 추가
        request.getItems().forEach(itemReq -> {
            OrderItem item = OrderItem.builder()
                    .productId(itemReq.getProductId())
                    .productName(itemReq.getProductName())
                    .price(itemReq.getPrice())
                    .quantity(itemReq.getQuantity())
                    .build();
            order.addItem(item);
        });

        // 5. 저장
        Order saved = orderRepository.save(order);

        // 6. 이벤트 발행 → 결제 서비스가 이 이벤트를 받아 처리
        eventPublisher.publishOrderCreated(OrderCreatedEvent.builder()
                .orderId(saved.getId())
                .orderNumber(saved.getOrderNumber())
                .userId(saved.getUserId())
                .totalAmount(saved.getTotalAmount())
                .usedPoints(saved.getUsedPoints())
                .build());

        return OrderResponse.from(saved);
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문", id));
        return OrderResponse.from(order);
    }

    public Page<OrderResponse> getByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문", id));

        try {
            order.changeStatus(OrderStatus.CANCELLED);
        } catch (IllegalStateException e) {
            throw new InvalidOrderStateException(e.getMessage());
        }

        // 보상 트랜잭션 이벤트 발행 → 결제 환불, 재고 복구, 포인트 환불
        eventPublisher.publishOrderCancelled(OrderCancelledEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .refundAmount(order.getTotalAmount())
                .restorePoints(order.getUsedPoints())
                .build());

        return OrderResponse.from(order);
    }

    /**
     * 외부 서비스(결제 등)에서 상태 변경 요청 시 사용.
     * 나중에 메시지 리스너에서 호출된다.
     */
    @Transactional
    public void changeStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문", id));

        try {
            order.changeStatus(newStatus);
        } catch (IllegalStateException e) {
            throw new InvalidOrderStateException(e.getMessage());
        }
    }
}
