// TODO(api): swap useCrud(mock) for nhanVienApi calls when the backend runs.
import { useCrud } from './useCrud'
import { nhanVien } from '../mock/data'

export function useNhanVien() {
  return useCrud(nhanVien, { searchKeys: ['ma', 'ten', 'taiKhoan'], codePrefix: 'NV', codeField: 'ma' })
}
