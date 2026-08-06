<script setup>
// Trang chi tiết sản phẩm ở storefront (công khai). Chọn biến thể (màu/size):
//   tồn > 0  -> thêm giỏ / mua ngay
//   tồn = 0  -> đặt trước (server chỉ cho đăng ký khi tồn = 0)
// Nút bám theo BIẾN THỂ đang chọn, không phải theo sản phẩm — cùng một mẫu có thể
// hết size 42 nhưng còn size 40.
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { sanPhamPublicApi } from '../api/sanPham'
import { useCart } from '../composables/useCart'
import { useToast } from '../composables/useToast'
import ToastHost from '../components/ui/ToastHost.vue'
import DatTruocModal from '../components/ui/DatTruocModal.vue'
import { vnd } from '../utils/format'

const route = useRoute()
const router = useRouter()
const { soLuong: cartCount, add: addToCart } = useCart()
const { notify } = useToast()

const PLACEHOLDER = '/images/shoes/img_shoe_10001.png'
const sp = ref(null)
const dangTai = ref(true)
const loi = ref('')
const chon = ref(null)        // biến thể đang chọn
const qty = ref(1)
const preOrder = ref(null)

const bienThe = computed(() => (sp.value?.bienThe || []).filter(b => b.trangThai !== false))
const mauList = computed(() => [...new Set(bienThe.value.map(b => b.mau).filter(Boolean))])
const sizeList = computed(() => [...new Set(bienThe.value.map(b => b.size).filter(Boolean))])
const mauChon = ref(null)
const sizeChon = ref(null)

// Biến thể = giao của màu + size đang chọn. Chỉ 1 chiều thì khớp theo chiều đó.
watch([mauChon, sizeChon, bienThe], () => {
  const list = bienThe.value.filter(b =>
    (!mauChon.value || b.mau === mauChon.value) && (!sizeChon.value || b.size === sizeChon.value))
  chon.value = list.length === 1 ? list[0] : (list[0] || null)
  qty.value = 1
})

const anh = computed(() => chon.value?.imageUrl || sp.value?.imageUrl || PLACEHOLDER)
// Giá luôn thuộc về biến thể. Chưa chọn biến thể thì lấy giá thấp nhất (giaTu) do
// backend tổng hợp — sản phẩm cha không còn trường `gia` của riêng nó.
const gia = computed(() => chon.value?.gia ?? sp.value?.giaTu ?? 0)
const ton = computed(() => chon.value?.ton ?? 0)
const conHang = computed(() => ton.value > 0)

function tonCuaMau(m) {
  return bienThe.value.filter(b => b.mau === m).reduce((s, b) => s + (b.ton || 0), 0)
}
function tonCuaSize(s) {
  return bienThe.value.filter(b => b.size === s).reduce((x, b) => x + (b.ton || 0), 0)
}

async function load() {
  dangTai.value = true
  loi.value = ''
  try {
    sp.value = await sanPhamPublicApi.findById(route.params.id)
    if (!sp.value) { loi.value = 'Không tìm thấy sản phẩm này.'; return }
    const first = bienThe.value[0]
    mauChon.value = first?.mau ?? null
    sizeChon.value = first?.size ?? null
  } catch (e) {
    loi.value = 'Không tải được sản phẩm (backend offline?).'
  } finally {
    dangTai.value = false
  }
}

function cartItem() {
  return {
    id: chon.value.id,
    ten: sp.value.ten,
    gia: gia.value,
    img: anh.value,
    mauSize: [chon.value.mau, chon.value.size].filter(Boolean).join(' / '),
    ton: ton.value,
  }
}
function themGio() {
  if (!chon.value) return
  const r = addToCart(cartItem(), qty.value)
  notify(r.ok ? `Đã thêm ${qty.value} × "${sp.value.ten}" vào giỏ` : r.message, r.ok ? 'success' : 'warning')
}
function muaNgay() {
  if (!chon.value) return
  const r = addToCart(cartItem(), qty.value)
  if (r.ok) router.push('/gio-hang')
  else notify(r.message, 'warning')
}
function moDatTruoc() {
  if (!chon.value) return
  preOrder.value = {
    id: chon.value.id,
    ten: sp.value.ten,
    gia: gia.value,
    img: anh.value,
    mauSize: [chon.value.mau, chon.value.size].filter(Boolean).join(' / '),
  }
}

