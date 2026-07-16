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
