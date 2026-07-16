import { http } from './http'

// Product-variant (san_pham_chi_tiet) management — consolidated onto /san-pham-chi-tiet.
export const bienTheApi = {
  findByProduct: (idSanPham) => http.get(`/san-pham-chi-tiet/by-product/${idSanPham}`).then(r => r.data),
  // storefront catalogue: hết hàng vẫn trả về (để hiện nút Đặt trước)
  store: () => http.get('/san-pham-chi-tiet/store').then(r => r.data),
  create: (idSanPham, dto) => http.post(`/san-pham-chi-tiet/product/${idSanPham}`, dto).then(r => r.data),
  update: (id, dto) => http.put(`/san-pham-chi-tiet/${id}`, dto).then(r => r.data),
  nhapKho: (id, soLuong) => http.post(`/san-pham-chi-tiet/${id}/nhap-kho`, null, { params: { soLuong } }).then(r => r.data),
  remove: (id) => http.delete(`/san-pham-chi-tiet/${id}`).then(r => r.data),
}
