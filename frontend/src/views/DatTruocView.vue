<script setup>
// Quản lý đặt trước (pre-order): khách đăng ký khi hàng hết → NV báo khi có hàng
// → chuyển thành hóa đơn chờ (giữ hàng). Trạng thái xử lý nằm ở Java service.
import { ref, computed, onMounted } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import { datTruocApi } from '../api/datTruoc'
import { useToast } from '../composables/useToast'
import { useAuth } from '../composables/useAuth'
import { vnd } from '../utils/format'

const { notify } = useToast()
const { user } = useAuth()
const rows = ref([])
const kw = ref('')
const tab = ref('all')
const sel = ref(null)

const STATUSES = ['Chờ hàng', 'Đã có hàng', 'Đã chuyển đơn', 'Đã hủy']
const tabs = [
  { key: 'all', label: 'Tất cả', st: null },
  { key: 'cho', label: 'Chờ hàng', st: 'Chờ hàng' },
  { key: 'co', label: 'Đã có hàng', st: 'Đã có hàng' },
  { key: 'don', label: 'Đã chuyển đơn', st: 'Đã chuyển đơn' },
  { key: 'huy', label: 'Đã hủy', st: 'Đã hủy' },
]
function countFor(t) { return t.st ? rows.value.filter(r => r.trangThai === t.st).length : rows.value.length }
const filtered = computed(() => {
  const t = tabs.find(x => x.key === tab.value)
  const k = kw.value.trim().toLowerCase()
  return rows.value.filter(r =>
    (!t.st || r.trangThai === t.st) &&
    (!k || (r.ma || '').toLowerCase().includes(k) || (r.tenKhachHang || '').toLowerCase().includes(k) ||
     (r.soDienThoai || '').includes(k) || (r.tenSanPham || '').toLowerCase().includes(k)))
})
function badge(s) {
  return { 'Chờ hàng': 'b-wait', 'Đã có hàng': 'b-proc', 'Đã chuyển đơn': 'b-done', 'Đã hủy': 'b-fail' }[s] || 'b-exp'
}
function fmt(s) { return s ? String(s).slice(0, 10) : '—' }

async function load() {
  try { rows.value = await datTruocApi.findAll() }
  catch (e) { notify('Không tải được danh sách đặt trước (backend offline?)', 'warning'); rows.value = [] }
  if (sel.value) sel.value = rows.value.find(r => r.id === sel.value.id) || null
}
async function setStatus(r, st) {
  try { await datTruocApi.capNhatTrangThai(r.id, st); notify(`${r.ma}: ${st}`, 'success'); await load() }
  catch (e) { notify('Cập nhật thất bại', 'warning') }
}
async function chuyenDon(r) {
  if (!confirm(`Chuyển ${r.ma} thành hóa đơn chờ? Hàng sẽ được giữ (trừ kho) cho khách.`)) return
  try {
    const d = await datTruocApi.chuyenDon(r.id, user.value?.id)
    notify(`Đã tạo hóa đơn ${d.maHoaDon || ''} từ ${r.ma}`, 'success')
    await load()
  } catch (e) {
    notify(e?.response?.data?.message || 'Không thể chuyển đơn', 'warning')
  }
}
async function remove(r) {
  if (!confirm(`Xoá phiếu ${r.ma}?`)) return
  try { await datTruocApi.remove(r.id); notify('Đã xoá', 'success'); sel.value = null; await load() }
  catch (e) { notify('Xoá thất bại', 'warning') }
}
onMounted(load)
</script>

