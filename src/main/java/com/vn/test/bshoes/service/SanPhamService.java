package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.SanPham;

import java.util.List;

public interface SanPhamService {
    List<SanPham> findAll();
    SanPham findById(int id);
    List<SanPham> findByMa(String ma);
    SanPham create(SanPham e);
    SanPham update(SanPham e);
    void delete(int id);
}
