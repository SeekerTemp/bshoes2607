package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.XuatXu;
import com.vn.test.bshoes.repository.XuatXuRepository;
import com.vn.test.bshoes.service.XuatXuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class XuatXuServiceImpl implements XuatXuService {

    private final XuatXuRepository repo;

    public XuatXuServiceImpl(XuatXuRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<XuatXu> findAll() {
        return repo.findAll();
    }

    @Override
    public XuatXu findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public XuatXu findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public XuatXu create(XuatXu e) {
        // Legacy pattern: insert, then set business code "XX" + generated id.
        XuatXu saved = repo.save(e);
        String ma = "XX" + saved.getId_xuat_su();
        saved.setMa_xuat_su(ma);
        repo.updateMa(ma, saved.getId_xuat_su());
        return saved;
    }

    @Override
    @Transactional
    public void update(XuatXu e) {
        repo.updateByMa(e.getTen_xuat_su(), e.getMa_xuat_su());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
