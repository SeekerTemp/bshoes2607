package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.LoaiSanPham;
import com.vn.test.bshoes.repository.LoaiSanPhamRepository;
import com.vn.test.bshoes.service.LoaiSanPhamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoaiSanPhamServiceImpl implements LoaiSanPhamService {

    private final LoaiSanPhamRepository repo;

    public LoaiSanPhamServiceImpl(LoaiSanPhamRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<LoaiSanPham> findAll() {
        return repo.findAll();
    }

    @Override
    public LoaiSanPham findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public LoaiSanPham findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public LoaiSanPham create(LoaiSanPham e) {
        // Legacy pattern: insert, then set business code "LSP" + generated id.
        LoaiSanPham saved = repo.save(e);
        String ma = "LSP" + saved.getId_loai_san_pham();
        saved.setMa_loai_san_pham(ma);
        repo.updateMa(ma, saved.getId_loai_san_pham());
        return saved;
    }

    @Override
    @Transactional
    public void update(LoaiSanPham e) {
        repo.updateByMa(e.getTen_loai_san_pham(), e.getMo_ta(), e.getMa_loai_san_pham());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
