import { describe, it, expect } from 'vitest'
import { redact, trimBuffer } from './logger'

// Pure-helper tests only. vitest.setup.js stubs localStorage with a no-op
// getItem/setItem, so logEvent/getLogs/etc. are exercised indirectly at most;
// these tests target redact() and trimBuffer() directly and don't depend on
// real persistence.

describe('redact', () => {
  it('masks matKhau at the top level', () => {
    const out = redact({ taiKhoan: 'admin', matKhau: 'sieumatkhau123' })
    expect(out).toEqual({ taiKhoan: 'admin', matKhau: '***' })
  })

  it('masks password/token variants case-insensitively', () => {
    const out = redact({ Password: 'a', PASSWORD_CONFIRM: 'b', accessToken: 'c', pass: 'd' })
    expect(out).toEqual({ Password: '***', PASSWORD_CONFIRM: '***', accessToken: '***', pass: '***' })
  })

  it('masks sensitive keys nested at any depth', () => {
    const out = redact({
      status: 400,
      requestData: { taiKhoan: 'admin', matKhau: 'secret' },
      nested: { deeper: { token: 'abc123' } },
    })
    expect(out).toEqual({
      status: 400,
      requestData: { taiKhoan: 'admin', matKhau: '***' },
      nested: { deeper: { token: '***' } },
    })
  })

  it('masks sensitive keys inside arrays of objects', () => {
    const out = redact([{ ten: 'A', matKhau: 'x' }, { ten: 'B', password: 'y' }])
    expect(out).toEqual([{ ten: 'A', matKhau: '***' }, { ten: 'B', password: '***' }])
  })

  it('leaves non-sensitive fields intact', () => {
    const input = { url: '/api/khach-hang', method: 'post', status: 500, count: 3, ok: false, note: null }
    expect(redact(input)).toEqual(input)
  })

  it('truncates very long strings instead of storing them whole', () => {
    const longStr = 'x'.repeat(3000)
    const out = redact({ blob: longStr })
    expect(out.blob.length).toBeLessThan(longStr.length)
    expect(out.blob.startsWith('xxxx')).toBe(true)
  })

  it('does not blow up on circular references', () => {
    const obj = { a: 1 }
    obj.self = obj
    expect(() => redact(obj)).not.toThrow()
  })

  it('passes through primitives unchanged', () => {
    expect(redact(42)).toBe(42)
    expect(redact(true)).toBe(true)
    expect(redact(null)).toBe(null)
    expect(redact('hello')).toBe('hello')
  })
})

describe('trimBuffer', () => {
  it('keeps the array unchanged when at or below max', () => {
    const arr = [1, 2, 3]
    expect(trimBuffer(arr, 5)).toEqual([1, 2, 3])
    expect(trimBuffer(arr, 3)).toEqual([1, 2, 3])
  })

  it('keeps only the last max entries, dropping the oldest', () => {
    const arr = [1, 2, 3, 4, 5]
    expect(trimBuffer(arr, 3)).toEqual([3, 4, 5])
  })

  it('keeps only the last MAX (400) entries for a large buffer', () => {
    const arr = Array.from({ length: 500 }, (_, i) => i)
    const out = trimBuffer(arr, 400)
    expect(out.length).toBe(400)
    expect(out[0]).toBe(100)
    expect(out[out.length - 1]).toBe(499)
  })

  it('returns an empty array for non-array input', () => {
    expect(trimBuffer(null, 10)).toEqual([])
    expect(trimBuffer(undefined, 10)).toEqual([])
  })

  it('does not mutate the original array', () => {
    const arr = [1, 2, 3, 4, 5]
    trimBuffer(arr, 2)
    expect(arr).toEqual([1, 2, 3, 4, 5])
  })
})
