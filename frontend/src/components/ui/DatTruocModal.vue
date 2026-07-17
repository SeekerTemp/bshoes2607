<script setup>
// Modal đăng ký đặt trước, dùng chung cho trang chủ và trang chi tiết sản phẩm.
// Truyền vào biến thể đang hết hàng: { id, ten, gia, img, mauSize }.
// id = id_san_pham_chi_tiet — server chỉ cho đăng ký khi tồn = 0.
import { ref, watch } from 'vue'
import { datTruocApi } from '../../api/datTruoc'
import { vnd } from '../../utils/format'

const props = defineProps({
  product: { type: Object, default: null },   // null = đóng
})
const emit = defineEmits(['close', 'done'])

const form = ref(blank())
const waiting = ref(0)
const saving = ref(false)
const done = ref(null)
const error = ref('')

function blank() {
  return { tenKhachHang: '', soDienThoai: '', email: '', soLuong: 1, ngayDuKien: '', ghiChu: '' }
}

// Mở modal cho SP nào thì nạp lại số người đang chờ của đúng SP đó.
watch(() => props.product, async (p) => {
  done.value = null
  error.value = ''
  waiting.value = 0
  form.value = blank()
  if (!p) return
  try {
    const r = await datTruocApi.demChoHang(p.id)
    waiting.value = r?.choHang ?? 0
  } catch (e) { /* backend offline — để 0 */ }
}, { immediate: true })

async function submit() {
  error.value = ''
  if (!form.value.tenKhachHang.trim()) { error.value = 'Vui lòng nhập họ tên.'; return }
  if (!/^0\d{8,10}$/.test(form.value.soDienThoai.trim())) { error.value = 'Số điện thoại không hợp lệ.'; return }
  saving.value = true
  try {
    const d = await datTruocApi.dangKy({ idSanPhamChiTiet: props.product.id, ...form.value })
    done.value = d
    emit('done', d)
  } catch (e) {
    error.value = e?.response?.data?.message || 'Đăng ký đặt trước thất bại, vui lòng thử lại.'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div v-if="product" class="pre-back" @click.self="emit('close')">
    <div class="pre-modal">
      <div class="pre-head">
        <div><i class="bi bi-bookmark-star"></i> Đặt trước sản phẩm</div>
        <button class="btn-x" @click="emit('close')"><i class="bi bi-x-lg"></i></button>
      </div>

      <!-- xong -->
      <div v-if="done" class="pre-body text-center py-4">
        <i class="bi bi-check-circle-fill" style="font-size:44px;color:#0B895A"></i>
        <h5 class="fw-bold mt-2 mb-1">Đăng ký thành công!</h5>
        <p class="text-muted mb-2">Mã phiếu của bạn: <span class="fw-bold" style="color:#0B895A">{{ done.ma }}</span></p>
        <p class="text-muted small mb-3">
          BShoes sẽ gọi lại số <b>{{ done.soDienThoai }}</b> ngay khi hàng về
          <span v-if="done.ngayDuKien">(dự kiến {{ String(done.ngayDuKien).slice(0, 10) }})</span>.
        </p>
        <button class="btn btn-pre-ok px-4" @click="emit('close')">Đóng</button>
      </div>

      <!-- form -->
      <div v-else class="pre-body">
        <div class="pre-prod mb-3">
          <img :src="product.img" :alt="product.ten">
          <div>
            <div class="fw-semibold" style="font-size:14px">{{ product.ten }}</div>
            <div class="text-muted small" v-if="product.mauSize">{{ product.mauSize }}</div>
            <div class="pre-price mt-1">{{ vnd(product.gia) }}</div>
          </div>
        </div>
        <div class="pre-note mb-3">
          <i class="bi bi-info-circle"></i>
          Sản phẩm đang hết hàng. Để lại thông tin, cửa hàng sẽ báo ngay khi có hàng — <b>không cần trả trước</b>.
          <div v-if="waiting > 0" class="mt-1">Hiện có <b>{{ waiting }}</b> khách đang chờ mẫu này.</div>
        </div>

        <div class="row g-2">
          <div class="col-12">
            <label class="lbl">Họ và tên <span class="text-danger">*</span></label>
            <input class="form-control form-control-sm" v-model="form.tenKhachHang" placeholder="Nguyễn Văn A">
          </div>
          <div class="col-7">
            <label class="lbl">Số điện thoại <span class="text-danger">*</span></label>
            <input class="form-control form-control-sm" v-model="form.soDienThoai" placeholder="09xxxxxxxx">
          </div>
          <div class="col-5">
            <label class="lbl">Số lượng</label>
            <input type="number" min="1" class="form-control form-control-sm" v-model.number="form.soLuong">
          </div>
          <div class="col-12">
            <label class="lbl">Email (tuỳ chọn)</label>
            <input class="form-control form-control-sm" v-model="form.email" placeholder="email@example.com">
          </div>
          <div class="col-12">
            <label class="lbl">Mong muốn nhận hàng trước ngày</label>
            <input type="date" class="form-control form-control-sm" v-model="form.ngayDuKien">
          </div>
          <div class="col-12">
            <label class="lbl">Ghi chú</label>
            <textarea rows="2" class="form-control form-control-sm" v-model="form.ghiChu"
                      placeholder="Ví dụ: cần đúng size 42, gọi sau 18h..."></textarea>
          </div>
        </div>

        <div class="alert alert-danger py-1 px-2 small mt-2 mb-0" v-if="error">{{ error }}</div>

        <div class="d-flex gap-2 mt-3">
          <button class="btn btn-outline-secondary flex-shrink-0" @click="emit('close')">Huỷ</button>
          <button class="btn btn-pre-ok flex-fill" :disabled="saving" @click="submit">
            <i class="bi bi-bookmark-check"></i> {{ saving ? 'Đang gửi...' : 'Đăng ký đặt trước' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pre-back { position: fixed; inset: 0; background: rgba(15,28,23,.55); display: flex; align-items: center; justify-content: center; z-index: 1080; padding: 16px; }
.pre-modal { background: #fff; border-radius: 14px; width: 100%; max-width: 440px; max-height: 92vh; overflow: auto; box-shadow: 0 18px 48px rgba(0,0,0,.28); }
.pre-head { background: #0B895A; color: #fff; padding: 12px 16px; font-weight: 700; display: flex; justify-content: space-between; align-items: center; border-radius: 14px 14px 0 0; }
.pre-head .btn-x { background: none; border: 0; color: #fff; opacity: .85; }
.pre-body { padding: 16px; }
.pre-prod { display: flex; gap: 12px; align-items: center; background: #f7f9fb; border-radius: 10px; padding: 10px; }
.pre-prod img { width: 72px; height: 72px; object-fit: contain; flex-shrink: 0; }
.pre-price { color: #0B895A; font-weight: 800; font-size: 15px; }
.pre-note { background: #FFF4E0; color: #7a5b00; font-size: 12.5px; border-radius: 8px; padding: 8px 10px; }
.lbl { font-size: 12px; font-weight: 600; color: #6b7280; margin-bottom: 2px; }
.btn-pre-ok { background: #0B895A; color: #fff; border-radius: 8px; font-size: 13px; font-weight: 600; }
.btn-pre-ok:hover { background: #0E9F67; color: #fff; }
.btn-pre-ok:disabled { background: #7fb9a2; }
</style>
