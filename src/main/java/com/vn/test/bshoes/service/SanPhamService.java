package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.SanPhamDto;

import java.util.List;

public interface SanPhamService {
    List<SanPhamDto> findAllActive();
    List<SanPhamDto> findRecycle();
    SanPhamDto findById(int id);
    List<SanPhamDto> search(String keyword);
    SanPhamDto create(SanPhamDto dto);
    SanPhamDto update(SanPhamDto dto);
    /** Assign a product to a category (idLoai=null removes it from any category). */
    SanPhamDto setDanhMuc(int idSanPham, Integer idLoai);
    void softDelete(String ma);
    void restore(String ma);
}
