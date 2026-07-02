package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.SanPhamChiTiet_ql;

import java.util.List;

public interface SanPhamChiTietQlService {
    List<SanPhamChiTiet_ql> findAllActive();
    SanPhamChiTiet_ql findByIdJoined(int id);
    List<SanPhamChiTiet_ql> findByProduct(int idSanPham);
    List<SanPhamChiTiet_ql> search(String keyword);
    SanPhamChiTiet_ql create(SanPhamChiTiet_ql e);
    void update(SanPhamChiTiet_ql e);
    void delete(int id);
}
