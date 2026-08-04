// POS — faithful to the NetBeans Pnl_6_qlHoaDon layout (see frontend/refui):
//   left  : product table + cart (with steppers/actions) + invoice-queue table
//   right : a tabbed panel — "Hóa đơn" (sell at counter) / "Đặt hàng" (delivery)
// Several invoices sit on the queue at once; selecting a row in the queue table
// loads that invoice into the editor. Cart math stays client-side; products load
// from /api/hoa-don/pos-products with a mock fallback when the backend is offline.
import { ref, computed, onMounted } from 'vue'
import { hoaDonApi } from '../api/hoaDon'
import { useAuth } from './useAuth'
import { posSanPham, posKhachHang, posHinhThuc, posHoaDonQueue } from '../mock/data'
import { tinhGiamGia } from '../utils/voucher'

const deep = v => JSON.parse(JSON.stringify(v))

export function useHoaDon() {
  const { user } = useAuth()
  const nvId = () => user.value?.id ?? null
  const sanPham = ref(deep(posSanPham))
  const khachHangOptions = ref([...posKhachHang])
  // Real phiếu giảm giá (id + rule) from the server. The mock `posVouchers` values
  // were discount ENCODINGS (0.1 / 50000), not row ids — sending those as
  // idPhieuGiamGia made the backend find no voucher and record giảm = 0 while the
  // screen showed a discount (customer charged a different total than displayed).
  const voucherOptions = ref([{ value: 0, label: 'Không' }])
  async function loadVouchers() {
    try {
      const rows = await hoaDonApi.vouchersActive()
      voucherOptions.value = [
        { value: 0, label: 'Không' },
        ...(rows || []).map(v => ({
          value: v.id, label: v.ten || v.ma,
          loai: v.loai, giaTri: Number(v.giaTri) || 0,
          donToiThieu: Number(v.donToiThieu) || 0, giamToiDa: Number(v.giamToiDa) || 0,
        })),
      ]
    } catch (e) {
      // No fake vouchers offline — offering one would promise a discount the
      // backend will not honour.
      console.warn('Không tải được phiếu giảm giá', e)
      voucherOptions.value = [{ value: 0, label: 'Không' }]
    }
  }
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

  // Maps one server invoice line (HoaDonChiTietDto) into the local cart-row shape,
  // enriching with product metadata (ma/mau/size/ton) looked up from the loaded catalogue.
  function chiTietLine(c) {
    const prod = sanPham.value.find(p => p.id === c.idSanPhamChiTiet) || {}
    return {
      id: c.id, spId: c.idSanPhamChiTiet, ma: prod.ma, ten: c.ten,
      mau: prod.mau, size: prod.size, gia: c.donGia, ton: prod.ton,
      soLuong: c.soLuong, trangThai: '-',
    }
  }

  // Maps a server HoaDonDto (a pending invoice, trang_thai = 0) into the local queue
  // row shape. `existing` (if given) is a local row already tracked by serverId — its
  // user-editable fields (khách hàng/voucher/hình thức/ghi chú/...) are left untouched;
  // only server-derived fields (mã/nhân viên/ngày tạo/trạng thái/giỏ) are refreshed.
  function serverDtoToRow(dto, existing) {
    const gioMapped = (dto.chiTiet || []).map(chiTietLine)
    if (existing) {
      existing.serverId = dto.id
      existing.ma = dto.ma || existing.ma
      existing.nhanVien = dto.nhanVien || existing.nhanVien
      existing.ngayTao = dto.ngayTao || existing.ngayTao
      existing.trangThai = dto.trangThai || existing.trangThai
      existing.gio = gioMapped
      return existing
    }
    return {
      id: dto.id, serverId: dto.id, ma: dto.ma || ('HD' + pad(dto.id)),
      nhanVien: dto.nhanVien || '', khachHang: dto.khach || 'Khách lẻ',
      sdt: dto.soDienThoai || '', diaChi: dto.diaChi || '',
      trangThai: dto.trangThai || 'Chờ', trangThaiHang: '-',
      ngayTao: dto.ngayTao || '',
      voucher: 0, hinhThuc: dto.phuongThucThanhToan || 'Tiền mặt',
      khachDua: 0, memberCode: '', phiShip: Number(dto.phiShip) || 0, ghiChu: dto.ghiChu || '',
      idKhachHang: null,
      gio: gioMapped,
    }
  }

  // Sync the queue with the server's pending invoices — the source of truth for
  // "which invoices are still open". Existing local rows are updated in place (by
  // serverId) so in-progress edits survive; rows whose server invoice is no longer
  // pending (paid/cancelled elsewhere) are dropped — this is how a just-paid invoice
  // disappears from the queue (see checkout()). Purely local rows not yet persisted
  // (serverId still null) are left alone. On failure (offline), the local queue is
  // kept as-is for DISPLAY only — this never masks a failed sale, since checkout()'s
  // own success/failure is decided independently of this refresh.
  async function loadQueue() {
    try {
      const list = await hoaDonApi.cart()
      const serverIds = new Set(list.map(d => d.id))
      for (const dto of list) {
        const existing = hoaDons.value.find(h => h.serverId === dto.id)
        if (existing) serverDtoToRow(dto, existing)
        else hoaDons.value.push(serverDtoToRow(dto, null))
      }
      hoaDons.value = hoaDons.value.filter(h => !h.serverId || serverIds.has(h.serverId))
      if (hoaDons.value.length === 0) { createHoaDon(); return }
      if (!hoaDons.value.some(h => h.id === activeId.value)) {
        activeId.value = hoaDons.value[0].id
      }
    } catch (e) {
      console.warn('loadQueue offline — keeping local invoice queue as-is', e)
    }
  }

  // ---- products ----
  // True while `sanPham` holds the mock catalogue because /pos-products failed,
  // so the POS screen can say so instead of letting a cashier ring up
  // non-existent stock (see DemoDataBanner).
  const isDemo = ref(false)
  async function load() {
    try {
      const rows = await hoaDonApi.posProducts()
      // Use the variant's REAL ma_san_pham_chi_tiet. This used to synthesise
      // 'SP1', 'SP2'… by row index, so "Nhập mã" / QR then looked up
      // /san-pham-chi-tiet/by-ma/sp1 and always got 404.
      sanPham.value = rows.map(p => ({ ...p, ma: p.ma || '' }))
      isDemo.value = false
    } catch (e) {
      console.warn('API offline, using mock', e)
      sanPham.value = deep(posSanPham)
      isDemo.value = true
    }
  }
  onMounted(() => { load(); loadQueue(); loadVouchers() })

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
    inv.gio = (dto.chiTiet || []).map(chiTietLine)
  }

  // Re-fetch `inv` from the server and rebuild its local cart from that truth.
  // Used whenever a mutation fails partway (partial checkout, rejected qty change)
  // so the cart shown to the cashier never lies about what the server actually holds.
  async function resyncCartFromServer(inv) {
    if (!inv.serverId) return
    try {
      const dto = await hoaDonApi.findById(inv.serverId)
      syncCartFromServer(inv, dto)
      await load()
    } catch (e) {
      console.warn('resyncCartFromServer failed — cart may be stale', e)
    }
  }

  async function addLine(p) {
    if (!p || p.ton <= 0) return   // sold out — let the caller report "hết hàng"; never call the API
    const inv = active.value
    const sid = await ensureServerInvoice(inv)
    if (!sid) {                       // offline — local-only cart
      const found = inv.gio.find(l => l.spId === p.id)
      if (found) { if (found.soLuong < p.ton) found.soLuong++ }
      else if (p.ton > 0) inv.gio.push({ id: ++seqLine, spId: p.id, ma: p.ma, ten: p.ten, mau: p.mau, size: p.size, gia: p.gia, ton: p.ton, soLuong: 1, trangThai: '-' })
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
  // Returns a distinguished result so the caller can toast the right thing instead
  // of collapsing "code not found" and "found but out of stock" into one message:
  //   not found         -> { ok:false, reason:'not_found' }
  //   found, ton <= 0    -> { ok:false, reason:'out_of_stock', ten }
  //   addItem/API failed -> { ok:false, reason:'error', message, ten }
  //   success            -> { ok:true, ten }
  async function scanAdd(ma) {
    let p
    try {
      p = await hoaDonApi.scanByMa(ma)
    } catch (e) {
      if (e?.response?.status === 404) return { ok: false, reason: 'not_found' }
      return { ok: false, reason: 'error', message: e?.response?.data?.message || e?.message }
    }
    if (!p) return { ok: false, reason: 'not_found' }
    if (p.ton <= 0) return { ok: false, reason: 'out_of_stock', ten: p.ten }
    try {
      await addLine(p)
      return { ok: true, ten: p.ten }
    } catch (e) {
      return { ok: false, reason: 'error', message: e?.response?.data?.message || e?.message, ten: p.ten }
    }
  }
  function selectLine(l) { selectedLineId.value = l.id }

  // Returns { ok:true } or { ok:false, message } — never swallows a backend
  // rejection: on failure the local cart is re-synced from server truth (so a
  // rejected quantity is never left displayed) and the caller can toast `message`.
  async function setQty(l, qty) {
    const inv = active.value
    if (!inv.serverId || typeof l.id !== 'number') {   // offline / local line
      if (qty <= 0) { removeLineLocal(l) } else { l.soLuong = Math.min(qty, l.ton || qty) }
      return { ok: true }
    }
    try {
      const dto = await hoaDonApi.updateItem(l.id, qty)
      syncCartFromServer(inv, dto)
      await load()
      return { ok: true }
    } catch (e) {
      await resyncCartFromServer(inv)
      return { ok: false, error: e, message: e?.response?.data?.message || e?.message || 'Không thể cập nhật số lượng' }
    }
  }
  function inc(l) { return setQty(l, (l.soLuong || 0) + 1) }
  function dec(l) { return l.soLuong > 1 ? setQty(l, l.soLuong - 1) : Promise.resolve({ ok: true }) }
  function clampLine(l) {
    let q = Number(l.soLuong) || 1
    if (q < 1) q = 1
    if (typeof l.ton === 'number' && q > l.ton) q = l.ton
    return setQty(l, q)
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
  // NOTE: this only toggles a LABEL on the line. It does not call the backend and
  // does not put stock back — returning goods on a paid invoice goes through
  // POST /hoa-don/{id}/tra-hang (nút "Hoàn trả" ở tab Đặt hàng / màn Giao Hàng).
  // Tracked as F-POS-07 in docs/checklist/feature-backlog.csv.
  function hoanTra(l) { l.trangThai = l.trangThai === 'Hoàn trả' ? '-' : 'Hoàn trả' }

  // ---- money (active invoice) ----
  // Number(...) || 0: a single line with a missing price must not turn the whole
  // bill into NaN at the counter.
  const soLuong = computed(() => active.value.gio.reduce((s, l) => s + (Number(l.soLuong) || 0), 0))
  const tamTinh = computed(() =>
    active.value.gio.reduce((s, l) => s + (Number(l.gia) || 0) * (Number(l.soLuong) || 0), 0))
  // Delegates to utils/voucher.js, which mirrors the backend function
  // tinh_tien_giam_gia (used by HoaDonServiceImpl.thanhToan) so the total shown
  // at the counter equals the total the server records.
  const giamGia = computed(() => {
    const id = Number(active.value.voucher) || 0
    if (id === 0) return 0
    return tinhGiamGia(tamTinh.value, voucherOptions.value.find(o => o.value === id))
  })
  const phiShip = computed(() => orderTab.value === 'dathang' ? (Number(active.value.phiShip) || 0) : 0)
  const phaiTra = computed(() => Math.max(0, tamTinh.value - giamGia.value + phiShip.value))
  const tienThua = computed(() => Math.max(0, (Number(active.value.khachDua) || 0) - phaiTra.value))

  // ---- queue footer: totals for today ----
  const tongHoaDonHomNay = computed(() => hoaDons.value.length)
  const tongTienHomNay = computed(() =>
    hoaDons.value.reduce((s, h) =>
      s + h.gio.reduce((a, l) => a + (Number(l.gia) || 0) * (Number(l.soLuong) || 0), 0), 0)
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
    const voucher = Number(active.value.voucher) || 0
    const inv = active.value
    try {
      let invId = inv.serverId
      if (!invId) {                                   // offline-created invoice: create + fill now
        const created = await hoaDonApi.createEmpty(payment.idNhanVien ?? nvId())
        invId = created.id
        inv.serverId = created.id
        if (created.ma) inv.ma = created.ma
        for (const l of lines) {
          try {
            await hoaDonApi.addItem(invId, { idSanPhamChiTiet: l.spId, soLuong: l.soLuong })
          } catch (e) {
            // A line failed partway through this loop (e.g. out of stock). Earlier
            // lines in the SAME loop are already persisted server-side (their stock
            // already decremented) — `inv.serverId` is now set, so a naive retry would
            // skip this create+fill branch entirely and go straight to thanh-toan,
            // silently finalizing a sale missing this line. Instead: stop (never call
            // thanh-toan here), and re-sync the local cart from the server so it shows
            // EXACTLY what was actually persisted. A subsequent retry then sees
            // `invId` already set, skips this loop, and pays for precisely the
            // (now honestly displayed, possibly truncated) server cart — never a
            // silently-truncated one.
            await resyncCartFromServer(inv)
            await loadQueue()
            return {
              ok: false, error: e, reason: 'partial',
              failedLine: l.ma || l.ten,
              message: e?.response?.data?.message || e?.message,
            }
          }
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
        idPhieuGiamGia: voucher > 0 ? voucher : null,
        phiShip: giaoHang ? (Number(active.value.phiShip) || 0) : 0,
        giaoHang,
        ...payment,
      })
      if (paid?.ma) active.value.ma = paid.ma
      thanhToan()
      await load()               // refresh product list so reduced stock shows
      await loadQueue()          // paid invoice is no longer pending — it drops out of the queue
      return { ok: true, online: true, invoice: paid }
    } catch (e) {
      // Honest failure: never apply a local-only payment or claim success here.
      // The caller (UI) must see ok:false and the real error and let the cashier retry.
      console.error('Checkout failed — sale NOT completed', e)
      return { ok: false, error: e }
    }
  }

  return {
    // catalogue + option lists
    sanPham, khachHangOptions, voucherOptions, hinhThucOptions, isDemo, load,
    // ui state
    keyword, ketQua, orderTab, leftTab,
    // queue
    hoaDons, activeId, active, createHoaDon, switchHoaDon, removeHoaDon, loadQueue,
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
