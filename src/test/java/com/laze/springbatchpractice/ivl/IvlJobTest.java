package com.laze.springbatchpractice.ivl;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IvlJobTest {

    private final JobLauncherTestUtils jobLauncherTestUtils;
    private final JobRepositoryTestUtils jobRepositoryTestUtils;
    private final JdbcTemplate jdbcTemplate;

    // 테스트 실행 전, 테스트용 데이터를 DB에 준비
    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("CREATE TABLE investment_products (" +
                "id BIGINT PRIMARY KEY, " +
                "product_code VARCHAR(255), " +
                "base_value DECIMAL(19, 4), " +
                "evaluated_value DECIMAL(19, 4), " +
                "target_date VARCHAR(10))");

        jdbcTemplate.update("INSERT INTO investment_products VALUES (1, 'A001', 1000.0, NULL, '2025-07-03')");
        jdbcTemplate.update("INSERT INTO investment_products VALUES (2, 'B002', 2000.0, NULL, '2025-07-03')");
    }

    // 테스트 실행 후, 배치 메타데이터 및 테스트 테이블 정리
    @AfterEach
    void tearDown() {
        jobRepositoryTestUtils.removeJobExecutions();
        jdbcTemplate.execute("DROP TABLE investment_products");
    }

    @Test
    void ivlJob_성공_테스트() throws Exception {
        // given: Job 파라미터 준비
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("targetDate", "2025-07-03")
                .toJobParameters();

        // when: Job 실행
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then: Job 실행 결과 검증
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // then: DB 데이터 처리 결과 검증
        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT evaluated_value FROM investment_products WHERE id = 1");
        // 1000.0 * 1.1 = 1100.0
        assertThat(result.get("evaluated_value")).isEqualTo(new BigDecimal("1100.0000"));
    }

}
