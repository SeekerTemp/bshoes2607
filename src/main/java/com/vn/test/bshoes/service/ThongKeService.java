package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.ThongKeDoanhThuDto;
import com.vn.test.bshoes.dto.ThongKeSanPhamDto;

import java.sql.Date;
import java.util.List;

public interface ThongKeService {
    List<ThongKeDoanhThuDto> homNay();
    List<ThongKeDoanhThuDto> theoNgay(Date from, Date to);
    List<ThongKeDoanhThuDto> theoThang(int thang, int nam);
    List<ThongKeDoanhThuDto> theoNam(int nam);
    List<ThongKeSanPhamDto> tatCaSanPham();
    List<Integer> loatNam();
}
