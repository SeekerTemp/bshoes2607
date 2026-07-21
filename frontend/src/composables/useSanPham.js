// Wired to /api/san-pham-ql (+ /api/thuong-hieu, /api/chat-lieu for the form
// selects) with a mock fallback when the backend is offline.
import { useCrud } from './useCrud'
import { sanPhamApi } from '../api/sanPham'
import { thuongHieuApi, chatLieuApi } from '../api/thuocTinh'
import { sanPham, thuongHieuList as mockThuongHieu, chatLieuList as mockChatLieu } from '../mock/data'

export function useSanPham() {
  const crud = useCrud(sanPhamApi, sanPham, { searchKeys: ['ma', 'ten'] })

  // The views consume these as plain arrays (thuongHieuList.map(...)), so keep
  // them as stable array references seeded from mock and filled in place from
  // the attribute endpoints when the backend is reachable.
  const thuongHieuList = [...mockThuongHieu]
  const chatLieuList = [...mockChatLieu]

  async function loadAttributes() {
    try {
      const th = await thuongHieuApi.findAll()
      thuongHieuList.splice(0, thuongHieuList.length, ...th.map(x => x.ten))
    } catch (e) { console.warn('API offline, using mock thuongHieu', e) }
    try {
      const cl = await chatLieuApi.findAll()
      chatLieuList.splice(0, chatLieuList.length, ...cl.map(x => x.ten))
    } catch (e) { console.warn('API offline, using mock chatLieu', e) }
  }
  loadAttributes()

  // san-pham-ql deletes by `ma` (soft delete), not numeric id.
  // Honest: propagate errors so the view can surface them (no silent local fallback).
  async function remove(ma) {
    await sanPhamApi.remove(ma)
    await crud.load()
  }

  return { ...crud, remove, thuongHieuList, chatLieuList }
}
