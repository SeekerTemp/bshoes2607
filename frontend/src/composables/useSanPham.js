// TODO(api): swap useCrud(mock) for sanPhamApi calls when the backend runs.
import { useCrud } from './useCrud'
import { sanPham, thuongHieuList, chatLieuList } from '../mock/data'

export function useSanPham() {
  const crud = useCrud(sanPham, { searchKeys: ['ma', 'ten'], codePrefix: 'SP', codeField: 'ma' })
  return { ...crud, thuongHieuList, chatLieuList }
}
