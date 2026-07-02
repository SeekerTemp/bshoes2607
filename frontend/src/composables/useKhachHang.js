// TODO(api): swap useCrud(mock) for khachHangApi calls when the backend runs.
import { useCrud } from './useCrud'
import { khachHang } from '../mock/data'

export function useKhachHang() {
  return useCrud(khachHang, { searchKeys: ['ma', 'ten', 'sdt'], codePrefix: 'KH', codeField: 'ma' })
}
