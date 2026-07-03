package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDoanhThuDto {
    private Integer thang;
    private Integer soSanPhamBan;
    private BigDecimal tongGiaBan;
    private BigDecimal tongGiamGia;
    private BigDecimal tongDoanhThu;
    private Integer soDon;
    private Integer donThanhCong;
    private Integer donHuy;
}
