// Giỏ hàng của khách — sống ở trình duyệt, không có bảng gio_hang dưới DB.
// Kho chỉ bị trừ khi khách bấm đặt hàng (POST /gio-hang/checkout), không phải
// lúc thêm vào giỏ — khác với POS ở quầy (giữ hàng ngay khi thêm).
import { ref, computed } from 'vue'

const KEY = 'bshoes_cart'

// Reads the persisted cart defensively. This runs at MODULE LOAD time, so an
// unguarded JSON.parse on a corrupted/half-written localStorage value would
// throw during import and take the whole SPA down with a blank page — a broken
// cart must degrade to an empty cart, never to a dead app.
export function readCart() {
  try {
    const parsed = JSON.parse(localStorage.getItem(KEY) || '[]')
    if (!Array.isArray(parsed)) return []
    // Drop rows that lost their id or quantity; a NaN soLuong would poison every
    // total on the screen.
    return parsed.filter(i => i && i.id != null && Number.isFinite(Number(i.soLuong)) && Number(i.soLuong) > 0)
  } catch {
    return []
  }
}

// module-level: mọi component dùng chung một giỏ
const items = ref(readCart())

function persist() {
  try {
    localStorage.setItem(KEY, JSON.stringify(items.value))
  } catch {
    // quota / private mode — keep the in-memory cart working
  }
}

export function useCart() {
  // Number(...) || 0 throughout: one bad row must not turn the badge and the
  // order total into NaN.
  const soLuong = computed(() => items.value.reduce((s, i) => s + (Number(i.soLuong) || 0), 0))
  const tongTien = computed(() => items.value.reduce((s, i) => s + (Number(i.gia) || 0) * (Number(i.soLuong) || 0), 0))

  /** p: { id, ten, gia, img, mauSize, ton } — id là id_san_pham_chi_tiet. */
  function add(p, n = 1) {
    if (!p || p.id == null) return { ok: false, message: 'Sản phẩm không hợp lệ.' }
    const soLuongThem = Math.floor(Number(n) || 0)
    if (soLuongThem <= 0) return { ok: false, message: 'Số lượng phải lớn hơn 0.' }
    if (p.ton != null && p.ton <= 0) return { ok: false, message: 'Sản phẩm đã hết hàng.' }

    const line = items.value.find(i => i.id === p.id)
    const dangCo = line ? Number(line.soLuong) || 0 : 0
    if (p.ton != null && dangCo + soLuongThem > p.ton) {
      return { ok: false, message: `Chỉ còn ${p.ton} sản phẩm trong kho.` }
    }
    if (line) {
      line.soLuong = dangCo + soLuongThem
      // Refresh the stock snapshot: the line may have been sitting in the cart
      // since before a restock, and setQty() clamps against this copy.
      if (p.ton != null) line.ton = p.ton
    } else {
      items.value.push({
        id: p.id, ten: p.ten, gia: p.gia, img: p.img,
        mauSize: p.mauSize || '', ton: p.ton, soLuong: soLuongThem,
      })
    }
    persist()
    return { ok: true }
  }

  function setQty(id, n) {
    const line = items.value.find(i => i.id === id)
    if (!line) return { ok: false, message: 'Không tìm thấy dòng hàng.' }
    const soLuongMoi = Math.floor(Number(n) || 0)
    if (soLuongMoi <= 0) return remove(id)
    if (line.ton != null && soLuongMoi > line.ton) {
      return { ok: false, message: `Chỉ còn ${line.ton} sản phẩm trong kho.` }
    }
    line.soLuong = soLuongMoi
    persist()
    return { ok: true }
  }

  function remove(id) {
    items.value = items.value.filter(i => i.id !== id)
    persist()
    return { ok: true }
  }

  function clear() {
    items.value = []
    persist()
  }

  return { items, soLuong, tongTien, add, setQty, remove, clear }
}
