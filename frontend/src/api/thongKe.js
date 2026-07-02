import { http } from './http'

export const thongKeApi = {
  homNay: () => http.get('/thong-ke/hom-nay').then(r => r.data),
  theoThang: (params) => http.get('/thong-ke/theo-thang', { params }).then(r => r.data),
  theoNam: (params) => http.get('/thong-ke/theo-nam', { params }).then(r => r.data),
  sanPham: () => http.get('/thong-ke/san-pham').then(r => r.data),
  nam: () => http.get('/thong-ke/nam').then(r => r.data),
}
