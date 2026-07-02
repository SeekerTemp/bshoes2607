package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.DiaChi;
import com.vn.test.bshoes.repository.DiaChiRepository;
import com.vn.test.bshoes.service.DiaChiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// NOTE: pure CRUD delegation — DiaChi has no getters/setters, so no accessor is called here.
@Service
public class DiaChiServiceImpl implements DiaChiService {

    private final DiaChiRepository repo;

    public DiaChiServiceImpl(DiaChiRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<DiaChi> findAll() {
        return repo.findAll();
    }

    @Override
    public DiaChi findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<DiaChi> findByKhachHang(int idKhachHang) {
        return repo.findByKhachHang(idKhachHang);
    }

    @Override
    @Transactional
    public DiaChi save(DiaChi e) {
        return repo.save(e);
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
