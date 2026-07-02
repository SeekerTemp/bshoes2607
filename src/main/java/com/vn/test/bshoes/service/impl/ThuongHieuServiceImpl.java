package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.ThuongHieu;
import com.vn.test.bshoes.repository.ThuongHieuRepository;
import com.vn.test.bshoes.service.ThuongHieuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThuongHieuServiceImpl implements ThuongHieuService {

    private final ThuongHieuRepository repo;

    public ThuongHieuServiceImpl(ThuongHieuRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ThuongHieu> findAll() {
        return repo.findAll();
    }

    @Override
    public ThuongHieu findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public ThuongHieu findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public ThuongHieu create(ThuongHieu e) {
        // Legacy pattern: insert, then set business code "TH" + generated id.
        ThuongHieu saved = repo.save(e);
        String ma = "TH" + saved.getId_thuong_hieu();
        saved.setMa_thuong_hieu(ma);
        repo.updateMa(ma, saved.getId_thuong_hieu());
        return saved;
    }

    @Override
    @Transactional
    public void update(ThuongHieu e) {
        repo.updateByMa(e.getTen_thuong_hieu(), e.getMa_thuong_hieu());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
