<script setup>
import { ref } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import SearchBar from '../components/ui/SearchBar.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import DataTable from '../components/ui/DataTable.vue'
import AppButton from '../components/ui/AppButton.vue'
import AppModal from '../components/ui/AppModal.vue'
import StatusBadge from '../components/ui/StatusBadge.vue'
import { useLichSu } from '../composables/useLichSu'
import { vnd } from '../utils/format'

const { keyword, trangThai, filtered } = useLichSu()

const statusOptions = [
  { value: '', label: '-- Trạng thái --' },
  { value: 'Chờ', label: 'Chờ' },
  { value: 'Thành công', label: 'Thành công' },
  { value: 'Huỷ', label: 'Huỷ' }
]

const columns = [
  { key: 'ma', label: 'Mã HĐ' },
  { key: 'khach', label: 'Khách' },
  { key: 'nhanVien', label: 'Nhân viên' },
  { key: 'ngayTao', label: 'Ngày tạo' },
  { key: 'tongTien', label: 'Tổng tiền', align: 'end' },
  { key: 'trangThai', label: 'Trạng thái' }
]

function statusOf(t) {
  return t === 'Thành công' ? 'success' : (t === 'Chờ' ? 'warning' : 'danger')
}

const modalOpen = ref(false)
const chon = ref(null)

function xem(row) {
  chon.value = row
  modalOpen.value = true
}
</script>

<template>
  <AppShell>
    <PageHeader title="Lịch sử hoá đơn" />

    <div class="d-flex gap-2 mb-3">
      <SearchBar v-model="keyword" placeholder="Tìm mã HĐ / khách..." />
      <AppSelect v-model="trangThai" :options="statusOptions" style="max-width: 200px" />
    </div>

    <DataTable :columns="columns" :rows="filtered">
      <template #cell-tongTien="{ value }">{{ vnd(value) }}</template>
      <template #cell-trangThai="{ value }"><StatusBadge :status="statusOf(value)" :label="value" /></template>
      <template #actions="{ row }">
        <AppButton size="sm" variant="outline-secondary" @click="xem(row)">Chi tiết</AppButton>
      </template>
    </DataTable>

    <AppModal v-model:open="modalOpen" :title="chon ? `Hoá đơn ${chon.ma}` : ''">
      <template v-if="chon">
        <p class="mb-2">
          <b>Khách:</b> {{ chon.khach }} — <b>NV:</b> {{ chon.nhanVien }} — <b>Ngày:</b> {{ chon.ngayTao }}
        </p>
        <table class="table table-sm">
          <thead>
            <tr><th>Sản phẩm</th><th class="text-end">SL</th><th class="text-end">Đơn giá</th><th class="text-end">Thành tiền</th></tr>
          </thead>
          <tbody>
            <tr v-for="d in chon.chiTiet" :key="d.ten">
              <td>{{ d.ten }}</td>
              <td class="text-end">{{ d.soLuong }}</td>
              <td class="text-end">{{ vnd(d.donGia) }}</td>
              <td class="text-end">{{ vnd(d.donGia * d.soLuong) }}</td>
            </tr>
          </tbody>
        </table>
        <div class="text-end fs-5">Tổng: <b style="color: var(--c-primary)">{{ vnd(chon.tongTien) }}</b></div>
      </template>
      <template #footer>
        <AppButton variant="secondary" @click="modalOpen = false">Đóng</AppButton>
      </template>
    </AppModal>
  </AppShell>
</template>
