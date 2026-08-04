<script setup>
import { ref } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import DemoDataBanner from '../components/ui/DemoDataBanner.vue'
import AppButton from '../components/ui/AppButton.vue'
import SearchBar from '../components/ui/SearchBar.vue'
import DataTable from '../components/ui/DataTable.vue'
import AppModal from '../components/ui/AppModal.vue'
import ConfirmDialog from '../components/ui/ConfirmDialog.vue'
import FormField from '../components/ui/FormField.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import StatusBadge from '../components/ui/StatusBadge.vue'
import { useKhachHang } from '../composables/useKhachHang'
import { useToast } from '../composables/useToast'
import { crudErrorMessage } from '../composables/useCrud'
import { diaChiApi } from '../api/diaChi'

const { keyword, filtered, add, update, remove, load, isDemo } = useKhachHang()
const { notify } = useToast()

const tab = ref('kh')   // 'kh' | 'diachi'

const columns = [
  { key: 'ma', label: 'Mã KH' },
  { key: 'ten', label: 'Tên' },
  { key: 'gioiTinh', label: 'Giới tính' },
  { key: 'sdt', label: 'SĐT' },
  { key: 'email', label: 'Email' },
  { key: 'diaChi', label: 'Địa chỉ' },
  { key: 'trangThai', label: 'Trạng thái' }
]

const gioiTinhOptions = ['Nam', 'Nữ']

function blankForm() {
  return { id: null, ma: '', ten: '', gioiTinh: 'Nam', sdt: '', email: '', diaChi: '', trangThai: true }
}

const modalOpen = ref(false)
const form = ref(blankForm())
const confirmOpen = ref(false)
const target = ref(null)

function openCreate() {
  form.value = blankForm()
  modalOpen.value = true
}

function openEdit(row) {
  form.value = JSON.parse(JSON.stringify(row))
  modalOpen.value = true
}

async function save() {
  try {
    if (form.value.id) {
      await update(form.value)
    } else {
      await add(form.value)
    }
    modalOpen.value = false
    notify('Đã lưu', 'success')
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
async function saveAddr() {
  if (!addrKhId.value) { notify('Chọn khách hàng trước', 'warning'); return }
  if (!addrForm.value.diaChiMacDinh && !addrForm.value.thanhPho) { notify('Nhập địa chỉ', 'warning'); return }
  try {
    if (addrForm.value.id) await diaChiApi.update(addrForm.value.id, addrForm.value)
    else await diaChiApi.create({ ...addrForm.value, idKhachHang: Number(addrKhId.value) })
    notify('Đã lưu địa chỉ', 'success'); await loadAddresses()
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
    <PageHeader title="Khách hàng">
      <template #actions>
        <AppButton icon="plus-lg" @click="openCreate">Thêm khách hàng</AppButton>
      </template>
    </PageHeader>

    <div class="kh-tabs mb-3">
      <button class="kh-tab" :class="{ active: tab === 'kh' }" @click="tab = 'kh'">Khách hàng</button>
      <button class="kh-tab" :class="{ active: tab === 'diachi' }" @click="tab = 'diachi'">Địa chỉ</button>
    </div>

    <div v-show="tab === 'kh'">
    <SearchBar v-model="keyword" class="mb-3" placeholder="Tìm theo mã / tên / SĐT..." />

    <DataTable :columns="columns" :rows="filtered">
      <template #cell-trangThai="{ value }"><StatusBadge :active="value" /></template>
      <template #actions="{ row }">
        <AppButton size="sm" variant="outline-secondary" icon="pencil" @click="openEdit(row)">Sửa</AppButton>
        <AppButton size="sm" variant="outline-danger" icon="trash" class="ms-1" @click="askDelete(row)">Xoá</AppButton>
      </template>
    </DataTable>
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
            <button class="btn btn-success flex-fill" :disabled="!addrKhId" @click="saveAddr">Lưu</button>
          </div>
        </div></div></div>
      </div>
    </div>

    <AppModal v-model:open="modalOpen" :title="form.id ? 'Sửa khách hàng' : 'Thêm khách hàng'">
      <FormField label="Tên"><input class="form-control" v-model="form.ten"></FormField>
      <FormField label="Giới tính"><AppSelect v-model="form.gioiTinh" :options="gioiTinhOptions" /></FormField>
      <FormField label="Số điện thoại"><input class="form-control" v-model="form.sdt"></FormField>
      <FormField label="Email"><input type="email" class="form-control" v-model="form.email"></FormField>
      <FormField label="Địa chỉ"><input class="form-control" v-model="form.diaChi"></FormField>
      <div class="form-check">
        <input class="form-check-input" type="checkbox" v-model="form.trangThai" id="kh-trangthai">
        <label class="form-check-label" for="kh-trangthai">Hoạt động</label>
      </div>
      <template #footer>
        <AppButton variant="secondary" @click="modalOpen = false">Huỷ</AppButton>
        <AppButton @click="save">Lưu</AppButton>
      </template>
    </AppModal>

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
.kh-addr-table thead th { position: sticky; top: 0; background: var(--c-primary); color: #fff; font-size: 12px; white-space: nowrap; }
.kh-addr-table tbody tr.sel > td { background: var(--c-primary-subtle); }
</style>
