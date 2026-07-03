package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDto {
    private Integer id;
    private String ma;
    private String khach;
    private String nhanVien;
    private String ngayTao;
    private BigDecimal tongTien;
    private String trangThai;
    private List<HoaDonChiTietDto> chiTiet;
}
