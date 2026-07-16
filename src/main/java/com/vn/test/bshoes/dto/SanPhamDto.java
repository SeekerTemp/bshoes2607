package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDto {
    private Integer id;
    private String ma;
    private String ten;
    private Integer idLoaiSanPham;   // category id
    private String loaiSP;           // category name
    private String thuongHieu;       // brand (hãng giày)
    private String kieuDang;         // style (kiểu dáng)
    private String chatLieu;
    private BigDecimal gia;
    private String moTa;
    private String imageUrl;
    private List<BienTheDto> bienThe;
}
