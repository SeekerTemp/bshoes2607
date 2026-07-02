package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.HoaDon;
import com.vn.test.bshoes.entity.PhieuGiamGia;
import com.vn.test.bshoes.repository.HoaDonRepository;
import com.vn.test.bshoes.service.HoaDonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository repo;

    public HoaDonServiceImpl(HoaDonRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<HoaDon> findAll() {
        return repo.findAll();
    }

    @Override
    public HoaDon findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public HoaDon findByMa(String ma) {
        return repo.findByMa(ma);
    }

    @Override
    public List<HoaDon> findAllCart() {
        return repo.findAllCart();
    }

    @Override
    @Transactional
    public void create(int trangThai, int loaiHoaDon, int idNhanVien) {
        repo.create(trangThai, loaiHoaDon, idNhanVien);
    }

    @Override
    @Transactional
    public void update(HoaDon e) {
        repo.update(
                e.getTong_tien_ban_dau(),
                e.getTien_giam_gia(),
                e.getTong_tien_phai_tra(),
                e.getTrang_thai(),
                e.isLoai_hoa_don(),
                e.getNguoi_cap_nhat(),
                e.getId_hoa_don()
        );
    }

    @Override
    @Transactional
    public void markPaid(int id, String nguoiCapNhat) {
        repo.markPaid(id, nguoiCapNhat);
    }

    @Override
    @Transactional
    public void updateKhachHang(int idHoaDon, int idKhachHang) {
        repo.updateKhachHang(idHoaDon, idKhachHang);
    }

    @Override
    public BigDecimal tinhGiamGia(int idPhieu, BigDecimal tongTien) {
        return repo.tinhGiamGia(idPhieu, tongTien);
    }

    @Override
    public List<PhieuGiamGia> getPhieuGiamGiaHoatDong() {
        return repo.getPhieuGiamGiaHoatDong();
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        repo.deleteById(id);
    }
}
