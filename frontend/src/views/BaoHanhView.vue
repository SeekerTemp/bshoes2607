<script setup>
import { ref, computed, onMounted } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import AppModal from '../components/ui/AppModal.vue'
import ConfirmDialog from '../components/ui/ConfirmDialog.vue'
import FormField from '../components/ui/FormField.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import { baoHanhApi } from '../api/baoHanh'
import { khachHangApi } from '../api/khachHang'
import { nhanVienApi } from '../api/nhanVien'
import { useToast } from '../composables/useToast'
import { crudErrorMessage } from '../composables/useCrud'
import { vnd } from '../utils/format'
import { printHtml } from '../utils/receipt'
import { buildWarrantyHtml } from '../utils/warranty'

const { notify } = useToast()

const kw = ref(''); const proc = ref('all'); const tab = ref('all'); const sel = ref(null)
const rows = ref([])
const steps = ['Xác thực SP', 'Chẩn đoán', 'Ước tính chi phí', 'Xác nhận sửa']
const statuses = ['Chưa xử lý', 'Đã chẩn đoán', 'Đang xử lý', 'Đã xử lý', 'Đã thu phí', 'Đã trả']
const tabs = [
  { key: 'all', label: 'Tất cả' }, { key: 'wait', label: 'Chờ xác thực' },
  { key: 'pickup', label: 'Chờ lấy hàng' }, { key: 'expired', label: 'Hết hạn bảo hành' },
  { key: 'success', label: 'Bảo hành thành công' }, { key: 'fail', label: 'Hỏng / Hoàn trả' },
]

const mock = [
  { id: 1, ma: 'BH0001', maKH: 'KH01', tenKH: 'Nguyễn Trung Nghĩa', sdt: '0968291160', maHD: 'HD202511001', model: 'Giày thể thao Nike', mauSize: 'Đen / 42', serial: 'NK-42-000123', batDau: '2026-05-08', hetHan: '2026-11-08', loai: 'Bong đế', donVi: 'BShoes Center', nv: 'Lê Thị B', chiPhi: 120000, thayLinhKien: true, moTa: 'Đế bị bong ở mũi giày.', trangThai: 'Đang xử lý', cat: 'pickup' },
  { id: 2, ma: 'BH0002', maKH: 'KH08', tenKH: 'Đặng Thị Hồng', sdt: '0923456789', maHD: '', model: 'Giày da Adidas', mauSize: 'Nâu / 40', serial: 'AD-40-000456', batDau: '2026-10-20', hetHan: '2027-04-20', loai: 'Đứt chỉ', donVi: 'Adidas Care', nv: 'Trần Văn C', chiPhi: 80000, thayLinhKien: false, moTa: 'Đường chỉ gót bị bung.', trangThai: 'Chưa xử lý', cat: 'wait' },
  { id: 3, ma: 'BH0003', maKH: 'KH09', tenKH: 'Phan Minh Tuấn', sdt: '0956789123', maHD: '', model: 'Sandal Puma', mauSize: 'Đen / 41', serial: 'PM-41-000789', batDau: '2026-09-12', hetHan: '2027-03-12', loai: 'Gãy quai', donVi: 'BShoes Center', nv: 'Ngô Văn E', chiPhi: 60000, thayLinhKien: true, moTa: 'Quai hậu bị gãy.', trangThai: 'Đã xử lý', cat: 'success' },
]

