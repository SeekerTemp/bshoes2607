// TODO(api): swap useCrud(mock) for phieuGiamGiaApi calls when the backend runs.
import { useCrud } from './useCrud'
import { phieuGiamGia } from '../mock/data'

export function usePhieuGiamGia() {
  return useCrud(phieuGiamGia, { searchKeys: ['ma', 'ten'], codePrefix: 'PGG', codeField: 'ma' })
}
