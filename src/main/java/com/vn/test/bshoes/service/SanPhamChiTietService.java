package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.BienTheDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;

import java.util.List;

public interface SanPhamChiTietService {
    List<BienTheDto> findActive();
    List<BienTheDto> findByProduct(int idSanPham);
    List<BienTheDto> findAvailable();
    BienTheDto findById(int id);
    BienTheDto create(int idSanPham, BienTheDto dto);
    BienTheDto update(int id, BienTheDto dto);
    /** Stock-in: add {@code soLuong} units to a variant's on-hand quantity. */
    BienTheDto nhapKho(int id, int soLuong);
    void softDelete(int id);

    /** Look up one variant by its business code (for QR/barcode scan at POS). Cart-friendly shape. */
    PosSanPhamDto findPosByMa(String ma);
}
