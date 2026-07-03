package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.BienTheDto;
import com.vn.test.bshoes.service.SanPhamChiTietQlService;
import com.vn.test.bshoes.service.SanPhamChiTietService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SanPhamChiTietQlServiceImpl implements SanPhamChiTietQlService {

    private final SanPhamChiTietService sanPhamChiTietService;

    public SanPhamChiTietQlServiceImpl(SanPhamChiTietService sanPhamChiTietService) {
        this.sanPhamChiTietService = sanPhamChiTietService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BienTheDto> findActive() {
        return sanPhamChiTietService.findActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BienTheDto> findByProduct(int idSanPham) {
        return sanPhamChiTietService.findByProduct(idSanPham);
    }

    @Override
    @Transactional(readOnly = true)
    public BienTheDto findById(int id) {
        return sanPhamChiTietService.findById(id);
    }

    @Override
    @Transactional
    public BienTheDto create(int idSanPham, BienTheDto dto) {
        return sanPhamChiTietService.create(idSanPham, dto);
    }

    @Override
    @Transactional
    public void softDelete(int id) {
        sanPhamChiTietService.softDelete(id);
    }
}
