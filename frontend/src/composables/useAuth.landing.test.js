import { describe, it, expect } from 'vitest'
import { expandQuyen, landingFor } from './useAuth'

describe('expandQuyen', () => {
  it('expands "*" to every screen key', () => {
    const keys = expandQuyen('*')
    expect(keys).toContain('dashboard')
    expect(keys).toContain('he-thong')
    expect(keys.length).toBeGreaterThan(5)
  })
  it('splits a CSV, trimming blanks', () => {
    expect(expandQuyen('hoa-don, don-hang ,')).toEqual(['hoa-don', 'don-hang'])
  })
  it('returns [] for empty / null', () => {
    expect(expandQuyen('')).toEqual([])
    expect(expandQuyen(null)).toEqual([])
    expect(expandQuyen(undefined)).toEqual([])
  })
})

describe('landingFor', () => {
  it('sends admin ("*") to /', () => {
    expect(landingFor('*')).toBe('/')
  })
  it('sends a role that includes dashboard to /', () => {
    expect(landingFor('dashboard,hoa-don')).toBe('/')
  })
  it('sends a role WITHOUT dashboard to its first authorized screen', () => {
    // SCREENS order: dashboard, san-pham, hoa-don, don-hang, ...
    expect(landingFor('hoa-don,don-hang')).toBe('/hoa-don')
    expect(landingFor('san-pham,khach-hang,phieu-giam-gia')).toBe('/san-pham')
  })
  it('falls back to /login when nothing is allowed', () => {
    expect(landingFor('')).toBe('/login')
  })
})
