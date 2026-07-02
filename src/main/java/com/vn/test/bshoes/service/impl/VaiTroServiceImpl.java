package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.VaiTro;
import com.vn.test.bshoes.repository.VaiTroRepository;
import com.vn.test.bshoes.service.VaiTroService;
import org.springframework.stereotype.Service;

import java.util.List;

// NOTE: pure CRUD delegation — VaiTro has no getters/setters, so no accessor is called here.
@Service
public class VaiTroServiceImpl implements VaiTroService {

    private final VaiTroRepository repo;

    public VaiTroServiceImpl(VaiTroRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<VaiTro> findAll() {
        return repo.findAll();
    }

    @Override
    public VaiTro findById(int id) {
        return repo.findById(id).orElse(null);
    }
}
