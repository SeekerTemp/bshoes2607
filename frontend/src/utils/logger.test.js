import { describe, it, expect } from 'vitest'
import { redact, trimBuffer, selectUnsent, markSent } from './logger'

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

describe('selectUnsent', () => {
  it('picks only entries without a sent flag', () => {
    const buf = [{ id: 1, sent: true }, { id: 2 }, { id: 3, sent: false }]
    expect(selectUnsent(buf)).toEqual([{ id: 2 }, { id: 3, sent: false }])
  })

  it('returns everything unsent when nothing has been shipped yet', () => {
    const buf = [{ id: 1 }, { id: 2 }, { id: 3 }]
    expect(selectUnsent(buf)).toEqual(buf)
  })

  it('returns an empty array once everything has been marked sent', () => {
    const buf = [{ id: 1, sent: true }, { id: 2, sent: true }]
    expect(selectUnsent(buf)).toEqual([])
  })

  it('caps the result at the given limit, oldest first', () => {
    const buf = [{ id: 1 }, { id: 2 }, { id: 3 }, { id: 4 }]
    expect(selectUnsent(buf, 2)).toEqual([{ id: 1 }, { id: 2 }])
  })

  it('does not apply a cap when limit is omitted', () => {
    const buf = Array.from({ length: 150 }, (_, i) => ({ id: i }))
    expect(selectUnsent(buf).length).toBe(150)
  })

  it('returns an empty array for non-array input', () => {
    expect(selectUnsent(null, 10)).toEqual([])
    expect(selectUnsent(undefined, 10)).toEqual([])
  })

  it('does not mutate the original buffer', () => {
    const buf = [{ id: 1 }, { id: 2 }]
    selectUnsent(buf, 1)
    expect(buf).toEqual([{ id: 1 }, { id: 2 }])
  })
})

describe('markSent', () => {
  it('marks only the given entries as sent, leaving the rest untouched', () => {
    const e1 = { id: 1 }
    const e2 = { id: 2 }
    const e3 = { id: 3 }
    const buf = [e1, e2, e3]

    const out = markSent(buf, [e1, e2])

    expect(out).toEqual([{ id: 1, sent: true }, { id: 2, sent: true }, { id: 3 }])
    // original array/objects are not mutated
    expect(e1).toEqual({ id: 1 })
    expect(buf).toEqual([{ id: 1 }, { id: 2 }, { id: 3 }])
  })

  it('a failed flush (marking nothing) loses no entries', () => {
    const buf = [{ id: 1 }, { id: 2 }, { id: 3 }]
    const out = markSent(buf, [])
    expect(out).toEqual(buf)
  })

  it('a subsequent flush only picks up entries left unsent by the previous one', () => {
    const e1 = { id: 1 }
    const e2 = { id: 2 }
    const e3 = { id: 3 }
    let buf = [e1, e2, e3]

    // First flush ships e1, e2 and marks them sent; e3 wasn't part of the
    // batch (e.g. batch limit) so it must remain selectable afterwards.
    buf = markSent(buf, [e1, e2])
    expect(selectUnsent(buf)).toEqual([{ id: 3 }])
  })

  it('returns an empty array for non-array input', () => {
    expect(markSent(null, [])).toEqual([])
    expect(markSent(undefined, [])).toEqual([])
  })

  // Hồi quy cho log 2026-08-05: file 6.902 dòng chỉ chứa ~100 sự kiện thật, mỗi sự
  // kiện lặp đúng 66 lần = số lần flush. Nguyên nhân: flushLogs() gọi readBuffer()
  // LẦN NỮA trong .then(), tức là JSON.parse ra các object MỚI, nên markSent() so
  // sánh theo tham chiếu không khớp gì cả -> không entry nào được đánh dấu sent ->
  // mọi flush gửi lại toàn bộ lịch sử.
  it('marks entries sent across a localStorage round-trip (not by reference)', () => {
    const buf = [
      { id: 'a1', t: '2026-08-05T10:00:00.000Z', message: 'x' },
      { id: 'a2', t: '2026-08-05T10:00:01.000Z', message: 'y' },
    ]
    const shipped = JSON.parse(JSON.stringify(buf))   // bản đã "gửi đi"
    const reread = JSON.parse(JSON.stringify(buf))    // bản đọc lại từ localStorage

    const out = markSent(reread, shipped)

    expect(out.map((e) => e.sent)).toEqual([true, true])
    expect(selectUnsent(out)).toEqual([])
  })

  it('still matches legacy entries that carry no id, by reference', () => {
    const e1 = { message: 'cũ' }
    const e2 = { message: 'cũ hơn' }
    expect(markSent([e1, e2], [e1])).toEqual([{ message: 'cũ', sent: true }, { message: 'cũ hơn' }])
  })
})
