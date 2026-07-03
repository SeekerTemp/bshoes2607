package com.vn.test.bshoes.service;
import com.vn.test.bshoes.dto.AttributeDto;
import java.util.List;
public interface VaiTroService {
    List<AttributeDto> findAll();
    AttributeDto findById(int id);
}
