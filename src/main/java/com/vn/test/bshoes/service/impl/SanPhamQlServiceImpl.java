package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.SanPhamDto;
import com.vn.test.bshoes.service.SanPhamQlService;
import com.vn.test.bshoes.service.SanPhamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SanPhamQlServiceImpl implements SanPhamQlService {

    private final SanPhamService sanPhamService;

    public SanPhamQlServiceImpl(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamDto> findAllActive() {
        return sanPhamService.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamDto> findRecycle() {
        return sanPhamService.findRecycle();
    }

    @Override
    @Transactional(readOnly = true)
    public SanPhamDto findById(int id) {
        return sanPhamService.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamDto> search(String keyword) {
        return sanPhamService.search(keyword);
    }

    @Override
    @Transactional
    public SanPhamDto create(SanPhamDto dto) {
        return sanPhamService.create(dto);
    }

    @Override
    @Transactional
    public SanPhamDto update(SanPhamDto dto) {
        return sanPhamService.update(dto);
    }

    @Override
    @Transactional
    public SanPhamDto setDanhMuc(int idSanPham, Integer idLoai) {
        return sanPhamService.setDanhMuc(idSanPham, idLoai);
    }

    @Override
    @Transactional
    public void softDelete(String ma) {
        sanPhamService.softDelete(ma);
    }

    @Override
    @Transactional
    public void restore(String ma) {
        sanPhamService.restore(ma);
    }
}
