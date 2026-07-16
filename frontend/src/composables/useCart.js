// Giỏ hàng của khách — sống ở trình duyệt, không có bảng gio_hang dưới DB.
// Kho chỉ bị trừ khi khách bấm đặt hàng (POST /gio-hang/checkout), không phải
// lúc thêm vào giỏ — khác với POS ở quầy (giữ hàng ngay khi thêm).
import { ref, computed } from 'vue'

const KEY = 'bshoes_cart'
// module-level: mọi component dùng chung một giỏ
const items = ref(JSON.parse(localStorage.getItem(KEY) || '[]'))

function persist() {
  localStorage.setItem(KEY, JSON.stringify(items.value))
}

export function useCart() {
  const soLuong = computed(() => items.value.reduce((s, i) => s + i.soLuong, 0))
  const tongTien = computed(() => items.value.reduce((s, i) => s + i.gia * i.soLuong, 0))

  /** p: { id, ten, gia, img, mauSize, ton } — id là id_san_pham_chi_tiet. */
  function add(p, n = 1) {
    const line = items.value.find(i => i.id === p.id)
    const dangCo = line ? line.soLuong : 0
    if (p.ton != null && dangCo + n > p.ton) {
      return { ok: false, message: `Chỉ còn ${p.ton} sản phẩm trong kho.` }
    }
    if (line) line.soLuong += n
    else items.value.push({ id: p.id, ten: p.ten, gia: p.gia, img: p.img, mauSize: p.mauSize || '', ton: p.ton, soLuong: n })
    persist()
    return { ok: true }
  }

  function setQty(id, n) {
    const line = items.value.find(i => i.id === id)
    if (!line) return { ok: false }
    if (n <= 0) return remove(id)
    if (line.ton != null && n > line.ton) return { ok: false, message: `Chỉ còn ${line.ton} sản phẩm trong kho.` }
    line.soLuong = n
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
