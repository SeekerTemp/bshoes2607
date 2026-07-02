<script setup>
import { ref } from 'vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppButton from '../components/ui/AppButton.vue'
import SearchBar from '../components/ui/SearchBar.vue'
import DataTable from '../components/ui/DataTable.vue'
import AppModal from '../components/ui/AppModal.vue'
import ConfirmDialog from '../components/ui/ConfirmDialog.vue'
import FormField from '../components/ui/FormField.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import StatusBadge from '../components/ui/StatusBadge.vue'
import StatCard from '../components/ui/StatCard.vue'
import ToastHost from '../components/ui/ToastHost.vue'
import { useToast } from '../composables/useToast'

const { notify } = useToast()

// SearchBar demo
const keyword = ref('')

// DataTable demo
const columns = [
  { key: 'code', label: 'Mã' },
  { key: 'name', label: 'Tên' },
  { key: 'status', label: 'Trạng thái' },
  { key: 'price', label: 'Giá', align: 'end' }
]
const rows = [
  { id: 1, code: 'SP001', name: 'Giày thể thao Nam', status: true, price: '1.200.000đ' },
  { id: 2, code: 'SP002', name: 'Giày da công sở', status: false, price: '2.500.000đ' },
  { id: 3, code: 'SP003', name: 'Dép sandal', status: true, price: '350.000đ' }
]

function editRow(row) {
  notify(`Sửa ${row.name}`, 'info')
}
function deleteRow(row) {
  notify(`Đã xoá ${row.name}`, 'danger')
}

// AppModal demo
const showModal = ref(false)
const formName = ref('')
const formCategory = ref('')
const categoryOptions = [
  { value: '', label: '-- Chọn loại --' },
  { value: 'the-thao', label: 'Thể thao' },
  { value: 'cong-so', label: 'Công sở' },
  { value: 'sandal', label: 'Sandal' }
]

// ConfirmDialog demo
const showConfirm = ref(false)
function onConfirmDelete() {
  showConfirm.value = false
  notify('Đã xác nhận xoá', 'success')
}

function showToast() {
  notify('Thao tác thành công!', 'success')
}
</script>

<template>
  <div class="container-fluid py-4">
    <PageHeader title="Style Guide — Design System">
      <template #actions>
        <AppButton variant="outline-secondary" icon="arrow-clockwise">Làm mới</AppButton>
        <AppButton variant="primary" icon="plus-lg">Thêm mới</AppButton>
      </template>
    </PageHeader>

    <!-- AppButton -->
    <section class="mb-5">
      <h5 class="mb-3">AppButton</h5>
      <div class="d-flex flex-wrap gap-2 mb-2">
        <AppButton variant="primary">Primary</AppButton>
        <AppButton variant="secondary">Secondary</AppButton>
        <AppButton variant="outline-secondary">Outline Secondary</AppButton>
        <AppButton variant="outline-danger">Outline Danger</AppButton>
        <AppButton variant="danger">Danger</AppButton>
      </div>
      <div class="d-flex flex-wrap align-items-center gap-2 mb-2">
        <AppButton variant="primary" size="sm">Small</AppButton>
        <AppButton variant="primary">Default</AppButton>
        <AppButton variant="primary" size="lg">Large</AppButton>
      </div>
      <div class="d-flex flex-wrap gap-2">
        <AppButton variant="primary" icon="plus-lg">Thêm</AppButton>
        <AppButton variant="outline-secondary" icon="pencil">Sửa</AppButton>
        <AppButton variant="outline-danger" icon="trash">Xoá</AppButton>
      </div>
    </section>

    <!-- SearchBar -->
    <section class="mb-5">
      <h5 class="mb-3">SearchBar</h5>
      <SearchBar v-model="keyword" placeholder="Tìm kiếm sản phẩm..." />
      <p class="text-muted small mt-2">Giá trị hiện tại: {{ keyword || '(trống)' }}</p>
    </section>

    <!-- DataTable -->
    <section class="mb-5">
      <h5 class="mb-3">DataTable</h5>
      <div class="border rounded overflow-hidden">
        <DataTable :columns="columns" :rows="rows">
          <template #cell-status="{ value }">
            <StatusBadge :active="value" />
          </template>
          <template #actions="{ row }">
            <AppButton variant="outline-secondary" size="sm" icon="pencil" @click="editRow(row)">Sửa</AppButton>
            <AppButton variant="outline-danger" size="sm" icon="trash" class="ms-1" @click="deleteRow(row)">Xoá</AppButton>
          </template>
        </DataTable>
      </div>
    </section>

    <!-- AppModal -->
    <section class="mb-5">
      <h5 class="mb-3">AppModal</h5>
      <AppButton variant="primary" icon="box-arrow-up-right" @click="showModal = true">Mở Modal</AppButton>
      <AppModal v-model:open="showModal" title="Thêm sản phẩm">
        <FormField label="Tên sản phẩm">
          <input class="form-control" v-model="formName" placeholder="Nhập tên sản phẩm" />
        </FormField>
        <FormField label="Loại sản phẩm">
          <AppSelect v-model="formCategory" :options="categoryOptions" />
        </FormField>
        <template #footer>
          <AppButton variant="secondary" @click="showModal = false">Huỷ</AppButton>
          <AppButton variant="primary" @click="showModal = false; notify('Đã lưu sản phẩm')">Lưu</AppButton>
        </template>
      </AppModal>
    </section>

    <!-- ConfirmDialog -->
    <section class="mb-5">
      <h5 class="mb-3">ConfirmDialog</h5>
      <AppButton variant="danger" icon="trash" @click="showConfirm = true">Xoá sản phẩm</AppButton>
      <ConfirmDialog
        v-model:open="showConfirm"
        title="Xác nhận xoá"
        message="Bạn có chắc chắn muốn xoá sản phẩm này không?"
        @confirm="onConfirmDelete"
      />
    </section>

    <!-- StatusBadge -->
    <section class="mb-5">
      <h5 class="mb-3">StatusBadge</h5>
      <div class="d-flex gap-2">
        <StatusBadge :active="true" />
        <StatusBadge :active="false" />
      </div>
    </section>

    <!-- StatCard -->
    <section class="mb-5">
      <h5 class="mb-3">StatCard</h5>
      <div class="row g-3">
        <div class="col-md-3 col-sm-6">
          <StatCard label="Doanh thu hôm nay" value="12.500.000đ" />
        </div>
        <div class="col-md-3 col-sm-6">
          <StatCard label="Đơn hàng" value="24" />
        </div>
        <div class="col-md-3 col-sm-6">
          <StatCard label="Khách hàng mới" value="8" />
        </div>
        <div class="col-md-3 col-sm-6">
          <StatCard label="Sản phẩm bán chạy" value="Giày thể thao" />
        </div>
      </div>
    </section>

    <!-- Toast -->
    <section class="mb-5">
      <h5 class="mb-3">Toast / useToast</h5>
      <AppButton variant="primary" icon="bell" @click="showToast">Hiện thông báo</AppButton>
    </section>

    <ToastHost />
  </div>
</template>
