package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.BaoHanhDto;

import java.util.List;

public interface BaoHanhService {
    List<BaoHanhDto> findAll();
    BaoHanhDto findById(int id);
    BaoHanhDto create(BaoHanhDto dto);
    BaoHanhDto updateTrangThai(int id, String trangThai);
    void softDelete(int id);
}
