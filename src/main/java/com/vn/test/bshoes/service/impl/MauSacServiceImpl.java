package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.MauSac;
import com.vn.test.bshoes.repository.MauSacRepository;
import com.vn.test.bshoes.service.MauSacService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MauSacServiceImpl implements MauSacService {

    private final MauSacRepository repo;

    public MauSacServiceImpl(MauSacRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<MauSac> findAll() {
        return repo.findAll();
    }

    @Override
    public MauSac findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public MauSac findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public MauSac create(MauSac e) {
        // No auto business code for this entity; code is supplied by the caller.
        return repo.save(e);
    }

    @Override
    @Transactional
    public void update(MauSac e) {
        repo.updateByMa(e.getTen_mau_sac(), e.getMa_mau_sac());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