const filtered = computed(() => {
  const k = kw.value.trim().toLowerCase()
  return rows.value.filter(r =>
    (tab.value === 'all' || r.cat === tab.value) &&
    (proc.value === 'all' ||
      (proc.value === 'open' && ['Chưa xử lý', 'Đã chẩn đoán', 'Đang xử lý'].includes(r.trangThai)) ||
      (proc.value === 'closed' && ['Đã xử lý', 'Đã thu phí', 'Đã trả'].includes(r.trangThai))) &&
    (!k || (r.ma || '').toLowerCase().includes(k) || (r.serial || '').toLowerCase().includes(k) || (r.model || '').toLowerCase().includes(k))
  )
})
const stepIndex = computed(() => {
  if (!sel.value) return 0
  const m = { 'Chưa xử lý': 0, 'Đã chẩn đoán': 1, 'Đang xử lý': 1, 'Đã thu phí': 2, 'Đã xử lý': 3, 'Đã trả': 3 }
  return m[sel.value.trangThai] ?? 0
})
function countFor(key) { return key === 'all' ? rows.value.length : rows.value.filter(r => r.cat === key).length }
function badgeClass(s) {
  if (s === 'Đã xử lý') return 'b-done'
  if (['Đang xử lý', 'Đã chẩn đoán'].includes(s)) return 'b-proc'
  if (s === 'Chưa xử lý') return 'b-wait'
  if (s === 'Đã thu phí') return 'b-fail'
  return 'b-exp'
}
function fmtDate(s) { return s ? String(s).slice(0, 10) : '—' }

async function load() {
  try {
    const data = await baoHanhApi.findAll()
    rows.value = (data && data.length) ? data : mock
  } catch (e) {
    console.warn('API offline, using mock warranty', e)
    rows.value = mock
  }
  sel.value = rows.value[0] || null
}
async function saveStatus() {
  if (!sel.value) return
  try {
    await baoHanhApi.updateTrangThai(sel.value.id, sel.value.trangThai)
    notify('Đã cập nhật trạng thái ' + sel.value.ma, 'success')
    await load()
  } catch (e) {
    notify('Cập nhật cục bộ (backend offline)', 'warning')
  }
}

// --- Thêm mới đơn bảo hành ---
const khachHangOptions = ref([])
const nhanVienOptions = ref([])
const modalOpen = ref(false)
function blankForm() {
  return {
    idKhachHang: null, idNhanVien: null, serial: '', loai: '',
    donVi: '', moTa: '', chiPhi: 0, thayLinhKien: false, trangThai: statuses[0],
  }
}
const form = ref(blankForm())

async function loadOptions() {
  try {
    const list = await khachHangApi.findAll()
    khachHangOptions.value = (list || []).map(k => ({ value: k.id, label: k.ten }))
  } catch (e) {
    console.warn('Không tải được danh sách khách hàng', e)
  }
  try {
    const list = await nhanVienApi.findAll()
    nhanVienOptions.value = (list || []).map(n => ({ value: n.id, label: n.ten }))
  } catch (e) {
    console.warn('Không tải được danh sách nhân viên', e)
  }
}

function openCreate() {
  form.value = blankForm()
  modalOpen.value = true
}

