import { http } from './http'

export const phieuGiamGiaApi = {
  findAll: () => http.get('/phieu-giam-gia').then(r => r.data),
  findById: (id) => http.get(`/phieu-giam-gia/${id}`).then(r => r.data),
  create: (e) => http.post('/phieu-giam-gia', e).then(r => r.data),
  update: (e) => http.put('/phieu-giam-gia', e).then(r => r.data),
  remove: (id) => http.delete(`/phieu-giam-gia/${id}`),
}
