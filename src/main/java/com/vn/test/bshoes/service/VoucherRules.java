package com.vn.test.bshoes.service;

import java.time.Instant;

/**
 * Server-side validity rules for a phiếu giảm giá, kept pure so they can be
 * unit-tested without a database.
 *
 * These mirror the WHERE clause of {@code view_phieu_giam_gia_hoat_dong}, which
 * is what the POS uses to LIST vouchers. Until now nothing re-checked them when
 * a voucher was actually APPLIED: HoaDonServiceImpl.thanhToan() looked the id up
 * with findById() and discounted with it, so an expired, disabled, soft-deleted
 * or fully-used voucher still worked if its id was posted directly.
 */
public final class VoucherRules {

    private VoucherRules() {
    }

    /**
     * @return null when the voucher may be used, otherwise a message explaining
     *         why it may not (safe to show to the cashier).
     */
    public static String validate(Boolean trangThai, Boolean trangThaiXoa,
                                  Instant batDau, Instant ketThuc,
                                  Integer soLuong, Instant now) {
        if (Boolean.TRUE.equals(trangThaiXoa)) {
            return "Phiếu giảm giá đã bị xoá.";
        }
        if (!Boolean.TRUE.equals(trangThai)) {
            return "Phiếu giảm giá đang bị tắt.";
        }
        if (batDau != null && now.isBefore(batDau)) {
            return "Phiếu giảm giá chưa đến ngày áp dụng.";
        }
        if (ketThuc != null && now.isAfter(ketThuc)) {
            return "Phiếu giảm giá đã hết hạn.";
        }
        if (soLuong != null && soLuong <= 0) {
            return "Phiếu giảm giá đã hết lượt sử dụng.";
        }
        return null;
    }
}
