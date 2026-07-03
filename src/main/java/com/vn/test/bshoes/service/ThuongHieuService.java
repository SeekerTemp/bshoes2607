package com.vn.test.bshoes.service;
import com.vn.test.bshoes.dto.AttributeMoTaDto;
import java.util.List;
public interface ThuongHieuService {
    List<AttributeMoTaDto> findAll();
    AttributeMoTaDto findById(int id);
    AttributeMoTaDto create(AttributeMoTaDto dto);
    AttributeMoTaDto update(AttributeMoTaDto dto);
    void delete(int id);
}
