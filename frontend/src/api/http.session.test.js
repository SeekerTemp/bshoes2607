// Hồi quy cho "bão 401" trong logs/bshoes260805.log (17:41–17:42):
// một phiên đăng nhập THẬT lưu từ 2026-07-31 — trước khi ApiAuthFilter và token
// tồn tại — nên object trong localStorage không có trường `token`. http.js khi đó
// kiểm tra `storedUser()?.token` để quyết định có đăng xuất hay không, nên phiên
// này bị coi là phiên demo: không bao giờ bị xoá, không bao giờ chuyển về /login.
// Dashboard cứ thế bắn lại cả 7 request /thong-ke, vòng này qua vòng khác, trong
// khi màn hình vẫn hiển thị "Nguyễn Văn A".
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'

const USER_KEY = 'bshoes_user'

// Nạp lại module cho mỗi test: interceptor được đăng ký lúc import.
async function freshHttp() {
  vi.resetModules()
  const mod = await import('./http')
  return mod.http
}

function setUser(u) {
  localStorage.setItem(USER_KEY, JSON.stringify(u))
}

/** Chạy nhánh xử lý lỗi của response interceptor với một lỗi 401 giả lập. */
async function fire401(http, url = '/thong-ke/tong-quan') {
  const handlers = http.interceptors.response.handlers.filter(Boolean)
  const onRejected = handlers[handlers.length - 1].rejected
  const err = {
    config: { url, method: 'get' },
    response: { status: 401, data: { error: true, message: 'Bạn cần đăng nhập để dùng chức năng này' } },
  }
  await expect(onRejected(err)).rejects.toBe(err)
}

describe('http 401 handling', () => {
  let hrefSpy

  beforeEach(() => {
    localStorage.clear()
    hrefSpy = ''
    // environment là 'node' (không có jsdom), nên dựng window tối thiểu giống
    // cách vitest.setup.js dựng localStorage.
    globalThis.window = {
      location: {
        pathname: '/',
        set href(v) { hrefSpy = v },
        get href() { return hrefSpy },
      },
    }
  })

  afterEach(() => {
    localStorage.clear()
    delete globalThis.window
  })

  it('logs out a real session that carries NO token (the 2026-08-05 401 storm)', async () => {
    setUser({ id: 1, ma: 'NV1', ten: 'Nguyễn Văn A', quyen: '*' })   // không có `token`
    const http = await freshHttp()

    await fire401(http)

    expect(localStorage.getItem(USER_KEY)).toBeNull()
    expect(hrefSpy).toBe('/login?expired=1')
  })

  it('logs out a real session whose token the server no longer knows', async () => {
    setUser({ id: 1, ma: 'NV1', ten: 'Nguyễn Văn A', quyen: '*', token: 'da-het-han' })
    const http = await freshHttp()

    await fire401(http)

    expect(localStorage.getItem(USER_KEY)).toBeNull()
    expect(hrefSpy).toBe('/login?expired=1')
  })

  it('keeps the offline demo session so screens fall back to mock data', async () => {
    setUser({ id: null, ma: 'ADMIN', ten: 'Demo Admin', quyen: '*', token: null, demo: true })
    const http = await freshHttp()

    await fire401(http)

    expect(localStorage.getItem(USER_KEY)).not.toBeNull()
    expect(hrefSpy).toBe('')
  })

  it('never bounces a 401 coming from the auth endpoints themselves', async () => {
    setUser({ id: 1, ma: 'NV1', ten: 'Nguyễn Văn A', quyen: '*', token: 'x' })
    const http = await freshHttp()

    await fire401(http, '/auth/login')

    expect(localStorage.getItem(USER_KEY)).not.toBeNull()
    expect(hrefSpy).toBe('')
  })
})
