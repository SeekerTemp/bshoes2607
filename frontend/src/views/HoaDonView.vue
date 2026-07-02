<script setup>
import { computed } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppButton from '../components/ui/AppButton.vue'
import SearchBar from '../components/ui/SearchBar.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import { useHoaDon } from '../composables/useHoaDon'
import { useToast } from '../composables/useToast'
import { vnd } from '../utils/format'

const {
  keyword, khachHang, voucher, gio, ketQua,
  khachHangOptions, voucherOptions,
  tamTinh, giamGia, phaiTra,
  addLine, removeLine, thanhToan
} = useHoaDon()
const { notify } = useToast()

const voucherSelectOptions = computed(() => voucherOptions.value.map(v => ({ value: v.value, label: v.label })))
// AppSelect's native <select> always emits string values; useHoaDon's giamGia
// computed does strict/numeric comparisons on `voucher`, so bridge through a
// numeric proxy instead of changing the composable.
const voucherModel = computed({
  get: () => voucher.value,
  set: (v) => { voucher.value = Number(v) }
})

function pay() {
  notify('Thanh toán: ' + vnd(phaiTra.value), 'success')
  thanhToan()
}
</script>

<template>
  <AppShell>
    <PageHeader title="Bán hàng tại quầy" />

    <div class="pos-grid" style="display: grid; grid-template-columns: 2fr 1fr; gap: 16px">
      <div class="card">
        <div class="card-body">
          <SearchBar v-model="keyword" placeholder="Tìm sản phẩm..." class="mb-3" style="max-width: none" />
          <div class="row g-2">
            <div class="col-6" v-for="p in ketQua" :key="p.id">
              <div class="card h-100">
                <div class="card-body p-2">
                  <div class="fw-bold">{{ p.ten }}</div>
                  <div class="small text-muted">{{ p.mau }} • Size {{ p.size }} • Tồn {{ p.ton }}</div>
                  <div class="d-flex justify-content-between align-items-center mt-1">
                    <span>{{ vnd(p.gia) }}</span>
                    <AppButton size="sm" @click="addLine(p)">Thêm</AppButton>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-body">
          <h6>Hoá đơn hiện tại</h6>
          <div class="mb-2">
            <label class="form-label small mb-0">Khách hàng</label>
            <AppSelect v-model="khachHang" :options="khachHangOptions" />
          </div>
          <table class="table table-sm align-middle">
            <thead>
              <tr><th>SP</th><th class="text-end">SL</th><th class="text-end">Đơn giá</th><th class="text-end">TT</th><th></th></tr>
            </thead>
            <tbody>
              <tr v-for="l in gio" :key="l.id">
                <td>{{ l.ten }}<div class="small text-muted">{{ l.mau }}/{{ l.size }}</div></td>
                <td class="text-end" style="width: 70px">
                  <input type="number" min="1" class="form-control form-control-sm text-end" v-model.number="l.soLuong">
                </td>
                <td class="text-end">{{ vnd(l.gia) }}</td>
                <td class="text-end">{{ vnd(l.gia * l.soLuong) }}</td>
                <td class="text-end"><button class="btn btn-sm btn-outline-danger" @click="removeLine(l)">×</button></td>
              </tr>
              <tr v-if="gio.length === 0"><td colspan="5" class="text-center text-muted">Chưa có sản phẩm</td></tr>
            </tbody>
          </table>
          <div class="mb-2">
            <label class="form-label small mb-0">Phiếu giảm giá</label>
            <AppSelect v-model="voucherModel" :options="voucherSelectOptions" />
          </div>
          <ul class="list-group list-group-flush">
            <li class="list-group-item d-flex justify-content-between"><span>Tạm tính</span><b>{{ vnd(tamTinh) }}</b></li>
            <li class="list-group-item d-flex justify-content-between"><span>Giảm giá</span><b>-{{ vnd(giamGia) }}</b></li>
            <li class="list-group-item d-flex justify-content-between fs-5"><span>Phải trả</span><b style="color: var(--c-primary)">{{ vnd(phaiTra) }}</b></li>
          </ul>
          <AppButton class="w-100 mt-3" :disabled="gio.length === 0" @click="pay">Thanh toán</AppButton>
        </div>
      </div>
    </div>
  </AppShell>
</template>
