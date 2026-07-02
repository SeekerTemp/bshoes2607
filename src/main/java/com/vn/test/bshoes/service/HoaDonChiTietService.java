package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.HoaDonChiTiet;

import java.math.BigDecimal;
import java.util.List;

public interface HoaDonChiTietService {
    HoaDonChiTiet findById(int id);
    List<HoaDonChiTiet> findByHoaDon(int idHoaDon);
    List<HoaDonChiTiet> findDetailByHoaDon(int idHoaDon);
    HoaDonChiTiet findByHoaDonAndSpct(int idHoaDon, int idSanPhamChiTiet);
    void create(int idSanPhamChiTiet, int idHoaDon, int soLuong, String nguoiTao, String nguoiCapNhat, BigDecimal thanhTien);
    void createViaProc(int idSanPhamChiTiet, int soLuong, BigDecimal giaGiam, String nguoiTao);
    void update(HoaDonChiTiet e);
    void updateStock(int soLuong, int id);
    void delete(int id);
}
