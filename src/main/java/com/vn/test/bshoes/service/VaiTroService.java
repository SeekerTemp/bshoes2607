package com.vn.test.bshoes.service;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.dto.PhanQuyenDto;
import java.util.List;
public interface VaiTroService {
    List<AttributeDto> findAll();
    AttributeDto findById(int id);
    /** Roles with their permission set (for the phân quyền panel). */
    List<PhanQuyenDto> findAllQuyen();
    /** Save a role's allowed-screen CSV. */
    PhanQuyenDto updateQuyen(int id, String quyen);
}
