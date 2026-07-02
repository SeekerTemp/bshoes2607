package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.HoaDon;
import com.vn.test.bshoes.entity.PhieuGiamGia;

import java.math.BigDecimal;
import java.util.List;

public interface HoaDonService {
    List<HoaDon> findAll();
    HoaDon findById(int id);
    HoaDon findByMa(String ma);
    List<HoaDon> findAllCart();
    void create(int trangThai, int loaiHoaDon, int idNhanVien);
    void update(HoaDon e);
    void markPaid(int id, String nguoiCapNhat);
    void updateKhachHang(int idHoaDon, int idKhachHang);
    BigDecimal tinhGiamGia(int idPhieu, BigDecimal tongTien);
    List<PhieuGiamGia> getPhieuGiamGiaHoatDong();
    void deleteById(int id);
}
