package com.vn.test.bshoes.service;
import com.vn.test.bshoes.dto.AttributeDto;
import java.util.List;
public interface KieuCoGiayService {
    List<AttributeDto> findAll();
    AttributeDto findById(int id);
    AttributeDto create(AttributeDto dto);
    AttributeDto update(AttributeDto dto);
    void delete(int id);
}
