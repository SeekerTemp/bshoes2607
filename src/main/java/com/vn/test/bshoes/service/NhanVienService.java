package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.NhanVienDto;

import java.util.List;

public interface NhanVienService {
    List<NhanVienDto> findAll();
    NhanVienDto findById(int id);
    List<NhanVienDto> search(String ten, String gioiTinh);
    NhanVienDto create(NhanVienDto dto);
    NhanVienDto update(NhanVienDto dto);
    void delete(int id);
}
