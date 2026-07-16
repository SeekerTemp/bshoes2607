package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiDto {
    private Integer id;
    private Integer idKhachHang;
    private String diaChiMacDinh;   // địa chỉ chính (số nhà, đường)
    private String thanhPho;
    private String phuong;
    private String diaChiThem;
    private Boolean trangThai;
}
