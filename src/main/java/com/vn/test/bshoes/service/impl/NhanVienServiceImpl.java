package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.service.NhanVienService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository repo;

    public NhanVienServiceImpl(NhanVienRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<NhanVien> findAllActive() {
        return repo.findAllActive();
    }

    @Override
    public NhanVien findByIdActive(int id) {
        return repo.findByIdActive(id);
    }

    @Override
    public List<NhanVien> search(String ten, String gioiTinh) {
        String gt = StringUtils.hasText(gioiTinh) ? gioiTinh : "all";
        return repo.searchByName("%" + ten + "%", gt);
    }

    @Override
    @Transactional
    public NhanVien create(NhanVien e) {
        // bug-fix: legacy create() returned null; return the persisted entity instead.
        return repo.save(e);
    }

    @Override
    @Transactional
    public void update(NhanVien e) {
        repo.updateByMa(
                e.getTen_nhan_vien(),
                e.getCccd(),
                e.getEmail(),
                e.getSo_dien_thoai(),
                e.getGioi_tinh(),
                e.getDia_chi(),
                e.getNgay_sinh(),
                e.getTai_khoan(),
                e.getMat_khau(),
                e.getMa_nhan_vien()
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
    public boolean existsTaiKhoan(String taiKhoan) {
        return repo.existsTaiKhoan(taiKhoan) > 0;
    }

    @Override
    public boolean existsCCCD(String cccd) {
        return repo.existsCCCD(cccd) > 0;
    }

    @Override
    public boolean existsEmail(String email) {
        return repo.existsEmail(email) > 0;
    }

    @Override
    public boolean existsSdt(String sdt) {
        return repo.existsSdt(sdt) > 0;
    }
}
