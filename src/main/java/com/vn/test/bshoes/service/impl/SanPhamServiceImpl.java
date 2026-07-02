package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.SanPham;
import com.vn.test.bshoes.repository.SanPhamRepository;
import com.vn.test.bshoes.service.SanPhamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository repo;

    public SanPhamServiceImpl(SanPhamRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<SanPham> findAll() {
        return repo.findAll();
    }

    @Override
    public SanPham findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<SanPham> findByMa(String ma) {
        return repo.findByMa("%" + ma + "%");
    }

    @Override
    @Transactional
    public SanPham create(SanPham e) {
        return repo.save(e);
    }

    @Override
    @Transactional
    public SanPham update(SanPham e) {
        return repo.save(e);
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
