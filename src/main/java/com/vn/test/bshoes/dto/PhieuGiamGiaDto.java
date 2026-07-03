package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuGiamGiaDto {
    private Integer id;
    private String ma;
    private String ten;
    private Integer loai;
    private BigDecimal giaTri;
    private BigDecimal donToiThieu;
    private BigDecimal giamToiDa;
    private Integer soLuong;
    private String batDau;
    private String ketThuc;
    private Boolean trangThai;
}
