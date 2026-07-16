package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.AddItemRequest;
import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.dto.PhieuGiamGiaDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;
import com.vn.test.bshoes.dto.ThanhToanRequest;

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

    /** Create a new empty pending invoice (trang_thai=0). Max 3 pending at a time. */
    HoaDonDto createEmpty(Integer idNhanVien);

    /** Add a variant to the cart (INSERT line + decrement stock + recompute totals). */
    HoaDonDto addItem(int idHoaDon, AddItemRequest req);

    /** Set a line's quantity (adjusts stock by the delta); qty &lt;= 0 removes the line. */
    HoaDonDto updateItemQuantity(int idChiTiet, int soLuong);

    /** Remove a line and return its units to stock. */
    HoaDonDto removeItem(int idChiTiet);

    /** Finalize payment: status=1, loai_hoa_don=true, totals+discount, write history. */
    HoaDonDto thanhToan(int idHoaDon, ThanhToanRequest req);

    /** Cancel a pending invoice: restore stock for all lines, status=2, write history. */
    HoaDonDto huy(int idHoaDon, Integer idNhanVien);

    /** Mark a delivery order (status "Chờ giao") as delivered ("Đã giao"). */
    HoaDonDto daGiao(int idHoaDon);

    /** Return goods: restore stock, status="Trả hàng", write history. */
    HoaDonDto traHang(int idHoaDon, Integer idNhanVien);
}
