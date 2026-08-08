import axios from 'axios'
import { logEvent } from '../utils/logger'

export const http = axios.create({ baseURL: '/api', headers: { 'Content-Type': 'application/json' } })

const MUTATING_METHODS = new Set(['post', 'put', 'delete'])

// Same localStorage key useAuth persists to. Read directly rather than importing
// useAuth, which would create an import cycle (useAuth -> api/auth -> http).
const USER_KEY = 'bshoes_user'

function storedUser() {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

// Every request carries the session token the backend handed out at login;
// ApiAuthFilter rejects admin endpoints without it.
http.interceptors.request.use((config) => {
  const token = storedUser()?.token
  if (token) config.headers['X-Auth-Token'] = token
  return config
})

// Success path: log mutations (POST/PUT/DELETE) only - GETs are too noisy.
// This shows what was attempted right before a failure in the log timeline.
http.interceptors.response.use(
  (response) => {
    const method = (response.config?.method || '').toLowerCase()
    if (MUTATING_METHODS.has(method)) {
      logEvent({
        level: 'info',
        category: 'api',
        message: `${method.toUpperCase()} ${response.config?.url} -> ${response.status}`,
        detail: {
          status: response.status,
          url: response.config?.url,
          method,
          requestData: response.config?.data,
        },
      })
    }
    return response
  },
  (error) => {
    const config = error.config || {}
    const method = (config.method || '').toUpperCase()
    const status = error.response?.status
    logEvent({
      level: 'error',
      category: 'api',
      message: `${method} ${config.url} -> ${status ?? 'NETWORK_ERROR'}`,
      detail: {
        status,
        url: config.url,
        method,
        requestData: config.data,
        responseData: error.response?.data,
      },
    })
    // An expired/invalid session: drop it and send the user back to login.
    // Deliberately skipped for the offline "Vào demo" login, where the 401 should
    // fall through so the screen shows its mock data behind the red
    // DemoDataBanner instead of bouncing the user to a login page they cannot
    // use while the backend is down.
    //
    // The demo case is identified by the explicit `demo` flag, NOT by a missing
    // token. Testing `storedUser()?.token` treated every tokenless session as
    // demo — including a REAL session saved before tokens existed. Such a user
    // sent no X-Auth-Token, got 401 ("thiếu token") on every protected call, and
    // was never logged out: the dashboard re-fired all seven /thong-ke requests
    // round after round while still showing their name. That is the 401 storm in
    // logs/bshoes260805.log 17:41–17:42, which never resolved on its own.
    const u = storedUser()
    if (status === 401 && !String(config.url || '').includes('/auth/') && u && !u.demo) {
      try {
        localStorage.removeItem(USER_KEY)
        if (!window.location.pathname.startsWith('/login')) {
          window.location.href = '/login?expired=1'
        }
      } catch {
        // never let session cleanup break the error path
      }
    }
    // MUST rethrow so all existing try/catch + toasts keep working.
    return Promise.reject(error)
  }
)
