package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.dto.PhieuGiamGiaDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;

import java.math.BigDecimal;
import java.util.List;

public interface HoaDonService {
    List<HoaDonDto> findAll();
    HoaDonDto findById(int id);
    List<HoaDonDto> findCart();
    List<PosSanPhamDto> posProducts();
    List<PhieuGiamGiaDto> vouchersActive();
    BigDecimal tinhGiamGia(int idPhieu, BigDecimal tongTien);
    HoaDonDto create(HoaDonDto dto);
}
