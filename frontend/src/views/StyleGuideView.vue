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

// Colors demo
const colorSwatches = [
  { name: 'Primary', base: '--c-primary', subtle: '--c-primary-subtle', text: '--c-primary-subtle-text' },
  { name: 'Success', base: '--c-success', subtle: '--c-success-subtle', text: '--c-success-text' },
  { name: 'Warning', base: '--c-warning', subtle: '--c-warning-subtle', text: '--c-warning-text' },
  { name: 'Info', base: '--c-info', subtle: '--c-info-subtle', text: '--c-info-text' },
  { name: 'Danger', base: '--c-danger', subtle: '--c-danger-subtle', text: '--c-danger-text' }
]

// AppButton demo
const loadingDemo = ref(false)
function triggerLoadingDemo() {
  loadingDemo.value = true
  setTimeout(() => { loadingDemo.value = false }, 1500)
}

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
  { id: 1, code: 'SP001', name: 'Giày thể thao Nam', status: 'success', price: '1.200.000đ' },
  { id: 2, code: 'SP002', name: 'Giày da công sở', status: 'neutral', price: '2.500.000đ' },
  { id: 3, code: 'SP003', name: 'Dép sandal', status: 'warning', price: '350.000đ' },
  { id: 4, code: 'SP004', name: 'Giày boot da lộn', status: 'danger', price: '1.850.000đ' }
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

