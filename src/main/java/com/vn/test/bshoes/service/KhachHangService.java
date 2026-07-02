package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.KhachHang;

import java.util.List;

public interface KhachHangService {
    List<KhachHang> findAllActive();
    KhachHang findByIdActive(int id);
    List<KhachHang> search(String keyword);
    KhachHang findBySdt(String sdt);
    KhachHang create(KhachHang e);
    void update(KhachHang e);
    void delete(int id);
    boolean existsMa(String ma);
    boolean existsEmail(String email);
    boolean existsSdt(String sdt);
    boolean existsMaExcludingId(String ma, int id);
}
