package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienDto {
    private Integer id;
    private String ma;
    private String ten;
    private String taiKhoan;
    private String email;
    private String sdt;
    private String cccd;
    private String chucVu;
    private String gioiTinh;
    private String vaiTro;
    private Boolean trangThai;
    private Integer idVaiTro;
    private String matKhau;
}
