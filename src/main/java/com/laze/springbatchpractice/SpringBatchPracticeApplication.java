package com.laze.springbatchpractice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.laze.springbatchpractice.ivl.mapper")
public class SpringBatchPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchPracticeApplication.class, args);
    }

}
