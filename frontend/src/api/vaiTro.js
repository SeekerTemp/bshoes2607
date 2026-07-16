import { http } from './http'

export const vaiTroApi = {
  findAll: () => http.get('/vai-tro').then(r => r.data),
  quyen: () => http.get('/vai-tro/quyen').then(r => r.data),
  updateQuyen: (id, quyen) => http.put(`/vai-tro/${id}/quyen`, { quyen }).then(r => r.data),
}
