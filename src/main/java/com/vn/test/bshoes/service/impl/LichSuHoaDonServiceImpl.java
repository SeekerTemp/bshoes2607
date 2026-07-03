package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.service.HoaDonService;
import com.vn.test.bshoes.service.LichSuHoaDonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LichSuHoaDonServiceImpl implements LichSuHoaDonService {

    private final HoaDonService hoaDonService;

    public LichSuHoaDonServiceImpl(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    @Override
    public List<HoaDonDto> findAll() {
        return hoaDonService.findAll();
    }

    @Override
    public HoaDonDto findById(int id) {
        return hoaDonService.findById(id);
    }
}
