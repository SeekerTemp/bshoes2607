package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.SanPham_ql;

import java.util.List;

public interface SanPhamQlService {
    List<SanPham_ql> findAllActive();
    List<SanPham_ql> findRecycle();
    SanPham_ql findByIdJoined(int id);
    List<SanPham_ql> search(String keyword);
    SanPham_ql create(SanPham_ql e);
    void update(SanPham_ql e);
    void softDelete(String maSp);
    void restore(String maSp);
}
