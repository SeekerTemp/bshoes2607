import { http } from './http'

export const datTruocApi = {
  findAll: () => http.get('/dat-truoc').then(r => r.data),
  findById: (id) => http.get(`/dat-truoc/${id}`).then(r => r.data),
  dangKy: (e) => http.post('/dat-truoc', e).then(r => r.data),
  demChoHang: (idSanPhamChiTiet) => http.get('/dat-truoc/dem', { params: { idSanPhamChiTiet } }).then(r => r.data),
  capNhatTrangThai: (id, trangThai) => http.put(`/dat-truoc/${id}/trang-thai`, null, { params: { trangThai } }).then(r => r.data),
  chuyenDon: (id, idNhanVien) => http.post(`/dat-truoc/${id}/chuyen-don`, null, { params: idNhanVien ? { idNhanVien } : {} }).then(r => r.data),
  remove: (id) => http.delete(`/dat-truoc/${id}`).then(r => r.data),
}
