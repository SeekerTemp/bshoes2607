import { http } from './http'

export const diaChiApi = {
  findAll: () => http.get('/dia-chi').then(r => r.data),
  byKhachHang: (idKhachHang) => http.get(`/dia-chi/by-khach-hang/${idKhachHang}`).then(r => r.data),
  create: (e) => http.post('/dia-chi', e).then(r => r.data),
  update: (id, e) => http.put(`/dia-chi/${id}`, e).then(r => r.data),
  remove: (id) => http.delete(`/dia-chi/${id}`).then(r => r.data),
}
