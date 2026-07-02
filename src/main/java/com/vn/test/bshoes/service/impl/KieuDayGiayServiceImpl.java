package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.KieuDayGiay;
import com.vn.test.bshoes.repository.KieuDayGiayRepository;
import com.vn.test.bshoes.service.KieuDayGiayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KieuDayGiayServiceImpl implements KieuDayGiayService {

    private final KieuDayGiayRepository repo;

    public KieuDayGiayServiceImpl(KieuDayGiayRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<KieuDayGiay> findAll() {
        return repo.findAll();
    }

    @Override
    public KieuDayGiay findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public KieuDayGiay findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public KieuDayGiay create(KieuDayGiay e) {
        // Legacy pattern: insert, then set business code "DG" + generated id.
        KieuDayGiay saved = repo.save(e);
        String ma = "DG" + saved.getId_kieu_day_giay();
        saved.setMa_day_giay(ma);
        repo.updateMa(ma, saved.getId_kieu_day_giay());
        return saved;
    }

    @Override
    @Transactional
    public void update(KieuDayGiay e) {
        repo.updateByMa(e.getTen_day_giay(), e.getMa_day_giay());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
