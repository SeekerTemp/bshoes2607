<script setup>
import { ref, onMounted } from 'vue'
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
import PhanQuyenPanel from '../components/panels/PhanQuyenPanel.vue'
import VaiTroPanel from '../components/panels/VaiTroPanel.vue'
import { useNhanVien } from '../composables/useNhanVien'
import { useToast } from '../composables/useToast'
import { crudErrorMessage } from '../composables/useCrud'
import { vaiTroApi } from '../api/vaiTro'

const { keyword, filtered, add, update, remove } = useNhanVien()
const { notify } = useToast()

const tab = ref('ds')   // 'ds' | 'quyen' | 'vaitro'

const columns = [
  { key: 'ma', label: 'Mã NV' },
  { key: 'ten', label: 'Tên' },
  { key: 'taiKhoan', label: 'Tài khoản' },
  { key: 'email', label: 'Email' },
  { key: 'sdt', label: 'SĐT' },
  { key: 'cccd', label: 'CCCD' },
  { key: 'chucVu', label: 'Chức vụ' },
  { key: 'vaiTro', label: 'Vai trò' },
  { key: 'trangThai', label: 'Trạng thái' }
]

const gioiTinhOptions = ['Nam', 'Nữ']
const vaiTroOptions = ref([])

onMounted(async () => {
  try {
    const list = await vaiTroApi.findAll()
    vaiTroOptions.value = list.map(v => ({ value: v.id, label: v.ten }))
  } catch (e) {
    console.warn('Không tải được danh sách vai trò', e)
  }
})

function blankForm() {
  return { id: null, ma: '', ten: '', taiKhoan: '', email: '', sdt: '', cccd: '', chucVu: '', gioiTinh: 'Nam', idVaiTro: null, matKhau: '', trangThai: true }
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
  form.value.matKhau = ''
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
    <PageHeader title="Nhân viên">
      <template #actions>
        <AppButton v-if="tab === 'ds'" icon="plus-lg" @click="openCreate">Thêm nhân viên</AppButton>
      </template>
    </PageHeader>

    <div class="nv-tabs mb-3">
      <button class="nv-tab" :class="{ active: tab === 'ds' }" @click="tab = 'ds'">Danh sách</button>
      <button class="nv-tab" :class="{ active: tab === 'quyen' }" @click="tab = 'quyen'">Phân quyền</button>
      <button class="nv-tab" :class="{ active: tab === 'vaitro' }" @click="tab = 'vaitro'">Vai trò (template)</button>
    </div>

    <div v-show="tab === 'ds'">
      <SearchBar v-model="keyword" class="mb-3" placeholder="Tìm theo mã / tên / tài khoản..." />

      <DataTable :columns="columns" :rows="filtered">
        <template #cell-trangThai="{ value }"><StatusBadge :active="value" /></template>
        <template #actions="{ row }">
          <AppButton size="sm" variant="outline-secondary" icon="pencil" @click="openEdit(row)">Sửa</AppButton>
          <AppButton size="sm" variant="outline-danger" icon="trash" class="ms-1" @click="askDelete(row)">Xoá</AppButton>
        </template>
      </DataTable>
    </div>

    <!-- v-if để panel chỉ gọi API khi thực sự mở tab -->
    <PhanQuyenPanel v-if="tab === 'quyen'" />
    <VaiTroPanel v-if="tab === 'vaitro'" />

    <AppModal v-model:open="modalOpen" :title="form.id ? 'Sửa nhân viên' : 'Thêm nhân viên'">
      <FormField label="Tên"><input class="form-control" v-model="form.ten"></FormField>
      <div class="row">
        <div class="col"><FormField label="Tài khoản"><input class="form-control" v-model="form.taiKhoan"></FormField></div>
        <div class="col"><FormField label="Email"><input type="email" class="form-control" v-model="form.email"></FormField></div>
      </div>
      <div class="row">
        <div class="col"><FormField label="Số điện thoại"><input class="form-control" v-model="form.sdt"></FormField></div>
        <div class="col"><FormField label="CCCD"><input class="form-control" v-model="form.cccd"></FormField></div>
      </div>
      <div class="row">
        <div class="col"><FormField label="Chức vụ"><input class="form-control" v-model="form.chucVu"></FormField></div>
        <div class="col"><FormField label="Giới tính"><AppSelect v-model="form.gioiTinh" :options="gioiTinhOptions" /></FormField></div>
      </div>
      <FormField label="Vai trò"><AppSelect v-model="form.idVaiTro" :options="vaiTroOptions" /></FormField>
      <FormField label="Mật khẩu">
        <input type="password" class="form-control" v-model="form.matKhau">
        <small class="text-muted" v-if="form.id">Để trống nếu không muốn đổi mật khẩu hiện tại.</small>
      </FormField>
      <div class="form-check">
        <input class="form-check-input" type="checkbox" v-model="form.trangThai" id="nv-trangthai">
        <label class="form-check-label" for="nv-trangthai">Hoạt động</label>
      </div>
      <template #footer>
        <AppButton variant="secondary" @click="modalOpen = false">Huỷ</AppButton>
        <AppButton @click="save">Lưu</AppButton>
      </template>
    </AppModal>

    <ConfirmDialog v-model:open="confirmOpen" :message="`Xoá nhân viên ${target?.ma}?`" @confirm="doDelete" />
  </AppShell>
</template>

<style scoped>
/* cùng pattern tab với KhachHangView / SanPhamView */
.nv-tabs { display: flex; gap: 4px; }
.nv-tab {
  border: 1px solid #e5e9ef; background: #eef1f4; color: #6b7280;
  border-radius: 8px; padding: 8px 20px; font-size: 13px; font-weight: 600; cursor: pointer;
}
.nv-tab.active { background: var(--c-primary, #0B895A); color: #fff; border-color: var(--c-primary, #0B895A); }
</style>
