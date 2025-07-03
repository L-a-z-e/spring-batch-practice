package com.laze.springbatchpractice.ico.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VipCustomer {
    private Long customerId;
    private String customerName;
    private BigDecimal totalAmount;
    private String newGrade;
}
