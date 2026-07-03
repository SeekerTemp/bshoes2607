package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.BienTheDto;

import java.util.List;

public interface SanPhamChiTietQlService {
    List<BienTheDto> findActive();
    List<BienTheDto> findByProduct(int idSanPham);
    BienTheDto findById(int id);
    BienTheDto create(int idSanPham, BienTheDto dto);
    void softDelete(int id);
}
