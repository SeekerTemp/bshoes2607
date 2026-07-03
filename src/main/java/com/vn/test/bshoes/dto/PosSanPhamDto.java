package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosSanPhamDto {
    private Integer id;
    private String ten;
    private String mau;
    private String size;
    private Integer ton;
    private BigDecimal gia;
}
