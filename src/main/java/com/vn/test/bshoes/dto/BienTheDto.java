package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BienTheDto {
    private Integer id;
    private String ma;
    private String mau;
    private String size;
    private Integer ton;
    private BigDecimal gia;
    private String imageUrl;
    private Boolean trangThai;
}
