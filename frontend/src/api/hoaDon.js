import { http } from './http'

export const hoaDonApi = {
  findAll: () => http.get('/hoa-don').then(r => r.data),
  findById: (id) => http.get(`/hoa-don/${id}`).then(r => r.data),
  create: (e) => http.post('/hoa-don', e).then(r => r.data),
  update: (e) => http.put('/hoa-don', e).then(r => r.data),
  remove: (id) => http.delete(`/hoa-don/${id}`),
  cart: () => http.get('/hoa-don/cart').then(r => r.data),
  vouchersActive: () => http.get('/hoa-don/vouchers-active').then(r => r.data),
  markPaid: (id) => http.put(`/hoa-don/mark-paid/${id}`).then(r => r.data),
}

export const hoaDonChiTietApi = {
  findAll: () => http.get('/hoa-don-chi-tiet').then(r => r.data),
  create: (e) => http.post('/hoa-don-chi-tiet', e).then(r => r.data),
  update: (e) => http.put('/hoa-don-chi-tiet', e).then(r => r.data),
  remove: (id) => http.delete(`/hoa-don-chi-tiet/${id}`),
}
