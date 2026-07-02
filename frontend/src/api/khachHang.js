import { http } from './http'

export const khachHangApi = {
  findAll: () => http.get('/khach-hang').then(r => r.data),
  findById: (id) => http.get(`/khach-hang/${id}`).then(r => r.data),
  create: (e) => http.post('/khach-hang', e).then(r => r.data),
  update: (e) => http.put('/khach-hang', e).then(r => r.data),
  remove: (id) => http.delete(`/khach-hang/${id}`),
  search: (keyword) => http.get('/khach-hang/search', { params: { keyword } }).then(r => r.data),
}
