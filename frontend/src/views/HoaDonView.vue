<script setup>
import { computed, ref } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import AppButton from '../components/ui/AppButton.vue'
import QrScanner from '../components/ui/QrScanner.vue'
import DemoDataBanner from '../components/ui/DemoDataBanner.vue'
import { useHoaDon } from '../composables/useHoaDon'
import { useToast } from '../composables/useToast'
import { vnd } from '../utils/format'
import { hoaDonApi } from '../api/hoaDon'
import { khachHangApi } from '../api/khachHang'
import { printReceipt as printReceiptDoc } from '../utils/receipt'
import { downloadJson, stamp } from '../utils/csv'

// The "Thuộc tính sản phẩm" tab has no panel behind it yet — flip to true once one
// exists (the button is hidden, `leftTab` stays wired up).
const SHOW_THUOC_TINH_TAB = false

const {
  sanPham, khachHangOptions, voucherOptions, hinhThucOptions, isDemo, load,
  loadKhachHang, chonKhachHang, chonKhachVangLai, chonHoiVien,
  keyword, ketQua, orderTab, leftTab,
  hoaDons, activeId, active, createHoaDon, switchHoaDon, removeHoaDon,
  tongHoaDonHomNay, tongTienHomNay,
  gio, selectedLineId, selectedLine, selectLine,
  addLine, removeLine, inc, dec, clampLine, hoanTra, scanAdd,
  bienTheOptions, doiBienThe, locTheoTen, xoaLoc,
  soLuong, tamTinh, giamGia, phiShip, phaiTra, tienThua,
  thanhToan, checkout,
} = useHoaDon()
const { notify } = useToast()

// ---- xuất / nhập JSON danh mục sản phẩm bán tại quầy ----
function xuatJsonSanPham() {
  if (!sanPham.value.length) { notify('Không có sản phẩm nào để xuất', 'warning'); return }
  const ok = downloadJson(`pos-san-pham-${stamp()}.json`, {
    exportedAt: new Date().toISOString(),
    count: sanPham.value.length,
    sanPham: sanPham.value,
  })
  notify(ok ? `Đã xuất ${sanPham.value.length} sản phẩm` : 'Không xuất được file', ok ? 'success' : 'warning')
}

const jsonInput = ref(null)
function triggerNhapJson() { jsonInput.value?.click() }

// Adds cart lines from a JSON list of { ma, soLuong }. Each code is resolved
// against the live catalogue, so an import can never invent stock that is not
// really there.
async function nhapJsonSanPham(ev) {
  const file = ev.target.files?.[0]
  if (!file) return
  ev.target.value = ''

  let items
  try {
    const parsed = JSON.parse(await file.text())
    items = Array.isArray(parsed) ? parsed : parsed?.sanPham
  } catch (e) {
    notify('File JSON không hợp lệ', 'warning')
    return
  }
  if (!Array.isArray(items) || !items.length) { notify('File JSON không có sản phẩm nào', 'warning'); return }

  let ok = 0
  const loi = []
  for (const [i, item] of items.entries()) {
    const ma = String(item?.ma ?? '').trim()
    const p = sanPham.value.find(x => String(x.ma) === ma || String(x.id) === ma)
    if (!p) { loi.push(`mục ${i + 1}: không tìm thấy mã ${ma || '(trống)'}`); continue }
    if (p.ton <= 0) { loi.push(`mục ${i + 1}: ${p.ten} đã hết hàng`); continue }
    const lan = Math.max(1, Number(item?.soLuong) || 1)
    try {
      for (let n = 0; n < lan; n++) await addLine(p)
      ok++
    } catch (e) {
      loi.push(`mục ${i + 1} (${p.ten}): ${e?.response?.data?.message || e?.message || 'lỗi'}`)
    }
  }
  if (ok) notify(`Đã thêm ${ok}/${items.length} dòng hàng từ file`, 'success')
  if (loi.length) notify(`${loi.length} mục lỗi — ${loi.slice(0, 3).join('; ')}`, 'warning')
}

const showScanner = ref(false)
async function onScan(text) {
  const r = await scanAdd(text)
  if (r.ok) { notify('Đã thêm: ' + r.ten, 'success'); return }
  if (r.reason === 'out_of_stock') { notify(`${r.ten} đã hết hàng`, 'warning'); return }
  if (r.reason === 'error') { notify(r.message || 'Không thể thêm sản phẩm', 'danger'); return }
  notify('Không tìm thấy mã: ' + text, 'warning')   // reason === 'not_found'
}

// ---- manual code entry ----
const showManual = ref(false)
const manualCode = ref('')
function openManual() { manualCode.value = ''; showManual.value = true }
async function confirmManual() {
  const code = manualCode.value.trim()
  if (!code) return
  await onScan(code)            // reuse the same lookup+add+toast path as scanning
  showManual.value = false
}

