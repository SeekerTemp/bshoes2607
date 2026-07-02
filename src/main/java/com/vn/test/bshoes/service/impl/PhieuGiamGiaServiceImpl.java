package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.PhieuGiamGia;
import com.vn.test.bshoes.repository.PhieuGiamGiaRepository;
import com.vn.test.bshoes.service.PhieuGiamGiaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PhieuGiamGiaServiceImpl implements PhieuGiamGiaService {

    private final PhieuGiamGiaRepository repo;

    public PhieuGiamGiaServiceImpl(PhieuGiamGiaRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<PhieuGiamGia> findAllActive() {
        return repo.findAllActive();
    }

    @Override
    public PhieuGiamGia findByIdActive(int id) {
        return repo.findByIdActive(id);
    }

    @Override
    public List<PhieuGiamGia> search(String keyword) {
        return repo.searchByName("%" + keyword + "%");
    }

    @Override
    @Transactional
    public PhieuGiamGia create(PhieuGiamGia e) {
        // bug-fix: create() must return the persisted entity instead of null.
        return repo.save(e);
    }

    @Override
    @Transactional
    public void update(PhieuGiamGia e) {
        repo.updateByMa(
                e.getTen_phieu_giam(),
                e.getLoai_giam_gia(),
                e.getGia_tri_giam(),
                e.getDon_toi_thieu(),
                e.getGiam_toi_da(),
                e.getSo_luong(),
                e.getThoi_gian_bat_dau(),
                e.getThoi_gian_ket_thuc(),
                e.isTrang_thai(),
                e.getMa_phieu_giam()
        );
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.softDelete(id);
    }

    @Override
    public boolean existsMa(String ma) {
        return repo.existsMa(ma) > 0;
    }

    @Override
    public boolean existsTen(String ten) {
        return repo.existsTen(ten) > 0;
    }

    @Override
    @Transactional
    // NOTE: legacy uses a scheduled proc; a @Scheduled Java job could replace it later.
    public void capNhatTrangThai() {
        repo.capNhatTrangThai();
    }
}
