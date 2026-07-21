<script setup>
// Theo dõi đơn hàng / giao hàng — lists invoices with status filter and lets you
// advance delivery status (Đã giao) or process a return (Trả hàng → restores stock).
import { ref, computed, onMounted } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AddKhachHangModal from '../components/ui/AddKhachHangModal.vue'
import { hoaDonApi } from '../api/hoaDon'
import { useToast } from '../composables/useToast'
import { useAuth } from '../composables/useAuth'
import { vnd } from '../utils/format'
import { printReceipt } from '../utils/receipt'

const { notify } = useToast()
const { user } = useAuth()
const rows = ref([])
const kw = ref('')
const tab = ref('all')
const sel = ref(null)

const tabs = [
  { key: 'all', label: 'Tất cả', codes: null },
  { key: 'cho', label: 'Chờ', codes: [0] },
  { key: 'tc', label: 'Thành công', codes: [1] },
  { key: 'chogiao', label: 'Chờ giao', codes: [3] },
  { key: 'dagiao', label: 'Đã giao', codes: [4] },
  { key: 'trahang', label: 'Trả hàng', codes: [5] },
  { key: 'huy', label: 'Huỷ', codes: [2] },
]
function countFor(t) { return t.codes ? rows.value.filter(r => t.codes.includes(r.trangThaiCode)).length : rows.value.length }
const filtered = computed(() => {
  const t = tabs.find(x => x.key === tab.value)
  const k = kw.value.trim().toLowerCase()
  return rows.value.filter(r =>
    (!t.codes || t.codes.includes(r.trangThaiCode)) &&
    (!k || (r.ma || '').toLowerCase().includes(k) || (r.khach || '').toLowerCase().includes(k)))
})
function badge(code) {
  return { 0: 'b-wait', 1: 'b-done', 2: 'b-fail', 3: 'b-wait', 4: 'b-done', 5: 'b-fail' }[code] || 'b-exp'
}

async function load() {
  try { rows.value = await hoaDonApi.findAll() }
  catch (e) { notify('Không tải được đơn hàng (backend offline?)', 'warning'); rows.value = [] }
}
async function selectRow(r) {
  try { sel.value = await hoaDonApi.findById(r.id) } catch (e) { sel.value = r }
}
async function daGiao(r) {
  try { await hoaDonApi.daGiao(r.id); notify('Đã giao ' + r.ma, 'success'); await refresh(r.id) }
  catch (e) { notify('Chỉ đơn "Chờ giao" mới đánh dấu Đã giao', 'warning') }
}
async function traHang(r) {
  if (!confirm(`Trả hàng ${r.ma}? Kho sẽ được hoàn lại.`)) return
  try { await hoaDonApi.traHang(r.id, user.value?.id); notify('Đã trả hàng ' + r.ma, 'success'); await refresh(r.id) }
  catch (e) { notify('Không thể trả hàng', 'warning') }
}
async function refresh(id) { await load(); if (sel.value && sel.value.id === id) await selectRow({ id }) }

// Reprint the selected order/invoice. Maps `sel` (HoaDonDto) into the normalized
// receipt shape shared with the POS (see utils/receipt.js). Fields not present on
// this payload default sensibly rather than being fabricated.
function inHoaDon() {
  if (!sel.value) return
  const s = sel.value
  const items = (s.chiTiet || []).map(c => ({
    ten: c.ten,
    soLuong: c.soLuong,
    donGia: c.donGia,
    thanhTien: c.thanhTien != null ? c.thanhTien : (c.donGia || 0) * (c.soLuong || 0),
  }))
  const phiShip = Number(s.phiShip) || 0
  const giamGia = Number(s.tienGiamGia) || 0
  const tongTien = Number(s.tongTien) || 0
  const tamTinh = s.tongTienBanDau != null ? Number(s.tongTienBanDau) : tongTien + giamGia - phiShip
  const ok = printReceipt({
    ma: s.ma,
    ngay: s.ngayTao,
    khach: s.khach || 'Khách lẻ',
    hinhThuc: s.phuongThucThanhToan || '',
    items,
    tamTinh, giamGia, phiShip,
    phaiTra: tongTien,
    paid: true,
  })
  if (!ok) notify('In hoá đơn thất bại — vui lòng thử lại', 'danger')
}
const showAddKH = ref(false)
function onCustomerCreated(kh) { notify('Đã thêm khách hàng: ' + (kh?.ten || ''), 'success') }
onMounted(load)
</script>

