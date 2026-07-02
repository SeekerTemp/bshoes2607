package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.ThongKeDoanhThu;
import com.vn.test.bshoes.entity.ThongKeSanPham;

import java.sql.Date;
import java.util.List;

public interface ThongKeService {
    List<ThongKeDoanhThu> homNay();
    List<ThongKeDoanhThu> theoNgay(Date from, Date to);
    List<ThongKeDoanhThu> theoThang(int thang, int nam);
    List<ThongKeDoanhThu> theoNam(int nam);
    List<ThongKeSanPham> tatCaSanPham();
    List<Integer> loatNam();
}
