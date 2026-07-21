import { http } from './http'

export const uploadApi = {
  image: (file) => {
    const fd = new FormData()
    fd.append('file', file)
    return http.post('/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } }).then(r => r.data)
  },
}
