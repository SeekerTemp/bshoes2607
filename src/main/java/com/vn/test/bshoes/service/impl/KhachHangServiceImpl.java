package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.KhachHang;
import com.vn.test.bshoes.repository.KhachHangRepository;
import com.vn.test.bshoes.service.KhachHangService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository repo;

    public KhachHangServiceImpl(KhachHangRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<KhachHang> findAllActive() {
        return repo.findAllActive();
    }

    @Override
    public KhachHang findByIdActive(int id) {
        return repo.findByIdActive(id);
    }

    @Override
    public List<KhachHang> search(String keyword) {
        return repo.searchByName("%" + keyword + "%");
    }

    @Override
    public KhachHang findBySdt(String sdt) {
        return repo.findBySdt(sdt);
    }

    @Override
    @Transactional
    public KhachHang create(KhachHang e) {
        // bug-fix: legacy create() returned null; return the persisted entity instead.
        return repo.save(e);
    }

    @Override
    @Transactional
    public void update(KhachHang e) {
        repo.updateByMa(
                e.getTen_khach_hang(),
                e.getGioi_tinh(),
                e.getSo_dien_thoai(),
                e.getDia_chi(),
                e.getEmail(),
                e.isTrang_thai(),
                e.getMa_khach_hang()
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
    public boolean existsEmail(String email) {
        return repo.existsEmail(email) > 0;
    }

    @Override
    public boolean existsSdt(String sdt) {
        return repo.existsSdt(sdt) > 0;
    }

    @Override
    public boolean existsMaExcludingId(String ma, int id) {
        return repo.existsMaExcludingId(ma, id) > 0;
    }
}