// ---- member lookup by phone ----
async function timHoiVien() {
  const q = (active.value.memberCode || '').trim()
  if (!q) { notify('Nhập SĐT hội viên', 'warning'); return }
  try {
    const list = await khachHangApi.search(q)
    const kh = (list || []).find(k => (k.sdt || '') === q) || (list || [])[0]
    if (!kh) { notify('Không tìm thấy hội viên với SĐT: ' + q, 'warning'); return }
    active.value.khachHang = kh.ten
    active.value.idKhachHang = kh.id
    active.value.sdt = kh.sdt
    notify('Đã gán khách: ' + kh.ten, 'success')
  } catch (e) {
    notify('Không tra cứu được hội viên (backend offline?)', 'warning')
  }
}

// Customer dropdown — options carry the khach_hang row id, so selecting a name
// binds id + tên + SĐT together (AppSelect emits the raw string value).
const khachHangSelectOptions = computed(() => [
  { value: 0, label: khachHangOptions.value.length ? '— Chọn khách hàng —' : '— Không tải được danh sách —' },
  ...khachHangOptions.value.map(k => ({ value: k.value, label: k.label })),
])
const khachHangModel = computed({
  get: () => active.value.idKhachHang || 0,
  set: v => { Number(v) ? chonKhachHang(v) : (active.value.idKhachHang = null) },
})

const voucherSelectOptions = computed(() =>
  voucherOptions.value.map(v => ({ value: v.value, label: v.label }))
)
const voucherModel = computed({
  get: () => active.value.voucher,
  set: v => { active.value.voucher = Number(v) },
})

// Adding a product narrows DANH SÁCH SẢN PHẨM to that product's name, so every
// other biến thể (màu / kích thước) of the same shoe is on screen ready to add too.
async function themVaLoc(p) {
  try {
    await addLine(p)
    locTheoTen(p.ten)
  } catch (e) {
    notify(e?.response?.data?.message || e?.message || 'Không thêm được sản phẩm', 'danger')
  }
}

// Swap a cart line to another available variant of the same product.
async function onDoiBienThe(l, ev) {
  const r = await doiBienThe(l, ev.target.value)
  if (!r.ok) {
    notify(r.message || 'Không đổi được biến thể', 'warning')
    ev.target.value = l.spId          // put the select back on the line's real variant
  }
}

function needLine(action) {
  if (!selectedLine.value) { notify('Chọn một dòng trong giỏ hàng trước', 'warning'); return false }
  return true
}
// setQty/inc/dec/clampLine resolve to { ok, message } — never silently swallow a
// backend rejection (e.g. "not enough stock"); the cart itself is already re-synced
// from server truth by the composable, this just surfaces the message.
async function bumpQty(fn, l) {
  const r = await fn(l)
  if (r && r.ok === false) notify(r.message || 'Không thể cập nhật số lượng', 'danger')
}
function tangSL() { if (needLine()) bumpQty(inc, selectedLine.value) }
function giamSL() { if (needLine()) bumpQty(dec, selectedLine.value) }
function boSanPham() { if (needLine()) removeLine(selectedLine.value) }
function hoanTraLine() { if (needLine()) hoanTra(selectedLine.value) }

// ---- payment (cash validation + VietQR for transfer + combined) ----
const showQR = ref(false)
const qrAmount = ref(0)
const qrLabel = ref('')
const qrUrl = computed(() =>
  `https://img.vietqr.io/image/970436-1234567890-compact2.png?amount=${qrAmount.value}&addInfo=${encodeURIComponent(active.value.ma || 'BShoes')}&accountName=BShoes`)

async function pay() {
  if (gio.value.length === 0) return
  const method = active.value.hinhThuc
  const phai = phaiTra.value
  const dua = Number(active.value.khachDua) || 0
  if (method === 'Tiền mặt') {
    if (dua < phai) { notify(`Khách đưa ${vnd(dua)} chưa đủ — cần ${vnd(phai)}`, 'warning'); return }
    await doCheckout(); return
  }
  if (method === 'Tiền mặt + Chuyển khoản') {
    if (dua < 0 || dua > phai) { notify(`Tiền mặt phải trong khoảng 0 – ${vnd(phai)}`, 'warning'); return }
    qrAmount.value = phai - dua
    qrLabel.value = `Tiền mặt ${vnd(dua)} · CK phần còn lại ${vnd(phai - dua)}`
    showQR.value = true
    return
  }
  // Chuyển khoản / Thẻ — full amount via QR
  qrAmount.value = phai
  qrLabel.value = ''
  showQR.value = true
}
async function confirmQR() { showQR.value = false; await doCheckout() }

