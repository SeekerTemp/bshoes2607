package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.HoaDonDto;

import java.util.List;

public interface LichSuHoaDonService {
    List<HoaDonDto> findAll();
    HoaDonDto findById(int id);
}
