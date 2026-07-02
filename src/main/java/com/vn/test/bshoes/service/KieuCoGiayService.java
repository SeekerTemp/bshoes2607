package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.KieuCoGiay;
import java.util.List;

public interface KieuCoGiayService {
    List<KieuCoGiay> findAll();
    KieuCoGiay findById(int id);
    KieuCoGiay findByTen(String ten);
    KieuCoGiay create(KieuCoGiay e);
    void update(KieuCoGiay e);
    void delete(int id);
}
