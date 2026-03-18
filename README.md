# product-wording-generator

이커머스 상품명을 분석하여 검색용 정규식 패턴(wording)을 자동 생성하고 DB에 저장/관리하는 어드민 웹 툴입니다.

- 상품명을 브랜드명, 상품 키워드, 용량/수량으로 파싱하여 정규식 패턴 자동 생성
- 생성된 패턴을 검토 후 DB에 저장 (개별 저장 / 페이지 전체 일괄 저장)
- 기저장된 패턴 목록 조회 및 수정

## Stack

- Java 8
- Spring Boot 2.1.4
- MyBatis
- MariaDB
- JSP

## 페이지 구성

| URL | JSP | 설명 |
|---|---|---|
| `/` | `index.jsp` | 메인 - 페이지 이동 |
| `/goods-wording-insert` | `goodsWordingInsert.jsp` | 미처리 상품(`STATUS=0`) 목록 조회 → 정규식 패턴 자동생성 후 신규 등록 |
| `/goods-wording-update` | `goodsWordingUpdate.jsp` | 기처리 상품(`STATUS=1`) 목록 조회 → 기존 패턴 수정 |

## word1 / word2

`MASTER_GOODS_WORDING` 테이블의 핵심 컬럼으로, 상품명을 분리한 검색 패턴입니다.

| 필드 | 설명 | 예시 |
|---|---|---|
| `word1` | 브랜드명 + 상품명 조합 정규식 패턴 | `브랜드A.*상품명.*` |
| `word2` | 용량 또는 수량 숫자 패턴 | `100`, `2` |

상품명의 첫 단어를 브랜드명, 마지막 단어의 숫자 부분을 용량/수량으로 가정하여 파싱합니다.

## 실행 방법

`application.properties`에 DB 정보를 설정 후 실행합니다.

```properties
spring.datasource.url=jdbc:mariadb://YOUR_DB_HOST/YOUR_DB_NAME?serverTimezone=UTC
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
```

## DB 테이블 명세

### GOODS_LIST

원본 상품 목록 테이블입니다.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| ID | BIGINT | 상품 고유 ID (PK) |
| GOODS_NAME | VARCHAR | 원본 상품명 |
| STATUS | INT | 처리 상태 (0: 미처리, 1: 처리완료) |

### MASTER_GOODS

정제된 마스터 상품 테이블입니다.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| ID | BIGINT | 상품 고유 ID (PK, GOODS_LIST.ID 참조) |
| BRAND_ID | INT | 브랜드 ID |
| MASTER_CODE | VARCHAR | 마스터 상품 코드 |
| GOODS_NAME | VARCHAR | 정제된 상품명 |
| GOODS_TYPE | INT | 상품 유형 |
| REFINE_TYPE | INT | 정제 유형 |
| IMAGE_URL | VARCHAR | 상품 이미지 URL |
| REG_DT | DATETIME | 등록일시 |

### MASTER_GOODS_WORDING

상품별 정규식 패턴(wording) 저장 테이블입니다.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| ID | BIGINT | 고유 ID (PK) |
| MASTER_GOODS_ID | BIGINT | 마스터 상품 ID (MASTER_GOODS.ID 참조) |
| COUNTRY_ID | INT | 국가 ID |
| WORD1 | VARCHAR | 브랜드/상품명 정규식 패턴 |
| WORD2 | VARCHAR | 용량/수량 패턴 |
| REG_DT | DATETIME | 등록일시 |
| UPT_DT | DATETIME | 수정일시 |

## 테이블 관계

```
GOODS_LIST (1) ──── (1) MASTER_GOODS (1) ──── (1) MASTER_GOODS_WORDING
```

## DDL

```sql
CREATE TABLE GOODS_LIST (
    ID        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    GOODS_NAME VARCHAR(500) NOT NULL,
    STATUS    INT          NOT NULL DEFAULT 0 COMMENT '0: 미처리, 1: 처리완료'
);

CREATE TABLE MASTER_GOODS (
    ID          BIGINT       NOT NULL PRIMARY KEY,
    BRAND_ID    INT          NOT NULL DEFAULT 0,
    MASTER_CODE VARCHAR(500) NOT NULL,
    GOODS_NAME  VARCHAR(500) NOT NULL,
    GOODS_TYPE  INT          NOT NULL DEFAULT 1,
    REFINE_TYPE INT          NOT NULL DEFAULT 0,
    IMAGE_URL   VARCHAR(500),
    REG_DT      DATETIME     NOT NULL DEFAULT NOW()
);

CREATE TABLE MASTER_GOODS_WORDING (
    ID              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    MASTER_GOODS_ID BIGINT       NOT NULL,
    COUNTRY_ID      INT          NOT NULL DEFAULT 1,
    WORD1           VARCHAR(500) NOT NULL,
    WORD2           VARCHAR(100),
    REG_DT          DATETIME     NOT NULL DEFAULT NOW(),
    UPT_DT          DATETIME     NOT NULL DEFAULT NOW()
);
```

## 예시 데이터

```sql
-- 1. 원본 상품 목록 (미처리)
INSERT INTO GOODS_LIST (ID, GOODS_NAME, STATUS) VALUES
(1, '브랜드A 수분크림 50ml', 0),
(2, '브랜드A 토너 200ml', 0),
(3, '브랜드B 선크림 세트 2종', 0),
(4, '브랜드C 에센스 30ml', 0);

-- 2. 마스터 상품 (GOODS_LIST 기반으로 자동 생성)
INSERT INTO MASTER_GOODS (ID, BRAND_ID, MASTER_CODE, GOODS_NAME, GOODS_TYPE, REFINE_TYPE, IMAGE_URL, REG_DT) VALUES
(1, 0, '브랜드A 수분크림 50ml', '브랜드A 수분크림 50ml', 1, 0, '1.jpg', NOW()),
(2, 0, '브랜드A 토너 200ml',   '브랜드A 토너 200ml',   1, 0, '2.jpg', NOW()),
(3, 0, '브랜드B 선크림 세트 2종', '브랜드B 선크림 세트 2종', 1, 0, '3.jpg', NOW()),
(4, 0, '브랜드C 에센스 30ml',  '브랜드C 에센스 30ml',  1, 0, '4.jpg', NOW());

-- 3. 정제어 패턴 (정제 완료 후 저장)
INSERT INTO MASTER_GOODS_WORDING (MASTER_GOODS_ID, COUNTRY_ID, WORD1, WORD2, REG_DT, UPT_DT) VALUES
(1, 1, '브랜드A.*수분크림.*', '50',  NOW(), NOW()),
(2, 1, '브랜드A.*토너.*',    '200', NOW(), NOW()),
(3, 1, '브랜드B.*선크림.*',  '2',   NOW(), NOW()),
(4, 1, '브랜드C.*에센스.*',  '30',  NOW(), NOW());

-- STATUS 처리완료로 업데이트
UPDATE GOODS_LIST SET STATUS = 1 WHERE ID IN (1, 2, 3, 4);
```