<template>
  <AppShell>
    <PageHeader title="Quản lý đặt trước (Pre-order)" />
    <div class="dt-tabs mb-2">
      <button v-for="t in tabs" :key="t.key" class="dt-tab" :class="{ active: tab === t.key }" @click="tab = t.key">
        {{ t.label }}<span class="n">{{ countFor(t) }}</span>
      </button>
    </div>

    <div class="dt-grid">
      <div class="card"><div class="card-body">
        <input class="form-control form-control-sm mb-3" style="max-width:280px" v-model="kw"
               placeholder="Tìm mã phiếu / khách / SĐT / sản phẩm...">
        <div style="max-height:560px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
          <table class="table table-sm table-hover align-middle mb-0 dt-table">
            <thead><tr>
              <th>Mã</th><th>Sản phẩm</th><th>Khách</th><th class="text-center">SL</th>
              <th>Đăng ký</th><th>Dự kiến</th><th class="text-center">Tồn</th><th>Trạng thái</th><th></th>
            </tr></thead>
            <tbody>
              <tr v-for="r in filtered" :key="r.id" @click="sel = r" style="cursor:pointer" :class="{ sel: sel && sel.id === r.id }">
                <td class="fw-semibold">{{ r.ma }}</td>
                <td>{{ r.tenSanPham }}<div class="small text-muted">{{ r.mauSize }}</div></td>
                <td>{{ r.tenKhachHang }}<div class="small text-muted">{{ r.soDienThoai }}</div></td>
                <td class="text-center">{{ r.soLuong }}</td>
                <td class="small">{{ fmt(r.ngayDangKy) }}</td>
                <td class="small">{{ fmt(r.ngayDuKien) }}</td>
                <td class="text-center"><span :class="r.ton > 0 ? 'text-success fw-bold' : 'text-muted'">{{ r.ton }}</span></td>
                <td><span class="badge-soft" :class="badge(r.trangThai)">{{ r.trangThai }}</span></td>
                <td class="text-end text-nowrap">
                  <button v-if="r.trangThai === 'Chờ hàng' && r.ton > 0" class="btn btn-sm btn-outline-primary py-0 px-2 me-1"
                          @click.stop="setStatus(r, 'Đã có hàng')" title="Báo khách đã có hàng"><i class="bi bi-bell"></i></button>
                  <button v-if="['Chờ hàng','Đã có hàng'].includes(r.trangThai)" class="btn btn-sm btn-success py-0 px-2"
                          @click.stop="chuyenDon(r)" title="Chuyển thành đơn hàng"><i class="bi bi-cart-check"></i></button>
                </td>
              </tr>
              <tr v-if="filtered.length === 0"><td colspan="9" class="text-center text-muted py-4">Không có phiếu đặt trước</td></tr>
            </tbody>
          </table>
        </div>
      </div></div>

      <!-- chi tiết -->
      <div class="card"><div class="card-body">
        <div v-if="!sel" class="text-muted text-center py-5">
          <i class="bi bi-bookmark-heart fs-1 d-block mb-2 opacity-50"></i>Chọn một phiếu để xem chi tiết
        </div>
        <div v-else>
          <div class="d-flex justify-content-between align-items-start mb-2">
            <h6 class="fw-bold mb-0">{{ sel.ma }}</h6>
            <span class="badge-soft" :class="badge(sel.trangThai)">{{ sel.trangThai }}</span>
          </div>
          <div class="text-center mb-2" v-if="sel.imageUrl">
            <img :src="sel.imageUrl" style="max-height:110px;object-fit:contain" @error="e => e.target.style.display='none'">
          </div>
          <dl class="det row mb-0">
            <div class="col-12"><dt>Sản phẩm</dt><dd>{{ sel.tenSanPham }} — {{ sel.mauSize }}</dd></div>
            <div class="col-6"><dt>Đơn giá</dt><dd>{{ vnd(sel.gia) }}</dd></div>
            <div class="col-6"><dt>Số lượng đặt</dt><dd>{{ sel.soLuong }}</dd></div>
            <div class="col-6"><dt>Tồn hiện tại</dt><dd :class="sel.ton > 0 ? 'text-success' : 'text-danger'">{{ sel.ton }}</dd></div>
            <div class="col-6"><dt>Dự kiến về</dt><dd>{{ fmt(sel.ngayDuKien) }}</dd></div>
            <div class="col-6"><dt>Khách hàng</dt><dd>{{ sel.tenKhachHang }}</dd></div>
            <div class="col-6"><dt>SĐT</dt><dd>{{ sel.soDienThoai }}</dd></div>
            <div class="col-12" v-if="sel.email"><dt>Email</dt><dd>{{ sel.email }}</dd></div>
            <div class="col-12" v-if="sel.ghiChu"><dt>Ghi chú</dt><dd>{{ sel.ghiChu }}</dd></div>
            <div class="col-12" v-if="sel.maHoaDon"><dt>Hóa đơn đã tạo</dt><dd style="color:#0B895A">{{ sel.maHoaDon }}</dd></div>
          </dl>

          <div class="grp-title">Đổi trạng thái</div>
          <div class="d-flex flex-wrap gap-1 mb-3">
            <button v-for="s in STATUSES" :key="s" class="btn btn-sm"
                    :class="sel.trangThai === s ? 'btn-success' : 'btn-outline-secondary'"
                    @click="setStatus(sel, s)">{{ s }}</button>
          </div>
          <div class="d-flex gap-2">
            <button class="btn btn-success flex-fill" :disabled="!['Chờ hàng','Đã có hàng'].includes(sel.trangThai)"
                    @click="chuyenDon(sel)"><i class="bi bi-cart-check"></i> Chuyển thành đơn</button>
            <button class="btn btn-outline-danger" @click="remove(sel)"><i class="bi bi-trash"></i></button>
          </div>
          <div class="small text-muted mt-2" v-if="sel.ton <= 0">
            Chưa có hàng — chỉ chuyển đơn được khi tồn kho ≥ số lượng đặt.
          </div>
        </div>
      </div></div>
    </div>
  </AppShell>
</template>

<style scoped>
.dt-tabs { display: flex; gap: 4px; flex-wrap: wrap; }
.dt-tab { border: 1px solid #e5e9ef; background: #eef1f4; color: #6b7280; border-radius: 8px; padding: 6px 14px; font-size: 13px; font-weight: 600; cursor: pointer; }
.dt-tab .n { display: inline-block; min-width: 18px; padding: 0 5px; margin-left: 6px; border-radius: 999px; background: #d5dbe2; color: #374151; font-size: 11px; }
.dt-tab.active { background: #0B895A; color: #fff; border-color: #0B895A; }
.dt-tab.active .n { background: #fff; color: #0B895A; }
.dt-grid { display: grid; grid-template-columns: minmax(0, 1fr) 360px; gap: 16px; align-items: start; }
.dt-table thead th { position: sticky; top: 0; background: #0B895A; color: #fff; font-size: 12px; white-space: nowrap; }
.dt-table tbody tr.sel > td { background: #E7F4EF; }
.badge-soft { font-weight: 600; font-size: 11px; padding: 4px 8px; border-radius: 999px; white-space: nowrap; }
.b-wait { background: #FFF4E0; color: #9A6700; } .b-proc { background: #E7F1FB; color: #1F6FB2; }
.b-done { background: #E6F4EA; color: #157347; } .b-fail { background: #FDECEA; color: #B42318; } .b-exp { background: #eceff3; color: #556; }
.det dt { font-weight: 500; font-size: 12px; color: #6b7280; }
.det dd { margin: 0 0 8px; font-size: 13px; font-weight: 600; color: #1f2933; }
.grp-title { font-weight: 700; color: #0B895A; font-size: 13px; text-transform: uppercase; letter-spacing: .3px; margin: 12px 0 8px; }
@media (max-width: 992px) { .dt-grid { grid-template-columns: 1fr; } }
</style>
