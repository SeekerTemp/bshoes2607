import { describe, it, expect } from 'vitest'
import { readFileSync } from 'node:fs'
import { SCREENS } from './screens'

// The screen registry is load-bearing in three places at once: the sidebar, the
// router's permission gate, and the per-employee quyền grid stored in
// nhan_vien_quyen (and now checked server-side by ScreenPermissions.java).
// A key that drifts out of sync in any one of them locks a role out of a screen
// with no visible error, so pin the invariants here.

const routerSrc = readFileSync(new URL('../router/index.js', import.meta.url), 'utf8')
const routeNames = [...routerSrc.matchAll(/name:\s*'([^']+)'/g)].map(m => m[1])
const publicNames = (routerSrc.match(/const PUBLIC = \[([^\]]*)\]/) || [, ''])[1]
  .split(',').map(s => s.trim().replace(/'/g, '')).filter(Boolean)

describe('SCREENS registry', () => {
  it('has no duplicate keys', () => {
    const keys = SCREENS.map(s => s.key)
    expect(new Set(keys).size).toBe(keys.length)
  })

  it('gives every screen a key, label and path', () => {
    for (const s of SCREENS) {
      expect(s.key, JSON.stringify(s)).toBeTruthy()
      expect(s.label, s.key).toBeTruthy()
      expect(s.to, s.key).toMatch(/^\//)
    }
  })

  it('maps every screen key onto a real route name', () => {
    const missing = SCREENS.filter(s => !routeNames.includes(s.key)).map(s => s.key)
    expect(missing).toEqual([])
  })

  it('never marks an admin screen as public — that would bypass the guard', () => {
    const leaked = SCREENS.filter(s => publicNames.includes(s.key)).map(s => s.key)
    expect(leaked).toEqual([])
  })

  it('keeps dashboard first so it stays the default landing screen', () => {
    expect(SCREENS[0].key).toBe('dashboard')
  })

  // Mirrors ScreenPermissions.SCREEN_BY_PREFIX on the backend. If the two drift,
  // the server rejects a screen the UI happily shows (403 with no explanation).
  it('covers every screen key the backend guards', () => {
    const guardedByBackend = [
      'dashboard', 'san-pham', 'hoa-don', 'dat-truoc',
      'nhan-vien', 'khach-hang', 'lich-su', 'bao-hanh', 'phieu-giam-gia',
    ]
    const keys = SCREENS.map(s => s.key)
    expect(guardedByBackend.filter(k => !keys.includes(k))).toEqual([])
  })
})
