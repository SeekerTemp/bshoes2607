package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.DiaChiDto;

import java.util.List;

public interface DiaChiService {
    List<DiaChiDto> findAll();
    DiaChiDto findById(int id);
    List<DiaChiDto> findByKhachHang(int idKhachHang);
    DiaChiDto create(DiaChiDto dto);
    void delete(int id);
}
