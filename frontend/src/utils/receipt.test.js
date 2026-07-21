import { describe, it, expect } from 'vitest'
import { buildReceiptHtml } from './receipt'

const sample = {
  ma: 'HD1',
  ngay: '01/07/2026 10:00:00',
  khach: 'Nguyễn Văn A',
  hinhThuc: 'Tiền mặt',
  items: [
    { ten: 'Nike Air Zoom', soLuong: 1, donGia: 2000000, thanhTien: 2000000 },
    { ten: 'Adidas Ultraboost', soLuong: 2, donGia: 2500000, thanhTien: 5000000 },
  ],
  tamTinh: 7000000,
  giamGia: 200000,
  phiShip: 0,
  phaiTra: 6800000,
}

describe('buildReceiptHtml', () => {
  it('includes the invoice code', () => {
    expect(buildReceiptHtml(sample)).toContain('HD1')
  })

  it('includes each item name', () => {
    const html = buildReceiptHtml(sample)
    expect(html).toContain('Nike Air Zoom')
    expect(html).toContain('Adidas Ultraboost')
  })

  it('renders exactly one item row (header row excluded)', () => {
    const html = buildReceiptHtml(sample)
    const rowCount = (html.match(/<tr>\s*<td>/g) || []).length
    expect(rowCount).toBe(sample.items.length)
  })

  it('includes the formatted total (phaiTra)', () => {
    const html = buildReceiptHtml(sample)
    expect(html).toContain('6.800.000')
  })

  it('defaults to a paid receipt tag when paid is not explicitly false', () => {
    const html = buildReceiptHtml(sample)
    expect(html).toContain('HÓA ĐƠN THANH TOÁN')
  })

  it('shows the tạm tính tag when paid is false', () => {
    const html = buildReceiptHtml({ ...sample, paid: false })
    expect(html).toContain('PHIẾU TẠM TÍNH')
  })

  it('handles an empty items array without throwing', () => {
    expect(() => buildReceiptHtml({ ...sample, items: [] })).not.toThrow()
  })
})
