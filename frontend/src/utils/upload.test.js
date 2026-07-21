import { describe, it, expect } from 'vitest'
import { validateImageFile } from './upload'

describe('validateImageFile', () => {
  it('accepts a png under 1MB', () => {
    const r = validateImageFile({ type: 'image/png', size: 500_000 })
    expect(r.ok).toBe(true)
  })

  it('accepts a jpg under 1MB', () => {
    const r = validateImageFile({ type: 'image/jpeg', size: 1000 })
    expect(r.ok).toBe(true)
  })

  it('rejects an unsupported type', () => {
    const r = validateImageFile({ type: 'image/gif', size: 1000 })
    expect(r.ok).toBe(false)
    expect(r.error).toBeTruthy()
  })

  it('rejects a file 1MB or larger', () => {
    const r = validateImageFile({ type: 'image/png', size: 1_048_576 })
    expect(r.ok).toBe(false)
    expect(r.error).toBeTruthy()
  })

  it('rejects null/undefined', () => {
    expect(validateImageFile(null).ok).toBe(false)
    expect(validateImageFile(undefined).ok).toBe(false)
  })
})
