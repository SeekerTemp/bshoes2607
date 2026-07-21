// Wired to /api/khach-hang with a mock fallback when the backend is offline.
import { useCrud } from './useCrud'
import { khachHangApi } from '../api/khachHang'
import { khachHang } from '../mock/data'

export function useKhachHang() {
  return useCrud(khachHangApi, khachHang, { searchKeys: ['ma', 'ten', 'sdt'] })
}
