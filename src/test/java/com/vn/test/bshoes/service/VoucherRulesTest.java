package com.vn.test.bshoes.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Guards the apply-time voucher check. Without it, POSTing a raw
 * idPhieuGiamGia discounted an invoice with an expired / disabled / deleted /
 * exhausted voucher, because only the LIST endpoint filtered on those columns.
 */
class VoucherRulesTest {

    private static final Instant NOW = Instant.parse("2026-08-04T10:00:00Z");
    private static final Instant YESTERDAY = NOW.minus(1, ChronoUnit.DAYS);
    private static final Instant TOMORROW = NOW.plus(1, ChronoUnit.DAYS);

    @Test
    void acceptsAVoucherThatIsOnActiveInDateAndHasUsesLeft() {
        assertNull(VoucherRules.validate(true, false, YESTERDAY, TOMORROW, 10, NOW));
    }

    @Test
    void rejectsASoftDeletedVoucher() {
        assertEquals("Phiếu giảm giá đã bị xoá.",
                VoucherRules.validate(true, true, YESTERDAY, TOMORROW, 10, NOW));
    }

    @Test
    void rejectsADisabledVoucher() {
        assertEquals("Phiếu giảm giá đang bị tắt.",
                VoucherRules.validate(false, false, YESTERDAY, TOMORROW, 10, NOW));
    }

    @Test
    void rejectsAVoucherWithNoTrangThaiSet() {
        assertEquals("Phiếu giảm giá đang bị tắt.",
                VoucherRules.validate(null, false, YESTERDAY, TOMORROW, 10, NOW));
    }

    @Test
    void rejectsAVoucherThatHasNotStartedYet() {
        assertEquals("Phiếu giảm giá chưa đến ngày áp dụng.",
                VoucherRules.validate(true, false, TOMORROW, TOMORROW.plus(5, ChronoUnit.DAYS), 10, NOW));
    }

    @Test
    void rejectsAnExpiredVoucher() {
        assertEquals("Phiếu giảm giá đã hết hạn.",
                VoucherRules.validate(true, false, YESTERDAY.minus(5, ChronoUnit.DAYS), YESTERDAY, 10, NOW));
    }

    @Test
    void rejectsAVoucherWithNoUsesLeft() {
        assertEquals("Phiếu giảm giá đã hết lượt sử dụng.",
                VoucherRules.validate(true, false, YESTERDAY, TOMORROW, 0, NOW));
    }

    @Test
    void acceptsAVoucherWithNoUsageLimitRecorded() {
        assertNull(VoucherRules.validate(true, false, YESTERDAY, TOMORROW, null, NOW));
    }

    @Test
    void acceptsAVoucherWithOpenEndedDates() {
        assertNull(VoucherRules.validate(true, false, null, null, 5, NOW));
    }

    @Test
    void acceptsExactlyOnTheFirstAndLastInstant() {
        assertNull(VoucherRules.validate(true, false, NOW, TOMORROW, 5, NOW));
        assertNull(VoucherRules.validate(true, false, YESTERDAY, NOW, 5, NOW));
    }

    @Test
    void reportsDeletionBeforeExpiry_soTheClearestReasonWins() {
        assertEquals("Phiếu giảm giá đã bị xoá.",
                VoucherRules.validate(false, true, TOMORROW, TOMORROW, 0, NOW));
    }
}
