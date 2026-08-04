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
    // An expired/invalid token on a REAL session: drop it and send the user
    // back to login. Deliberately skipped for the offline "Vào demo" login,
    // which has no token — there the 401 should fall through so the screen
    // shows its mock data behind the red DemoDataBanner instead of bouncing
    // the user to a login page they cannot use while the backend is down.
    if (status === 401 && !String(config.url || '').includes('/auth/') && storedUser()?.token) {
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
