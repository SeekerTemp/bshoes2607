package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.MauSac;
import java.util.List;

public interface MauSacService {
    List<MauSac> findAll();
    MauSac findById(int id);
    MauSac findByTen(String ten);
    MauSac create(MauSac e);
    void update(MauSac e);
    void delete(int id);
}
