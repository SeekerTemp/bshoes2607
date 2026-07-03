package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.BienTheDto;

import java.util.List;

public interface SanPhamChiTietService {
    List<BienTheDto> findActive();
    List<BienTheDto> findByProduct(int idSanPham);
    List<BienTheDto> findAvailable();
    BienTheDto findById(int id);
    BienTheDto create(int idSanPham, BienTheDto dto);
    void softDelete(int id);
}