onMounted(load)
watch(() => route.params.id, load)
</script>

<template>
  <div class="store">
    <ToastHost />
    <header class="shop-head py-3"><div class="container d-flex align-items-center gap-3">
      <router-link to="/home" class="brand text-nowrap"><i class="bi bi-bag-heart-fill"></i> BShoes</router-link>
      <div class="flex-grow-1"></div>
      <router-link to="/home" class="btn btn-sm btn-outline-success"><i class="bi bi-arrow-left"></i> Tiếp tục mua sắm</router-link>
      <router-link to="/gio-hang" class="icon-btn ms-2">
        <i class="bi bi-cart3"></i><span class="dot" v-if="cartCount">{{ cartCount }}</span>
      </router-link>
    </div></header>

    <div class="container py-4" style="max-width:1040px">
      <div v-if="dangTai" class="sec p-5 text-center text-muted">Đang tải...</div>
      <div v-else-if="loi" class="sec p-5 text-center">
        <i class="bi bi-exclamation-triangle text-warning" style="font-size:40px"></i>
        <div class="mt-2 mb-3">{{ loi }}</div>
        <router-link to="/home" class="btn btn-cart px-4">Về trang chủ</router-link>
      </div>

      <div v-else class="sec p-3 p-md-4">
        <nav class="small text-muted mb-3">
          <router-link to="/home" style="color:#0B895A">Trang chủ</router-link>
          <span class="mx-1">/</span><span>{{ sp.loaiSP || 'Sản phẩm' }}</span>
          <span class="mx-1">/</span><span class="text-dark">{{ sp.ten }}</span>
        </nav>

        <div class="d-grid gap-4" style="grid-template-columns:minmax(0,420px) minmax(0,1fr)">
          <!-- ảnh -->
          <div class="detail-img">
            <img :src="anh" :alt="sp.ten" @error="e => e.target.src = PLACEHOLDER">
            <span class="tag-out" v-if="!conHang">HẾT HÀNG</span>
          </div>

          <!-- thông tin -->
          <div>
            <div class="text-muted small">{{ sp.thuongHieu || 'BShoes' }}</div>
            <h4 class="fw-bold mb-1">{{ sp.ten }}</h4>
            <div class="d-flex align-items-center gap-2 mb-2">
              <span class="p-star"><i class="bi bi-star-fill" v-for="s in 5" :key="s"></i></span>
              <span class="text-muted small">Mã: {{ sp.ma }}</span>
            </div>
            <div class="price-box mb-3">{{ vnd(gia) }}</div>

            <div class="row g-2 small mb-3">
              <div class="col-6" v-if="sp.chatLieu"><span class="text-muted">Chất liệu:</span> <b>{{ sp.chatLieu }}</b></div>
              <div class="col-6" v-if="sp.kieuDang"><span class="text-muted">Kiểu dáng:</span> <b>{{ sp.kieuDang }}</b></div>
              <div class="col-6" v-if="sp.loaiSP"><span class="text-muted">Danh mục:</span> <b>{{ sp.loaiSP }}</b></div>
              <div class="col-6"><span class="text-muted">Tồn kho:</span>
                <b :class="conHang ? 'text-success' : 'text-danger'">{{ ton }}</b>
              </div>
            </div>

            <!-- chọn màu -->
            <div class="mb-2" v-if="mauList.length">
              <div class="lbl mb-1">Màu sắc</div>
              <div class="d-flex flex-wrap gap-1">
                <button v-for="m in mauList" :key="m" class="opt" :class="{ on: mauChon === m, out: tonCuaMau(m) === 0 }"
                        @click="mauChon = m">{{ m }}</button>
              </div>
            </div>
            <!-- chọn size -->
            <div class="mb-3" v-if="sizeList.length">
              <div class="lbl mb-1">Kích cỡ</div>
              <div class="d-flex flex-wrap gap-1">
                <button v-for="s in sizeList" :key="s" class="opt" :class="{ on: sizeChon === s, out: tonCuaSize(s) === 0 }"
                        @click="sizeChon = s">{{ s }}</button>
              </div>
            </div>

            <div v-if="!chon" class="alert alert-warning py-2 small">Sản phẩm chưa có biến thể để bán.</div>

            <!-- còn hàng -> mua -->
            <template v-else-if="conHang">
              <div class="d-flex align-items-center gap-2 mb-3">
                <div class="lbl">Số lượng</div>
                <div class="input-group input-group-sm" style="width:120px">
                  <button class="btn btn-outline-secondary" @click="qty = Math.max(1, qty - 1)">−</button>
                  <input class="form-control text-center" :value="qty" readonly>
                  <button class="btn btn-outline-secondary" @click="qty = Math.min(ton, qty + 1)">+</button>
                </div>
                <span class="text-muted small">còn {{ ton }} sản phẩm</span>
              </div>
              <div class="d-flex gap-2">
                <button class="btn btn-outline-success flex-fill" @click="themGio">
                  <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                </button>
                <button class="btn btn-cart flex-fill" @click="muaNgay">
                  <i class="bi bi-lightning-charge"></i> Mua ngay
                </button>
              </div>
            </template>

            <!-- hết hàng -> đặt trước -->
            <template v-else>
              <div class="pre-note mb-2">
                <i class="bi bi-info-circle"></i>
                Biến thể <b>{{ [chon.mau, chon.size].filter(Boolean).join(' / ') }}</b> đang hết hàng.
                Đặt trước để cửa hàng báo bạn ngay khi có — không cần trả trước.
              </div>
              <button class="btn btn-pre w-100" @click="moDatTruoc">
                <i class="bi bi-bookmark-star"></i> Đặt trước
              </button>
            </template>

            <div class="mt-3 pt-3 border-top small text-muted">
              <div><i class="bi bi-truck" style="color:#0B895A"></i> Freeship đơn từ 500.000₫ · thanh toán khi nhận hàng</div>
              <div><i class="bi bi-shield-check" style="color:#0B895A"></i> Bảo hành 6 tháng · đổi trả 30 ngày</div>
            </div>
          </div>
        </div>

        <div class="mt-4 pt-3 border-top" v-if="sp.moTa">
          <h6 class="fw-bold mb-2">Mô tả sản phẩm</h6>
          <p class="text-muted mb-0" style="white-space:pre-line">{{ sp.moTa }}</p>
        </div>
      </div>
    </div>

    <DatTruocModal :product="preOrder" @close="preOrder = null" @done="load" />
  </div>
