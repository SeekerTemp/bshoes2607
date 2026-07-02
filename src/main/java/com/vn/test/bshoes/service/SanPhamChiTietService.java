package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.SanPhamChiTiet;

import java.util.List;

public interface SanPhamChiTietService {
    List<SanPhamChiTiet> findAll();
    SanPhamChiTiet findById(int id);
    List<SanPhamChiTiet> findAllAvailable();
    SanPhamChiTiet findByMa(String ma);
    List<SanPhamChiTiet> findAvailableByName(String keyword);
    SanPhamChiTiet create(SanPhamChiTiet e);
    SanPhamChiTiet update(SanPhamChiTiet e);
    void delete(int id);
    void updateStock(int soLuongTon, int idSanPhamChiTiet);
}
