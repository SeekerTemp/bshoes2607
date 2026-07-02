package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.KieuDang;
import java.util.List;

public interface KieuDangService {
    List<KieuDang> findAll();
    KieuDang findById(int id);
    KieuDang findByTen(String ten);
    KieuDang create(KieuDang e);
    void update(KieuDang e);
    void delete(int id);
}
