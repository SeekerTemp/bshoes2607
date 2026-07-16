// POS — faithful to the NetBeans Pnl_6_qlHoaDon layout (see frontend/refui):
//   left  : product table + cart (with steppers/actions) + invoice-queue table
//   right : a tabbed panel — "Hóa đơn" (sell at counter) / "Đặt hàng" (delivery)
// Several invoices sit on the queue at once; selecting a row in the queue table
// loads that invoice into the editor. Cart math stays client-side; products load
// from /api/hoa-don/pos-products with a mock fallback when the backend is offline.
import { ref, computed, onMounted } from 'vue'
import { hoaDonApi } from '../api/hoaDon'
import { useAuth } from './useAuth'
import { posSanPham, posKhachHang, posVouchers, posHinhThuc, posHoaDonQueue } from '../mock/data'

const deep = v => JSON.parse(JSON.stringify(v))

export function useHoaDon() {
  const { user } = useAuth()
  const nvId = () => user.value?.id ?? null
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
  // Creates a new invoice tab. The local row appears instantly (so the queue never
  // empties); in the background it persists a real pending hoa_don (trang_thai=0) and
  // patches in the server id + ma. checkout() reuses that serverId (no duplicate).
  function createHoaDon() {
    seqHD++
    const hd = {
      id: seqHD, serverId: null, ma: 'HD' + pad(seqHD), nhanVien: 'admin', khachHang: 'Khách lẻ',
      sdt: '', diaChi: '', trangThai: 'Đang tạo', trangThaiHang: '-',
      ngayTao: posHoaDonQueue[0]?.ngayTao || '',
      voucher: 0, hinhThuc: 'Tiền mặt', khachDua: 0, memberCode: '', phiShip: 0, ghiChu: '',
      gio: [],
    }
    hoaDons.value.push(hd)
    activeId.value = hd.id
    selectedLineId.value = null
    hoaDonApi.createEmpty(nvId())
      .then(inv => { hd.serverId = inv.id; if (inv.ma) hd.ma = inv.ma })
      .catch(e => console.warn('createEmpty offline — local-only invoice', e))
    return hd
  }
  function switchHoaDon(id) {
    activeId.value = id
    selectedLineId.value = null
  }
  function removeHoaDon(id) {
    const idx = hoaDons.value.findIndex(h => h.id === id)
    if (idx === -1) return
    const inv = hoaDons.value[idx]
    // a still-pending server invoice must be cancelled so its held stock is restored
    if (inv.serverId && inv.trangThai !== 'Hoàn thành') {
      hoaDonApi.huyHoaDon(inv.serverId).catch(e => console.warn('huy offline', e))
    }
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

  // ---- cart (SERVER-BACKED: stock is decremented on the server the moment an item
  //      is added — matching the legacy NetBeans behaviour. Each mutation calls the
  //      backend, then rebuilds the local cart from the returned invoice. Falls back
  //      to a purely local cart when the API is offline. ----
  const gio = computed(() => active.value.gio)
  const selectedLine = computed(() => active.value.gio.find(l => l.id === selectedLineId.value) || null)

  // Ensure the active invoice is persisted (trang_thai=0) so items can attach to it.
  async function ensureServerInvoice(inv) {
    if (inv.serverId) return inv.serverId
    try {
      const s = await hoaDonApi.createEmpty(nvId())
      inv.serverId = s.id
      if (s.ma) inv.ma = s.ma
      return s.id
    } catch (e) { return null }
  }

  // Rebuild the local cart from a server HoaDonDto, enriching each line with product
  // metadata (mau/size/ton) looked up from the loaded product list.
  function syncCartFromServer(inv, dto) {
    inv.gio = (dto.chiTiet || []).map(c => {
      const prod = sanPham.value.find(p => p.id === c.idSanPhamChiTiet) || {}
      return {
        id: c.id, spId: c.idSanPhamChiTiet, ma: prod.ma, ten: c.ten,
        mau: prod.mau, size: prod.size, gia: c.donGia, ton: prod.ton,
        soLuong: c.soLuong, trangThai: '-',
      }
    })
  }

  async function addLine(p) {
    const inv = active.value
    const sid = await ensureServerInvoice(inv)
    if (!sid) {                       // offline — local-only cart
      const found = inv.gio.find(l => l.spId === p.id)
      if (found) { if (found.soLuong < p.ton) found.soLuong++ }
      else inv.gio.push({ id: ++seqLine, spId: p.id, ma: p.ma, ten: p.ten, mau: p.mau, size: p.size, gia: p.gia, ton: p.ton, soLuong: 1, trangThai: '-' })
      return
    }
    try {
      const dto = await hoaDonApi.addItem(sid, { idSanPhamChiTiet: p.id, soLuong: 1 })
      syncCartFromServer(inv, dto)
      await load()                    // refresh product list → decremented stock shows
    } catch (e) {
      console.warn('addItem failed (đủ tồn?) — bỏ qua', e)
      throw e
    }
  }
  async function scanAdd(ma) {
    try {
      const p = await hoaDonApi.scanByMa(ma)
      if (p) { await addLine(p); return { ok: true, ten: p.ten } }
      return { ok: false }
    } catch (e) {
      return { ok: false, error: e }
    }
  }
  function selectLine(l) { selectedLineId.value = l.id }

  async function setQty(l, qty) {
    const inv = active.value
    if (!inv.serverId || typeof l.id !== 'number') {   // offline / local line
      if (qty <= 0) { removeLineLocal(l) } else { l.soLuong = Math.min(qty, l.ton || qty) }
      return
    }
    try {
      const dto = await hoaDonApi.updateItem(l.id, qty)
      syncCartFromServer(inv, dto)
      await load()
    } catch (e) { console.warn('updateItem failed', e) }
  }
  function inc(l) { setQty(l, (l.soLuong || 0) + 1) }
  function dec(l) { if (l.soLuong > 1) setQty(l, l.soLuong - 1) }
  function clampLine(l) {
    let q = Number(l.soLuong) || 1
    if (q < 1) q = 1
    setQty(l, q)
  }
  function removeLineLocal(l) {
    active.value.gio = active.value.gio.filter(x => x.id !== l.id)
    if (selectedLineId.value === l.id) selectedLineId.value = null
  }
  async function removeLine(l) {
    const inv = active.value
    if (inv.serverId && typeof l.id === 'number') {
      try {
        const dto = await hoaDonApi.removeItem(l.id)
        syncCartFromServer(inv, dto)
        await load()
        if (selectedLineId.value === l.id) selectedLineId.value = null
        return
      } catch (e) { console.warn('removeItem failed', e) }
    }
    removeLineLocal(l)
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

  // Finalize payment. Items were already added (and stock decremented) at add-to-cart,
  // so with a serverId we just call thanh-toan. Offline-created invoices are created +
  // filled here. Delivery orders ("Đặt hàng" tab) carry phí ship + go to "Chờ giao".
  async function checkout(payment = {}) {
    const lines = active.value.gio
    if (!lines.length) return { ok: false, reason: 'empty' }
    const giaoHang = orderTab.value === 'dathang'
    try {
      let invId = active.value.serverId
      if (!invId) {                                   // offline-created invoice: create + fill now
        const inv = await hoaDonApi.createEmpty(payment.idNhanVien ?? nvId())
        invId = inv.id
        active.value.serverId = inv.id
        if (inv.ma) active.value.ma = inv.ma
        for (const l of lines) {
          await hoaDonApi.addItem(invId, { idSanPhamChiTiet: l.spId, soLuong: l.soLuong })
        }
      }
      const paid = await hoaDonApi.thanhToan(invId, {
        phuongThucThanhToan: active.value.hinhThuc,
        tenNguoiNhan: active.value.khachHang,
        soDienThoai: active.value.sdt,
        diaChi: active.value.diaChi,
        ghiChu: active.value.ghiChu,
        idKhachHang: active.value.idKhachHang || null,
        idNhanVien: nvId(),
        phiShip: giaoHang ? (Number(active.value.phiShip) || 0) : 0,
        giaoHang,
        ...payment,
      })
      if (paid?.ma) active.value.ma = paid.ma
      thanhToan()
      await load()               // refresh product list so reduced stock shows
      return { ok: true, online: true, invoice: paid }
    } catch (e) {
      console.warn('Checkout API failed — applying local-only payment', e)
      thanhToan()
      return { ok: true, online: false, error: e }
    }
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
    thanhToan, checkout,
  }
}