function showToast(type) {
  const messages = {
    success: 'Thao tác thành công!',
    warning: 'Cảnh báo: vui lòng kiểm tra lại dữ liệu.',
    danger: 'Đã xảy ra lỗi, thao tác thất bại.',
    info: 'Thông tin: đơn hàng đang được xử lý.'
  }
  notify(messages[type] || messages.success, type)
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

    <!-- Colors -->
    <section class="mb-5">
      <h5 class="mb-1">Colors</h5>
      <p class="text-muted small mb-3">Base + subtle tones for each semantic color used across the app.</p>
      <div class="row g-3">
        <div class="col-md-2 col-sm-4 col-6" v-for="c in colorSwatches" :key="c.name">
          <div class="swatch-base mb-2" :style="{ background: `var(${c.base})` }"></div>
          <div class="swatch-subtle d-flex align-items-center justify-content-center"
               :style="{ background: `var(${c.subtle})`, color: `var(${c.text})` }">
            subtle
          </div>
          <div class="small fw-semibold mt-1">{{ c.name }}</div>
        </div>
      </div>
    </section>

    <!-- AppButton -->
    <section class="mb-5">
      <h5 class="mb-1">Buttons &amp; states</h5>
      <p class="text-muted small mb-3">All semantic variants, sizes, icons, and interaction states.</p>

      <div class="mb-2 small text-muted">Variants</div>
      <div class="d-flex flex-wrap gap-2 mb-3">
        <AppButton variant="primary">Primary</AppButton>
        <AppButton variant="secondary">Secondary</AppButton>
        <AppButton variant="outline-secondary">Outline Secondary</AppButton>
        <AppButton variant="success">Success</AppButton>
        <AppButton variant="warning">Warning</AppButton>
        <AppButton variant="info">Info</AppButton>
        <AppButton variant="outline-danger">Outline Danger</AppButton>
        <AppButton variant="danger">Danger</AppButton>
      </div>

      <div class="mb-2 small text-muted">Sizes</div>
      <div class="d-flex flex-wrap align-items-center gap-2 mb-3">
        <AppButton variant="primary" size="sm">Small</AppButton>
        <AppButton variant="primary">Default</AppButton>
        <AppButton variant="primary" size="lg">Large</AppButton>
      </div>

      <div class="mb-2 small text-muted">With icons</div>
      <div class="d-flex flex-wrap gap-2 mb-3">
        <AppButton variant="primary" icon="plus-lg">Thêm</AppButton>
        <AppButton variant="outline-secondary" icon="pencil">Sửa</AppButton>
        <AppButton variant="outline-danger" icon="trash">Xoá</AppButton>
      </div>

      <div class="mb-2 small text-muted">States — normal / hover (mouse over) / focus (tab to it) / disabled / loading</div>
      <div class="d-flex flex-wrap align-items-center gap-2">
        <AppButton variant="primary">Normal</AppButton>
        <AppButton variant="primary">Hover me</AppButton>
        <AppButton variant="primary">Tab-focus me</AppButton>
        <AppButton variant="primary" disabled>Disabled</AppButton>
        <AppButton variant="primary" :loading="loadingDemo" @click="triggerLoadingDemo">
          {{ loadingDemo ? 'Đang xử lý…' : 'Click để loading' }}
        </AppButton>
      </div>
    </section>

    <!-- SearchBar / FormField / AppSelect -->
    <section class="mb-5">
      <h5 class="mb-1">Inputs</h5>
      <p class="text-muted small mb-3">SearchBar, FormField labels, and AppSelect — all share the green focus ring.</p>
      <SearchBar v-model="keyword" placeholder="Tìm kiếm sản phẩm..." />
      <p class="text-muted small mt-2">Giá trị hiện tại: {{ keyword || '(trống)' }}</p>
    </section>

    <!-- StatusBadge -->
    <section class="mb-5">
      <h5 class="mb-1">Badges / Status</h5>
      <p class="text-muted small mb-3">Soft "subtle" pills with a leading semantic dot.</p>
      <div class="d-flex flex-wrap gap-2">
        <StatusBadge status="success" />
        <StatusBadge status="warning" />
        <StatusBadge status="danger" />
        <StatusBadge status="info" />
        <StatusBadge status="neutral" />
      </div>
      <div class="mt-2 small text-muted">Backward-compatible boolean API:</div>
      <div class="d-flex gap-2 mt-1">
        <StatusBadge :active="true" />
        <StatusBadge :active="false" />
      </div>
    </section>

    <!-- DataTable -->
    <section class="mb-5">
      <h5 class="mb-1">DataTable</h5>
      <p class="text-muted small mb-3">Card-wrapped table, uppercase muted header, subtle row hover.</p>
      <DataTable :columns="columns" :rows="rows">
        <template #cell-status="{ value }">
          <StatusBadge :status="value" />
        </template>
        <template #actions="{ row }">
          <AppButton variant="outline-secondary" size="sm" icon="pencil" @click="editRow(row)">Sửa</AppButton>
          <AppButton variant="outline-danger" size="sm" icon="trash" class="ms-1" @click="deleteRow(row)">Xoá</AppButton>
        </template>
      </DataTable>
    </section>

    <!-- AppModal -->
    <section class="mb-5">
      <h5 class="mb-1">AppModal</h5>
      <p class="text-muted small mb-3">Borderless rounded content, elevated shadow, with FormField + AppSelect inside.</p>
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
      <h5 class="mb-1">ConfirmDialog</h5>
      <p class="text-muted small mb-3">Built on AppModal + AppButton, inherits the same polish.</p>
      <AppButton variant="danger" icon="trash" @click="showConfirm = true">Xoá sản phẩm</AppButton>
      <ConfirmDialog
        v-model:open="showConfirm"
        title="Xác nhận xoá"
        message="Bạn có chắc chắn muốn xoá sản phẩm này không?"
        @confirm="onConfirmDelete"
      />
    </section>

    <!-- StatCard -->
    <section class="mb-5">
      <h5 class="mb-1">StatCard</h5>
      <p class="text-muted small mb-3">Left accent border + tinted icon chip, one per semantic variant.</p>
      <div class="row g-3">
        <div class="col-md-3 col-sm-6">
          <StatCard label="Doanh thu hôm nay" value="12.500.000đ" icon="cash-stack" variant="primary" />
        </div>
        <div class="col-md-3 col-sm-6">
          <StatCard label="Đơn hàng hoàn tất" value="24" icon="check-circle" variant="success" />
        </div>
        <div class="col-md-3 col-sm-6">
          <StatCard label="Đơn hàng chờ xử lý" value="5" icon="exclamation-triangle" variant="warning" />
        </div>
        <div class="col-md-3 col-sm-6">
          <StatCard label="Khách hàng mới" value="8" icon="info-circle" variant="info" />
        </div>
      </div>
    </section>

    <!-- Toast -->
    <section class="mb-5">
      <h5 class="mb-1">Toasts</h5>
      <p class="text-muted small mb-3">Subtle semantic backgrounds with matching icon per type.</p>
      <div class="d-flex flex-wrap gap-2">
        <AppButton variant="success" icon="check-circle" @click="showToast('success')">Success toast</AppButton>
        <AppButton variant="warning" icon="exclamation-triangle" @click="showToast('warning')">Warning toast</AppButton>
        <AppButton variant="danger" icon="x-circle" @click="showToast('danger')">Danger toast</AppButton>
        <AppButton variant="info" icon="info-circle" @click="showToast('info')">Info toast</AppButton>
      </div>
    </section>

    <ToastHost />
  </div>
</template>

<style scoped>
.swatch-base {
  height: 56px;
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
}
.swatch-subtle {
  height: 36px;
  border-radius: var(--radius-sm);
  font-size: .72rem;
  font-weight: 600;
}
</style>
