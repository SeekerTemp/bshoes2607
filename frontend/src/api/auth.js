import { http } from './http'

export const authApi = {
  login: (taiKhoan, matKhau) => http.post('/auth/login', { taiKhoan, matKhau }).then(r => r.data),
  // Invalidates the token server-side so a copied token stops working the
  // moment the user logs out, not 8 hours later when it expires.
  logout: () => http.post('/auth/logout').then(r => r.data).catch(() => null),
  me: () => http.get('/auth/me').then(r => r.data),
}
