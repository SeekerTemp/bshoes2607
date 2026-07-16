<script setup>
// Giỏ hàng + đơn của khách. Trang công khai (khách chưa có đăng nhập) nên
// không dùng AppShell — tự gắn ToastHost như HomeView.
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCart } from '../composables/useCart'
import { useToast } from '../composables/useToast'
import ToastHost from '../components/ui/ToastHost.vue'
import { gioHangApi } from '../api/gioHang'
import { datTruocApi } from '../api/datTruoc'
import { vnd } from '../utils/format'

const router = useRouter()
const { items, soLuong, tongTien, setQty, remove, clear } = useCart()
const { notify } = useToast()

const tab = ref('gio')
const PHONE_KEY = 'bshoes_sdt'   // nhớ SĐT để khách khỏi gõ lại mỗi lần tra đơn

// ---- đặt hàng ----
const form = ref({
  tenNguoiNhan: '', soDienThoai: localStorage.getItem(PHONE_KEY) || '', diaChi: '', ghiChu: '',
})
const dangGui = ref(false)
const loi = ref('')
const donVuaTao = ref(null)

async function datHang() {
  loi.value = ''
  if (!items.value.length) { loi.value = 'Giỏ hàng đang trống.'; return }
  if (!form.value.tenNguoiNhan.trim()) { loi.value = 'Nhập tên người nhận.'; return }
  if (!/^0\d{8,10}$/.test(form.value.soDienThoai.trim())) { loi.value = 'Số điện thoại không hợp lệ.'; return }
  if (!form.value.diaChi.trim()) { loi.value = 'Nhập địa chỉ nhận hàng.'; return }
  dangGui.value = true
  try {
    const don = await gioHangApi.checkout({
      ...form.value,
      items: items.value.map(i => ({ idSanPhamChiTiet: i.id, soLuong: i.soLuong })),
    })
    donVuaTao.value = don
    localStorage.setItem(PHONE_KEY, form.value.soDienThoai.trim())
    tra.value.sdt = form.value.soDienThoai.trim()
    clear()
    notify(`Đặt hàng thành công — ${don.ma}`, 'success')
  } catch (e) {
    loi.value = e?.response?.data?.message || 'Đặt hàng thất bại, vui lòng thử lại.'
  } finally {
    dangGui.value = false
  }
}

// ---- đơn của tôi ----
const tra = ref({ sdt: localStorage.getItem(PHONE_KEY) || '', dons: [], preorders: [], daTra: false, dangTra: false })

async function traCuu() {
  if (!/^0\d{8,10}$/.test(tra.value.sdt.trim())) { notify('Số điện thoại không hợp lệ.', 'warning'); return }
  tra.value.dangTra = true
  try {
    const sdt = tra.value.sdt.trim()
    localStorage.setItem(PHONE_KEY, sdt)
    tra.value.dons = await gioHangApi.donHang(sdt)
    const all = await datTruocApi.findAll().catch(() => [])
    tra.value.preorders = all.filter(p => p.soDienThoai === sdt)
    tra.value.daTra = true
  } catch (e) {
    notify('Không tra cứu được đơn (backend offline?)', 'warning')
  } finally {
    tra.value.dangTra = false
  }
}

async function daNhan(don) {
  if (!confirm(`Xác nhận đã nhận ${don.ma}?`)) return
  try {
    await gioHangApi.nhanHang(don.id)
    notify(`${don.ma}: đã nhận hàng`, 'success')
    await traCuu()
  } catch (e) {
    notify(e?.response?.data?.message || 'Không cập nhật được', 'warning')
  }
}

const dangGiao = computed(() => tra.value.dons.filter(d => d.trangThaiCode === 3))
function badge(code) {
  return { 1: 'b-done', 2: 'b-fail', 3: 'b-wait', 4: 'b-done', 5: 'b-exp' }[code] || 'b-exp'
}
function d(s) { return s ? String(s).slice(0, 10) : '' }
</script>

