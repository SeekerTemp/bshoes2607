<script setup>
import { ref } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import DemoDataBanner from '../components/ui/DemoDataBanner.vue'
import SearchBar from '../components/ui/SearchBar.vue'
import ConfirmDialog from '../components/ui/ConfirmDialog.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import InspectorPanel from '../components/ui/InspectorPanel.vue'
import InspectorField from '../components/ui/InspectorField.vue'
import { useKhachHang } from '../composables/useKhachHang'
import { useToast } from '../composables/useToast'
import { crudErrorMessage } from '../composables/useCrud'
import { diaChiApi } from '../api/diaChi'

const { keyword, filtered, add, update, remove, load, isDemo } = useKhachHang()
const { notify } = useToast()

const tab = ref('kh')   // 'kh' | 'diachi'

const gioiTinhOptions = ['Nam', 'Nữ']

function blankForm() {
  return { id: null, ma: '', ten: '', gioiTinh: 'Nam', sdt: '', email: '', diaChi: '', trangThai: true }
}

// Master-detail thay cho modal: chọn dòng là form bên phải điền luôn, không phải mở /
// đóng popup cho từng lần sửa.
const form = ref(blankForm())
const selectedId = ref(null)
const confirmOpen = ref(false)
const target = ref(null)

function selectRow(row) {
  selectedId.value = row.id
  form.value = { ...blankForm(), ...JSON.parse(JSON.stringify(row)) }
}

// "Làm mới" = XOÁ TRẮNG inspector. Trước đây nó nạp lại dòng đang chọn, nên bấm vào
// không thấy gì thay đổi và không có cách nào dọn form về rỗng.
function lamMoi() {
  selectedId.value = null
  form.value = blankForm()
}

// Chỉ SĐT là bắt buộc — bảng khach_hang không có cột CCCD.
function loiForm() {
  if (!form.value.ten?.trim()) return 'Nhập tên khách hàng'
  if (!form.value.sdt?.trim()) return 'Số điện thoại là bắt buộc'
  if (!/^0\d{8,10}$/.test(form.value.sdt.trim())) return 'Số điện thoại không hợp lệ (bắt đầu bằng 0, 9–11 chữ số)'
  return ''
}

/*
 * Hai nút, hai việc rõ ràng — trước đây chỉ có một nút tự đổi nghĩa theo việc đang
 * chọn dòng hay không, nên thao tác "chọn một khách hàng, sửa tên, tạo thành khách
 * hàng mới" là KHÔNG THỂ: nút đã âm thầm chuyển thành Lưu (Sửa) và ghi đè bản gốc.
 */

