import { http } from './http'

export const authApi = {
  login: (taiKhoan, matKhau) => http.post('/auth/login', { taiKhoan, matKhau }).then(r => r.data),
}
