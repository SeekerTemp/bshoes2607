package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.NhanVien;

import java.util.List;

public interface NhanVienService {
    List<NhanVien> findAllActive();
    NhanVien findByIdActive(int id);
    List<NhanVien> search(String ten, String gioiTinh);
    NhanVien create(NhanVien e);
    void update(NhanVien e);
    void delete(int id);
    boolean existsMa(String ma);
    boolean existsTaiKhoan(String taiKhoan);
    boolean existsCCCD(String cccd);
    boolean existsEmail(String email);
    boolean existsSdt(String sdt);
}
