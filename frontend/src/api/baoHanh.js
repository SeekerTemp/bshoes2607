import { http } from './http'

export const baoHanhApi = {
  findAll: () => http.get('/bao-hanh').then(r => r.data),
  findById: (id) => http.get(`/bao-hanh/${id}`).then(r => r.data),
  create: (e) => http.post('/bao-hanh', e).then(r => r.data),
  updateTrangThai: (id, trangThai) => http.put(`/bao-hanh/${id}/trang-thai`, null, { params: { trangThai } }).then(r => r.data),
  remove: (id) => http.delete(`/bao-hanh/${id}`).then(r => r.data),
}
