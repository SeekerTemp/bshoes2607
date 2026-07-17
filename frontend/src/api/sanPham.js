import { http } from './http'

// Trang chi tiết ở storefront dùng endpoint công khai /san-pham (không phải /san-pham-ql
// của màn quản trị) — nó đã trả kèm danh sách biến thể.
export const sanPhamPublicApi = {
  findById: (id) => http.get(`/san-pham/${id}`).then(r => r.data),
}

export const sanPhamApi = {
  findAll: () => http.get('/san-pham-ql').then(r => r.data),
  findById: (id) => http.get(`/san-pham-ql/${id}`).then(r => r.data),
  search: (keyword) => http.get('/san-pham-ql/search', { params: { keyword } }).then(r => r.data),
  recycle: () => http.get('/san-pham-ql/recycle').then(r => r.data),
  create: (e) => http.post('/san-pham-ql', e).then(r => r.data),
  update: (e) => http.put('/san-pham-ql', e).then(r => r.data),
  restore: (ma) => http.post(`/san-pham-ql/restore/${ma}`).then(r => r.data),
  remove: (ma) => http.delete(`/san-pham-ql/soft/${ma}`).then(r => r.data),
  setDanhMuc: (id, idLoai) => http.put(`/san-pham-ql/${id}/danh-muc`, null, { params: idLoai != null ? { idLoai } : {} }).then(r => r.data),
}
