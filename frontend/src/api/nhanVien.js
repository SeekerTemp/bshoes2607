import { http } from './http'

export const nhanVienApi = {
  findAll: () => http.get('/nhan-vien').then(r => r.data),
  findById: (id) => http.get(`/nhan-vien/${id}`).then(r => r.data),
  create: (e) => http.post('/nhan-vien', e).then(r => r.data),
  update: (e) => http.put('/nhan-vien', e).then(r => r.data),
  remove: (id) => http.delete(`/nhan-vien/${id}`),
  search: (ten, gioiTinh) => http.get('/nhan-vien/search', { params: { ten, gioiTinh } }).then(r => r.data),
}
