<script setup>
// Add-only customer popup (no list) — for staff who may create customers but
// shouldn't browse the full list. Emits `created` with the new customer.
import { ref, watch } from 'vue'
import { khachHangApi } from '../../api/khachHang'
import { useToast } from '../../composables/useToast'

const props = defineProps({ open: { type: Boolean, default: false } })
const emit = defineEmits(['update:open', 'created'])
const { notify } = useToast()

const form = ref(blank())
const busy = ref(false)
function blank() { return { ten: '', sdt: '', gioiTinh: 'Nam', email: '', diaChi: '' } }
watch(() => props.open, o => { if (o) form.value = blank() })

function close() { emit('update:open', false) }
async function save() {
  if (!form.value.ten) { notify('Nhập tên khách hàng', 'warning'); return }
  busy.value = true
  try {
    const kh = await khachHangApi.create({ ...form.value })
    notify('Đã thêm khách hàng: ' + (kh?.ten || form.value.ten), 'success')
    emit('created', kh || { ...form.value })
    close()
  } catch (e) {
    notify('Lưu khách hàng thất bại (backend offline?)', 'warning')
  } finally { busy.value = false }
}
</script>

<template>
  <div v-if="open" class="akh-overlay" @click.self="close">
    <div class="akh-modal">
      <h5 class="mb-3"><i class="bi bi-person-plus me-2" style="color:#0B895A"></i>Thêm khách hàng</h5>
      <div class="mb-2"><label class="form-label small mb-1">Tên khách hàng *</label><input class="form-control form-control-sm" v-model="form.ten"></div>
      <div class="row g-2 mb-2">
        <div class="col-7"><label class="form-label small mb-1">SĐT</label><input class="form-control form-control-sm" v-model="form.sdt"></div>
        <div class="col-5"><label class="form-label small mb-1">Giới tính</label>
          <select class="form-select form-select-sm" v-model="form.gioiTinh"><option>Nam</option><option>Nữ</option></select></div>
      </div>
      <div class="mb-2"><label class="form-label small mb-1">Email</label><input class="form-control form-control-sm" v-model="form.email"></div>
      <div class="mb-3"><label class="form-label small mb-1">Địa chỉ</label><input class="form-control form-control-sm" v-model="form.diaChi"></div>
      <div class="d-flex gap-2">
        <button class="btn btn-outline-secondary flex-fill" @click="close">Huỷ</button>
        <button class="btn btn-success flex-fill" :disabled="busy" @click="save">{{ busy ? 'Đang lưu...' : 'Lưu' }}</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.akh-overlay { position: fixed; inset: 0; background: rgba(15,23,20,.5); display: flex; align-items: center; justify-content: center; z-index: 1090; }
.akh-modal { background: #fff; border-radius: 12px; padding: 20px; width: 360px; max-width: 92vw; box-shadow: 0 20px 50px rgba(0,0,0,.3); }
</style>
