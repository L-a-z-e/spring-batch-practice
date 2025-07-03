package com.laze.springbatchpractice.ivl;

import com.laze.springbatchpractice.ivl.domain.Ivl0001;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Ivl0001Config {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SqlSessionFactory sqlSessionFactory;

    @Bean
    public Job ivlJob(
            Step ivlStep, JobRepository jobRepository, PlatformTransactionManager transactionManager
    ) {
        return new JobBuilder("ivlJob", jobRepository) // Job 이름: ivlJob
                .start(ivlStep)
                .build();
    }

    @Bean
    public Step ivlStepItemReader(ItemReader<Ivl0001> reader, ItemProcessor<Ivl0001, Ivl0001> processor, ItemWriter<Ivl0001> writer) {
        return new StepBuilder("ivlStep", jobRepository)
                .<Ivl0001, Ivl0001>chunk(100, transactionManager) // 100개씩 처리
                .reader(reader) // 파라미터는 Spring이 자동으로 주입
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Ivl0001> ivlReader(
            @Value("#{jobParameters['targetDate']}") String targetDate) {
        log.info("Reading ivl data for targetDate: {}", targetDate);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetDate", targetDate);

        return new MyBatisPagingItemReaderBuilder<Ivl0001>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.laze.springbatchpractice.ivl.mapper.IvlMapper.selectIvlData")
                .parameterValues(parameters)
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<Ivl0001, Ivl0001> ivlProcessor() {
        return item -> {
            log.info("Before evaluatedValue : {} ", item.getEvaluatedValue());
            BigDecimal evaluated = item.getBaseValue().multiply(new BigDecimal("1.1"));
            item.setEvaluatedValue(evaluated);
            log.info("After evaluatedValue : {} ", item.getEvaluatedValue());
            return item;
        };
    }

    @Bean
    public MyBatisBatchItemWriter<Ivl0001> ivlWriter() {
        MyBatisBatchItemWriter<Ivl0001> writer = new MyBatisBatchItemWriter<>();
        writer.setSqlSessionFactory(sqlSessionFactory);
        writer.setStatementId("com.laze.springbatchpractice.ivl.mapper.IvlMapper.updateIvlData");
        return writer;
    }
}
