package com.jje.order.domain.order.service;

import com.jje.order.common.exception.EntityNotFoundException;
import com.jje.order.common.exception.InvalidOrderStateException;
import com.jje.order.domain.order.dto.OrderCreateRequest;
import com.jje.order.domain.order.dto.OrderItemRequest;
import com.jje.order.domain.order.dto.OrderResponse;
import com.jje.order.domain.order.entity.Order;
import com.jje.order.domain.order.entity.OrderStatus;
import com.jje.order.domain.order.event.OrderEventPublisher;
import com.jje.order.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @Test
    @DisplayName("주문을 정상적으로 생성하고 이벤트를 발행한다")
    void createOrder() {
        // given
        Order order = Order.builder()
                .orderNumber("test-order-001")
                .userId(1L)
                .totalAmount(BigDecimal.valueOf(30000))
                .build();

        given(orderRepository.save(any(Order.class))).willReturn(order);

        OrderCreateRequest request = createOrderRequest(1L, BigDecimal.ZERO);

        // when
        OrderResponse response = orderService.create(request);

        // then
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getUserId()).isEqualTo(1L);
        then(eventPublisher).should().publishOrderCreated(any());
    }

    @Test
    @DisplayName("존재하지 않는 주문 조회 시 예외가 발생한다")
    void getOrderNotFound() {
        given(orderRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("PENDING 상태의 주문을 취소하고 이벤트를 발행한다")
    void cancelOrder() {
        // given
        Order order = Order.builder()
                .orderNumber("test-order-002")
                .userId(1L)
                .totalAmount(BigDecimal.valueOf(20000))
                .usedPoints(BigDecimal.valueOf(1000))
                .build();

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // when
        OrderResponse response = orderService.cancel(1L);

        // then
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        then(eventPublisher).should().publishOrderCancelled(any());
    }

    @Test
    @DisplayName("COMPLETED 상태의 주문은 취소할 수 없다")
    void cannotCancelCompletedOrder() {
        // given
        Order order = Order.builder()
                .orderNumber("test-order-003")
                .userId(1L)
                .totalAmount(BigDecimal.valueOf(20000))
                .build();
        // PENDING → PAYMENT_REQUESTED → PAID → COMPLETED
        order.changeStatus(OrderStatus.PAYMENT_REQUESTED);
        order.changeStatus(OrderStatus.PAID);
        order.changeStatus(OrderStatus.COMPLETED);

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.cancel(1L))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    private OrderCreateRequest createOrderRequest(Long userId, BigDecimal usedPoints) {
        try {
            OrderItemRequest itemRequest = new OrderItemRequest();
            setField(itemRequest, "productId", 1L);
            setField(itemRequest, "productName", "테스트 상품");
            setField(itemRequest, "price", BigDecimal.valueOf(10000));
            setField(itemRequest, "quantity", 3);

            OrderCreateRequest request = new OrderCreateRequest();
            setField(request, "userId", userId);
            setField(request, "items", List.of(itemRequest));
            setField(request, "usedPoints", usedPoints);
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
