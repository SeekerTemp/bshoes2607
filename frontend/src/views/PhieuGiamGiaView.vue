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
import { usePhieuGiamGia } from '../composables/usePhieuGiamGia'
import { useToast } from '../composables/useToast'
import { crudErrorMessage } from '../composables/useCrud'
import { vnd } from '../utils/format'

const { keyword, filtered, add, update, remove, load, isDemo } = usePhieuGiamGia()
const { notify } = useToast()

const columns = [
  { key: 'ma', label: 'Mã' },
  { key: 'ten', label: 'Tên' },
  { key: 'loai', label: 'Loại' },
  { key: 'giaTri', label: 'Giá trị', align: 'end' },
  { key: 'donToiThieu', label: 'Đơn tối thiểu', align: 'end' },
  { key: 'soLuong', label: 'SL', align: 'end' },
  { key: 'batDau', label: 'Bắt đầu' },
  { key: 'ketThuc', label: 'Kết thúc' },
  { key: 'trangThai', label: 'Trạng thái' }
]

const loaiOptions = [
  { value: 0, label: 'Phần trăm' },
  { value: 1, label: 'Số tiền' }
]

function blankForm() {
  return { id: null, ma: '', ten: '', loai: 0, giaTri: 0, donToiThieu: 0, giamToiDa: 0, soLuong: 0, batDau: '', ketThuc: '', trangThai: true }
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
</script>

<template>
  <AppShell>
    <DemoDataBanner v-if="isDemo" what="danh sách phiếu giảm giá" @retry="load" />
    <PageHeader title="Phiếu giảm giá">
      <template #actions>
        <AppButton icon="plus-lg" @click="openCreate">Thêm phiếu</AppButton>
      </template>
    </PageHeader>

    <SearchBar v-model="keyword" class="mb-3" placeholder="Tìm theo mã / tên..." />

    <DataTable :columns="columns" :rows="filtered">
      <template #cell-loai="{ value }">{{ value === 0 ? '%' : 'Tiền' }}</template>
      <template #cell-giaTri="{ row }">{{ row.loai === 0 ? row.giaTri + '%' : vnd(row.giaTri) }}</template>
      <template #cell-donToiThieu="{ value }">{{ vnd(value) }}</template>
      <template #cell-trangThai="{ value }"><StatusBadge :active="value" /></template>
      <template #actions="{ row }">
        <AppButton size="sm" variant="outline-secondary" icon="pencil" @click="openEdit(row)">Sửa</AppButton>
        <AppButton size="sm" variant="outline-danger" icon="trash" class="ms-1" @click="askDelete(row)">Xoá</AppButton>
      </template>
    </DataTable>

    <AppModal v-model:open="modalOpen" :title="form.id ? 'Sửa phiếu giảm giá' : 'Thêm phiếu giảm giá'">
      <FormField label="Tên"><input class="form-control" v-model="form.ten"></FormField>
      <FormField label="Loại"><AppSelect v-model.number="form.loai" :options="loaiOptions" /></FormField>
      <div class="row">
        <div class="col"><FormField label="Giá trị giảm"><input type="number" class="form-control" v-model.number="form.giaTri"></FormField></div>
        <div class="col"><FormField label="Số lượng"><input type="number" class="form-control" v-model.number="form.soLuong"></FormField></div>
      </div>
      <div class="row">
        <div class="col"><FormField label="Đơn tối thiểu"><input type="number" class="form-control" v-model.number="form.donToiThieu"></FormField></div>
        <div class="col"><FormField label="Giảm tối đa"><input type="number" class="form-control" v-model.number="form.giamToiDa"></FormField></div>
      </div>
      <div class="row">
        <div class="col"><FormField label="Bắt đầu"><input type="date" class="form-control" v-model="form.batDau"></FormField></div>
        <div class="col"><FormField label="Kết thúc"><input type="date" class="form-control" v-model="form.ketThuc"></FormField></div>
      </div>
      <div class="form-check">
        <input class="form-check-input" type="checkbox" v-model="form.trangThai" id="pgg-trangthai">
        <label class="form-check-label" for="pgg-trangthai">Hoạt động</label>
      </div>
      <template #footer>
        <AppButton variant="secondary" @click="modalOpen = false">Huỷ</AppButton>
        <AppButton @click="save">Lưu</AppButton>
      </template>
    </AppModal>

    <ConfirmDialog v-model:open="confirmOpen" :message="`Xoá phiếu ${target?.ma}?`" @confirm="doDelete" />
  </AppShell>
</template>
