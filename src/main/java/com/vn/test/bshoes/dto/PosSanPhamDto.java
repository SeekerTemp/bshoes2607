package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosSanPhamDto {
    private Integer id;              // id_san_pham_chi_tiet (biến thể)
    private Integer idSanPham;       // sản phẩm cha — storefront cần để mở trang chi tiết
    private String ten;
    private String mau;
    private String size;
    private Integer ton;
    private BigDecimal gia;
    private String imageUrl;
    private Integer idLoaiSanPham;   // danh mục sản phẩm — storefront cần để lọc theo danh mục
    private String loaiSP;           // tên danh mục hiển thị
}
