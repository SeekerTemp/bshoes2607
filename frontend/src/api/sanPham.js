import { http } from './http'

export const sanPhamApi = {
  findAll: () => http.get('/san-pham-ql').then(r => r.data),
  findById: (id) => http.get(`/san-pham-ql/${id}`).then(r => r.data),
  search: (keyword) => http.get('/san-pham-ql/search', { params: { keyword } }).then(r => r.data),
  recycle: () => http.get('/san-pham-ql/recycle').then(r => r.data),
  create: (e) => http.post('/san-pham-ql', e).then(r => r.data),
  update: (e) => http.put('/san-pham-ql', e).then(r => r.data),
  restore: (ma) => http.post(`/san-pham-ql/restore/${ma}`).then(r => r.data),
  remove: (ma) => http.delete(`/san-pham-ql/soft/${ma}`).then(r => r.data),
}
