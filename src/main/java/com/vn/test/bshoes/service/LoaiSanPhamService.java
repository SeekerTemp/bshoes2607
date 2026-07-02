package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.LoaiSanPham;
import java.util.List;

public interface LoaiSanPhamService {
    List<LoaiSanPham> findAll();
    LoaiSanPham findById(int id);
    LoaiSanPham findByTen(String ten);
    LoaiSanPham create(LoaiSanPham e);
    void update(LoaiSanPham e);
    void delete(int id);
}
