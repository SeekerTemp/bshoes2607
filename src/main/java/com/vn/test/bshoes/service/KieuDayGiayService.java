package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.KieuDayGiay;
import java.util.List;

public interface KieuDayGiayService {
    List<KieuDayGiay> findAll();
    KieuDayGiay findById(int id);
    KieuDayGiay findByTen(String ten);
    KieuDayGiay create(KieuDayGiay e);
    void update(KieuDayGiay e);
    void delete(int id);
}
