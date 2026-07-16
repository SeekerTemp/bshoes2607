import { http } from './http'

// Giỏ hàng phía khách: giỏ ở localStorage, chỉ 3 API này chạm server.
export const gioHangApi = {
  checkout: (req) => http.post('/gio-hang/checkout', req).then(r => r.data),
  donHang: (soDienThoai) => http.get('/gio-hang/don-hang', { params: { soDienThoai } }).then(r => r.data),
  nhanHang: (idHoaDon) => http.put(`/gio-hang/nhan-hang/${idHoaDon}`).then(r => r.data),
}
