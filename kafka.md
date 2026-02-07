# Kafka 도입

## Kafka란?
Apache Kafka는 분산 이벤트 스트리밍 플랫폼이다. 대용량의 실시간 데이터를 높은 처리량과 낮은 지연으로 전달할 수 있다.

### 핵심 개념
- **Producer**: 메시지를 Topic에 발행하는 주체
- **Consumer**: Topic에서 메시지를 구독하여 소비하는 주체
- **Topic**: 메시지가 저장되는 카테고리 (논리적 채널)
- **Partition**: Topic을 나눈 물리적 단위. 같은 Key의 메시지는 같은 Partition으로 간다
- **Consumer Group**: 같은 Group의 Consumer들이 Partition을 분담하여 병렬 처리
- **Offset**: Consumer가 어디까지 읽었는지 추적하는 위치값

### 왜 Kafka인가?
- **서비스 간 디커플링**: Producer와 Consumer가 서로를 모른다
- **비동기 처리**: 발행 후 즉시 반환, Consumer가 자기 속도로 처리
- **내구성**: 디스크에 메시지 저장, 장애 시 재처리 가능
- **확장성**: Partition 수 증가로 처리량 선형 확장

## 도입 배경
기존에는 Spring `ApplicationEventPublisher`로 이벤트를 발행했다. 그러나 각 서비스가 독립된 JVM에서 실행되므로, 같은 프로세스 내에서만 동작하는 ApplicationEvent로는 서비스 간 통신이 불가능했다. Kafka를 도입하여 진짜 서비스 간 비동기 메시징을 구현한다.

## 아키텍처

### 이벤트 흐름
```
[주문 생성 - Happy Path]
  Order ──(order-created)──→ Payment ──(payment-approved)──→ Inventory

[주문 취소 - Cancel Path]
  Order ──(order-cancelled)──→ Payment (환불)
                             → Inventory (재고 복구)
                             → Point (포인트 복원)
```

### Topics
| Topic | Producer | Consumer | 설명 |
|-------|----------|----------|------|
| `order-created` | Order | Payment | 주문 생성 → 결제 요청 |
| `order-cancelled` | Order | Payment, Inventory, Point | 주문 취소 → 보상 트랜잭션 |
| `payment-approved` | Payment | Inventory | 결제 승인 → 재고 차감 |
| `payment-failed` | Payment | (미래 확장) | 결제 실패 알림 |
| `point-restored` | Point | (미래 확장) | 포인트 복원 완료 알림 |

### Consumer Groups
| 서비스 | Group ID | 구독 Topics |
|--------|----------|-------------|
| Payment | `payment-consumer` | order-created, order-cancelled |
| Inventory | `inventory-consumer` | payment-approved, order-cancelled |
| Point | `point-consumer` | order-cancelled |

## 구현 방식

### 설계 원칙
- **EventPublisher 인터페이스 유지**: 비즈니스 로직 코드 변경 없음
- **Spring*EventPublisher 내부만 교체**: `ApplicationEventPublisher` → `KafkaTemplate`
- **@EventListener → @KafkaListener**: Consumer 쪽 어노테이션 교체
- **메시지 키**: `orderNumber` 사용하여 같은 주문의 이벤트가 같은 Partition으로 전달 (순서 보장)

### 직렬화
- Producer: `JsonSerializer` (객체 → JSON)
- Consumer: `JsonDeserializer` (JSON → 객체)
- `trusted-packages` 설정으로 역직렬화 허용 패키지 지정

### 실행 방법
```bash
# Kafka 인프라 실행
docker compose up -d

# Kafka UI 접속
# http://localhost:8080

# 각 서비스 실행 (별도 터미널)
cd order && ./gradlew bootRun --args='--spring.profiles.active=local'
cd payment && ./gradlew bootRun --args='--spring.profiles.active=local'
cd inventory && ./gradlew bootRun --args='--spring.profiles.active=local'
cd point && ./gradlew bootRun --args='--spring.profiles.active=local'
```

## 변경 이력
- 기존: Spring ApplicationEventPublisher (단일 JVM 내 이벤트)
- 변경: Apache Kafka (서비스 간 비동기 메시징)
