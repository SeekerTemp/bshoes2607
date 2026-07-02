package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.KieuCoGiay;
import com.vn.test.bshoes.repository.KieuCoGiayRepository;
import com.vn.test.bshoes.service.KieuCoGiayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KieuCoGiayServiceImpl implements KieuCoGiayService {

    private final KieuCoGiayRepository repo;

    public KieuCoGiayServiceImpl(KieuCoGiayRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<KieuCoGiay> findAll() {
        return repo.findAll();
    }

    @Override
    public KieuCoGiay findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public KieuCoGiay findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public KieuCoGiay create(KieuCoGiay e) {
        // Legacy pattern: insert, then set business code "KC" + generated id.
        KieuCoGiay saved = repo.save(e);
        String ma = "KC" + saved.getId_kieu_co_giay();
        saved.setMa_co_giay(ma);
        repo.updateMa(ma, saved.getId_kieu_co_giay());
        return saved;
    }

    @Override
    @Transactional
    public void update(KieuCoGiay e) {
        repo.updateByMa(e.getTen_co_giay(), e.getMa_co_giay());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
