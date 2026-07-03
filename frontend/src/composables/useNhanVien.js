// Wired to /api/nhan-vien with a mock fallback when the backend is offline.
import { useCrud } from './useCrud'
import { nhanVienApi } from '../api/nhanVien'
import { nhanVien } from '../mock/data'

export function useNhanVien() {
  return useCrud(nhanVienApi, nhanVien, { searchKeys: ['ma', 'ten', 'taiKhoan'], codePrefix: 'NV', codeField: 'ma' })
}