/** Luôn TẠO MỚI từ nội dung inspector hiện tại, bỏ id + mã (server tự sinh mã). */
async function taoMoi() {
  const loi = loiForm()
  if (loi) { notify(loi, 'warning'); return }
  try {
    await add({ ...form.value, id: null, ma: '' })
    notify('Đã thêm khách hàng', 'success')
    lamMoi()
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

/** Chỉ CẬP NHẬT dòng đang chọn. */
async function luuSua() {
  if (!form.value.id) { notify('Chọn khách hàng trong danh sách để sửa', 'warning'); return }
  const loi = loiForm()
  if (loi) { notify(loi, 'warning'); return }
  try {
    await update(form.value)
    notify('Đã cập nhật khách hàng', 'success')
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

function askDelete(row) {
  target.value = row
  confirmOpen.value = true
}

async function doDelete() {
  try {
    await remove(target.value.id)
    confirmOpen.value = false
    if (target.value.id === selectedId.value) lamMoi()
    notify('Đã xoá', 'success')
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

/* ===== Địa chỉ (tab 2) — CRUD addresses per customer ===== */
const addrKhId = ref('')
const addresses = ref([])
const addrForm = ref(blankAddr())
function blankAddr() { return { id: null, diaChiMacDinh: '', thanhPho: '', phuong: '', diaChiThem: '', trangThai: true } }
async function loadAddresses() {
  if (!addrKhId.value) { addresses.value = []; addrForm.value = blankAddr(); return }
  try { addresses.value = await diaChiApi.byKhachHang(addrKhId.value) }
  catch (e) { notify('Không tải được địa chỉ (backend offline?)', 'warning'); addresses.value = [] }
  addrForm.value = blankAddr()
}
function editAddr(a) { addrForm.value = { ...a } }
function newAddr() { addrForm.value = blankAddr() }

function loiAddr() {
  if (!addrKhId.value) return 'Chọn khách hàng trước'
  if (!addrForm.value.diaChiMacDinh && !addrForm.value.thanhPho) return 'Nhập địa chỉ'
  return ''
}
// Cùng quy ước với inspector khách hàng: Tạo mới luôn tạo, Lưu (Sửa) chỉ sửa.
async function taoMoiAddr() {
  const loi = loiAddr()
  if (loi) { notify(loi, 'warning'); return }
  try {
    await diaChiApi.create({ ...addrForm.value, id: null, idKhachHang: Number(addrKhId.value) })
    notify('Đã thêm địa chỉ', 'success'); await loadAddresses()
  } catch (e) { notify('Lưu địa chỉ thất bại', 'warning') }
}
async function saveAddr() {
  if (!addrForm.value.id) { notify('Chọn địa chỉ trong danh sách để sửa', 'warning'); return }
  const loi = loiAddr()
  if (loi) { notify(loi, 'warning'); return }
  try {
    await diaChiApi.update(addrForm.value.id, addrForm.value)
    notify('Đã cập nhật địa chỉ', 'success'); await loadAddresses()
  } catch (e) { notify('Lưu địa chỉ thất bại', 'warning') }
}
async function removeAddr(a) {
  if (!confirm('Xoá địa chỉ này?')) return
  try { await diaChiApi.remove(a.id); notify('Đã xoá địa chỉ', 'success'); await loadAddresses() }
  catch (e) { notify('Xoá thất bại', 'warning') }
}
</script>

<template>
  <AppShell>
    <DemoDataBanner v-if="isDemo" what="danh sách khách hàng" @retry="load" />
    <PageHeader title="Khách hàng" />

    <div class="kh-tabs mb-3">
      <button class="kh-tab" :class="{ active: tab === 'kh' }" @click="tab = 'kh'">Khách hàng</button>
      <button class="kh-tab" :class="{ active: tab === 'diachi' }" @click="tab = 'diachi'">Địa chỉ</button>
    </div>

    <!-- Master-detail: bảng bên trái, inspector bên phải (bỏ modal) -->
    <div v-show="tab === 'kh'" class="kh-grid">
      <div class="kh-master">
        <SearchBar v-model="keyword" class="mb-2" placeholder="Tìm theo mã / tên / SĐT..." />
        <div class="table-scroll">
          <table class="table table-sm table-hover align-middle mb-0 kh-table">
            <thead><tr>
              <th class="text-center">STT</th><th>Mã KH</th><th>Tên</th><th>Giới tính</th>
              <th>SĐT</th><th>Email</th><th>Địa chỉ</th><th>Trạng thái</th><th></th>
            </tr></thead>
            <tbody>
              <tr v-for="(k, i) in filtered" :key="k.id" :class="{ 'row-active': k.id === selectedId }" @click="selectRow(k)">
                <td class="text-center">{{ i + 1 }}</td>
                <td class="fw-medium">{{ k.ma }}</td><td>{{ k.ten }}</td><td>{{ k.gioiTinh }}</td>
                <td>{{ k.sdt }}</td><td>{{ k.email }}</td><td>{{ k.diaChi }}</td>
                <td>{{ k.trangThai ? 'Hoạt động' : 'Ngừng' }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-danger py-0 px-2" @click.stop="askDelete(k)" title="Xoá">
                    <i class="bi bi-trash"></i>
                  </button>
                </td>
              </tr>
              <tr v-if="!filtered.length"><td colspan="9" class="text-center text-muted py-4">Không có khách hàng</td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <InspectorPanel :tab-label="form.id ? 'Sửa khách hàng' : 'Thêm khách hàng'" title="Thông tin khách hàng">
        <!-- Mã do server sinh ("KH" + id) khi tạo, nên hiển thị dạng chữ chứ không phải ô nhập. -->
        <InspectorField label="Mã KH">
          <p class="ins-static" :class="{ 'chua-co': !form.ma }">{{ form.ma || 'Tự sinh khi lưu' }}</p>
        </InspectorField>
        <InspectorField label="Tên" required>
          <input class="form-control form-control-sm" v-model="form.ten">
        </InspectorField>
        <InspectorField label="Giới tính">
          <AppSelect v-model="form.gioiTinh" :options="gioiTinhOptions" />
        </InspectorField>
        <InspectorField label="Số điện thoại" required>
          <input class="form-control form-control-sm" v-model="form.sdt" :class="{ 'is-invalid': !form.sdt }" placeholder="0xxxxxxxxx">
        </InspectorField>
        <InspectorField label="Email">
          <input type="email" class="form-control form-control-sm" v-model="form.email">
        </InspectorField>
        <InspectorField label="Địa chỉ">
          <input class="form-control form-control-sm" v-model="form.diaChi">
        </InspectorField>
        <InspectorField label="Trạng thái">
          <label class="kh-radio"><input type="radio" :value="true" v-model="form.trangThai"> Hoạt động</label>
          <label class="kh-radio ms-3"><input type="radio" :value="false" v-model="form.trangThai"> Ngừng</label>
        </InspectorField>

        <template #actions>
          <button class="btn btn-success w-100" @click="taoMoi">Tạo mới</button>
          <button class="btn btn-success w-100" :disabled="!form.id" @click="luuSua">Lưu (Sửa)</button>
          <button class="btn btn-success w-100" @click="lamMoi">Làm mới</button>
          <button class="btn btn-outline-danger w-100" :disabled="!form.id" @click="askDelete(form)">Xoá</button>
        </template>
      </InspectorPanel>
    </div>

    <!-- ===== Địa chỉ tab ===== -->
    <div v-show="tab === 'diachi'">
      <div class="row g-3">
        <div class="col-lg-7"><div class="card"><div class="card-body">
          <div class="d-flex gap-2 mb-3 align-items-center flex-wrap">
            <label class="small text-muted mb-0">Khách hàng:</label>
            <select class="form-select form-select-sm" style="max-width:280px" v-model="addrKhId" @change="loadAddresses">
              <option value="">-- Chọn khách hàng --</option>
              <option v-for="k in filtered" :key="k.id" :value="k.id">{{ k.ma }} · {{ k.ten }}</option>
            </select>
            <button class="btn btn-sm btn-success ms-auto" :disabled="!addrKhId" @click="newAddr"><i class="bi bi-plus-lg"></i> Thêm địa chỉ</button>
          </div>
          <div style="max-height:440px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
            <table class="table table-sm table-hover align-middle mb-0 kh-addr-table">
              <thead><tr><th>Địa chỉ</th><th>Phường</th><th>Thành phố</th><th class="text-center">TT</th><th></th></tr></thead>
              <tbody>
                <tr v-for="a in addresses" :key="a.id" @click="editAddr(a)" style="cursor:pointer" :class="{ sel: a.id === addrForm.id }">
                  <td>{{ a.diaChiMacDinh || '—' }}<div class="small text-muted" v-if="a.diaChiThem">{{ a.diaChiThem }}</div></td>
                  <td>{{ a.phuong }}</td><td>{{ a.thanhPho }}</td>
                  <td class="text-center"><i class="bi" :class="a.trangThai !== false ? 'bi-check-circle text-success' : 'bi-slash-circle text-muted'"></i></td>
                  <td class="text-end"><button class="btn btn-sm btn-outline-danger py-0 px-2" @click.stop="removeAddr(a)"><i class="bi bi-trash"></i></button></td>
                </tr>
                <tr v-if="!addrKhId"><td colspan="5" class="text-center text-muted py-4">Chọn khách hàng để xem địa chỉ</td></tr>
                <tr v-else-if="addresses.length === 0"><td colspan="5" class="text-center text-muted py-4">Chưa có địa chỉ</td></tr>
              </tbody>
            </table>
          </div>
        </div></div></div>
        <div class="col-lg-5"><div class="card"><div class="card-body">
          <h6 class="fw-bold mb-3">{{ addrForm.id ? 'Sửa' : 'Thêm' }} địa chỉ</h6>
          <div class="mb-2"><label class="form-label small mb-1">Địa chỉ (số nhà, đường)</label><input class="form-control form-control-sm" v-model="addrForm.diaChiMacDinh"></div>
          <div class="row g-2 mb-2">
            <div class="col-6"><label class="form-label small mb-1">Phường</label><input class="form-control form-control-sm" v-model="addrForm.phuong"></div>
            <div class="col-6"><label class="form-label small mb-1">Thành phố</label><input class="form-control form-control-sm" v-model="addrForm.thanhPho"></div>
          </div>
          <div class="mb-2"><label class="form-label small mb-1">Địa chỉ thêm / ghi chú</label><input class="form-control form-control-sm" v-model="addrForm.diaChiThem"></div>
          <div class="form-check mb-3"><input class="form-check-input" type="checkbox" v-model="addrForm.trangThai" id="addr-tt"><label class="form-check-label" for="addr-tt">Hoạt động</label></div>
          <div class="d-flex gap-2">
            <button class="btn btn-outline-secondary flex-fill" @click="newAddr">Làm mới</button>
            <button class="btn btn-success flex-fill" :disabled="!addrKhId" @click="taoMoiAddr">Tạo mới</button>
            <button class="btn btn-success flex-fill" :disabled="!addrForm.id" @click="saveAddr">Lưu (Sửa)</button>
          </div>
        </div></div></div>
      </div>
    </div>

    <ConfirmDialog v-model:open="confirmOpen" :message="`Xoá khách hàng ${target?.ma}?`" @confirm="doDelete" />
  </AppShell>
</template>

<style scoped>
.kh-tabs { display: flex; gap: 4px; }
.kh-tab {
  padding: 8px 20px; border: 1px solid var(--c-border); border-bottom: none;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
  background: #eef1f4; color: var(--c-text-muted); font-weight: 500; cursor: pointer;
}
.kh-tab.active { background: var(--c-primary); color: #fff; border-color: var(--c-primary); }

.kh-grid { display: grid; grid-template-columns: minmax(0, 1fr) 380px; gap: 16px; align-items: start; }
.kh-master { min-width: 0; }
.table-scroll { overflow: auto; max-height: 560px; border: 1px solid var(--c-border); border-radius: var(--radius-sm); }
.kh-table { min-width: 720px; }
.kh-table thead th {
  position: sticky; top: 0; z-index: 1;
  background: var(--c-primary); color: #fff; font-weight: 600; font-size: 12px; white-space: nowrap;
}
.kh-table tbody td { font-size: 13px; }
.kh-table tbody tr { cursor: pointer; }
.kh-table tbody tr.row-active > td { background: var(--c-primary); color: #fff; }
.kh-radio { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; cursor: pointer; }

@media (max-width: 992px) { .kh-grid { grid-template-columns: 1fr; } }
.kh-addr-table thead th { position: sticky; top: 0; background: var(--c-primary); color: #fff; font-size: 12px; white-space: nowrap; }
.kh-addr-table tbody tr.sel > td { background: var(--c-primary-subtle); }
</style>
