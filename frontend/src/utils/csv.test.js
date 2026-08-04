import { describe, it, expect } from 'vitest'
import { buildCsv, parseCsv, parseCsvLine, stamp } from './csv'

describe('buildCsv', () => {
  const cols = [{ key: 'ma', label: 'Mã' }, { key: 'ten', label: 'Tên' }]

  it('emits a UTF-8 BOM so Excel reads Vietnamese correctly', () => {
    expect(buildCsv([], cols).startsWith('﻿')).toBe(true)
  })

  it('writes the header from column labels', () => {
    expect(buildCsv([], cols)).toBe('﻿"Mã","Tên"')
  })

  it('quotes every cell and escapes embedded quotes', () => {
    const csv = buildCsv([{ ma: 'A1', ten: 'Giày "xịn"' }], cols)
    expect(csv).toContain('"A1","Giày ""xịn"""')
  })

  it('keeps commas and newlines inside a cell instead of splitting the row', () => {
    const csv = buildCsv([{ ma: 'A1', ten: 'đen, size 42' }], cols)
    expect(csv).toContain('"đen, size 42"')
  })

  it('renders null/undefined as an empty cell', () => {
    expect(buildCsv([{ ma: null, ten: undefined }], cols)).toContain('"",""')
  })

  it('uses a column get() when provided', () => {
    const csv = buildCsv([{ thay: true }], [{ key: 'thay', label: 'Thay đế', get: (r) => (r.thay ? 'Có' : 'Không') }])
    expect(csv).toContain('"Có"')
  })
})

describe('parseCsvLine', () => {
  it('splits plain cells', () => {
    expect(parseCsvLine('a,b,c')).toEqual(['a', 'b', 'c'])
  })

  it('keeps a comma that lives inside quotes', () => {
    expect(parseCsvLine('"a,b",c')).toEqual(['a,b', 'c'])
  })

  it('unescapes doubled quotes', () => {
    expect(parseCsvLine('"say ""hi""",x')).toEqual(['say "hi"', 'x'])
  })

  it('produces empty strings for empty cells', () => {
    expect(parseCsvLine('a,,c')).toEqual(['a', '', 'c'])
  })
})

describe('parseCsv', () => {
  it('maps rows onto the header keys', () => {
    expect(parseCsv('ma,ten\nA1,Giày')).toEqual([{ ma: 'A1', ten: 'Giày' }])
  })

  it('strips a leading BOM', () => {
    expect(parseCsv('﻿ma\nA1')).toEqual([{ ma: 'A1' }])
  })

  it('skips blank lines', () => {
    expect(parseCsv('ma\nA1\n\nA2\n')).toEqual([{ ma: 'A1' }, { ma: 'A2' }])
  })

  it('handles CRLF line endings', () => {
    expect(parseCsv('ma,ten\r\nA1,Giày')).toEqual([{ ma: 'A1', ten: 'Giày' }])
  })

  it('returns [] for empty input', () => {
    expect(parseCsv('')).toEqual([])
    expect(parseCsv(null)).toEqual([])
  })

  it('fills missing trailing cells with empty strings', () => {
    expect(parseCsv('ma,ten\nA1')).toEqual([{ ma: 'A1', ten: '' }])
  })

  it('round-trips what buildCsv produced', () => {
    const cols = [{ key: 'ma', label: 'ma' }, { key: 'ten', label: 'ten' }]
    const rows = [{ ma: 'A1', ten: 'Giày "xịn", đen' }]
    expect(parseCsv(buildCsv(rows, cols))).toEqual(rows)
  })
})

describe('stamp', () => {
  it('zero-pads to YYYYMMDD-HHmm', () => {
    expect(stamp(new Date(2026, 7, 4, 9, 5))).toBe('20260804-0905')
  })
})
