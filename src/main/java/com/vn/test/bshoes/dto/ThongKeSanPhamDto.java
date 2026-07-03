package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeSanPhamDto {
    private String maSP;
    private String tenSP;
    private String loaiSP;
    private String chatLieu;
    private String mauSac;
    private String kichThuoc;
    private Integer soLuongTon;
}
