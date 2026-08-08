import { describe, it, expect } from 'vitest'
import { tinhGiamGia } from './voucher'

describe('tinhGiamGia', () => {
  it('gives no discount without a voucher', () => {
    expect(tinhGiamGia(500000, null)).toBe(0)
    expect(tinhGiamGia(500000, undefined)).toBe(0)
  })

  it('applies a percentage voucher (loại 0)', () => {
    expect(tinhGiamGia(1000000, { loai: 0, giaTri: 10 })).toBe(100000)
  })

  it('applies a fixed-amount voucher (loại 1)', () => {
    expect(tinhGiamGia(1000000, { loai: 1, giaTri: 50000 })).toBe(50000)
  })

  it('gives no discount below đơn tối thiểu', () => {
    expect(tinhGiamGia(200000, { loai: 1, giaTri: 50000, donToiThieu: 500000 })).toBe(0)
  })

  it('applies exactly at đơn tối thiểu', () => {
    expect(tinhGiamGia(500000, { loai: 1, giaTri: 50000, donToiThieu: 500000 })).toBe(50000)
  })

  it('caps at giảm tối đa when one is set', () => {
    expect(tinhGiamGia(1000000, { loai: 0, giaTri: 50, giamToiDa: 200000 })).toBe(200000)
  })

  // The bug that made khuyến mãi look broken at the till: `giamToiDa || 0`
  // turned "no cap" into "cap of 0đ", so every uncapped voucher discounted 0.
  it('treats a missing giảm tối đa as NO cap, not a cap of 0', () => {
    expect(tinhGiamGia(1000000, { loai: 0, giaTri: 10 })).toBe(100000)
    expect(tinhGiamGia(1000000, { loai: 1, giaTri: 50000, giamToiDa: null })).toBe(50000)
    expect(tinhGiamGia(1000000, { loai: 1, giaTri: 50000, giamToiDa: 0 })).toBe(50000)
  })

  it('never discounts more than the bill', () => {
    expect(tinhGiamGia(30000, { loai: 1, giaTri: 50000 })).toBe(30000)
  })

  it('never returns a negative discount', () => {
    expect(tinhGiamGia(100000, { loai: 1, giaTri: -50000 })).toBe(0)
  })

  it('rounds to whole đồng', () => {
    expect(tinhGiamGia(333333, { loai: 0, giaTri: 10 })).toBe(33333)
  })

  it('handles a zero bill', () => {
    expect(tinhGiamGia(0, { loai: 0, giaTri: 10 })).toBe(0)
  })
})
