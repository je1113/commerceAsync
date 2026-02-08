# Commerce

비동기 메시징(Kafka)을 학습할 수 있는 커머스 백엔드 프로젝트.
6개의 독립된 마이크로서비스가 Kafka를 통해 이벤트 기반으로 통신한다.

## 기술 스택

- Java 21
- Spring Boot 4.0.2
- Spring Kafka
- Spring Data JPA
- H2 Database
- Gradle 9.3.0
- Docker (Kafka 인프라)

## 서비스 구성

| 서비스 | 포트 | 설명 |
|--------|------|------|
| Product | 8081 | 상품/카테고리 관리 |
| Order | 8082 | 주문 생성/취소 |
| Payment | 8083 | 결제/환불 처리 |
| User | 8084 | 회원 관리 |
| Point | 8085 | 포인트 적립/사용/복원 |
| Inventory | 8086 | 재고 관리/차감/복구 |

## Kafka 이벤트 흐름

### 주문 생성 (Happy Path)
```
Order ──(order-created)──> Payment ──(payment-approved)──> Inventory
```

### 주문 취소 (Cancel Path)
```
Order ──(order-cancelled)──> Payment    (환불)
                           > Inventory  (재고 복구)
                           > Point      (포인트 복원)
```

### Topics

| Topic | Producer | Consumer | 설명 |
|-------|----------|----------|------|
| `order-created` | Order | Payment | 주문 생성 -> 결제 요청 |
| `order-cancelled` | Order | Payment, Inventory, Point | 주문 취소 -> 보상 트랜잭션 |
| `payment-approved` | Payment | Inventory | 결제 승인 -> 재고 차감 |
| `payment-failed` | Payment | - | 결제 실패 알림 |
| `stock-deducted` | Inventory | - | 재고 차감 완료 |
| `stock-restored` | Inventory | - | 재고 복구 완료 |
| `point-restored` | Point | - | 포인트 복원 완료 |

### Consumer Groups

| 서비스 | Group ID | 구독 Topics |
|--------|----------|-------------|
| Payment | `payment-consumer` | order-created, order-cancelled |
| Inventory | `inventory-consumer` | payment-approved, order-cancelled |
| Point | `point-consumer` | order-cancelled |

## 실행 방법

### 1. Kafka 인프라 실행
```bash
docker compose up -d
```
- Kafka: `localhost:9092`
- Kafka UI: `http://localhost:8080`

### 2. 서비스 실행
각 서비스를 별도 터미널에서 실행한다.
```bash
cd order && ./gradlew bootRun
cd payment && ./gradlew bootRun
cd inventory && ./gradlew bootRun
cd point && ./gradlew bootRun
```

Kafka를 사용하지 않는 서비스는 독립 실행 가능하다.
```bash
cd product && ./gradlew bootRun
cd user && ./gradlew bootRun
```

## API

### Product (8081)
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/products` | 상품 등록 |
| GET | `/api/products/{id}` | 상품 조회 |
| GET | `/api/products` | 상품 목록 |
| PUT | `/api/products/{id}` | 상품 수정 |
| DELETE | `/api/products/{id}` | 상품 삭제 |
| POST | `/api/categories` | 카테고리 등록 |
| GET | `/api/categories` | 카테고리 목록 |
| GET | `/api/categories/{id}` | 카테고리 조회 |
| PUT | `/api/categories/{id}` | 카테고리 수정 |
| DELETE | `/api/categories/{id}` | 카테고리 삭제 |

### Order (8082)
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/orders` | 주문 생성 |
| GET | `/api/orders/{id}` | 주문 조회 |
| GET | `/api/orders` | 주문 목록 |
| POST | `/api/orders/{id}/cancel` | 주문 취소 |

### Payment (8083)
| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/payments/{id}` | 결제 조회 |
| GET | `/api/payments/order/{orderId}` | 주문별 결제 조회 |
| POST | `/api/payments/order/{orderId}/refund` | 환불 처리 |

### User (8084)
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/users` | 회원 가입 |
| GET | `/api/users/{id}` | 회원 조회 |
| PUT | `/api/users/{id}` | 회원 수정 |
| DELETE | `/api/users/{id}` | 회원 탈퇴 |

### Point (8085)
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/points/earn` | 포인트 적립 |
| POST | `/api/points/use` | 포인트 사용 |
| GET | `/api/points/{userId}` | 포인트 잔액 조회 |
| GET | `/api/points/{userId}/history` | 포인트 이력 조회 |

### Inventory (8086)
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/inventories` | 재고 등록 |
| GET | `/api/inventories/product/{productId}` | 재고 조회 |
| PUT | `/api/inventories/product/{productId}` | 재고 수정 |
| POST | `/api/inventories/product/{productId}/deduct` | 재고 차감 |
| POST | `/api/inventories/product/{productId}/restore` | 재고 복구 |

## 프로젝트 구조

```
commerce/
├── docker-compose.yml          # Kafka 인프라
├── product/                    # 상품 서비스
├── order/                      # 주문 서비스 (Producer)
├── payment/                    # 결제 서비스 (Producer + Consumer)
├── user/                       # 회원 서비스
├── point/                      # 포인트 서비스 (Consumer)
└── inventory/                  # 재고 서비스 (Producer + Consumer)
```

각 서비스 내부 패키지 구조:
```
com.jje.{service}/
├── domain/{domain-name}/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   └── event/                  # Kafka Producer/Consumer
└── common/
    ├── entity/                 # BaseEntity
    ├── exception/              # GlobalExceptionHandler
    └── config/                 # JPA, Kafka 설정
```
