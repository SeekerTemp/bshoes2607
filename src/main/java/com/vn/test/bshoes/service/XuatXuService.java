package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.XuatXu;
import java.util.List;

public interface XuatXuService {
    List<XuatXu> findAll();
    XuatXu findById(int id);
    XuatXu findByTen(String ten);
    XuatXu create(XuatXu e);
    void update(XuatXu e);
    void delete(int id);
}
