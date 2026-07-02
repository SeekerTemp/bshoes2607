package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.LichSuHoaDon;
import java.util.List;

public interface LichSuHoaDonService {
    List<LichSuHoaDon> findAllActive();
    List<LichSuHoaDon> findAllCancel();
    List<LichSuHoaDon> findByDate(String tuNgay, String denNgay);
    LichSuHoaDon findById(int id);
}
