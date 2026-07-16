import { http } from './http'

// The 8 product-attribute catalogs (all full CRUD on the backend).
export const ATTR_TYPES = [
  { key: 'chat-lieu', label: 'Chất liệu', moTa: false },
  { key: 'mau-sac', label: 'Màu sắc', moTa: false },
  { key: 'kich-co', label: 'Kích cỡ', moTa: false },
  { key: 'kieu-co-giay', label: 'Kiểu cổ giày', moTa: false },
  { key: 'kieu-dang', label: 'Kiểu dáng', moTa: false },
  { key: 'kieu-day-giay', label: 'Kiểu dây giày', moTa: false },
  { key: 'thuong-hieu', label: 'Thương hiệu', moTa: true },
  { key: 'xuat-su', label: 'Xuất xứ', moTa: true },
]

/** Generic CRUD client for an attribute/category endpoint (AttributeDto / AttributeMoTaDto shaped). */
export function catalogApi(path) {
  return {
    findAll: () => http.get(`/${path}`).then(r => r.data),
    create: (e) => http.post(`/${path}`, e).then(r => r.data),
    update: (e) => http.put(`/${path}`, e).then(r => r.data),
    remove: (id) => http.delete(`/${path}/${id}`).then(r => r.data),
  }
}

export const loaiSanPhamApi = catalogApi('loai-san-pham')
