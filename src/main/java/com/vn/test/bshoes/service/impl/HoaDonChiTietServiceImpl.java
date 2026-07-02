package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.HoaDonChiTiet;
import com.vn.test.bshoes.repository.HoaDonChiTietRepository;
import com.vn.test.bshoes.service.HoaDonChiTietService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HoaDonChiTietServiceImpl implements HoaDonChiTietService {

    private final HoaDonChiTietRepository repo;

    public HoaDonChiTietServiceImpl(HoaDonChiTietRepository repo) {
        this.repo = repo;
    }

    @Override
    public HoaDonChiTiet findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<HoaDonChiTiet> findByHoaDon(int idHoaDon) {
        return repo.findByHoaDon(idHoaDon);
    }

    @Override
    public List<HoaDonChiTiet> findDetailByHoaDon(int idHoaDon) {
        return repo.findDetailByHoaDon(idHoaDon);
    }

    @Override
    public HoaDonChiTiet findByHoaDonAndSpct(int idHoaDon, int idSanPhamChiTiet) {
        return repo.findByHoaDonAndSpct(idHoaDon, idSanPhamChiTiet);
    }

    @Override
    @Transactional
    public void create(int idSanPhamChiTiet, int idHoaDon, int soLuong, String nguoiTao, String nguoiCapNhat, BigDecimal thanhTien) {
        repo.create(idSanPhamChiTiet, idHoaDon, soLuong, nguoiTao, nguoiCapNhat, thanhTien);
    }

    @Override
    @Transactional
    public void createViaProc(int idSanPhamChiTiet, int soLuong, BigDecimal giaGiam, String nguoiTao) {
        repo.createViaProc(idSanPhamChiTiet, soLuong, giaGiam, nguoiTao);
    }

    @Override
    @Transactional
    public void update(HoaDonChiTiet e) {
        repo.update(e.getSo_luong(), e.getThanh_tien(), e.getId_hoa_don_chi_tiet());
    }

    @Override
    @Transactional
    public void updateStock(int soLuong, int id) {
        repo.updateStock(soLuong, id);
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
