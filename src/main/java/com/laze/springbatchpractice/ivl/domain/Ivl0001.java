package com.laze.springbatchpractice.ivl.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Ivl0001 {
    private Long id;
    private String productCode;
    private BigDecimal baseValue;
    private BigDecimal evaluatedValue; // 평가 후 계산될 가치
    private String targetDate;
}
