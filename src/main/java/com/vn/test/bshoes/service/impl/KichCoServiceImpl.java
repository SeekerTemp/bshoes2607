package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.KichCo;
import com.vn.test.bshoes.repository.KichCoRepository;
import com.vn.test.bshoes.service.KichCoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KichCoServiceImpl implements KichCoService {

    private final KichCoRepository repo;

    public KichCoServiceImpl(KichCoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<KichCo> findAll() {
        return repo.findAll();
    }

    @Override
    public KichCo findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public KichCo findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public KichCo create(KichCo e) {
        // No auto business code for this entity; code is supplied by the caller.
        return repo.save(e);
    }

    @Override
    @Transactional
    public void update(KichCo e) {
        repo.updateByMa(e.getTen_kich_co(), e.getMa_kich_co());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
