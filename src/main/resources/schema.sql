-- 기존 테이블이 있으면 삭제 (테스트 시 매번 깨끗한 상태에서 시작하기 위함)
DROP TABLE IF EXISTS CUSTOMER;
DROP TABLE IF EXISTS PURCHASE_HISTORY;
DROP TABLE IF EXISTS VIP_CUSTOMER_REPORT; -- 리포트 테이블도 만든다고 가정

-- 고객 정보 테이블
CREATE TABLE CUSTOMER (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    grade VARCHAR(255) DEFAULT 'BRONZE'
);

-- 월별 구매 이력 테이블
CREATE TABLE PURCHASE_HISTORY (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    price DECIMAL(19, 2),
    created_at TIMESTAMP
);

-- (참고) 만약 Writer를 DB로 한다면, 이런 결과 테이블이 있을 수 있음
CREATE TABLE VIP_CUSTOMER_REPORT (
    customer_id BIGINT,
    customer_name VARCHAR(255),
    total_amount DECIMAL(19, 2),
    new_grade VARCHAR(255)
);