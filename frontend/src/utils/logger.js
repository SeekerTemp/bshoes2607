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

// Auto-ship-to-backend tuning. Entries are marked `sent: true` once a flush
// to the backend succeeds, so a flush never re-sends and a failed flush never
// loses anything - the next flush just retries the still-unsent entries.
const SHIP_URL = '/api/logs/client'
const FLUSH_BATCH_LIMIT = 100
const FLUSH_INTERVAL_MS = 20000

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

// ---- ship-to-backend helpers (pure, exported for testing) -----------------

// Picks the entries that haven't been shipped yet (no `sent` flag), oldest
// first, capped at `limit`. Never picks an already-`sent` entry, so a flush
// never re-sends what a previous flush already delivered.
export function selectUnsent(buf, limit) {
  if (!Array.isArray(buf)) return []
  const unsent = buf.filter((e) => e && !e.sent)
  return typeof limit === 'number' ? unsent.slice(0, limit) : unsent
}

// Returns a NEW array where every entry that is one of `entries` is replaced
// with a copy carrying `sent: true`; every other entry is left exactly as-is -
// so entries that were not part of this flush are never lost or altered.
//
// Matching is by `id`, NOT by object reference. flushLogs() re-reads the buffer
// from localStorage inside its .then(), so the entries it hands back here have
// been through JSON.parse and are different objects than the ones it shipped.
// Reference matching silently marked nothing, and every flush re-sent the whole
// history (log 2026-08-05: ~100 real events, 6.902 dòng, mỗi cái lặp 66 lần).
// Reference matching is kept as a fallback for entries logged before ids existed.
export function markSent(buf, entries) {
  if (!Array.isArray(buf)) return []
  const sentRefs = new Set(entries)
  const sentIds = new Set((entries || []).map((e) => e && e.id).filter((id) => id != null))
  return buf.map((e) => {
    const daGui = sentRefs.has(e) || (e && e.id != null && sentIds.has(e.id))
    return daGui ? { ...e, sent: true } : e
  })
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

// Id duy nhất cho mỗi entry. Phải duy nhất cả GIỮA các tab/lần tải trang, vì
// buffer nằm trong localStorage dùng chung — nên có phần ngẫu nhiên, không chỉ
// là bộ đếm theo phiên.
let entrySeq = 0
const LOGGER_RUN = Math.random().toString(36).slice(2, 8)
function nextEntryId() {
  return `${LOGGER_RUN}-${Date.now().toString(36)}-${++entrySeq}`
}

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
      // Danh tính bền vững qua JSON.stringify/parse, để markSent() biết entry nào
      // đã gửi rồi. Timestamp không đủ: hai sự kiện có thể trùng mili giây.
      id: nextEntryId(),
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

// ---- auto-ship to backend ---------------------------------------------
//
// Ships unsent entries to POST /api/logs/client so the backend persists them
// into the same rolling file as backend events (see
// ClientLogController + logback-spring.xml). After a deploy test run this
// means only ONE server-side file needs collecting.
//
// CRITICAL - no recursion: this uses the raw fetch() API, NEVER the `http`
// axios instance from api/http.js. That instance's response interceptor calls
// logEvent() on every failed request; if flushing went through axios, a
// failed flush would itself create a new "api error" log entry, which the
// next flush would try to ship, which would fail the same way, forever. For
// the same reason, the failure paths below only ever `console.warn` - they
// MUST NEVER call logEvent().

// Best-effort string form of the `user` field (an object like
// { ma, ten, vaiTro } or null) for the wire payload, whose `user` field is a
// plain string (see ClientLogEntryDto on the backend).
function userToString(user) {
  if (!user) return ''
  try {
    return user.ten || user.ma || JSON.stringify(user)
  } catch {
    return ''
  }
}

// Best-effort string form of `detail` (already redacted/capped at capture
// time in logEvent) for the wire payload, whose `detail` field is a plain
// string.
function detailToString(detail) {
  if (detail === undefined || detail === null) return ''
  if (typeof detail === 'string') return detail
  try {
    return JSON.stringify(detail)
  } catch {
    return String(detail)
  }
}

function toWireEntry(e) {
  return {
    t: e.t,
    level: e.level,
    category: e.category,
    message: e.message,
    route: e.route,
    user: userToString(e.user),
    detail: detailToString(e.detail),
  }
}

function buildPayload(unsent) {
  return JSON.stringify({ logs: unsent.map(toWireEntry) })
}

// Ships up to FLUSH_BATCH_LIMIT unsent entries to the backend. Never throws.
// On success, marks those entries `sent: true` (persisted) so they're never
// re-sent. On any failure - network down, backend down, CORS, etc. - it
// leaves them unsent so the NEXT flush (interval or page-hide) retries them;
// nothing is ever dropped just because one flush failed.
export function flushLogs() {
  try {
    const buf = readBuffer()
    const unsent = selectUnsent(buf, FLUSH_BATCH_LIMIT)
    if (!unsent.length) return

    fetch(SHIP_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: buildPayload(unsent),
    })
      .then((res) => {
        if (res && res.ok) {
          writeBuffer(markSent(readBuffer(), unsent))
        }
        // non-ok response: leave unsent, next flush retries.
      })
      .catch(() => {
        // Backend/network unreachable. Only console.warn - NEVER logEvent()
        // here, or a failed flush would generate a new entry for the next
        // flush to try (and fail) forever.
        console.warn('[logger] flushLogs: failed to ship logs to backend')
      })
  } catch {
    // shipping must never throw / break the app
  }
}

