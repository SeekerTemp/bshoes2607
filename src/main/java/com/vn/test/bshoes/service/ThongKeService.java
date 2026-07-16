package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.ThongKeDoanhThuDto;
import com.vn.test.bshoes.dto.ThongKeDongTienDto;
import com.vn.test.bshoes.dto.ThongKeRetentionDto;
import com.vn.test.bshoes.dto.ThongKeRoiDto;
import com.vn.test.bshoes.dto.ThongKeSanPhamDto;
import com.vn.test.bshoes.dto.ThongKeTongQuanDto;
import com.vn.test.bshoes.dto.ThongKeTrendingDto;

import java.sql.Date;
import java.util.List;

public interface ThongKeService {
    List<ThongKeDoanhThuDto> homNay();
    List<ThongKeDoanhThuDto> theoNgay(Date from, Date to);
    List<ThongKeDoanhThuDto> theoThang(int thang, int nam);
    List<ThongKeDoanhThuDto> theoNam(int nam);
    List<ThongKeSanPhamDto> tatCaSanPham();
    List<Integer> loatNam();
    ThongKeTongQuanDto tongQuan();
    List<ThongKeDongTienDto> dongTien(int nam);
    List<ThongKeRoiDto> roiSanPham();
    ThongKeRetentionDto retention();
    List<ThongKeTrendingDto> trending(int nam);
}
