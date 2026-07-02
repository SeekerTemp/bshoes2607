package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.PhieuGiamGia;
import java.util.List;

public interface PhieuGiamGiaService {
    List<PhieuGiamGia> findAllActive();
    PhieuGiamGia findByIdActive(int id);
    List<PhieuGiamGia> search(String keyword);
    PhieuGiamGia create(PhieuGiamGia e);
    void update(PhieuGiamGia e);
    void delete(int id);
    boolean existsMa(String ma);
    boolean existsTen(String ten);
    void capNhatTrangThai();
}