// Double-submit guard: disables the pay buttons for the duration of a checkout
// (see :disabled on "Thanh toán" / "Giao hàng (thanh toán)").
const paying = ref(false)
async function doCheckout() {
  if (paying.value) return
  paying.value = true
  try {
    const snapshot = buildReceipt(true)   // capture cart/amounts before checkout can shift `active`
    const r = await checkout()
    if (r.ok) {
      // Success: honest confirmation — paid, printed, and the invoice is gone from
      // the pending queue (checkout() already refreshed it via loadQueue()).
      notify(`Đã thanh toán ${snapshot.ma}: ${vnd(snapshot.phaiTra)} — đã lưu DB, ghi lịch sử & trừ kho`, 'success')
      printReceipt(snapshot)
    } else {
      // Failure: no fake success — surface the real error, keep the cart/queue
      // untouched so the cashier can fix the issue (stock/voucher/etc.) and retry.
      const msg = r.error?.response?.data?.message || r.error?.message || 'Không thể thanh toán — vui lòng thử lại'
      notify(`Thanh toán ${snapshot.ma} thất bại: ${msg}`, 'danger')
    }
  } finally {
    paying.value = false
  }
}
function huy() {
  if (confirm(`Huỷ hoá đơn ${active.value.ma}?`)) removeHoaDon(active.value.id)
}

// ---- delivery (Đặt hàng tab) ----
async function giaoHang() {   // create a delivery order: pay + status "Chờ giao"
  if (gio.value.length === 0) { notify('Giỏ hàng trống', 'warning'); return }
  await doCheckout()
}
async function daGiaoAction() {
  const sid = active.value.serverId
  if (!sid) { notify('Đơn chưa lưu trên hệ thống', 'warning'); return }
  try { await hoaDonApi.daGiao(sid); active.value.trangThaiHang = 'Đã giao'; notify('Đã giao hàng ' + active.value.ma, 'success') }
  catch (e) { notify('Đơn phải ở trạng thái "Chờ giao" mới đánh dấu Đã giao', 'warning') }
}
async function traHangAction() {
  const sid = active.value.serverId
  if (!sid) { notify('Đơn chưa lưu trên hệ thống', 'warning'); return }
  if (!confirm(`Xác nhận trả hàng ${active.value.ma}? Kho sẽ được hoàn lại.`)) return
  try { await hoaDonApi.traHang(sid); active.value.trangThaiHang = 'Trả hàng'; notify('Đã trả hàng ' + active.value.ma, 'success') }
  catch (e) { notify('Không thể trả hàng (chỉ đơn đã bán/đã giao)', 'warning') }
}

// ---- quick add customer at the counter ----
const showAddKH = ref(false)
const newKH = ref({ ten: '', sdt: '', diaChi: '', email: '', gioiTinh: 'Nam' })
function openAddKH() { newKH.value = { ten: '', sdt: '', diaChi: '', email: '', gioiTinh: 'Nam' }; showAddKH.value = true }
async function saveKH() {
  if (!newKH.value.ten) { notify('Nhập tên khách hàng', 'warning'); return }
  try {
    const kh = await khachHangApi.create({ ...newKH.value })
    active.value.khachHang = kh?.ten || newKH.value.ten
    active.value.idKhachHang = kh?.id || null
    active.value.khachType = 'hoivien'
    if (kh?.sdt || newKH.value.sdt) active.value.sdt = kh?.sdt || newKH.value.sdt
    await loadKhachHang()          // the new row must appear in the dropdown too
    notify('Đã thêm khách hàng: ' + (kh?.ten || newKH.value.ten), 'success')
  } catch (e) {
    active.value.khachHang = newKH.value.ten
    notify('Lưu khách offline — gán tạm vào hóa đơn', 'warning')
  }
  showAddKH.value = false
}

// ---- receipt / print ----
// Maps the POS cart (`gio`) + active invoice into the normalized receipt shape
// consumed by utils/receipt.js (shared with LichSuView.vue / DonHangView.vue).
function buildReceipt(paid) {
  return {
    ma: active.value.ma,
    ngay: new Date().toLocaleString('vi-VN'),
    khach: active.value.khachHang || 'Khách lẻ',
    hinhThuc: active.value.hinhThuc,
    paid,
    items: gio.value.map(l => ({
      ten: l.ten,
      mota: [l.mau, l.size].filter(Boolean).join(' / ') || undefined,
      soLuong: l.soLuong,
      donGia: l.gia,
      thanhTien: l.gia * l.soLuong,
    })),
    tamTinh: tamTinh.value, giamGia: giamGia.value, phiShip: phiShip.value,
    phaiTra: phaiTra.value, khachDua: Number(active.value.khachDua) || 0, tienThua: tienThua.value,
  }
}
function printReceipt(data) {
  if (!data || !data.items.length) { notify('Giỏ hàng trống, không có gì để in', 'warning'); return }
  const ok = printReceiptDoc(data)
  if (!ok) notify('In hoá đơn thất bại — vui lòng thử lại', 'danger')
}
function inTamTinh() { printReceipt(buildReceipt(false)) }
function taoHoaDon() {
  const hd = createHoaDon()
  notify('Đã tạo hóa đơn mới: ' + hd.ma, 'success')
}
</script>

