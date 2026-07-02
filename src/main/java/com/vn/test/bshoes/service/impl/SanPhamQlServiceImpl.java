package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.SanPham_ql;
import com.vn.test.bshoes.repository.SanPhamChiTietQlRepository;
import com.vn.test.bshoes.repository.SanPhamQlRepository;
import com.vn.test.bshoes.service.SanPhamQlService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SanPhamQlServiceImpl implements SanPhamQlService {

    private final SanPhamQlRepository sanPhamQlRepo;
    private final SanPhamChiTietQlRepository sanPhamChiTietQlRepo;

    public SanPhamQlServiceImpl(SanPhamQlRepository sanPhamQlRepo, SanPhamChiTietQlRepository sanPhamChiTietQlRepo) {
        this.sanPhamQlRepo = sanPhamQlRepo;
        this.sanPhamChiTietQlRepo = sanPhamChiTietQlRepo;
    }

    @Override
    public List<SanPham_ql> findAllActive() {
        return sanPhamQlRepo.findAllActive();
    }

    @Override
    public List<SanPham_ql> findRecycle() {
        return sanPhamQlRepo.findRecycle();
    }

    @Override
    public SanPham_ql findByIdJoined(int id) {
        return sanPhamQlRepo.findByIdJoined(id);
    }

    @Override
    public List<SanPham_ql> search(String keyword) {
        String like = "%" + keyword + "%";
        return sanPhamQlRepo.search(like, like);
    }

    @Override
    @Transactional
    public SanPham_ql create(SanPham_ql e) {
        // Legacy pattern: insert via identity, mirrors insertAndReturnId.
        return sanPhamQlRepo.save(e);
    }

    @Override
    @Transactional
    public void update(SanPham_ql e) {
        sanPhamQlRepo.updateByMa(
                e.getTen_san_pham(),
                e.getId_chat_lieu(),
                e.getId_kieu_dang(),
                e.getId_kieu_co_giay(),
                e.getId_kieu_day_giay(),
                e.getId_thuong_hieu(),
                e.getId_xuat_su(),
                e.getMa_san_pham()
        );
    }

    @Override
    @Transactional
    public void softDelete(String maSp) {
        // Mirrors legacy cascade DaoImpl_SanPham_ql.softDelete -> spctDao.softDeleteBySanPham.
        sanPhamQlRepo.softDelete(maSp);
        Integer idSp = sanPhamQlRepo.findIdByMa(maSp);
        if (idSp != null) {
            sanPhamChiTietQlRepo.softDeleteByProduct(idSp);
        }
    }

    @Override
    @Transactional
    public void restore(String maSp) {
        sanPhamQlRepo.restore(maSp);
    }
}
