package com.vn.test.bshoes.repository;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Read projection over {@code view_phieu_giam_gia_hoat_dong} (see sqlBshoes.sql
 * §3.1) — exactly the columns that view selects, no more.
 *
 * It exists because mapping that view onto the {@code PhieuGiamGia} ENTITY threw
 * on every call:
 *
 *   JpaSystemException: Unable to find column position by name: ngay_cap_nhat
 *
 * Hibernate hydrating a full entity from a native query demands every mapped
 * column, and the view deliberately omits the audit columns (nguoi_tao_ma,
 * nguoi_cap_nhat, ngay_tao_ma, ngay_cap_nhat, trang_thai_xoa). Result: GET
 * /api/hoa-don/vouchers-active returned 500, the POS voucher list stayed empty
 * and no khuyến mãi could be applied at the till.
 *
 * A projection only binds the columns declared here, so the view and the entity
 * no longer have to agree column-for-column.
 */
public interface VoucherActiveView {
    Integer getId_phieu_giam_gia();
    String getMa_phieu_giam();
    String getTen_phieu_giam();
    Integer getLoai_giam_gia();
    BigDecimal getGia_tri_giam();
    BigDecimal getDon_toi_thieu();
    BigDecimal getGiam_toi_da();
    Integer getSo_luong();
    Instant getThoi_gian_bat_dau();
    Instant getThoi_gian_ket_thuc();
    Boolean getTrang_thai();
}
