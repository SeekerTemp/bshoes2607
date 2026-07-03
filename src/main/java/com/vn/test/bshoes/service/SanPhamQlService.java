package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.SanPhamDto;

import java.util.List;

public interface SanPhamQlService {
    List<SanPhamDto> findAllActive();
    List<SanPhamDto> findRecycle();
    SanPhamDto findById(int id);
    List<SanPhamDto> search(String keyword);
    SanPhamDto create(SanPhamDto dto);
    SanPhamDto update(SanPhamDto dto);
    void softDelete(String ma);
    void restore(String ma);
}