<template>
  <AppShell>
    <div class="d-flex justify-content-between align-items-center">
      <PageHeader title="Theo dõi đơn hàng &amp; giao hàng" />
      <button class="btn btn-sm btn-success" @click="showAddKH = true"><i class="bi bi-person-plus"></i> Thêm khách hàng</button>
    </div>
    <div class="dh-tabs mb-2">
      <button v-for="t in tabs" :key="t.key" class="dh-tab" :class="{ active: tab === t.key }" @click="tab = t.key">
        {{ t.label }}<span class="n">{{ countFor(t) }}</span>
      </button>
    </div>

    <div class="dh-grid">
      <div class="card"><div class="card-body">
        <input class="form-control form-control-sm mb-3" style="max-width:260px" v-model="kw" placeholder="Tìm mã HĐ / khách...">
        <div style="max-height:560px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
          <table class="table table-sm table-hover align-middle mb-0 dh-table">
            <thead><tr>
              <th>Mã HĐ</th><th>Khách</th><th>NV</th><th>Ngày</th><th class="text-end">Tổng tiền</th><th>Trạng thái</th><th class="text-end">Thao tác</th>
            </tr></thead>
            <tbody>
              <tr v-for="r in filtered" :key="r.id" @click="selectRow(r)" style="cursor:pointer" :class="{ sel: sel && sel.id === r.id }">
                <td class="fw-semibold">{{ r.ma }}</td><td>{{ r.khach || '—' }}</td><td>{{ r.nhanVien || '—' }}</td>
                <td class="small">{{ (r.ngayTao || '').slice(0, 10) }}</td>
                <td class="text-end">{{ vnd(r.tongTien) }}</td>
                <td><span class="badge-soft" :class="badge(r.trangThaiCode)">{{ r.trangThai }}</span></td>
                <td class="text-end text-nowrap">
                  <button v-if="r.trangThaiCode === 3" class="btn btn-sm btn-success py-0 px-2 me-1" @click.stop="daGiao(r)" title="Đã giao"><i class="bi bi-check2"></i></button>
                  <button v-if="[1,3,4].includes(r.trangThaiCode)" class="btn btn-sm btn-outline-danger py-0 px-2" @click.stop="traHang(r)" title="Trả hàng"><i class="bi bi-arrow-return-left"></i></button>
                </td>
              </tr>
              <tr v-if="filtered.length === 0"><td colspan="7" class="text-center text-muted py-4">Không có đơn</td></tr>
            </tbody>
          </table>
        </div>
      </div></div>

      <!-- detail: invoice lines + history -->
      <div class="card"><div class="card-body">
        <div v-if="!sel" class="text-muted text-center py-5"><i class="bi bi-truck fs-1 d-block mb-2 opacity-50"></i>Chọn một đơn để xem chi tiết</div>
        <div v-else>
          <div class="d-flex justify-content-between align-items-start mb-2">
            <div><h6 class="fw-bold mb-0">{{ sel.ma }}</h6><div class="small text-muted">{{ sel.khach }} · {{ sel.nhanVien || '—' }}</div></div>
            <span class="badge-soft" :class="badge(sel.trangThaiCode)">{{ sel.trangThai }}</span>
          </div>
          <table class="table table-sm mb-2">
            <thead><tr><th>Sản phẩm</th><th class="text-center">SL</th><th class="text-end">Đơn giá</th><th class="text-end">Thành tiền</th></tr></thead>
            <tbody>
              <tr v-for="(c,i) in (sel.chiTiet || [])" :key="i">
                <td>{{ c.ten }}</td><td class="text-center">{{ c.soLuong }}</td><td class="text-end">{{ vnd(c.donGia) }}</td><td class="text-end">{{ vnd(c.thanhTien) }}</td>
              </tr>
              <tr v-if="!(sel.chiTiet || []).length"><td colspan="4" class="text-center text-muted">Không có dòng</td></tr>
            </tbody>
          </table>
          <ul class="list-unstyled small mb-3">
            <li v-if="sel.diaChi"><b>Địa chỉ:</b> {{ sel.diaChi }}</li>
            <li v-if="sel.phuongThucThanhToan"><b>Thanh toán:</b> {{ sel.phuongThucThanhToan }}</li>
            <li v-if="sel.phiShip"><b>Phí ship:</b> {{ vnd(sel.phiShip) }}</li>
            <li class="fw-bold" style="color:#0B895A"><b>Tổng phải trả:</b> {{ vnd(sel.tongTien) }}</li>
          </ul>
          <div class="d-flex gap-2">
            <button class="btn btn-outline-secondary flex-fill" @click="inHoaDon"><i class="bi bi-printer"></i> In hóa đơn</button>
            <button v-if="sel.trangThaiCode === 3" class="btn btn-success flex-fill" @click="daGiao(sel)"><i class="bi bi-check2"></i> Đã giao</button>
            <button v-if="[1,3,4].includes(sel.trangThaiCode)" class="btn btn-outline-danger flex-fill" @click="traHang(sel)"><i class="bi bi-arrow-return-left"></i> Trả hàng</button>
          </div>
        </div>
      </div></div>
    </div>

    <AddKhachHangModal v-model:open="showAddKH" @created="onCustomerCreated" />
  </AppShell>
</template>

<style scoped>
.dh-tabs { display: flex; gap: 4px; flex-wrap: wrap; }
.dh-tab { border: 1px solid #e5e9ef; background: #eef1f4; color: #6b7280; border-radius: 8px; padding: 6px 14px; font-size: 13px; font-weight: 600; cursor: pointer; }
.dh-tab .n { display: inline-block; min-width: 18px; padding: 0 5px; margin-left: 6px; border-radius: 999px; background: #d5dbe2; color: #374151; font-size: 11px; }
.dh-tab.active { background: #0B895A; color: #fff; border-color: #0B895A; }
.dh-tab.active .n { background: #fff; color: #0B895A; }
.dh-grid { display: grid; grid-template-columns: minmax(0, 1fr) 380px; gap: 16px; align-items: start; }
.dh-table thead th { position: sticky; top: 0; background: #0B895A; color: #fff; font-size: 12px; white-space: nowrap; }
.dh-table tbody tr.sel > td { background: #E7F4EF; }
.badge-soft { font-weight: 600; font-size: 11px; padding: 4px 8px; border-radius: 999px; }
.b-wait { background: #FFF4E0; color: #9A6700; } .b-done { background: #E6F4EA; color: #157347; }
.b-fail { background: #FDECEA; color: #B42318; } .b-exp { background: #eceff3; color: #556; }
@media (max-width: 992px) { .dh-grid { grid-template-columns: 1fr; } }
</style>