// Same idea as flushLogs(), but used when the page is being hidden/unloaded,
// where a normal fetch() can be cancelled mid-flight by the browser.
// navigator.sendBeacon is designed to survive that; fall back to
// fetch(keepalive) when sendBeacon isn't available.
function flushLogsOnUnload() {
  try {
    const buf = readBuffer()
    const unsent = selectUnsent(buf, FLUSH_BATCH_LIMIT)
    if (!unsent.length) return

    const payload = buildPayload(unsent)

    if (typeof navigator !== 'undefined' && typeof navigator.sendBeacon === 'function') {
      const blob = new Blob([payload], { type: 'application/json' })
      const accepted = navigator.sendBeacon(SHIP_URL, blob)
      if (accepted) {
        writeBuffer(markSent(readBuffer(), unsent))
      }
      // sendBeacon only guarantees the browser accepted the request for
      // delivery, not that the server processed it, but that's the best
      // signal available synchronously at unload time.
      return
    }

    if (typeof fetch === 'function') {
      fetch(SHIP_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: payload,
        keepalive: true,
      }).catch(() => {
        // best-effort only at unload time - never logEvent() here either.
      })
    }
  } catch {
    // logging must never break page unload
  }
}

let autoShipStarted = false

// Wires up the periodic flush plus the page-hide/unload flush. Safe to call
// more than once (e.g. the module being imported from more than one entry
// point) - only registers the interval/listeners on the first call.
export function startAutoShip() {
  try {
    if (autoShipStarted) return
    autoShipStarted = true

    if (typeof window === 'undefined') return

    setInterval(() => {
      try {
        if (selectUnsent(readBuffer(), 1).length) {
          flushLogs()
        }
      } catch {
        // never let the interval throw
      }
    }, FLUSH_INTERVAL_MS)

    if (typeof document !== 'undefined' && typeof document.addEventListener === 'function') {
      document.addEventListener('visibilitychange', () => {
        if (document.visibilityState === 'hidden') {
          flushLogsOnUnload()
        }
      })
    }

    if (typeof window.addEventListener === 'function') {
      window.addEventListener('pagehide', flushLogsOnUnload)
    }
  } catch {
    // never break the app just because auto-ship setup failed
  }
}

// Start lazily as soon as the module loads. In the vitest node test
// environment `window` is undefined, so this is a no-op there (guarded
// above) and the pure helpers can still be tested in isolation.
startAutoShip()
