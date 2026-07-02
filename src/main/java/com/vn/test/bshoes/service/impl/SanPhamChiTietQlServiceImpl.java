package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.SanPhamChiTiet_ql;
import com.vn.test.bshoes.repository.SanPhamChiTietQlRepository;
import com.vn.test.bshoes.service.SanPhamChiTietQlService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SanPhamChiTietQlServiceImpl implements SanPhamChiTietQlService {

    private final SanPhamChiTietQlRepository repo;

    public SanPhamChiTietQlServiceImpl(SanPhamChiTietQlRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<SanPhamChiTiet_ql> findAllActive() {
        return repo.findAllActive();
    }

    @Override
    public SanPhamChiTiet_ql findByIdJoined(int id) {
        return repo.findByIdJoined(id);
    }

    @Override
    public List<SanPhamChiTiet_ql> findByProduct(int idSanPham) {
        return repo.findByProduct(idSanPham);
    }

    @Override
    public List<SanPhamChiTiet_ql> search(String keyword) {
        String like = "%" + keyword + "%";
        return repo.search(like, like);
    }

    @Override
    @Transactional
    public SanPhamChiTiet_ql create(SanPhamChiTiet_ql e) {
        // Mirrors the legacy SCOPE_IDENTITY() create: auto-generate "SPCT" + parent product code
        // when the caller did not supply one.
        if (!StringUtils.hasText(e.getMa_san_pham_chi_tiet())) {
            String parentCode = repo.findParentCode(e.getId_san_pham());
            e.setMa_san_pham_chi_tiet("SPCT" + parentCode);
        }
        return repo.save(e);
    }

    @Override
    @Transactional
    public void update(SanPhamChiTiet_ql e) {
        repo.update(
                e.getMa_san_pham_chi_tiet(),
                e.getId_mau_sac(),
                e.getId_kich_co(),
                e.getDon_gia(),
                e.getSo_luong_ton(),
                e.isTrang_thai(),
                e.getId_san_pham_chi_tiet()
        );
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.softDeleteById(id);
    }
}
