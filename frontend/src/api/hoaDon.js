import { http } from './http'

export const hoaDonApi = {
  posProducts: () => http.get('/hoa-don/pos-products').then(r => r.data),
  vouchersActive: () => http.get('/hoa-don/vouchers-active').then(r => r.data),
  cart: () => http.get('/hoa-don/cart').then(r => r.data),
  findAll: () => http.get('/hoa-don').then(r => r.data),
  findById: (id) => http.get(`/hoa-don/${id}`).then(r => r.data),
  giamGia: (idPhieu, tongTien) => http.get(`/hoa-don/${idPhieu}/giam-gia`, { params: { tongTien } }).then(r => r.data),
  create: (e) => http.post('/hoa-don', e).then(r => r.data),
  scanByMa: (ma) => http.get('/san-pham-chi-tiet/by-ma/' + encodeURIComponent(ma)).then(r => r.data),
  // --- POS write flow (server-backed cart + payment) ---
  createEmpty: (idNhanVien) => http.post('/hoa-don/create-empty', null, { params: idNhanVien ? { idNhanVien } : {} }).then(r => r.data),
  addItem: (id, body) => http.post(`/hoa-don/${id}/items`, body).then(r => r.data),
  updateItem: (idChiTiet, soLuong) => http.put(`/hoa-don/items/${idChiTiet}`, null, { params: { soLuong } }).then(r => r.data),
  removeItem: (idChiTiet) => http.delete(`/hoa-don/items/${idChiTiet}`).then(r => r.data),
  thanhToan: (id, body) => http.post(`/hoa-don/${id}/thanh-toan`, body).then(r => r.data),
  huyHoaDon: (id, idNhanVien) => http.post(`/hoa-don/${id}/huy`, null, { params: idNhanVien ? { idNhanVien } : {} }).then(r => r.data),
  daGiao: (id) => http.post(`/hoa-don/${id}/da-giao`).then(r => r.data),
  traHang: (id, idNhanVien) => http.post(`/hoa-don/${id}/tra-hang`, null, { params: idNhanVien ? { idNhanVien } : {} }).then(r => r.data),
}
