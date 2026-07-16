package com.vn.test.bshoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** ROI of a product: revenue, cost, and ROI% = (revenue - cost) / cost * 100. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeRoiDto {
    private String ten;
    private BigDecimal doanhThu;
    private BigDecimal giaVon;
    private BigDecimal roi;
}
