package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.KieuDang;
import com.vn.test.bshoes.repository.KieuDangRepository;
import com.vn.test.bshoes.service.KieuDangService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KieuDangServiceImpl implements KieuDangService {

    private final KieuDangRepository repo;

    public KieuDangServiceImpl(KieuDangRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<KieuDang> findAll() {
        return repo.findAll();
    }

    @Override
    public KieuDang findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public KieuDang findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public KieuDang create(KieuDang e) {
        // Legacy pattern: insert, then set business code "KD" + generated id.
        KieuDang saved = repo.save(e);
        String ma = "KD" + saved.getId_kieu_dang();
        saved.setMa_kieu_dang(ma);
        repo.updateMa(ma, saved.getId_kieu_dang());
        return saved;
    }

    @Override
    @Transactional
    public void update(KieuDang e) {
        repo.updateByMa(e.getTen_kieu_dang(), e.getMa_kieu_dang());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
