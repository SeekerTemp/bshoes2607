// TODO(api): swap mock data / local cart math for hoaDonApi calls when the backend runs.
import { ref, computed } from 'vue'
import { posSanPham, posKhachHang, posVouchers } from '../mock/data'

export function useHoaDon() {
  const sanPham = ref(JSON.parse(JSON.stringify(posSanPham)))
  const khachHangOptions = ref([...posKhachHang])
  const voucherOptions = ref(JSON.parse(JSON.stringify(posVouchers)))

  const keyword = ref('')
  const khachHang = ref('Khách lẻ')
  const voucher = ref(0)
  const gio = ref([])
  let seq = 0

  const ketQua = computed(() => {
    const k = keyword.value.toLowerCase()
    return sanPham.value.filter(p => p.ten.toLowerCase().includes(k))
  })

  const tamTinh = computed(() => gio.value.reduce((s, l) => s + l.gia * l.soLuong, 0))
  const giamGia = computed(() => {
    if (voucher.value === 0) return 0
    return voucher.value < 1 ? Math.round(tamTinh.value * voucher.value) : voucher.value
  })
  const phaiTra = computed(() => Math.max(0, tamTinh.value - giamGia.value))

  function addLine(p) {
    const found = gio.value.find(l => l.spId === p.id)
    if (found) found.soLuong++
    else gio.value.push({ id: ++seq, spId: p.id, ten: p.ten, mau: p.mau, size: p.size, gia: p.gia, soLuong: 1 })
  }
  function removeLine(l) {
    gio.value = gio.value.filter(x => x.id !== l.id)
  }
  function thanhToan() {
    gio.value = []
    voucher.value = 0
  }

  return {
    sanPham, khachHangOptions, voucherOptions,
    keyword, khachHang, voucher, gio, ketQua,
    tamTinh, giamGia, phaiTra,
    addLine, removeLine, thanhToan,
  }
}
