import { describe, it, expect } from 'vitest'
import { vnd } from './format'

// vnd() renders every money figure in the app — cart, POS, invoices, receipts,
// dashboard. A NaN or "undefined ₫" leaking through it is visible to customers.
describe('vnd', () => {
  it('groups thousands the Vietnamese way and appends the đồng sign', () => {
    expect(vnd(1500000)).toBe('1.500.000 ₫')
  })

  it('renders zero', () => {
    expect(vnd(0)).toBe('0 ₫')
  })

  it('renders null / undefined / NaN as 0 rather than leaking them to the screen', () => {
    expect(vnd(null)).toBe('0 ₫')
    expect(vnd(undefined)).toBe('0 ₫')
    expect(vnd(NaN)).toBe('0 ₫')
  })

  it('accepts a numeric string', () => {
    expect(vnd('250000')).toBe('250.000 ₫')
  })

  it('renders a negative amount with its sign', () => {
    expect(vnd(-50000)).toBe('-50.000 ₫')
  })
})
