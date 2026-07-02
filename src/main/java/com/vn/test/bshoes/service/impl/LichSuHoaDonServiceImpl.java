package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.LichSuHoaDon;
import com.vn.test.bshoes.repository.LichSuHoaDonRepository;
import com.vn.test.bshoes.service.LichSuHoaDonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LichSuHoaDonServiceImpl implements LichSuHoaDonService {

    private final LichSuHoaDonRepository repo;

    public LichSuHoaDonServiceImpl(LichSuHoaDonRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<LichSuHoaDon> findAllActive() {
        return repo.findAllActive();
    }

    @Override
    public List<LichSuHoaDon> findAllCancel() {
        return repo.findAllCancel();
    }

    @Override
    public List<LichSuHoaDon> findByDate(String tuNgay, String denNgay) {
        return repo.findByDate("%" + tuNgay + "%", "%" + denNgay + "%");
    }

    @Override
    public LichSuHoaDon findById(int id) {
        return repo.findById(id).orElse(null);
    }
}
