package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.KichCo;
import java.util.List;

public interface KichCoService {
    List<KichCo> findAll();
    KichCo findById(int id);
    KichCo findByTen(String ten);
    KichCo create(KichCo e);
    void update(KichCo e);
    void delete(int id);
}
