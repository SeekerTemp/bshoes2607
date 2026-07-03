package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.PhieuGiamGiaDto;

import java.util.List;

public interface PhieuGiamGiaService {
    List<PhieuGiamGiaDto> findAll();
    PhieuGiamGiaDto findById(int id);
    List<PhieuGiamGiaDto> search(String kw);
    PhieuGiamGiaDto create(PhieuGiamGiaDto dto);
    PhieuGiamGiaDto update(PhieuGiamGiaDto dto);
    void delete(int id);
}
