package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.HoaDonChiTietDto;

import java.util.List;

public interface HoaDonChiTietService {
    List<HoaDonChiTietDto> findByHoaDon(int idHoaDon);
    HoaDonChiTietDto findById(int id);
}
