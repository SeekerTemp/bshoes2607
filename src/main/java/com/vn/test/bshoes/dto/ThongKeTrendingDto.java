package com.vn.test.bshoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A trending product: units sold this period + growth % vs the prior period. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeTrendingDto {
    private String ten;
    private Integer soLuong;
    private Integer growth;   // % growth vs prior year
}
