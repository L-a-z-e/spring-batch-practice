package com.laze.springbatchpractice.config;

import com.laze.springbatchpractice.domain.CustomerPurchaseSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class VipCustomerJobConfig {
    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcCursorItemReader<CustomerPurchaseSummary> vipCustomerReader(
            @Value("#{jobParameters['yearMonth']}") String yearMonth
    ) {
        String sql = "SELECT C.id as customerId, C.name as customerName, SUM(P.price) as totalAmount " +
                "FROM CUSTOMER C " +
                "INNER JOIN PURCHASE_HISTORY P ON C.id = P.customer_id " +
                "WHERE FORMATDATETIME(P.created_at, 'yyyy-MM') = ? " + // H2 DB의 함수 사용
                "GROUP BY C.id, C.name " +
                "HAVING SUM(P.price) > 0 " + // 구매금액이 있는 고객만 대상
                "ORDER BY C.id";

        return new JdbcCursorItemReaderBuilder<CustomerPurchaseSummary>()
                .name("vipCustomerReader")
                .dataSource(this.dataSource)
                .sql(sql)
                .preparedStatementSetter(pstmt -> pstmt.setString(1, yearMonth))
                .rowMapper(new BeanPropertyRowMapper<>(CustomerPurchaseSummary.class))
                .build();
    }
}
