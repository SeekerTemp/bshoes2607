import { http } from './http'

export const thongKeApi = {
  homNay: () => http.get('/thong-ke/hom-nay').then(r => r.data),
  theoThang: (thang, nam) => http.get('/thong-ke/theo-thang', { params: { thang, nam } }).then(r => r.data),
  theoNam: (nam) => http.get('/thong-ke/theo-nam', { params: { nam } }).then(r => r.data),
  tatCaSanPham: () => http.get('/thong-ke/san-pham').then(r => r.data),
  nam: () => http.get('/thong-ke/nam').then(r => r.data),
}