<template>
  <AppShell>
    <DemoDataBanner v-if="isDemo" what="danh mục sản phẩm bán tại quầy" @retry="load" />
    <PageHeader title="Bán hàng tại quầy" />

    <div class="pos-grid">
      <!-- ================= LEFT COLUMN (section_1) ================= -->
      <div class="pos-left">
        <!-- left content tabs -->
        <div class="pos-toptabs">
          <button class="pos-toptab" :class="{ active: leftTab === 'chitiet' }" @click="leftTab = 'chitiet'">Thông tin chi tiết</button>
          <!-- "Thuộc tính sản phẩm" hidden until it has a panel to switch to — it
               currently only flips `leftTab`, which nothing renders on. -->
          <button v-if="SHOW_THUOC_TINH_TAB" class="pos-toptab" :class="{ active: leftTab === 'thuoctinh' }" @click="leftTab = 'thuoctinh'">Thuộc tính sản phẩm</button>
        </div>

        <!-- Danh sách sản phẩm -->
        <section class="card pos-panel">
          <header class="pos-panel-head">
            <h6 class="pos-title">DANH SÁCH SẢN PHẨM</h6>
            <div class="d-flex align-items-center gap-2">
              <div class="input-group input-group-sm pos-search">
                <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                <input class="form-control" v-model="keyword" placeholder="Tìm theo tên / mã / màu" />
                <button v-if="keyword" class="btn btn-outline-secondary" title="Bỏ lọc — xem lại toàn bộ sản phẩm" @click="xoaLoc">
                  <i class="bi bi-x-lg"></i>
                </button>
              </div>
              <AppButton size="sm" variant="primary" icon="qr-code-scan" @click="showScanner = true">Quét QR</AppButton>
            </div>
          </header>
          <div class="table-scroll" style="height: 210px">
            <table class="table table-sm table-hover align-middle pos-table mb-0">
              <thead>
                <tr>
                  <th class="text-center">STT</th><th>Mã SP</th><th>Tên sp</th>
                  <th>Màu sắc</th><th>Kích thước</th><th class="text-end">Tồn kho</th><th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(p, i) in ketQua" :key="p.id" @dblclick="themVaLoc(p)">
                  <td class="text-center">{{ i + 1 }}</td>
                  <td>{{ p.ma }}</td>
                  <td class="fw-medium">
                    <div class="d-flex align-items-center gap-2">
                      <img v-if="p.imageUrl" :src="p.imageUrl" alt="" style="width:40px;height:40px;object-fit:contain;background:#f5f7fa;border-radius:6px" @error="e => e.target.style.display='none'" />
                      <span>{{ p.ten }}</span>
                    </div>
                  </td>
                  <td>{{ p.mau }}</td>
                  <td>{{ p.size }}</td>
                  <td class="text-end">{{ p.ton }}</td>
                  <td class="text-end">
                    <button class="btn btn-sm btn-success py-0 px-2" :disabled="p.ton === 0" title="Thêm vào giỏ" @click="themVaLoc(p)">
                      <i class="bi bi-plus-lg"></i>
                    </button>
                  </td>
                </tr>
                <tr v-if="ketQua.length === 0"><td colspan="7" class="text-center text-muted py-3">Không tìm thấy sản phẩm</td></tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- Giỏ hàng -->
        <section class="card pos-panel">
          <header class="pos-panel-head">
            <h6 class="pos-title">GIỎ HÀNG</h6>
            <span class="text-muted small">Số lượng: <b>{{ soLuong }}</b> • Tạm tính: <b>{{ vnd(tamTinh) }}</b></span>
          </header>
          <div class="table-scroll" style="height: 190px">
            <table class="table table-sm align-middle pos-table mb-0">
              <thead>
                <tr>
                  <th class="text-center">STT</th><th>Mã SP</th><th>Tên sp</th><th>Biến thể</th>
                  <th class="text-center">Số lượng</th><th class="text-end">Đơn giá</th>
                  <th class="text-end">Giảm giá</th><th class="text-end">Thành tiền</th><th>Trạng thái</th><th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(l, i) in gio" :key="l.id"
                    :class="{ 'row-selected': l.id === selectedLineId, 'row-return': l.trangThai === 'Hoàn trả' }"
                    @click="selectLine(l)">
                  <td class="text-center">{{ i + 1 }}</td>
                  <td>{{ l.ma }}</td>
                  <td class="fw-medium">{{ l.ten }}</td>
                  <td>
                    <!-- Quick variant swap: only biến thể of the SAME sản phẩm that
                         are still in stock (plus this line's own, always shown). -->
                    <select class="form-select form-select-sm bien-the"
                            :value="l.spId" @click.stop @change="onDoiBienThe(l, $event)">
                      <option v-for="o in bienTheOptions(l)" :key="o.value" :value="o.value">{{ o.label }}</option>
                    </select>
                  </td>
                  <td>
                    <div class="stepper">
                      <button class="btn btn-sm btn-outline-danger" @click.stop="bumpQty(dec, l)">−</button>
                      <input type="number" min="1" :max="l.ton" class="form-control form-control-sm text-center"
                             v-model.number="l.soLuong" @change="bumpQty(clampLine, l)" @click.stop>
                      <button class="btn btn-sm btn-outline-success" @click.stop="bumpQty(inc, l)">+</button>
                    </div>
                  </td>
                  <td class="text-end">{{ vnd(l.gia) }}</td>
                  <td class="text-end">{{ vnd(0) }}</td>
                  <td class="text-end fw-medium">{{ vnd(l.gia * l.soLuong) }}</td>
                  <td>{{ l.trangThai }}</td>
                  <td class="text-end"><button class="btn btn-sm btn-outline-danger py-0 px-1" title="Xoá" @click.stop="removeLine(l)"><i class="bi bi-x"></i></button></td>
                </tr>
                <tr v-if="gio.length === 0"><td colspan="10" class="text-center text-muted py-4">Giỏ hàng trống — chọn sản phẩm bên trên</td></tr>
              </tbody>
            </table>
          </div>
          <footer class="pos-cart-actions">
            <button class="btn btn-sm btn-outline-secondary" @click="openManual">Nhập tay</button>
            <button class="btn btn-sm btn-outline-secondary" @click="showScanner = true">Nhập mã</button>
            <button class="btn btn-sm btn-outline-secondary" @click="tangSL">Tăng số lượng</button>
            <button class="btn btn-sm btn-outline-danger" @click="giamSL">Giảm số lượng</button>
            <button class="btn btn-sm btn-outline-danger" @click="boSanPham">Bỏ sản phẩm</button>
            <button class="btn btn-sm btn-outline-danger" @click="hoanTraLine">Hoàn trả</button>
          </footer>
        </section>

        <!-- Danh sách hóa đơn (invoice queue) -->
        <section class="card pos-panel">
          <header class="pos-panel-head"><h6 class="pos-title">DANH SÁCH HÓA ĐƠN</h6></header>
          <div class="table-scroll" style="height: 150px">
            <table class="table table-sm table-hover align-middle pos-table mb-0">
              <thead>
                <tr>
                  <th class="text-center">STT</th><th>Mã hóa đơn</th><th>Tên nhân viên</th><th>Tên KH</th>
                  <th>Trạng Thái</th><th>Trạng Thái hàng</th><th>Ngày tạo</th><th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(h, i) in hoaDons" :key="h.id"
                    :class="{ 'row-active': h.id === activeId }" @click="switchHoaDon(h.id)">
                  <td class="text-center">{{ i + 1 }}</td>
                  <td class="fw-medium">{{ h.ma }}</td>
                  <td>{{ h.nhanVien }}</td>
                  <td>{{ h.khachHang }}</td>
                  <td>{{ h.trangThai }}</td>
                  <td>{{ h.trangThaiHang }}</td>
                  <td>{{ h.ngayTao }}</td>
                  <td class="text-end"><button class="btn btn-sm btn-outline-danger py-0 px-1" title="Đóng" @click.stop="removeHoaDon(h.id)"><i class="bi bi-x"></i></button></td>
                </tr>
              </tbody>
            </table>
          </div>
          <footer class="pos-queue-foot">
            <span class="text-muted">Tổng hóa đơn hôm nay:</span>
            <span class="fw-semibold">{{ tongHoaDonHomNay }}</span>
            <span class="fw-bold" style="color: var(--c-primary)">{{ vnd(tongTienHomNay) }}</span>
          </footer>
        </section>
      </div>

      <!-- ================= RIGHT COLUMN (section_2 JTabbedPane) ================= -->
      <div class="pos-right">
        <div class="pos-tabs">
          <button class="pos-tab" :class="{ active: orderTab === 'hoadon' }" @click="orderTab = 'hoadon'">Hóa đơn</button>
          <button class="pos-tab" :class="{ active: orderTab === 'dathang' }" @click="orderTab = 'dathang'">Đặt hàng</button>
        </div>

        <div class="pos-panel-green">
          <!-- ---------- HÓA ĐƠN tab ---------- -->
          <template v-if="orderTab === 'hoadon'">
            <h5 class="green-title">Hóa đơn</h5>

            <!-- Loại khách: hội viên (a real khach_hang row) vs khách vãng lai
                 (anonymous walk-in). Only "Hội viên" needs customer fields. -->
            <div class="chip-group" role="tablist" aria-label="Loại khách hàng">
              <button class="chip" role="tab" :aria-selected="active.khachType === 'hoivien'"
                      :class="{ active: active.khachType === 'hoivien' }" @click="chonHoiVien">Hội viên</button>
              <button class="chip" role="tab" :aria-selected="active.khachType === 'vanglai'"
                      :class="{ active: active.khachType === 'vanglai' }" @click="chonKhachVangLai">Khách vãng lai</button>
            </div>

            <div v-if="active.khachType === 'hoivien'" class="green-box">
              <div class="input-group input-group-sm mb-2">
                <span class="input-group-text">Mã hội viên</span>
                <input class="form-control" v-model="active.memberCode" placeholder="Nhập SĐT hội viên" @keyup.enter="timHoiVien" />
                <button class="btn btn-white" @click="timHoiVien">Tra cứu</button>
              </div>
              <button class="btn btn-white btn-sm w-100" @click="openAddKH">Thêm khách hàng mới</button>
            </div>
            <div v-else class="green-note">Bán cho khách vãng lai — không gắn thông tin khách hàng.</div>

            <h6 class="green-sub">Thông tin đơn hàng</h6>
            <dl class="green-fields">
              <template v-if="active.khachType === 'hoivien'">
                <div class="gf"><dt>Tên khách hàng</dt><dd>
                  <AppSelect v-model="khachHangModel" :options="khachHangSelectOptions" />
                </dd></div>
                <div class="gf"><dt>SDT</dt><dd class="val">{{ active.sdt || '—' }}</dd></div>
              </template>
              <div class="gf"><dt>Tổng tiền hàng</dt><dd class="val">{{ vnd(tamTinh) }}</dd></div>
              <div class="gf"><dt>Mã giảm giá</dt><dd><AppSelect v-model="voucherModel" :options="voucherSelectOptions" /></dd></div>
              <div class="gf"><dt>Thành tiền</dt><dd class="val strong">{{ vnd(phaiTra) }}</dd></div>
              <div class="gf"><dt>Hình thức thanh toán</dt><dd><AppSelect v-model="active.hinhThuc" :options="hinhThucOptions" /></dd></div>
              <div class="gf"><dt>Tiền khách đưa</dt><dd>
                <input type="number" min="0" step="1000" class="form-control form-control-sm text-end" v-model.number="active.khachDua" placeholder="0">
              </dd></div>
              <div class="gf"><dt>Tiền thừa</dt><dd class="val">{{ vnd(tienThua) }}</dd></div>
            </dl>

            <div class="green-actions">
              <button class="btn btn-success w-100" :disabled="gio.length === 0 || paying" @click="pay">Thanh toán</button>
              <button class="btn btn-success w-100" @click="taoHoaDon">Tạo hóa đơn</button>
              <button class="btn btn-success w-100" :disabled="gio.length === 0" @click="inTamTinh">Phiếu tạm tính</button>
              <button class="btn btn-outline-danger w-100" @click="huy">Hủy</button>
              <button class="btn btn-light w-100" @click="xuatJsonSanPham">Xuất json thông tin sản phẩm</button>
              <button class="btn btn-light w-100" @click="triggerNhapJson">Import sản phẩm bằng list json</button>
            </div>
          </template>

          <!-- ---------- ĐẶT HÀNG tab ---------- -->
          <template v-else>
            <h5 class="green-title">Đặt hàng</h5>
            <div class="green-box">
              <button class="btn btn-white btn-sm w-100 mb-2" @click="openAddKH">Thêm khách hàng mới</button>
              <div class="input-group input-group-sm">
                <span class="input-group-text">Mã hội viên</span>
                <input class="form-control" v-model="active.memberCode" placeholder="Nhập SĐT hội viên" @keyup.enter="timHoiVien" />
                <button class="btn btn-white" @click="timHoiVien">Tra cứu</button>
              </div>
            </div>

            <h6 class="green-sub">Thông tin đơn hàng</h6>
            <dl class="green-fields">
              <div class="gf"><dt>Tên khách hàng</dt><dd><input class="form-control form-control-sm" v-model="active.khachHang"></dd></div>
              <div class="gf"><dt>SDT</dt><dd><input class="form-control form-control-sm" v-model="active.sdt"></dd></div>
              <div class="gf"><dt>Địa chỉ</dt><dd><input class="form-control form-control-sm" v-model="active.diaChi"></dd></div>
              <div class="gf"><dt>Tổng tiền hàng</dt><dd class="val">{{ vnd(tamTinh) }}</dd></div>
              <div class="gf"><dt>Mã giảm giá</dt><dd><AppSelect v-model="voucherModel" :options="voucherSelectOptions" /></dd></div>
              <div class="gf"><dt>Phí ship</dt><dd><input type="number" min="0" step="1000" class="form-control form-control-sm text-end" v-model.number="active.phiShip"></dd></div>
              <div class="gf"><dt>Thành tiền</dt><dd class="val strong">{{ vnd(phaiTra) }}</dd></div>
              <div class="gf"><dt>Hình thức thanh toán</dt><dd><AppSelect v-model="active.hinhThuc" :options="hinhThucOptions" /></dd></div>
              <div class="gf"><dt>Tiền khách đưa</dt><dd><input type="number" min="0" step="1000" class="form-control form-control-sm text-end" v-model.number="active.khachDua"></dd></div>
              <div class="gf"><dt>Tiền thừa</dt><dd class="val">{{ vnd(tienThua) }}</dd></div>
              <div class="gf gf-note"><dt>Ghi chú</dt><dd><textarea class="form-control form-control-sm" rows="3" v-model="active.ghiChu"></textarea></dd></div>
            </dl>

            <div class="green-actions">
              <button class="btn btn-success w-100" @click="taoHoaDon">Tạo hóa đơn</button>
              <button class="btn btn-success w-100" :disabled="gio.length === 0 || paying" @click="giaoHang">Giao hàng (thanh toán)</button>
              <button class="btn btn-success w-100" @click="daGiaoAction">Đã giao</button>
              <button class="btn btn-outline-danger w-100" @click="traHangAction">Hoàn trả</button>
              <button class="btn btn-light w-100" @click="xuatJsonSanPham">Xuất json thông tin sản phẩm</button>
              <button class="btn btn-light w-100" @click="triggerNhapJson">Import sản phẩm bằng list json</button>
            </div>
          </template>
        </div>
      </div>
    </div>

    <QrScanner v-model:open="showScanner" @detected="onScan" />

    <!-- VietQR bank-transfer modal -->
    <div v-if="showQR" class="bs-overlay" @click.self="showQR = false">
      <div class="bs-modal text-center">
        <h5 class="mb-1">Chuyển khoản VietQR</h5>
        <div class="text-muted small mb-2">Quét mã để thanh toán <b>{{ vnd(qrAmount) }}</b></div>
        <div v-if="qrLabel" class="small mb-2" style="color:#0B895A">{{ qrLabel }}</div>
        <img :src="qrUrl" alt="VietQR" style="width:240px;height:240px;object-fit:contain" @error="e => e.target.style.opacity = .2" />
        <div class="small text-muted mb-3">BShoes • VCB • 1234567890 • ND: {{ active.ma }}</div>
        <div class="d-flex gap-2">
          <button class="btn btn-outline-secondary flex-fill" @click="showQR = false">Huỷ</button>
          <button class="btn btn-success flex-fill" @click="confirmQR">Đã chuyển khoản</button>
        </div>
      </div>
    </div>

    <!-- Manual code entry modal -->
    <div v-if="showManual" class="bs-overlay" @click.self="showManual = false">
      <div class="bs-modal">
        <h5 class="mb-3">Nhập mã sản phẩm</h5>
        <div class="mb-3">
          <label class="form-label small mb-1">Mã / Serial</label>
          <input class="form-control" v-model="manualCode" placeholder="Nhập mã sản phẩm..." @keyup.enter="confirmManual" autofocus>
        </div>
        <div class="d-flex gap-2">
          <button class="btn btn-outline-secondary flex-fill" @click="showManual = false">Đóng</button>
          <button class="btn btn-success flex-fill" @click="confirmManual">Thêm</button>
        </div>
      </div>
    </div>

    <!-- Quick add-customer modal -->
    <div v-if="showAddKH" class="bs-overlay" @click.self="showAddKH = false">
      <div class="bs-modal">
        <h5 class="mb-3">Thêm khách hàng mới</h5>
        <div class="mb-2"><label class="form-label small mb-1">Tên khách hàng *</label><input class="form-control form-control-sm" v-model="newKH.ten"></div>
        <div class="row g-2 mb-2">
          <div class="col-7"><label class="form-label small mb-1">SĐT</label><input class="form-control form-control-sm" v-model="newKH.sdt"></div>
          <div class="col-5"><label class="form-label small mb-1">Giới tính</label>
            <select class="form-select form-select-sm" v-model="newKH.gioiTinh"><option>Nam</option><option>Nữ</option></select></div>
        </div>
        <div class="mb-2"><label class="form-label small mb-1">Email</label><input class="form-control form-control-sm" v-model="newKH.email"></div>
        <div class="mb-3"><label class="form-label small mb-1">Địa chỉ</label><input class="form-control form-control-sm" v-model="newKH.diaChi"></div>
        <div class="d-flex gap-2">
          <button class="btn btn-outline-secondary flex-fill" @click="showAddKH = false">Huỷ</button>
          <button class="btn btn-success flex-fill" @click="saveKH">Lưu &amp; gán</button>
        </div>
      </div>
    </div>
    <input ref="jsonInput" type="file" accept=".json,application/json" class="d-none" @change="nhapJsonSanPham">
  </AppShell>
</template>

<style scoped>
.pos-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 400px;
  gap: 16px;
  align-items: start;          /* the right panel no longer scales with the cart */
}
.pos-left { display: flex; flex-direction: column; gap: 14px; min-width: 0; }

