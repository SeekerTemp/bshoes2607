package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.VaiTro;

import java.util.List;

public interface VaiTroService {
    List<VaiTro> findAll();
    VaiTro findById(int id);
}
