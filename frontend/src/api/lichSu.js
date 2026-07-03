import { http } from './http'

export const lichSuApi = {
  findAll: () => http.get('/lich-su-hoa-don').then(r => r.data),
  findById: (id) => http.get(`/lich-su-hoa-don/${id}`).then(r => r.data),
}
