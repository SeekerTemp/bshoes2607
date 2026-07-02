package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.SanPhamChiTietRepository;
import com.vn.test.bshoes.service.SanPhamChiTietService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SanPhamChiTietServiceImpl implements SanPhamChiTietService {

    private final SanPhamChiTietRepository repo;

    public SanPhamChiTietServiceImpl(SanPhamChiTietRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<SanPhamChiTiet> findAll() {
        return repo.findAll();
    }

    @Override
    public SanPhamChiTiet findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<SanPhamChiTiet> findAllAvailable() {
        return repo.findAllAvailable();
    }

    @Override
    public SanPhamChiTiet findByMa(String ma) {
        return repo.findByMa(ma);
    }

    @Override
    public List<SanPhamChiTiet> findAvailableByName(String keyword) {
        String like = "%" + keyword + "%";
        return repo.findAvailableByName(like, like);
    }

    @Override
    @Transactional
    public SanPhamChiTiet create(SanPhamChiTiet e) {
        return repo.save(e);
    }

    @Override
    @Transactional
    public SanPhamChiTiet update(SanPhamChiTiet e) {
        return repo.save(e);
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStock(int soLuongTon, int idSanPhamChiTiet) {
        repo.updateStock(soLuongTon, idSanPhamChiTiet);
        if (soLuongTon == 0) {
            repo.deactivateWhenEmpty(idSanPhamChiTiet);
        }
    }
}
