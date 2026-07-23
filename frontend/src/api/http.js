import axios from 'axios'
import { logEvent } from '../utils/logger'

export const http = axios.create({ baseURL: '/api', headers: { 'Content-Type': 'application/json' } })

const MUTATING_METHODS = new Set(['post', 'put', 'delete'])

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
    // MUST rethrow so all existing try/catch + toasts keep working.
    return Promise.reject(error)
  }
)
