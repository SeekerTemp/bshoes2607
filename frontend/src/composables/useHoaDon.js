// POS — faithful to the NetBeans Pnl_6_qlHoaDon layout (see frontend/refui):
//   left  : product table + cart (with steppers/actions) + invoice-queue table
//   right : a tabbed panel — "Hóa đơn" (sell at counter) / "Đặt hàng" (delivery)
// Several invoices sit on the queue at once; selecting a row in the queue table
// loads that invoice into the editor. Cart math stays client-side; products load
// from /api/hoa-don/pos-products with a mock fallback when the backend is offline.
import { ref, computed, onMounted } from 'vue'
import { hoaDonApi } from '../api/hoaDon'
import { posSanPham, posKhachHang, posVouchers, posHinhThuc, posHoaDonQueue } from '../mock/data'

const deep = v => JSON.parse(JSON.stringify(v))

export function useHoaDon() {
  const sanPham = ref(deep(posSanPham))
  const khachHangOptions = ref([...posKhachHang])
  const voucherOptions = ref(deep(posVouchers))
  const hinhThucOptions = [...posHinhThuc]

  const keyword = ref('')
  const orderTab = ref('hoadon')     // 'hoadon' | 'dathang'  (right panel tabs)
  const leftTab = ref('chitiet')     // 'chitiet' | 'thuoctinh' (left panel tabs)

  // ---- invoice queue (the "Danh sách hóa đơn" table) ----
  let seqHD = posHoaDonQueue.length
  let seqLine = 0
  const hoaDons = ref(deep(posHoaDonQueue).map((h, i) => ({ id: i + 1, ...h })))
  hoaDons.value.forEach(h => h.gio.forEach(l => { l.id = ++seqLine }))

  const activeId = ref(hoaDons.value[0].id)
  const active = computed(
    () => hoaDons.value.find(h => h.id === activeId.value) || hoaDons.value[0]
  )
  const selectedLineId = ref(null)

  function pad(n) { return String(n).padStart(3, '0') }
  function createHoaDon() {
    seqHD++
    const hd = {
      id: seqHD, ma: 'HD' + pad(seqHD), nhanVien: 'admin', khachHang: 'Khách lẻ',
      sdt: '', diaChi: '', trangThai: 'Đang tạo', trangThaiHang: '-',
      ngayTao: posHoaDonQueue[0]?.ngayTao || '',
      voucher: 0, hinhThuc: 'Tiền mặt', khachDua: 0, memberCode: '', phiShip: 0, ghiChu: '',
      gio: [],
    }
    hoaDons.value.push(hd)
    activeId.value = hd.id
    selectedLineId.value = null
  }
  function switchHoaDon(id) {
    activeId.value = id
    selectedLineId.value = null
  }
  function removeHoaDon(id) {
    const idx = hoaDons.value.findIndex(h => h.id === id)
    if (idx === -1) return
    hoaDons.value.splice(idx, 1)
    if (hoaDons.value.length === 0) { createHoaDon(); return }
    if (activeId.value === id) {
      activeId.value = hoaDons.value[Math.min(idx, hoaDons.value.length - 1)].id
    }
  }

  // ---- products ----
  async function load() {
    try {
      const rows = await hoaDonApi.posProducts()
      sanPham.value = rows.map((p, i) => ({ ma: 'SP' + (i + 1), ...p }))
    } catch (e) {
      console.warn('API offline, using mock', e)
      sanPham.value = deep(posSanPham)
    }
  }
  onMounted(load)

  const ketQua = computed(() => {
    const k = keyword.value.trim().toLowerCase()
    if (!k) return sanPham.value
    return sanPham.value.filter(
      p => p.ten.toLowerCase().includes(k) ||
           String(p.mau).toLowerCase().includes(k) ||
           String(p.ma).toLowerCase().includes(k)
    )
  })

  // ---- cart (operates on the active invoice) ----
  const gio = computed(() => active.value.gio)
  const selectedLine = computed(() => active.value.gio.find(l => l.id === selectedLineId.value) || null)

  function addLine(p) {
    const cart = active.value.gio
    const found = cart.find(l => l.spId === p.id)
    if (found) {
      if (found.soLuong < p.ton) found.soLuong++
    } else {
      cart.push({
        id: ++seqLine, spId: p.id, ma: p.ma, ten: p.ten, mau: p.mau,
        size: p.size, gia: p.gia, ton: p.ton, soLuong: 1, trangThai: '-',
      })
    }
  }
  async function scanAdd(ma) {
    try {
      const p = await hoaDonApi.scanByMa(ma)
      if (p) { addLine(p); return { ok: true, ten: p.ten } }
      return { ok: false }
    } catch (e) {
      return { ok: false, error: e }
    }
  }
  function selectLine(l) { selectedLineId.value = l.id }
  function inc(l) { if (l.soLuong < l.ton) l.soLuong++ }
  function dec(l) { if (l.soLuong > 1) l.soLuong-- }
  function clampLine(l) {
    if (!l.soLuong || l.soLuong < 1) l.soLuong = 1
    if (l.ton && l.soLuong > l.ton) l.soLuong = l.ton
  }
  function removeLine(l) {
    active.value.gio = active.value.gio.filter(x => x.id !== l.id)
    if (selectedLineId.value === l.id) selectedLineId.value = null
  }
  function hoanTra(l) { l.trangThai = l.trangThai === 'Hoàn trả' ? '-' : 'Hoàn trả' }

  // ---- money (active invoice) ----
  const soLuong = computed(() => active.value.gio.reduce((s, l) => s + l.soLuong, 0))
  const tamTinh = computed(() => active.value.gio.reduce((s, l) => s + l.gia * l.soLuong, 0))
  const giamGia = computed(() => {
    const v = Number(active.value.voucher) || 0
    if (v === 0) return 0
    return v < 1 ? Math.round(tamTinh.value * v) : v
  })
  const phiShip = computed(() => orderTab.value === 'dathang' ? (Number(active.value.phiShip) || 0) : 0)
  const phaiTra = computed(() => Math.max(0, tamTinh.value - giamGia.value + phiShip.value))
  const tienThua = computed(() => Math.max(0, (Number(active.value.khachDua) || 0) - phaiTra.value))

  // ---- queue footer: totals for today ----
  const tongHoaDonHomNay = computed(() => hoaDons.value.length)
  const tongTienHomNay = computed(() =>
    hoaDons.value.reduce((s, h) => s + h.gio.reduce((a, l) => a + l.gia * l.soLuong, 0), 0)
  )

  function thanhToan() {
    active.value.trangThai = 'Hoàn thành'
    active.value.trangThaiHang = 'Đã thanh toán'
  }

  return {
    // catalogue + option lists
    sanPham, khachHangOptions, voucherOptions, hinhThucOptions,
    // ui state
    keyword, ketQua, orderTab, leftTab,
    // queue
    hoaDons, activeId, active, createHoaDon, switchHoaDon, removeHoaDon,
    tongHoaDonHomNay, tongTienHomNay,
    // cart
    gio, selectedLineId, selectedLine, selectLine,
    addLine, removeLine, inc, dec, clampLine, hoanTra, scanAdd,
    // money
    soLuong, tamTinh, giamGia, phiShip, phaiTra, tienThua,
    // actions
    thanhToan,
  }
}
