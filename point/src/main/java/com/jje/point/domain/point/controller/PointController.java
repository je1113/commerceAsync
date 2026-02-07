package com.jje.point.domain.point.controller;

import com.jje.point.domain.point.dto.PointEarnRequest;
import com.jje.point.domain.point.dto.PointHistoryResponse;
import com.jje.point.domain.point.dto.PointResponse;
import com.jje.point.domain.point.dto.PointUseRequest;
import com.jje.point.domain.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/earn")
    public ResponseEntity<PointResponse> earn(@Valid @RequestBody PointEarnRequest request) {
        PointResponse response = pointService.earn(
                request.getUserId(), request.getAmount(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/use")
    public ResponseEntity<PointResponse> use(@Valid @RequestBody PointUseRequest request) {
        PointResponse response = pointService.use(
                request.getUserId(), request.getAmount(), request.getOrderId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PointResponse> getBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(pointService.getBalance(userId));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<PointHistoryResponse>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(pointService.getHistory(userId));
    }
}
