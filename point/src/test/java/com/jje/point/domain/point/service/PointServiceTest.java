package com.jje.point.domain.point.service;

import com.jje.point.common.exception.InsufficientPointException;
import com.jje.point.domain.point.dto.PointResponse;
import com.jje.point.domain.point.entity.Point;
import com.jje.point.domain.point.entity.PointHistory;
import com.jje.point.domain.point.repository.PointHistoryRepository;
import com.jje.point.domain.point.repository.PointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("포인트를 적립한다")
    void earn() {
        // given
        Point point = Point.builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(1000))
                .build();

        given(pointRepository.findByUserId(1L)).willReturn(Optional.of(point));
        given(pointHistoryRepository.save(any(PointHistory.class))).willReturn(null);

        // when
        PointResponse response = pointService.earn(1L, BigDecimal.valueOf(500), "구매 적립");

        // then
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1500));
        then(pointHistoryRepository).should().save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트를 사용한다")
    void useSuccess() {
        // given
        Point point = Point.builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(5000))
                .build();

        given(pointRepository.findByUserId(1L)).willReturn(Optional.of(point));
        given(pointHistoryRepository.save(any(PointHistory.class))).willReturn(null);

        // when
        PointResponse response = pointService.use(1L, BigDecimal.valueOf(3000), 100L);

        // then
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(2000));
        then(pointHistoryRepository).should().save(any(PointHistory.class));
    }

    @Test
    @DisplayName("잔액 부족 시 포인트 사용에 실패한다")
    void useInsufficientBalance() {
        // given
        Point point = Point.builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(1000))
                .build();

        given(pointRepository.findByUserId(1L)).willReturn(Optional.of(point));

        // when & then
        assertThatThrownBy(() -> pointService.use(1L, BigDecimal.valueOf(5000), 100L))
                .isInstanceOf(InsufficientPointException.class);
    }

    @Test
    @DisplayName("포인트를 복원한다")
    void restore() {
        // given
        Point point = Point.builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(2000))
                .build();

        given(pointRepository.findByUserId(1L)).willReturn(Optional.of(point));
        given(pointHistoryRepository.save(any(PointHistory.class))).willReturn(null);

        // when
        PointResponse response = pointService.restore(1L, BigDecimal.valueOf(3000), 100L);

        // then
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        then(pointHistoryRepository).should().save(any(PointHistory.class));
    }
}
