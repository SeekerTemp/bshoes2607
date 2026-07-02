<script setup>
import { ref, computed } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppButton from '../components/ui/AppButton.vue'
import SearchBar from '../components/ui/SearchBar.vue'
import DataTable from '../components/ui/DataTable.vue'
import AppModal from '../components/ui/AppModal.vue'
import FormField from '../components/ui/FormField.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import { useSanPham } from '../composables/useSanPham'
import { useToast } from '../composables/useToast'
import { vnd } from '../utils/format'

const { filtered, keyword, add, update, thuongHieuList, chatLieuList } = useSanPham()
const { notify } = useToast()

const columns = [
  { key: 'ma', label: 'Mã' },
  { key: 'ten', label: 'Tên' },
  { key: 'thuongHieu', label: 'Thương hiệu' },
  { key: 'chatLieu', label: 'Chất liệu' },
  { key: 'gia', label: 'Giá', align: 'end' }
]

const brandOptions = computed(() => [{ value: '', label: '-- Thương hiệu --' }, ...thuongHieuList.map(t => ({ value: t, label: t }))])
const locThuongHieu = ref('')

const visible = computed(() => filtered.value.filter(p => !locThuongHieu.value || p.thuongHieu === locThuongHieu.value))

const chon = ref(null)
function chonSP(p) {
  chon.value = p
}

function blankForm() {
  return { id: null, ten: '', thuongHieu: thuongHieuList[0], chatLieu: chatLieuList[0], gia: 0, moTa: '' }
}

const modalOpen = ref(false)
const form = ref(blankForm())

function openCreate() {
  form.value = blankForm()
  modalOpen.value = true
}

function openEdit(row) {
  form.value = { id: row.id, ten: row.ten, thuongHieu: row.thuongHieu, chatLieu: row.chatLieu, gia: row.gia, moTa: row.moTa }
  modalOpen.value = true
}

function save() {
  if (form.value.id) {
    const existing = filtered.value.find(r => r.id === form.value.id)
    update({ ...existing, ...form.value })
  } else {
    add({ ...form.value, bienThe: [] })
  }
  modalOpen.value = false
  notify('Đã lưu', 'success')
}
</script>

<template>
  <AppShell>
    <PageHeader title="Quản lý sản phẩm">
      <template #actions>
        <AppButton icon="plus-lg" @click="openCreate">Thêm sản phẩm</AppButton>
      </template>
    </PageHeader>

    <div class="d-flex gap-2 mb-3">
      <SearchBar v-model="keyword" placeholder="Tìm mã / tên..." />
      <AppSelect v-model="locThuongHieu" :options="brandOptions" style="max-width: 200px" />
    </div>

    <div class="row g-3">
      <div class="col-7">
        <DataTable :columns="columns" :rows="visible">
          <template #cell-gia="{ value }">{{ vnd(value) }}</template>
          <template #actions="{ row }">
            <AppButton size="sm" variant="outline-secondary" @click="chonSP(row)">Chọn</AppButton>
            <AppButton size="sm" variant="outline-secondary" class="ms-1" @click="openEdit(row)">Sửa</AppButton>
          </template>
        </DataTable>
      </div>
      <div class="col-5">
        <div class="card">
          <div class="card-body">
            <h6 v-if="!chon" class="text-muted mb-0">Chọn 1 sản phẩm để xem biến thể</h6>
            <div v-else>
              <h6 class="mb-2">Biến thể: {{ chon.ten }}</h6>
              <table class="table table-sm mb-0">
                <thead>
                  <tr><th>Màu</th><th>Size</th><th class="text-end">Tồn</th><th class="text-end">Đơn giá</th></tr>
                </thead>
                <tbody>
                  <tr v-for="v in chon.bienThe" :key="v.ma">
                    <td>{{ v.mau }}</td><td>{{ v.size }}</td><td class="text-end">{{ v.ton }}</td><td class="text-end">{{ vnd(v.gia) }}</td>
                  </tr>
                  <tr v-if="!chon.bienThe || !chon.bienThe.length">
                    <td colspan="4" class="text-center text-muted">Không có biến thể</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <AppModal v-model:open="modalOpen" :title="form.id ? 'Sửa sản phẩm' : 'Thêm sản phẩm'">
      <FormField label="Tên"><input class="form-control" v-model="form.ten"></FormField>
      <div class="row">
        <div class="col">
          <FormField label="Thương hiệu"><AppSelect v-model="form.thuongHieu" :options="thuongHieuList" /></FormField>
        </div>
        <div class="col">
          <FormField label="Chất liệu"><AppSelect v-model="form.chatLieu" :options="chatLieuList" /></FormField>
        </div>
      </div>
      <FormField label="Giá"><input type="number" class="form-control" v-model.number="form.gia"></FormField>
      <FormField label="Mô tả"><textarea class="form-control" v-model="form.moTa"></textarea></FormField>
      <template #footer>
        <AppButton variant="secondary" @click="modalOpen = false">Huỷ</AppButton>
        <AppButton @click="save">Lưu</AppButton>
      </template>
    </AppModal>
  </AppShell>
</template>
