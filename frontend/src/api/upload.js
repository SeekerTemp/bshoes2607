import { http } from './http'

export const uploadApi = {
  image: (file) => {
    const fd = new FormData()
    fd.append('file', file)
    // Content-Type undefined -> browser sets 'multipart/form-data; boundary=...'.
    // Hardcoding it (or the http default application/json) drops the boundary and
    // Spring rejects the upload with "no multipart boundary was found".
    return http.post('/upload', fd, { headers: { 'Content-Type': undefined } }).then(r => r.data)
  },
}
