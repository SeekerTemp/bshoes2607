import { describe, it, expect } from 'vitest'
import { buildWarrantyHtml } from './warranty'

const sample = {
  ma: 'BH0001',
  tenKH: 'Nguyễn Trung Nghĩa',
  sdt: '0968291160',
  model: 'Giày thể thao Nike',
  mauSize: 'Đen / 42',
  serial: 'NK-42-000123',
  loai: 'Bong đế',
  donVi: 'BShoes Center',
  nv: 'Lê Thị B',
  chiPhi: 120000,
  thayLinhKien: true,
  batDau: '2026-05-08',
  hetHan: '2026-11-08',
  trangThai: 'Đang xử lý',
  moTa: 'Đế bị bong ở mũi giày.',
}

describe('buildWarrantyHtml', () => {
  it('includes the warranty code', () => {
    expect(buildWarrantyHtml(sample)).toContain('BH0001')
  })

  it('includes the serial number', () => {
    expect(buildWarrantyHtml(sample)).toContain('NK-42-000123')
  })

  it('includes the customer name', () => {
    expect(buildWarrantyHtml(sample)).toContain('Nguyễn Trung Nghĩa')
  })

  it('includes the current status', () => {
    expect(buildWarrantyHtml(sample)).toContain('Đang xử lý')
  })

  it('handles a missing/null record without throwing', () => {
    expect(() => buildWarrantyHtml(null)).not.toThrow()
  })
})
