package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDto {
    private Integer id;
    private String ma;
    private String ten;
    private String gioiTinh;
    private String sdt;
    private String email;
    private String diaChi;
    private Boolean trangThai;
}
