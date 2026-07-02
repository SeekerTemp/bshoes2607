<script setup>
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import DataTable from '../components/ui/DataTable.vue'
import StatCard from '../components/ui/StatCard.vue'
import { useThongKe } from '../composables/useThongKe'
import { vnd } from '../utils/format'

const { cards, sanPham } = useThongKe()

const cardMeta = [
  { icon: 'cash-stack', variant: 'primary' },
  { icon: 'receipt', variant: 'info' },
  { icon: 'check-circle', variant: 'success' },
  { icon: 'hourglass-split', variant: 'warning' },
  { icon: 'x-circle', variant: 'danger' }
]

const columns = [
  { key: 'maSP', label: 'Mã SP' },
  { key: 'tenSP', label: 'Tên' },
  { key: 'loaiSP', label: 'Loại' },
  { key: 'chatLieu', label: 'Chất liệu' },
  { key: 'mauSac', label: 'Màu' },
  { key: 'kichThuoc', label: 'Size' },
  { key: 'soLuongTon', label: 'Tồn', align: 'end' },
  { key: 'soLuongBan', label: 'Đã bán', align: 'end' },
  { key: 'doanhThu', label: 'Doanh thu', align: 'end' }
]
</script>

<template>
  <AppShell>
    <PageHeader title="Thống kê doanh thu" />

    <div class="row g-3 mb-4">
      <div class="col-md-3 col-sm-6" v-for="(c, i) in cards" :key="c.label">
        <StatCard :label="c.label" :value="c.value" :icon="cardMeta[i]?.icon" :variant="cardMeta[i]?.variant" />
      </div>
    </div>

    <h5 class="mb-2">Sản phẩm bán chạy</h5>
    <DataTable :columns="columns" :rows="sanPham" row-key="maSP">
      <template #cell-doanhThu="{ value }">{{ vnd(value) }}</template>
    </DataTable>
  </AppShell>
</template>