<template>
  <div class="store">
    <ToastHost />
    <header class="shop-head py-3"><div class="container d-flex align-items-center gap-4">
      <router-link to="/home" class="brand text-nowrap"><i class="bi bi-bag-heart-fill"></i> BShoes</router-link>
      <div class="flex-grow-1"></div>
      <router-link to="/home" class="btn btn-sm btn-outline-success"><i class="bi bi-arrow-left"></i> Tiếp tục mua sắm</router-link>
    </div></header>

    <div class="container py-4" style="max-width:1000px">
      <div class="gh-tabs mb-3">
        <button class="gh-tab" :class="{ active: tab === 'gio' }" @click="tab = 'gio'">
          Giỏ hàng<span class="n">{{ soLuong }}</span>
        </button>
        <button class="gh-tab" :class="{ active: tab === 'don' }" @click="tab = 'don'; !tra.daTra && tra.sdt && traCuu()">
          Đơn của tôi
        </button>
      </div>

      <!-- ============ GIỎ HÀNG ============ -->
      <div v-if="tab === 'gio'">
        <div v-if="donVuaTao" class="sec p-4 mb-3 text-center">
          <i class="bi bi-check-circle-fill" style="font-size:44px;color:#0B895A"></i>
          <h5 class="fw-bold mt-2 mb-1">Đặt hàng thành công!</h5>
          <p class="text-muted mb-1">Mã đơn: <b style="color:#0B895A">{{ donVuaTao.ma }}</b> — {{ vnd(donVuaTao.tongTien) }}</p>
          <p class="text-muted small mb-3">Thanh toán khi nhận hàng (COD). Cửa hàng sẽ liên hệ số {{ donVuaTao.soDienThoai }} để giao.</p>
          <button class="btn btn-cart px-4 me-2" @click="tab = 'don'; traCuu()">Xem đơn của tôi</button>
          <button class="btn btn-outline-secondary px-4" @click="donVuaTao = null; router.push('/home')">Mua tiếp</button>
        </div>

        <div v-else-if="!items.length" class="sec p-5 text-center text-muted">
          <i class="bi bi-cart-x d-block" style="font-size:44px"></i>
          <div class="mt-2 mb-3">Giỏ hàng của bạn đang trống.</div>
          <router-link to="/home" class="btn btn-cart px-4">Mua sắm ngay</router-link>
        </div>

        <div v-else class="gh-grid">
          <div class="sec p-3">
            <table class="table align-middle mb-0">
              <thead><tr class="small text-muted">
                <th>Sản phẩm</th><th class="text-end">Đơn giá</th><th class="text-center" style="width:130px">Số lượng</th>
                <th class="text-end">Thành tiền</th><th></th>
              </tr></thead>
              <tbody>
                <tr v-for="i in items" :key="i.id">
                  <td>
                    <div class="d-flex align-items-center gap-2">
                      <img :src="i.img" class="gh-img">
                      <div>
                        <div class="fw-semibold" style="font-size:13.5px">{{ i.ten }}</div>
                        <div class="text-muted small" v-if="i.mauSize">{{ i.mauSize }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="text-end">{{ vnd(i.gia) }}</td>
                  <td>
                    <div class="input-group input-group-sm">
                      <button class="btn btn-outline-secondary" @click="setQty(i.id, i.soLuong - 1)">−</button>
                      <input class="form-control text-center" :value="i.soLuong" readonly>
                      <button class="btn btn-outline-secondary"
                              @click="() => { const r = setQty(i.id, i.soLuong + 1); if (!r.ok) notify(r.message, 'warning') }">+</button>
                    </div>
                  </td>
                  <td class="text-end fw-bold" style="color:#0B895A">{{ vnd(i.gia * i.soLuong) }}</td>
                  <td class="text-end">
                    <button class="btn btn-sm btn-outline-danger py-0 px-2" @click="remove(i.id)"><i class="bi bi-trash"></i></button>
                  </td>
                </tr>
              </tbody>
            </table>
            <div class="text-end mt-2">
              <button class="btn btn-sm btn-outline-secondary" @click="clear()">Xoá hết giỏ</button>
            </div>
          </div>

          <!-- form đặt hàng -->
          <div class="sec p-3">
            <h6 class="fw-bold mb-3">Thông tin nhận hàng</h6>
            <label class="lbl">Người nhận <span class="text-danger">*</span></label>
            <input class="form-control form-control-sm mb-2" v-model="form.tenNguoiNhan" placeholder="Nguyễn Văn A">
            <label class="lbl">Số điện thoại <span class="text-danger">*</span></label>
            <input class="form-control form-control-sm mb-2" v-model="form.soDienThoai" placeholder="09xxxxxxxx">
            <label class="lbl">Địa chỉ <span class="text-danger">*</span></label>
            <textarea rows="2" class="form-control form-control-sm mb-2" v-model="form.diaChi" placeholder="Số nhà, đường, phường, quận, tỉnh"></textarea>
            <label class="lbl">Ghi chú</label>
            <textarea rows="2" class="form-control form-control-sm mb-3" v-model="form.ghiChu" placeholder="Giao giờ hành chính..."></textarea>

            <div class="d-flex justify-content-between mb-1 small"><span class="text-muted">Tạm tính ({{ soLuong }} sp)</span><span>{{ vnd(tongTien) }}</span></div>
            <div class="d-flex justify-content-between mb-2 small"><span class="text-muted">Phí ship</span><span class="text-muted">Nhân viên báo sau</span></div>
            <div class="d-flex justify-content-between fw-bold mb-3" style="font-size:16px">
              <span>Tổng cộng</span><span style="color:#0B895A">{{ vnd(tongTien) }}</span>
            </div>
            <div class="alert alert-danger py-1 px-2 small" v-if="loi">{{ loi }}</div>
            <button class="btn btn-cart w-100" :disabled="dangGui" @click="datHang">
              <i class="bi bi-bag-check"></i> {{ dangGui ? 'Đang gửi...' : 'Đặt hàng (COD)' }}
            </button>
            <div class="text-muted small mt-2 text-center">Thanh toán khi nhận hàng — chưa cần trả trước.</div>
          </div>
        </div>
      </div>

      <!-- ============ ĐƠN CỦA TÔI ============ -->
      <div v-else>
        <div class="sec p-3 mb-3">
          <label class="lbl">Tra cứu đơn bằng số điện thoại đã đặt</label>
          <div class="input-group input-group-sm" style="max-width:360px">
            <input class="form-control" v-model="tra.sdt" placeholder="09xxxxxxxx" @keyup.enter="traCuu">
            <button class="btn btn-success" :disabled="tra.dangTra" @click="traCuu">
              <i class="bi bi-search"></i> {{ tra.dangTra ? 'Đang tra...' : 'Tra cứu' }}
            </button>
          </div>
        </div>

        <div v-if="tra.daTra">
          <div class="sec p-3 mb-3" v-if="dangGiao.length">
            <h6 class="fw-bold mb-2"><i class="bi bi-truck"></i> Đang giao ({{ dangGiao.length }})</h6>
            <div v-for="o in dangGiao" :key="o.id" class="d-flex justify-content-between align-items-center border-top py-2">
              <div>
                <b>{{ o.ma }}</b> <span class="text-muted small">· {{ d(o.ngayTao) }}</span>
                <div class="text-muted small">{{ o.diaChi }}</div>
              </div>
              <div class="text-end">
                <div class="fw-bold" style="color:#0B895A">{{ vnd(o.tongTien) }}</div>
                <button class="btn btn-sm btn-success py-0 px-2 mt-1" @click="daNhan(o)">Đã nhận hàng</button>
              </div>
            </div>
          </div>

          <div class="sec p-3 mb-3">
            <h6 class="fw-bold mb-2">Tất cả đơn ({{ tra.dons.length }})</h6>
            <div v-if="!tra.dons.length" class="text-muted small py-2">Chưa có đơn nào với số này.</div>
            <table v-else class="table table-sm align-middle mb-0">
              <thead><tr class="small text-muted"><th>Mã</th><th>Ngày</th><th>Sản phẩm</th><th class="text-end">Tổng</th><th>Trạng thái</th></tr></thead>
              <tbody>
                <tr v-for="o in tra.dons" :key="o.id">
                  <td class="fw-semibold">{{ o.ma }}</td>
                  <td class="small">{{ d(o.ngayTao) }}</td>
                  <td class="small">{{ (o.chiTiet || []).map(c => c.ten).join(', ') || '—' }}</td>
                  <td class="text-end">{{ vnd(o.tongTien) }}</td>
                  <td><span class="badge-soft" :class="badge(o.trangThaiCode)">{{ o.trangThai }}</span></td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="sec p-3" v-if="tra.preorders.length">
            <h6 class="fw-bold mb-2"><i class="bi bi-bookmark-star"></i> Phiếu đặt trước ({{ tra.preorders.length }})</h6>
            <table class="table table-sm align-middle mb-0">
              <thead><tr class="small text-muted"><th>Mã</th><th>Sản phẩm</th><th class="text-center">SL</th><th>Dự kiến</th><th>Trạng thái</th></tr></thead>
              <tbody>
                <tr v-for="p in tra.preorders" :key="p.id">
                  <td class="fw-semibold">{{ p.ma }}</td>
                  <td class="small">{{ p.tenSanPham }} <span class="text-muted">{{ p.mauSize }}</span></td>
                  <td class="text-center">{{ p.soLuong }}</td>
                  <td class="small">{{ d(p.ngayDuKien) }}</td>
                  <td><span class="badge-soft b-wait">{{ p.trangThai }}</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.store { background:#f4f6f8; min-height:100vh; }
.store a { text-decoration:none; }
.shop-head { background:#fff; border-bottom:1px solid #e5e9ef; }
.brand { color:#0B895A; font-weight:800; font-size:24px; }
.sec { background:#fff; border-radius:12px; box-shadow:0 1px 2px rgba(16,24,40,.06); }
.gh-tabs { display:flex; gap:4px; }
.gh-tab { border:1px solid #e5e9ef; background:#eef1f4; color:#6b7280; border-radius:8px; padding:6px 16px; font-size:13px; font-weight:600; cursor:pointer; }
.gh-tab .n { display:inline-block; min-width:18px; padding:0 5px; margin-left:6px; border-radius:999px; background:#d5dbe2; color:#374151; font-size:11px; }
.gh-tab.active { background:#0B895A; color:#fff; border-color:#0B895A; }
.gh-tab.active .n { background:#fff; color:#0B895A; }
.gh-grid { display:grid; grid-template-columns:minmax(0,1fr) 340px; gap:16px; align-items:start; }
.gh-img { width:52px; height:52px; object-fit:contain; background:#f7f9fb; border-radius:8px; }
.lbl { font-size:12px; font-weight:600; color:#6b7280; margin-bottom:2px; }
.btn-cart { background:#0B895A; color:#fff; border-radius:8px; font-size:13px; font-weight:600; }
.btn-cart:hover { background:#0E9F67; color:#fff; }
.btn-cart:disabled { background:#7fb9a2; }
.badge-soft { font-weight:600; font-size:11px; padding:4px 8px; border-radius:999px; white-space:nowrap; }
.b-wait { background:#FFF4E0; color:#9A6700; } .b-done { background:#E6F4EA; color:#157347; }
.b-fail { background:#FDECEA; color:#B42318; } .b-exp { background:#eceff3; color:#556; }
@media (max-width: 992px) { .gh-grid { grid-template-columns:1fr; } }
</style>
