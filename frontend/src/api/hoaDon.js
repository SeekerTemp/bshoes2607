import { http } from './http'

export const hoaDonApi = {
  posProducts: () => http.get('/hoa-don/pos-products').then(r => r.data),
  vouchersActive: () => http.get('/hoa-don/vouchers-active').then(r => r.data),
  cart: () => http.get('/hoa-don/cart').then(r => r.data),
  findAll: () => http.get('/hoa-don').then(r => r.data),
  findById: (id) => http.get(`/hoa-don/${id}`).then(r => r.data),
  giamGia: (idPhieu, tongTien) => http.get(`/hoa-don/${idPhieu}/giam-gia`, { params: { tongTien } }).then(r => r.data),
  create: (e) => http.post('/hoa-don', e).then(r => r.data),
}
