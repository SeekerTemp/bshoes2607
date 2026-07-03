// Wired to /api/phieu-giam-gia with a mock fallback when the backend is offline.
import { useCrud } from './useCrud'
import { phieuGiamGiaApi } from '../api/phieuGiamGia'
import { phieuGiamGia } from '../mock/data'

export function usePhieuGiamGia() {
  return useCrud(phieuGiamGiaApi, phieuGiamGia, { searchKeys: ['ma', 'ten'], codePrefix: 'PGG', codeField: 'ma' })
}