/* ---- tab strips ---- */
.pos-toptabs, .pos-tabs { display: flex; gap: 4px; }
.pos-toptab, .pos-tab {
  padding: 6px 16px;
  border: 1px solid var(--c-border);
  border-bottom: none;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
  background: #eef1f4;
  color: var(--c-text-muted);
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
}
.pos-toptab.active { background: var(--c-surface); color: var(--c-text); }
.pos-tab { background: #eef1f4; }
.pos-tab.active { background: var(--c-primary); color: #fff; border-color: var(--c-primary); }

/* ---- panels ---- */
.pos-panel { padding: 10px 12px; }
.pos-panel-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 8px; }
.pos-title { margin: 0; font-weight: 700; letter-spacing: .3px; color: var(--c-text); }
.pos-search { max-width: 280px; }

/* fixed-height scroll areas: adding rows never grows the panel */
.table-scroll { overflow: auto; border: 1px solid var(--c-border); border-radius: var(--radius-sm); }
.pos-table { min-width: 560px; }
.pos-table thead th {
  position: sticky; top: 0; z-index: 1;
  background: var(--c-primary); color: #fff;
  font-weight: 600; font-size: 12px; white-space: nowrap;
}
.pos-table tbody td { font-size: 13px; }
.row-selected > td { background: var(--c-primary-subtle); }
.row-return > td { background: var(--c-danger-subtle); }
.row-active > td { background: var(--c-primary); color: #fff; cursor: pointer; }
.pos-table tbody tr { cursor: pointer; }

/* stepper inside cart */
.stepper { display: inline-flex; align-items: center; gap: 4px; }
.stepper input { width: 52px; }
.bien-the { min-width: 130px; font-size: 12.5px; }
.stepper .btn { line-height: 1; padding: 0 7px; }

.pos-cart-actions, .pos-queue-foot { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 8px; }
.pos-queue-foot { justify-content: space-between; align-items: center; padding-top: 6px; }

/* ---- right green panel ---- */
.pos-right { position: sticky; top: 16px; }
.pos-panel-green {
  background: var(--c-primary);
  border-radius: 0 var(--radius) var(--radius) var(--radius);
  padding: 14px;
  color: #fff;
}
.green-title { font-weight: 700; margin: 2px 0 10px; }
.green-sub { font-weight: 600; margin: 12px 0 8px; opacity: .95; }
.green-box { background: rgba(255,255,255,.12); border-radius: var(--radius-sm); padding: 10px; }
.green-box .input-group-text { background: #fff; }

/* Actions inside the green panel: white on green. `btn-success` here was the same
   green as the panel behind it, so "Thêm khách hàng mới" / "Khách vãng lai" /
   "Nhập sdt" read as flat text with no visible button edge. */
.btn-white {
  background: #fff;
  color: var(--c-primary);
  border-color: #fff;
  font-weight: 600;
}
.btn-white:hover:not(:disabled) { background: #f0f0f0; color: var(--c-primary); }
.btn-white:focus-visible { outline: 2px solid #fff; outline-offset: 2px; }
/* inside an input-group both the field and the button are white — keep a visible seam */
.green-box .input-group .btn-white { border-left: 1px solid var(--c-border); }

.green-note { font-size: 12.5px; opacity: .9; padding: 8px 10px; background: rgba(255,255,255,.12); border-radius: var(--radius-sm); }

/* ---- loại khách chips (Hội viên / Khách vãng lai) ---- */
.chip-group { display: flex; gap: 6px; margin-bottom: 10px; }
.chip {
  flex: 1;
  padding: 5px 10px;
  border: 1px solid rgba(255,255,255,.6);
  border-radius: 999px;
  background: transparent;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}
.chip:hover:not(.active) { background: rgba(255,255,255,.15); }
.chip.active { background: #fff; color: var(--c-primary); border-color: #fff; }
.chip:focus-visible { outline: 2px solid #fff; outline-offset: 2px; }

.green-fields { display: flex; flex-direction: column; gap: 6px; margin: 0; }
.gf { display: grid; grid-template-columns: 130px 1fr; align-items: center; gap: 8px; }
.gf dt { font-weight: 500; font-size: 13px; opacity: .95; }
.gf dd { margin: 0; }
.gf .val { background: #fff; color: var(--c-text); border-radius: var(--radius-sm); padding: 4px 8px; text-align: right; font-weight: 600; }
.gf .val.strong { color: var(--c-primary); }
.gf-note { grid-template-columns: 130px 1fr; align-items: start; }

.green-actions { display: flex; flex-direction: column; gap: 8px; margin-top: 14px; }
.green-actions .btn-success { background: #fff; color: var(--c-primary); border-color: #fff; font-weight: 600; }
.green-actions .btn-success:hover:not(:disabled) { background: #f0f0f0; }
.green-actions .btn-outline-danger { background: transparent; color: #ffdede; border-color: #ffb3b3; }
.green-actions .btn-light:disabled { opacity: .5; }

/* lightweight modal overlay (VietQR / add-customer) */
.bs-overlay { position: fixed; inset: 0; background: rgba(15,23,20,.5); display: flex; align-items: center; justify-content: center; z-index: 1080; }
.bs-modal { background: #fff; border-radius: 12px; padding: 20px; width: 340px; max-width: 92vw; box-shadow: 0 20px 50px rgba(0,0,0,.3); }

@media (max-width: 992px) {
  .pos-grid { grid-template-columns: 1fr; }
  .pos-right { position: static; }
}
</style>
