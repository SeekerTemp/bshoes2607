package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.DiaChi;

import java.util.List;

public interface DiaChiService {
    List<DiaChi> findAll();
    DiaChi findById(int id);
    List<DiaChi> findByKhachHang(int idKhachHang);
    DiaChi save(DiaChi e);
    void delete(int id);
}
