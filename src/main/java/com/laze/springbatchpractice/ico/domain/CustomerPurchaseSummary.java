package com.laze.springbatchpractice.ico.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CustomerPurchaseSummary {
    private Long customerId;
    private String customerName;
    private BigDecimal totalAmount;
}
