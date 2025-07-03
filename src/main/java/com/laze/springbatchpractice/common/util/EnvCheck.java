package com.laze.springbatchpractice.common.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvCheck {

    private final Environment env;

    @PostConstruct
    public void printEnv() {
        System.out.println("🔍 DB_URL = " + env.getProperty("DB_URL"));
        System.out.println("🔍 spring.datasource.url = " + env.getProperty("spring.datasource.url"));
    }
}