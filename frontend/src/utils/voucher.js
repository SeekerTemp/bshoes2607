// Discount maths for the POS screen.
//
// Mirrors the SQL function dbo.tinh_tien_giam_gia (sqlBshoes.sql §3.2), which is
// what HoaDonServiceImpl.thanhToan() actually charges. The two MUST agree: if
// the counter shows a discount the server does not apply, the customer is
// charged a different total than the screen promised.
//
//   loại 0 = giảm theo %          loại 1 = giảm số tiền cố định
//   dưới đơn tối thiểu            -> 0
//   giảm tối đa (nếu có)          -> trần

/**
 * @param {number} tong subtotal before discount
 * @param {{loai?: number, giaTri?: number, donToiThieu?: number, giamToiDa?: number}|null} v
 * @returns {number} discount amount, rounded, never negative, never above `tong`
 */
export function tinhGiamGia(tong, v) {
  const tongTien = Number(tong) || 0
  if (!v) return 0

  const donToiThieu = Number(v.donToiThieu) || 0
  if (tongTien < donToiThieu) return 0

  const giaTri = Number(v.giaTri) || 0
  let giam = Number(v.loai) === 0 ? tongTien * (giaTri / 100) : giaTri

  // A missing / null / 0 cap means "không giới hạn", NOT "giảm tối đa 0đ".
  // Treating it as 0 used to clamp every uncapped voucher down to no discount
  // at all, which is why khuyến mãi appeared to do nothing at the till.
  const giamToiDa = Number(v.giamToiDa)
  if (Number.isFinite(giamToiDa) && giamToiDa > 0 && giam > giamToiDa) {
    giam = giamToiDa
  }

  // Never discount more than the bill itself.
  return Math.round(Math.min(Math.max(0, giam), tongTien))
}
