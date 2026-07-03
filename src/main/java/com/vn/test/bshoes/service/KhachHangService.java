package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.KhachHangDto;

import java.util.List;

public interface KhachHangService {
    List<KhachHangDto> findAll();
    KhachHangDto findById(int id);
    List<KhachHangDto> search(String keyword);
    KhachHangDto create(KhachHangDto dto);
    KhachHangDto update(KhachHangDto dto);
    void delete(int id);
}
