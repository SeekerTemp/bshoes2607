// Module-level auth store (singleton). Holds the logged-in nhân viên + role
// permissions, persisted in localStorage so a refresh keeps the session.
import { ref, computed } from 'vue'
import { authApi } from '../api/auth'
import { SCREENS } from '../config/screens'

const KEY = 'bshoes_user'
const user = ref(JSON.parse(localStorage.getItem(KEY) || 'null'))

function persist(u) {
  user.value = u
  if (u) localStorage.setItem(KEY, JSON.stringify(u))
  else localStorage.removeItem(KEY)
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
  const allowed = computed(() => {
    if (!user.value) return []
    const q = (user.value.quyen || '').trim()
    if (q === '*') return SCREENS.map(s => s.key)
    return q.split(',').map(s => s.trim()).filter(Boolean)
  })
  function can(key) { return allowed.value.includes(key) }

  async function login(taiKhoan, matKhau) {
    const u = await authApi.login(taiKhoan, matKhau)   // 401 → throws
    persist(u)
    return u
  }
  // offline / demo fallback — full-access admin without hitting the API
  function loginDemo() {
    persist({ id: null, ma: 'ADMIN', ten: 'Demo Admin', vaiTro: 'Quản trị', idVaiTro: 1, quyen: '*' })
  }
  function logout() { persist(null) }

  return { user, isAuthed, allowed, can, login, loginDemo, logout }
}