</template>

<style scoped>
.store { background:#f4f6f8; min-height:100vh; }
.store a { text-decoration:none; }
.shop-head { background:#fff; border-bottom:1px solid #e5e9ef; }
.brand { color:#0B895A; font-weight:800; font-size:24px; }
.icon-btn { color:#374151; font-size:20px; position:relative; }
.icon-btn .dot { position:absolute; top:-4px; right:-8px; background:#0B895A; color:#fff; font-size:10px; border-radius:999px; padding:0 5px; }
.sec { background:#fff; border-radius:12px; box-shadow:0 1px 2px rgba(16,24,40,.06); }
.detail-img { position:relative; background:#f7f9fb; border-radius:12px; aspect-ratio:1/1; display:flex; align-items:center; justify-content:center; }
.detail-img img { width:86%; height:86%; object-fit:contain; }
.tag-out { position:absolute; top:12px; left:12px; background:#556; color:#fff; font-size:11px; font-weight:700; padding:3px 9px; border-radius:6px; }
.price-box { color:#0B895A; font-weight:800; font-size:26px; }
.p-star { color:#E8A317; font-size:12px; }
.lbl { font-size:12px; font-weight:600; color:#6b7280; }
.opt { border:1px solid #d5dbe2; background:#fff; border-radius:8px; padding:5px 14px; font-size:13px; font-weight:600; color:#374151; cursor:pointer; }
.opt.on { border-color:#0B895A; background:#E7F4EF; color:#0A6E48; }
.opt.out { opacity:.45; text-decoration:line-through; }
.pre-note { background:#FFF4E0; color:#7a5b00; font-size:12.5px; border-radius:8px; padding:8px 10px; }
.btn-cart { background:#0B895A; color:#fff; border-radius:8px; font-weight:600; }
.btn-cart:hover { background:#0E9F67; color:#fff; }
.btn-pre { background:#fff; color:#0B895A; border:1.5px solid #0B895A; border-radius:8px; font-weight:600; }
.btn-pre:hover { background:#0B895A; color:#fff; }
@media (max-width: 860px) { .d-grid { grid-template-columns:1fr !important; } }
</style>
