package com.jje.point.domain.point.service;

import com.jje.point.common.exception.EntityNotFoundException;
import com.jje.point.domain.point.dto.PointHistoryResponse;
import com.jje.point.domain.point.dto.PointResponse;
import com.jje.point.domain.point.entity.Point;
import com.jje.point.domain.point.entity.PointHistory;
import com.jje.point.domain.point.entity.PointTransactionType;
import com.jje.point.domain.point.repository.PointHistoryRepository;
import com.jje.point.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 포인트 적립.
     */
    @Transactional
    public PointResponse earn(Long userId, BigDecimal amount, String description) {
        Point point = pointRepository.findByUserId(userId)
                .orElseGet(() -> pointRepository.save(
                        Point.builder().userId(userId).balance(BigDecimal.ZERO).build()));

        point.earn(amount);

        pointHistoryRepository.save(PointHistory.builder()
                .userId(userId)
                .amount(amount)
                .type(PointTransactionType.EARN)
                .description(description)
                .build());

        log.info("[EARN] 포인트 적립 - userId={}, amount={}, balance={}", userId, amount, point.getBalance());
        return PointResponse.from(point);
    }

    /**
     * 포인트 사용.
     */
    @Transactional
    public PointResponse use(Long userId, BigDecimal amount, Long orderId) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("포인트", userId));

        point.use(amount);

        pointHistoryRepository.save(PointHistory.builder()
                .userId(userId)
                .amount(amount)
                .type(PointTransactionType.USE)
                .description("포인트 사용")
                .orderId(orderId)
                .build());

        log.info("[USE] 포인트 사용 - userId={}, amount={}, balance={}", userId, amount, point.getBalance());
        return PointResponse.from(point);
    }

    /**
     * 포인트 복원 (주문 취소 시).
     */
    @Transactional
    public PointResponse restore(Long userId, BigDecimal amount, Long orderId) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("포인트", userId));

        point.restore(amount);

        pointHistoryRepository.save(PointHistory.builder()
                .userId(userId)
                .amount(amount)
                .type(PointTransactionType.RESTORE)
                .description("주문 취소 포인트 복원")
                .orderId(orderId)
                .build());

        log.info("[RESTORE] 포인트 복원 - userId={}, amount={}, balance={}", userId, amount, point.getBalance());
        return PointResponse.from(point);
    }

    /**
     * 잔액 조회.
     */
    public PointResponse getBalance(Long userId) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("포인트", userId));
        return PointResponse.from(point);
    }

    /**
     * 포인트 이력 조회.
     */
    public List<PointHistoryResponse> getHistory(Long userId) {
        return pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PointHistoryResponse::from)
                .toList();
    }
}
