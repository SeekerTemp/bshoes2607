// Module-level auth store (singleton). Holds the logged-in nhân viên + role
// permissions, persisted in localStorage so a refresh keeps the session.
import { ref, computed } from 'vue'
import { authApi } from '../api/auth'
import { SCREENS } from '../config/screens'

const KEY = 'bshoes_user'

// Same reasoning as useCart.readCart(): this runs at module load, so an
// unguarded JSON.parse on a corrupted session would throw during import and
// leave the user staring at a blank page with no way to log in again. A broken
// session must degrade to "logged out".
export function readUser() {
  try {
    const parsed = JSON.parse(localStorage.getItem(KEY) || 'null')
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? parsed : null
  } catch {
    return null
  }
}

const user = ref(readUser())

function persist(u) {
  user.value = u
  try {
    if (u) localStorage.setItem(KEY, JSON.stringify(u))
    else localStorage.removeItem(KEY)
  } catch {
    // quota / private mode — the in-memory session still works for this tab
  }
}

// Pure: expand a quyen CSV ('*' or 'a,b,c') into an ordered list of allowed screen keys.
export function expandQuyen(quyen) {
  const q = (quyen || '').trim()
  if (q === '*') return SCREENS.map(s => s.key)
  return q.split(',').map(s => s.trim()).filter(Boolean)
}

// Pure: the path this quyen should land on after login.
// includes 'dashboard' (or '*', which expands to include it) -> '/';
// else the first SCREENS entry that is allowed; else '/login'.
export function landingFor(quyen) {
  const keys = expandQuyen(quyen)
  if (!keys.length) return '/login'
  if (keys.includes('dashboard')) return '/'
  const first = SCREENS.find(s => keys.includes(s.key))
  return first ? first.to : '/login'
}

export function useAuth() {
  const isAuthed = computed(() => !!user.value)
  const allowed = computed(() => expandQuyen(user.value?.quyen))
  function can(key) { return allowed.value.includes(key) }
  const landingRoute = computed(() => landingFor(user.value?.quyen))

  async function login(taiKhoan, matKhau) {
    const u = await authApi.login(taiKhoan, matKhau)   // 401 → throws
    persist(u)
    return u
  }
  // Offline / demo preview — no server token, so the backend will refuse every
  // admin endpoint (401) and each screen falls back to mock data behind the red
  // DemoDataBanner. That is the honest behaviour: this button lets you look at
  // the UI, it does not give you access to real data.
  function loginDemo() {
    persist({ id: null, ma: 'ADMIN', ten: 'Demo Admin', vaiTro: 'Quản trị', idVaiTro: 1, quyen: '*', token: null })
  }
  // Invalidate server-side first (best-effort), then clear locally either way.
  async function logout() {
    if (user.value?.token) await authApi.logout()
    persist(null)
  }

  return { user, isAuthed, allowed, can, landingRoute, login, loginDemo, logout }
}
