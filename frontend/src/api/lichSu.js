import { http } from './http'

export const lichSuApi = {
  findAll: () => http.get('/lich-su-hoa-don').then(r => r.data),
  cancelled: () => http.get('/lich-su-hoa-don/cancelled').then(r => r.data),
  byDate: (params) => http.get('/lich-su-hoa-don/by-date', { params }).then(r => r.data),
  findById: (id) => http.get(`/lich-su-hoa-don/${id}`).then(r => r.data),
}
