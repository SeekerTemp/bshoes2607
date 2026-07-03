import { http } from './http'

// Attribute lists for the product form (AttributeDto { id, ma, ten, trangThai }).
export const thuongHieuApi = {
  findAll: () => http.get('/thuong-hieu').then(r => r.data),
}

export const chatLieuApi = {
  findAll: () => http.get('/chat-lieu').then(r => r.data),
}
