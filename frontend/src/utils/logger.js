// Dependency-free ring-buffer event logger.
//
// Captures API failures + key app events into a bounded buffer persisted in
// localStorage (survives reloads), so a user hitting a bug in a deployed
// environment can download a JSON log file and send it for diagnosis.
//
// Logging must NEVER break the app: every public function guards its work in
// try/catch and fails silently rather than throwing.

const MAX = 400
const STORAGE_KEY = 'bshoes_logs'
const USER_KEY = 'bshoes_user'

// Matches matKhau / password / pass / token at any casing, anywhere in a key name.
const SENSITIVE_KEY_RE = /matKhau|password|pass|token/i

const MAX_STRING = 2000       // cap for any single stored string
const MAX_DETAIL_JSON = 4000  // cap for the whole `detail` payload once JSON-stringified

// ---- pure helpers (exported for testing) ----------------------------------

// Deep-redacts any key matching SENSITIVE_KEY_RE -> '***', recursing through
// plain objects/arrays. Leaves other fields intact. Also truncates long
// strings so a single field can't blow up localStorage.
export function redact(value, seen) {
  const seenSet = seen || new WeakSet()

  if (typeof value === 'string') {
    return value.length > MAX_STRING ? value.slice(0, MAX_STRING) + '...(truncated)' : value
  }

  if (Array.isArray(value)) {
    if (seenSet.has(value)) return '[circular]'
    seenSet.add(value)
    return value.map((v) => redact(v, seenSet))
  }

  if (value && typeof value === 'object') {
    if (seenSet.has(value)) return '[circular]'
    seenSet.add(value)
    const out = {}
    for (const key of Object.keys(value)) {
      out[key] = SENSITIVE_KEY_RE.test(key) ? '***' : redact(value[key], seenSet)
    }
    return out
  }

  return value
}

// Keeps only the last `max` entries of an array (oldest trimmed first).
export function trimBuffer(arr, max) {
  if (!Array.isArray(arr)) return []
  if (arr.length <= max) return arr.slice()
  return arr.slice(arr.length - max)
}

// ---- storage plumbing -------------------------------------------------------

function readBuffer() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return []
    const arr = JSON.parse(raw)
    return Array.isArray(arr) ? arr : []
  } catch {
    return []
  }
}

function writeBuffer(arr) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(arr))
  } catch {
    // quota exceeded / private mode / unavailable localStorage - never break the app
  }
}

function currentRoute() {
  try {
    return window.location.pathname + window.location.search
  } catch {
    return ''
  }
}

// Reads the logged-in tài khoản from the same key useAuth persists to
// ('bshoes_user'), exposing only non-sensitive fields.
function currentUser() {
  try {
    const raw = localStorage.getItem(USER_KEY)
    if (!raw) return null
    const u = JSON.parse(raw)
    if (!u || typeof u !== 'object') return null
    return { ma: u.ma ?? null, ten: u.ten ?? null, vaiTro: u.vaiTro ?? null }
  } catch {
    return null
  }
}

// ---- public API --------------------------------------------------------

// Pushes one log entry. Never throws.
export function logEvent({ level = 'info', category = 'app', message = '', detail } = {}) {
  try {
    let safeDetail
    if (detail !== undefined) {
      const redacted = redact(detail)
      let json
      try {
        json = JSON.stringify(redacted)
      } catch {
        json = String(redacted)
      }
      if (json && json.length > MAX_DETAIL_JSON) {
        safeDetail = { truncated: true, preview: json.slice(0, MAX_DETAIL_JSON) }
      } else {
        safeDetail = redacted
      }
    }

    const entry = {
      t: new Date().toISOString(),
      level,
      category,
      message: typeof message === 'string' ? message.slice(0, MAX_STRING) : message,
      detail: safeDetail,
      route: currentRoute(),
      user: currentUser(),
    }

    const buf = readBuffer()
    buf.push(entry)
    writeBuffer(trimBuffer(buf, MAX))
  } catch {
    // logging must never throw / break the app
  }
}

export function getLogs() {
  return readBuffer()
}

export function clearLogs() {
  try {
    localStorage.removeItem(STORAGE_KEY)
  } catch {
    // ignore
  }
}

// Builds a Blob of pretty JSON and triggers a browser download named
// bshoes-log-<YYYYMMDD-HHmm>.json via a temporary <a download>.
export function downloadLogs() {
  try {
    const logs = readBuffer()
    const payload = {
      meta: {
        exportedAt: new Date().toISOString(),
        userAgent: (typeof navigator !== 'undefined' && navigator.userAgent) || '',
        appUrl: (typeof window !== 'undefined' && window.location && window.location.href) || '',
        count: logs.length,
      },
      logs,
    }

    const json = JSON.stringify(payload, null, 2)
    const blob = new Blob([json], { type: 'application/json' })
    const url = URL.createObjectURL(blob)

    const pad = (n) => String(n).padStart(2, '0')
    const now = new Date()
    const filename = `bshoes-log-${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}-${pad(now.getHours())}${pad(now.getMinutes())}.json`

    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  } catch {
    // never break the app just because export failed
  }
}
