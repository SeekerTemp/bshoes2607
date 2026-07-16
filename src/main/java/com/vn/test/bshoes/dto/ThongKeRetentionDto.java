package com.vn.test.bshoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Customer retention: returning vs new + retention rate %. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeRetentionDto {
    private Integer quayLai;   // returning customers
    private Integer moi;       // new customers
    private BigDecimal tyLe;   // retention % = quayLai / (quayLai + moi) * 100
}
