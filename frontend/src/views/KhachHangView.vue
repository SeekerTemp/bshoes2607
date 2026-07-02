<script setup>
import { ref } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
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

const { keyword, filtered, add, update, remove } = useKhachHang()
const { notify } = useToast()

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

function save() {
  if (form.value.id) {
    update(form.value)
  } else {
    add(form.value)
  }
  modalOpen.value = false
  notify('Đã lưu', 'success')
}

function askDelete(row) {
  target.value = row
  confirmOpen.value = true
}

function doDelete() {
  remove(target.value.id)
  confirmOpen.value = false
  notify('Đã xoá', 'success')
}
</script>

<template>
  <AppShell>
    <PageHeader title="Khách hàng">
      <template #actions>
        <AppButton icon="plus-lg" @click="openCreate">Thêm khách hàng</AppButton>
      </template>
    </PageHeader>

    <SearchBar v-model="keyword" class="mb-3" placeholder="Tìm theo mã / tên / SĐT..." />

    <DataTable :columns="columns" :rows="filtered">
      <template #cell-trangThai="{ value }"><StatusBadge :active="value" /></template>
      <template #actions="{ row }">
        <AppButton size="sm" variant="outline-secondary" icon="pencil" @click="openEdit(row)">Sửa</AppButton>
        <AppButton size="sm" variant="outline-danger" icon="trash" class="ms-1" @click="askDelete(row)">Xoá</AppButton>
      </template>
    </DataTable>

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
