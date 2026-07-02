package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.ThuongHieu;
import java.util.List;

public interface ThuongHieuService {
    List<ThuongHieu> findAll();
    ThuongHieu findById(int id);
    ThuongHieu findByTen(String ten);
    ThuongHieu create(ThuongHieu e);
    void update(ThuongHieu e);
    void delete(int id);
}
