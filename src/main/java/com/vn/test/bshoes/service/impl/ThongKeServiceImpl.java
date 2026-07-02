package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.ThongKeDoanhThu;
import com.vn.test.bshoes.entity.ThongKeSanPham;
import com.vn.test.bshoes.repository.ThongKeRepository;
import com.vn.test.bshoes.service.ThongKeService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    private final ThongKeRepository repo;

    public ThongKeServiceImpl(ThongKeRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ThongKeDoanhThu> homNay() {
        return repo.homNay();
    }

    @Override
    public List<ThongKeDoanhThu> theoNgay(Date from, Date to) {
        return repo.theoNgay(from, to);
    }

    @Override
    public List<ThongKeDoanhThu> theoThang(int thang, int nam) {
        return repo.theoThang(thang, nam);
    }

    @Override
    public List<ThongKeDoanhThu> theoNam(int nam) {
        return repo.theoNam(nam);
    }

    @Override
    public List<ThongKeSanPham> tatCaSanPham() {
        return repo.tatCaSanPham();
    }

    @Override
    public List<Integer> loatNam() {
        return repo.loatNam();
    }
}
