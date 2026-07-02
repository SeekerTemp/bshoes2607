import { http } from './http'

export const sanPhamApi = {
  findAll: () => http.get('/san-pham-ql').then(r => r.data),
  create: (e) => http.post('/san-pham-ql', e).then(r => r.data),
  update: (e) => http.put('/san-pham-ql', e).then(r => r.data),
  softDelete: (ma) => http.delete(`/san-pham-ql/soft/${ma}`).then(r => r.data),
}

export const sanPhamChiTietApi = {
  findAll: () => http.get('/san-pham-chi-tiet-ql').then(r => r.data),
  create: (e) => http.post('/san-pham-chi-tiet-ql', e).then(r => r.data),
  update: (e) => http.put('/san-pham-chi-tiet-ql', e).then(r => r.data),
}