async function saveCreate() {
  try {
    await baoHanhApi.create(form.value)
    notify('Đã tạo đơn bảo hành', 'success')
    modalOpen.value = false
    await load()
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

// --- Hủy đơn / phiếu bảo hành ---
const confirmOpen = ref(false)
function askDeleteSel() {
  if (!sel.value) return
  confirmOpen.value = true
}
async function doDeleteSel() {
  if (!sel.value) return
  try {
    await baoHanhApi.remove(sel.value.id)
    notify('Đã hủy đơn bảo hành', 'success')
    confirmOpen.value = false
    sel.value = null
    await load()
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

// --- In phiếu bảo hành ---
function printWarranty() {
  if (!sel.value) return
  const ok = printHtml(buildWarrantyHtml(sel.value))
  if (!ok) notify('Không thể in phiếu bảo hành', 'warning')
}

onMounted(() => { load(); loadOptions() })
</script>

<template>
  <AppShell>
    <div class="bh">
      <div class="bh-tabs">
        <div v-for="t in tabs" :key="t.key" class="bh-tab" :class="{active:tab===t.key}" @click="tab=t.key">
          {{ t.label }}<span class="n">{{ countFor(t.key) }}</span>
        </div>
      </div>

      <div class="bh-grid">
        <!-- list -->
        <section class="panel panel-pad" style="border-top-left-radius:0">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="title">DỊCH VỤ BẢO HÀNH</h5>
            <div class="d-flex align-items-center gap-2">
              <input type="date" class="form-control form-control-sm" style="width:150px">
              <button class="btn btn-sm btn-outline-secondary"><i class="bi bi-download"></i> Export</button>
            </div>
          </div>
          <div class="toolbar d-flex gap-2 mb-2">
            <select class="form-select form-select-sm" style="max-width:150px"><option>-- Đơn vị --</option><option>BShoes Center</option></select>
            <input class="form-control form-control-sm" v-model="kw" placeholder="Nhập mã bảo hành / serial...">
            <button class="btn btn-green btn-sm px-3">Tìm kiếm</button>
            <button class="btn btn-outline-secondary btn-sm" @click="kw=''">Đặt lại</button>
          </div>
          <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="btn-group seg">
              <button class="btn btn-sm btn-outline-secondary" :class="{active:proc==='all'}" @click="proc='all'">Tất cả</button>
              <button class="btn btn-sm btn-outline-secondary" :class="{active:proc==='open'}" @click="proc='open'">Chưa xử lý</button>
              <button class="btn btn-sm btn-outline-secondary" :class="{active:proc==='closed'}" @click="proc='closed'">Đã xử lý</button>
            </div>
          </div>
          <div class="d-flex gap-2">
            <div class="flex-grow-1" style="max-height:440px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
              <table class="table table-hover align-middle bh-table mb-0">
                <thead><tr>
                  <th class="text-center">STT</th><th>Mã BH</th><th>Model (giày)</th><th>Serial</th><th>Thời hạn</th><th class="text-center">Thay đế</th><th>Đơn vị</th><th>Trạng thái</th>
                </tr></thead>
                <tbody>
                  <tr class="add-row"><td colspan="8" @click="openCreate"><i class="bi bi-plus-lg"></i> Thêm mới đơn bảo hành</td></tr>
                  <tr v-for="(r,i) in filtered" :key="r.id" :class="{sel:sel && sel.id===r.id}" @click="sel=r">
                    <td class="text-center">{{ i+1 }}</td><td class="fw-semibold">{{ r.ma }}</td><td>{{ r.model }}</td>
                    <td>{{ r.serial }}</td><td>{{ fmtDate(r.hetHan) }}</td><td class="text-center">{{ r.thayLinhKien ? 'Có' : '—' }}</td>
                    <td>{{ r.donVi }}</td><td><span class="badge-soft" :class="badgeClass(r.trangThai)">{{ r.trangThai }}</span></td>
                  </tr>
                  <tr v-if="filtered.length===0"><td colspan="8" class="text-center text-muted py-4">Không có đơn bảo hành</td></tr>
                </tbody>
              </table>
            </div>
            <div class="side-actions d-flex flex-column gap-2">
              <button class="btn btn-outline-secondary btn-sm" @click="load">Làm mới</button>
              <button class="btn btn-outline-secondary btn-sm" @click="notify('Import (demo)','info')">Import</button>
              <button class="btn btn-outline-danger btn-sm" :disabled="!sel" @click="askDeleteSel">Hủy phiếu</button>
            </div>
          </div>
        </section>

        <!-- detail -->
        <aside class="panel panel-pad">
          <div v-if="!sel" class="text-muted text-center py-5"><i class="bi bi-clipboard2-pulse fs-1 d-block mb-2 opacity-50"></i>Chọn một đơn bảo hành để xem chi tiết</div>
          <div v-else>
            <div class="stepper mb-3">
              <div v-for="(s,i) in steps" :key="s" class="step" :class="{on:i<=stepIndex}">
                <div class="dot"><i class="bi" :class="i<stepIndex ? 'bi-check' : 'bi-dot'"></i></div>{{ s }}
              </div>
            </div>
            <div class="grp-title">Thông tin khách hàng</div>
            <dl class="det row mb-0">
              <div class="col-6"><dt>Mã KH</dt><dd>{{ sel.maKH || '—' }}</dd></div>
              <div class="col-6"><dt>Tên KH</dt><dd>{{ sel.tenKH || '—' }}</dd></div>
              <div class="col-6"><dt>SĐT</dt><dd>{{ sel.sdt || '—' }}</dd></div>
              <div class="col-6"><dt>Mã hóa đơn</dt><dd>{{ sel.maHD || '—' }}</dd></div>
            </dl>
            <div class="grp-title">Thông tin sản phẩm</div>
            <dl class="det row mb-0">
              <div class="col-6"><dt>Model</dt><dd>{{ sel.model }}</dd></div>
              <div class="col-6"><dt>Màu / Size</dt><dd>{{ sel.mauSize }}</dd></div>
              <div class="col-6"><dt>Serial</dt><dd>{{ sel.serial }}</dd></div>
              <div class="col-6"><dt>Loại lỗi</dt><dd>{{ sel.loai }}</dd></div>
              <div class="col-6"><dt>BH bắt đầu</dt><dd>{{ fmtDate(sel.batDau) }}</dd></div>
              <div class="col-6"><dt>BH kết thúc</dt><dd>{{ fmtDate(sel.hetHan) }}</dd></div>
            </dl>
            <div class="grp-title">Đơn vị bảo hành</div>
            <textarea class="form-control form-control-sm mb-2" rows="2" v-model="sel.moTa"></textarea>
            <dl class="det row mb-1">
              <div class="col-6"><dt>Đơn vị BH</dt><dd>{{ sel.donVi }}</dd></div>
              <div class="col-6"><dt>NV bảo hành</dt><dd>{{ sel.nv || '—' }}</dd></div>
              <div class="col-6"><dt>Chi phí</dt><dd>{{ vnd(sel.chiPhi) }}</dd></div>
              <div class="col-6"><dt>Thay đế/linh kiện</dt><dd>{{ sel.thayLinhKien ? 'Có' : 'Không' }}</dd></div>
            </dl>
            <div class="grp-title">Trạng thái bảo hành</div>
            <div class="radio-grid mb-3">
              <div class="form-check" v-for="st in statuses" :key="st">
                <input class="form-check-input" type="radio" :id="'st'+st" :value="st" v-model="sel.trangThai">
                <label class="form-check-label small" :for="'st'+st">{{ st }}</label>
              </div>
            </div>
            <div class="d-grid gap-2">
              <div class="row g-2">
                <div class="col-6"><button class="btn btn-green btn-sm w-100" @click="saveStatus">Xác nhận xử lý</button></div>
                <div class="col-6"><button class="btn btn-outline-danger btn-sm w-100" @click="askDeleteSel">Hủy đơn</button></div>
              </div>
              <button class="btn btn-outline-secondary btn-sm" @click="printWarranty"><i class="bi bi-printer"></i> In phiếu bảo hành</button>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <AppModal v-model:open="modalOpen" title="Thêm mới đơn bảo hành">
      <div class="row">
        <div class="col-6"><FormField label="Khách hàng"><AppSelect v-model.number="form.idKhachHang" :options="khachHangOptions" /></FormField></div>
        <div class="col-6"><FormField label="Nhân viên xử lý"><AppSelect v-model.number="form.idNhanVien" :options="nhanVienOptions" /></FormField></div>
      </div>
      <div class="row">
        <div class="col-6"><FormField label="Serial"><input class="form-control" v-model="form.serial"></FormField></div>
        <div class="col-6"><FormField label="Loại lỗi"><input class="form-control" v-model="form.loai"></FormField></div>
      </div>
      <FormField label="Đơn vị bảo hành"><input class="form-control" v-model="form.donVi"></FormField>
      <FormField label="Mô tả lỗi"><textarea class="form-control" rows="2" v-model="form.moTa"></textarea></FormField>
      <div class="row">
        <div class="col-6"><FormField label="Chi phí"><input type="number" class="form-control" v-model.number="form.chiPhi"></FormField></div>
        <div class="col-6"><FormField label="Trạng thái"><AppSelect v-model="form.trangThai" :options="statuses" /></FormField></div>
      </div>
      <div class="form-check">
        <input class="form-check-input" type="checkbox" v-model="form.thayLinhKien" id="bh-thaylk">
        <label class="form-check-label" for="bh-thaylk">Thay đế / linh kiện</label>
      </div>
      <template #footer>
        <button class="btn btn-secondary" @click="modalOpen = false">Huỷ</button>
        <button class="btn btn-green" @click="saveCreate">Lưu</button>
      </template>
    </AppModal>

    <ConfirmDialog v-model:open="confirmOpen" title="Hủy đơn bảo hành" :message="`Hủy đơn bảo hành ${sel?.ma}?`" @confirm="doDeleteSel" />
  </AppShell>
</template>

<style scoped>
.bh { --brand:#0B895A; }
.bh-grid { display:grid; grid-template-columns:minmax(0,1fr) 380px; gap:16px; align-items:start; }
.panel { background:#fff; border:1px solid #e5e9ef; border-radius:10px; box-shadow:0 1px 2px rgba(16,24,40,.06); }
.panel-pad { padding:14px 16px; }
.bh-tabs { display:flex; gap:4px; flex-wrap:wrap; }
.bh-tab { border:1px solid #e5e9ef; border-bottom:none; background:#eef1f4; color:#6b7280; border-radius:8px 8px 0 0; padding:7px 14px; font-size:13px; font-weight:600; cursor:pointer; white-space:nowrap; }
.bh-tab .n { display:inline-block; min-width:18px; padding:0 5px; margin-left:6px; border-radius:999px; background:#d5dbe2; color:#374151; font-size:11px; }
.bh-tab.active { background:#fff; color:var(--brand); }
.bh-tab.active .n { background:var(--brand); color:#fff; }
.title { color:var(--brand); font-weight:800; letter-spacing:.3px; margin:0; }
.seg .btn.active { background:var(--brand); color:#fff; border-color:var(--brand); }
table.bh-table { font-size:13px; }
table.bh-table thead th { background:var(--brand); color:#fff; font-weight:600; font-size:12px; white-space:nowrap; position:sticky; top:0; }
table.bh-table tbody tr { cursor:pointer; }
table.bh-table tbody tr.sel > td { background:var(--brand); color:#fff; }
.add-row td { background:#f0faf5; color:var(--brand); font-weight:600; cursor:pointer; }
.badge-soft { font-weight:600; font-size:11px; padding:4px 8px; border-radius:999px; }
.b-wait { background:#FFF4E0; color:#9A6700; } .b-proc { background:#E7F1FB; color:#1F6FB2; }
.b-done { background:#E6F4EA; color:#157347; } .b-fail { background:#FDECEA; color:#B42318; } .b-exp { background:#eceff3; color:#556; }
.side-actions .btn { font-size:12px; }
.stepper { display:flex; align-items:center; }
.stepper .step { flex:1; text-align:center; position:relative; font-size:11px; color:#9aa4b2; }
.stepper .dot { width:22px; height:22px; border-radius:50%; background:#e2e7ee; color:#fff; margin:0 auto 4px; display:flex; align-items:center; justify-content:center; font-size:12px; }
.stepper .step.on { color:var(--brand); font-weight:600; }
.stepper .step.on .dot { background:var(--brand); }
.stepper .step::before { content:''; position:absolute; top:11px; left:-50%; width:100%; height:2px; background:#e2e7ee; z-index:0; }
.stepper .step:first-child::before { display:none; }
.stepper .step.on::before { background:var(--brand); }
.stepper .dot, .stepper .step > :not(.dot) { position:relative; z-index:1; }
.det dt { font-weight:500; font-size:12px; color:#6b7280; }
.det dd { margin:0 0 8px; font-size:13px; font-weight:600; color:#1f2933; }
.grp-title { font-weight:700; color:var(--brand); font-size:13px; text-transform:uppercase; letter-spacing:.3px; margin:14px 0 8px; }
.radio-grid { display:grid; grid-template-columns:1fr 1fr; gap:4px 12px; }
.form-check-input:checked { background-color:var(--brand); border-color:var(--brand); }
.btn-green { background:var(--brand); color:#fff; } .btn-green:hover { background:#0E9F67; color:#fff; }
</style>
