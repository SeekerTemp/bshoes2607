<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { bienTheApi } from '../api/bienThe'
import { loaiSanPhamApi } from '../api/catalog'
import { useCart } from '../composables/useCart'
import { useToast } from '../composables/useToast'
import ToastHost from '../components/ui/ToastHost.vue'   // storefront không nằm trong AppShell nên phải tự gắn
import DatTruocModal from '../components/ui/DatTruocModal.vue'
import { vnd } from '../utils/format'
import { filterByCategory } from '../utils/catalog'

const router = useRouter()

const IMG = n => `/images/shoes/img_shoe_${n}.png`
const activeCat = ref(0)   // 0 = "Tất cả" (không lọc)
const sort = ref('hot')
const { soLuong: cartCount, add: addToCart } = useCart()
const { notify } = useToast()

// SALE hiển thị trên tile chỉ mang tính trang trí (backend loai_san_pham
// không lưu %giảm giá riêng) — lặp vòng qua danh sách này theo thứ tự danh mục.
const DECOR_SALE = [50, 30, 40, 25, 35, 45]
const categories = ref([])

async function loadCategories() {
  try {
    const rows = await loaiSanPhamApi.findAll()
    categories.value = (rows || []).map((c, i) => ({
      id: c.id,
      name: c.ten,
      img: IMG(10001 + (i % 6)),
      sale: DECOR_SALE[i % DECOR_SALE.length],
    }))
  } catch (e) {
    console.warn('API offline, categories unavailable', e)
    categories.value = []
  }
}
function categoryCount(id) {
  return products.value.filter(p => p.idLoaiSanPham === id).length
}

const mockProducts = [
  { id: 1, ten: 'Giày thể thao Nike Air Zoom', brand: 'Nike', gia: 200000, sale: 50, ban: 120, ton: 12, img: IMG('10007') },
  { id: 2, ten: 'Giày da Adidas công sở', brand: 'Adidas', gia: 250000, sale: 30, ban: 98, ton: 4, img: IMG('10008') },
  { id: 3, ten: 'Sandal Puma thoáng khí', brand: 'Puma', gia: 320000, sale: 40, ban: 75, ton: 0, img: IMG('10009') },
  { id: 4, ten: 'Dép Gucci thời trang cao cấp', brand: 'Gucci', gia: 350000, sale: 0, ban: 54, ton: 7, img: IMG('10010') },
  { id: 5, ten: 'Giày tây Vans sang trọng', brand: 'Vans', gia: 400000, sale: 35, ban: 63, ton: 0, img: IMG('10011') },
  { id: 6, ten: 'Giày chạy bộ Reebok cổ điển', brand: 'Reebok', gia: 450000, sale: 45, ban: 88, ton: 9, img: IMG('10012') },
  { id: 7, ten: 'Nike Air Max phối màu', brand: 'Nike', gia: 520000, sale: 20, ban: 140, ton: 15, img: IMG('10013') },
  { id: 8, ten: 'Adidas Ultraboost êm chân', brand: 'Adidas', gia: 610000, sale: 15, ban: 110, ton: 3, img: IMG('10014') },
  { id: 9, ten: 'Puma Suede lifestyle', brand: 'Puma', gia: 380000, sale: 30, ban: 66, ton: 0, img: IMG('10015') },
  { id: 10, ten: 'Vans Old Skool canvas', brand: 'Vans', gia: 290000, sale: 25, ban: 132, ton: 6, img: IMG('10016') },
]
const products = ref([])

const brandOf = ten => ['Nike', 'Adidas', 'Puma', 'Gucci', 'Vans', 'Reebok'].find(b => (ten || '').includes(b)) || 'BShoes'

async function loadProducts() {
  try {
    // /store (không phải /pos-products): giữ cả SP hết hàng để khách đặt trước
    const rows = await bienTheApi.store()
    if (rows && rows.length) {
      products.value = rows.map((p, i) => ({
        id: p.id, idSanPham: p.idSanPham, ten: p.ten, brand: brandOf(p.ten), gia: p.gia, ton: p.ton ?? 0,
        mauSize: [p.mau, p.size].filter(Boolean).join(' / '),
        idLoaiSanPham: p.idLoaiSanPham,
        sale: [50, 30, 40, 0, 35, 45, 20, 15, 30, 25][i % 10], ban: 60 + (i * 13) % 90,
        img: p.imageUrl || IMG(10007 + (i % 10)),
      }))
    } else products.value = mockProducts.map(p => ({ ...p, idSanPham: p.id }))
  } catch (e) {
    console.warn('API offline, using mock storefront', e)
    products.value = mockProducts.map(p => ({ ...p, idSanPham: p.id }))
  }
}
onMounted(() => { loadProducts(); loadCategories() })

const sorted = computed(() => {
  const a = filterByCategory(products.value, activeCat.value)
  if (sort.value === 'asc') return [...a].sort((x, y) => x.gia - y.gia)
  if (sort.value === 'desc') return [...a].sort((x, y) => y.gia - x.gia)
  return [...a].sort((x, y) => y.ban - x.ban)
})
const oldPrice = p => Math.round(p.gia / (1 - p.sale / 100))
function add(p) {
  const r = addToCart(p)
  notify(r.ok ? `Đã thêm "${p.ten}" vào giỏ` : r.message, r.ok ? 'success' : 'warning')
}

// ---- đặt trước (pre-order) ----
const preOrder = ref(null)      // biến thể đang đặt, null = đóng modal
function openPreOrder(p) { preOrder.value = p }
function closePreOrder() { preOrder.value = null }

// ---- xem chi tiết ----
function xemChiTiet(p) {
  if (p.idSanPham) router.push(`/san-pham/${p.idSanPham}`)
}
</script>

<template>
  <div class="store">
    <ToastHost />
    <!-- utility -->
    <div class="util"><div class="container d-flex justify-content-between py-1">
      <div class="d-flex gap-3"><a href="#">bshoes.vn</a><a href="#">Tải ứng dụng</a><a href="#">Kết nối</a></div>
      <div class="d-flex gap-3">
        <a href="#"><i class="bi bi-bell"></i> Thông báo</a><a href="#"><i class="bi bi-question-circle"></i> Hỗ trợ</a>
        <router-link to="/login"><i class="bi bi-box-arrow-in-right"></i> Đăng nhập</router-link>
      </div>
    </div></div>

    <!-- header -->
    <header class="shop-head py-3"><div class="container d-flex align-items-center gap-4">
      <router-link to="/home" class="brand text-nowrap"><i class="bi bi-bag-heart-fill"></i> BShoes <small class="d-block">giày chính hãng</small></router-link>
      <div class="search-wrap input-group flex-grow-1">
        <input class="form-control" placeholder="Tìm giày, thương hiệu, mã sản phẩm...">
        <button class="btn px-4"><i class="bi bi-search"></i></button>
      </div>
      <router-link to="/" class="icon-btn" title="Trang quản trị"><i class="bi bi-speedometer2"></i></router-link>
      <router-link to="/gio-hang" class="icon-btn" title="Giỏ hàng của tôi">
        <i class="bi bi-cart3"></i><span class="dot" v-if="cartCount">{{ cartCount }}</span>
      </router-link>
    </div></header>

    <!-- category nav -->
    <nav class="catnav"><div class="container d-flex flex-wrap">
      <a href="#products" :class="{active:activeCat===0}" @click.prevent="activeCat=0">Tất cả</a>
      <a v-for="c in categories" :key="c.id" href="#products" :class="{active:c.id===activeCat}" @click.prevent="activeCat=c.id">{{ c.name }}</a>
    </div></nav>

    <div class="container py-4">
      <!-- hero -->
      <div class="hero p-4 p-md-5 mb-4 d-flex align-items-center justify-content-between flex-wrap">
        <div style="max-width:520px">
          <span class="badge badge-sale mb-2">FLASH SALE CUỐI TUẦN</span>
          <h1 class="fw-bold mb-2">Giày chính hãng<br>giảm đến <span class="text-warning">50%</span></h1>
          <p class="mb-3 opacity-75">Nike · Adidas · Puma · Vans · Reebok · Gucci — freeship đơn từ 500.000₫, bảo hành 6 tháng.</p>
          <a href="#products" class="btn btn-light px-4">Mua ngay <i class="bi bi-arrow-right"></i></a>
        </div>
        <img class="hero-img d-none d-md-block" :src="IMG('10005')" alt="Giày nổi bật">
      </div>

      <!-- categories -->
      <div class="sec p-3 p-md-4 mb-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="fw-bold mb-0">Danh mục giày</h5>
          <a href="#products" class="text-decoration-none" style="color:#0B895A">Xem tất cả <i class="bi bi-chevron-right"></i></a>
        </div>
        <div class="row row-cols-3 row-cols-md-6 g-2">
          <div class="col" v-for="c in categories" :key="c.id">
            <a href="#products" class="cat-tile" @click.prevent="activeCat=c.id">
              <div class="cat-thumb"><img :src="c.img" :alt="c.name"><span class="sale">SALE -{{ c.sale }}%</span></div>
              <div class="cat-name">{{ c.name }}</div><div class="text-muted" style="font-size:11px">{{ categoryCount(c.id) }} mẫu</div>
            </a>
          </div>
        </div>
      </div>

      <!-- products -->
      <div class="sec p-3 p-md-4" id="products">
        <div class="sortbar d-flex align-items-center gap-2 mb-3 flex-wrap">
          <span class="fw-bold me-2">Sản phẩm</span><span class="text-muted">Sắp xếp theo:</span>
          <a href="#" :class="{active:sort==='hot'}" @click.prevent="sort='hot'">Bán chạy</a> |
          <a href="#" :class="{active:sort==='asc'}" @click.prevent="sort='asc'">Giá thấp → cao</a> |
          <a href="#" :class="{active:sort==='desc'}" @click.prevent="sort='desc'">Giá cao → thấp</a>
          <span class="ms-auto text-muted small">{{ sorted.length }} sản phẩm</span>
        </div>
        <div class="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-3">
          <div class="col" v-for="p in sorted" :key="p.id">
            <div class="p-card" :class="{ soldout: p.ton === 0 }">
              <div class="p-thumb" @click="xemChiTiet(p)" style="cursor:pointer">
                <span class="sale" v-if="p.sale && p.ton > 0">-{{ p.sale }}%</span>
                <span class="tag-pre" v-if="p.ton === 0">HẾT HÀNG</span>
                <i class="bi bi-heart wish"></i>
                <img :src="p.img" :alt="p.ten">
              </div>
              <div class="p-body">
                <div class="p-brand">{{ p.brand }}</div>
                <div class="p-name p-link" @click="xemChiTiet(p)">{{ p.ten }}</div>
                <div class="p-star my-1"><i class="bi bi-star-fill" v-for="s in 5" :key="s"></i><span class="text-muted ms-1" style="font-size:11px">({{ p.ban }})</span></div>
                <div class="d-flex align-items-baseline gap-2">
                  <span class="p-price">{{ vnd(p.gia) }}</span>
                  <span class="p-old" v-if="p.sale && p.ton > 0">{{ vnd(oldPrice(p)) }}</span>
                </div>
                <button v-if="p.ton > 0" class="btn btn-cart w-100 mt-2" @click="add(p)">
                  <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                </button>
                <button v-else class="btn btn-pre w-100 mt-2" @click="openPreOrder(p)">
                  <i class="bi bi-bookmark-star"></i> Đặt trước
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- modal đặt trước (dùng chung với trang chi tiết) -->
    <DatTruocModal :product="preOrder" @close="closePreOrder" @done="loadProducts" />
    <!-- footer -->
    <footer class="shop-foot pt-4"><div class="container">
      <div class="row g-4 pb-4">
        <div class="col-6 col-md-3">
          <div class="brand mb-2" style="font-size:20px">BShoes</div>
          <p class="text-muted small mb-2">Hệ thống cửa hàng giày chính hãng — thể thao, da, sandal, giày tây.</p>
          <div class="d-flex gap-3 fs-5">
            <a href="#" style="color:#1877f2"><i class="bi bi-facebook"></i></a><a href="#" style="color:#e1306c"><i class="bi bi-instagram"></i></a><a href="#" style="color:#1da1f2"><i class="bi bi-twitter-x"></i></a>
          </div>
        </div>
        <div class="col-6 col-md-2"><h6>Về BShoes</h6><a href="#">Giới thiệu</a><a href="#">Tuyển dụng</a><a href="#">Tin tức</a></div>
        <div class="col-6 col-md-2"><h6>Hỗ trợ</h6><router-link to="/bao-hanh">Chính sách bảo hành</router-link><a href="#">Đổi trả 30 ngày</a><a href="#">Hướng dẫn chọn size</a></div>
        <div class="col-6 col-md-2"><h6>Thanh toán</h6><a href="#">Tiền mặt (COD)</a><a href="#">Chuyển khoản</a><a href="#">Thẻ / Ví</a></div>
        <div class="col-6 col-md-3"><h6>Vận chuyển</h6><a href="#">Giao hàng nhanh</a><router-link to="/gio-hang">Theo dõi đơn hàng</router-link><a href="#">Freeship đơn từ 500K</a></div>
      </div>
    </div>
      <div class="foot-bar text-center py-2">© 2026 BShoes — Cửa hàng giày chính hãng</div>
    </footer>
  </div>
</template>

<style scoped>
.store { background:#f4f6f8; min-height:100vh; }
.store a { text-decoration:none; }
.util { background:#eef1f4; font-size:12px; color:#556; }
.util a { color:#556; } .util a:hover { color:#0B895A; }
.shop-head { background:#fff; border-bottom:1px solid #e5e9ef; }
.brand { color:#0B895A; font-weight:800; font-size:26px; letter-spacing:.3px; }
.brand small { color:#9aa4b2; font-weight:500; font-size:12px; }
.search-wrap .form-control { border-radius:999px 0 0 999px; }
.search-wrap .btn { border-radius:0 999px 999px 0; background:#0B895A; color:#fff; }
.icon-btn { color:#374151; font-size:20px; position:relative; }
.icon-btn .dot { position:absolute; top:-4px; right:-8px; background:#0B895A; color:#fff; font-size:10px; border-radius:999px; padding:0 5px; }
.catnav { background:#0B895A; }
.catnav a { color:#eafff5; padding:12px 16px; font-weight:600; font-size:14px; display:inline-block; white-space:nowrap; }
.catnav a:hover, .catnav a.active { background:#fff; color:#0B895A; }
.hero { background:linear-gradient(120deg,#0B895A,#086B45); color:#fff; border-radius:14px; overflow:hidden; }
.hero .badge-sale { background:#ffe08a; color:#7a5b00; font-weight:700; }
.hero .btn-light { color:#0B895A; font-weight:700; }
.hero-img { width:100%; max-width:360px; filter:drop-shadow(0 18px 26px rgba(0,0,0,.35)); transform:rotate(-8deg); }
.sec { background:#fff; border-radius:12px; box-shadow:0 1px 2px rgba(16,24,40,.06); }
.cat-tile { text-align:center; padding:10px 6px; border-radius:10px; transition:.15s; display:block; color:#1f2933; }
.cat-tile:hover { background:#f0faf5; transform:translateY(-2px); }
.cat-thumb { position:relative; width:96px; height:96px; margin:0 auto 8px; border-radius:50%; background:radial-gradient(circle at 50% 35%, #E7F4EF, #d5ebe1); display:flex; align-items:center; justify-content:center; }
.cat-thumb img { width:78px; height:78px; object-fit:contain; }
.cat-thumb .sale { position:absolute; bottom:2px; left:50%; transform:translateX(-50%); background:#E5484D; color:#fff; font-size:10px; font-weight:700; padding:1px 6px; border-radius:4px; white-space:nowrap; }
.cat-name { font-size:13px; font-weight:600; }
.sortbar a { color:#556; font-weight:600; font-size:14px; padding:0 4px; }
.sortbar a.active, .sortbar a:hover { color:#0B895A; }
.p-card { background:#fff; border:1px solid #eef1f4; border-radius:12px; overflow:hidden; transition:.15s; height:100%; }
.p-card:hover { box-shadow:0 8px 22px rgba(16,24,40,.12); transform:translateY(-3px); }
.p-thumb { position:relative; aspect-ratio:1/1; background:#f7f9fb; display:flex; align-items:center; justify-content:center; }
.p-thumb img { width:82%; height:82%; object-fit:contain; }
.p-thumb .sale { position:absolute; top:8px; left:8px; background:#E5484D; color:#fff; font-size:11px; font-weight:700; padding:2px 7px; border-radius:6px; }
.p-thumb .wish { position:absolute; top:8px; right:8px; color:#c3ccd6; font-size:18px; }
.p-body { padding:10px 12px 12px; }
.p-name { font-size:13.5px; font-weight:600; line-height:1.3; height:2.6em; overflow:hidden; }
.p-brand { font-size:11px; color:#9aa4b2; }
.p-price { color:#0B895A; font-weight:800; font-size:15px; }
.p-old { color:#aab; text-decoration:line-through; font-size:12px; }
.p-star { color:#E8A317; font-size:12px; }
.btn-cart { background:#0B895A; color:#fff; border-radius:8px; font-size:12px; font-weight:600; }
.btn-cart:hover { background:#0E9F67; color:#fff; }
.btn-cart:disabled { background:#7fb9a2; }
/* hết hàng → đặt trước */
.p-card.soldout .p-thumb img { filter:grayscale(.75); opacity:.75; }
.p-thumb .tag-pre { position:absolute; top:8px; left:8px; background:#556; color:#fff; font-size:10px; font-weight:700; padding:2px 7px; border-radius:6px; }
.btn-pre { background:#fff; color:#0B895A; border:1.5px solid #0B895A; border-radius:8px; font-size:12px; font-weight:600; }
.btn-pre:hover { background:#0B895A; color:#fff; }
/* CSS của modal đặt trước đã chuyển sang components/ui/DatTruocModal.vue */
.p-link { cursor:pointer; }
.p-link:hover { color:#0B895A; }
.shop-foot { background:#fff; border-top:1px solid #e5e9ef; }
.shop-foot h6 { font-weight:700; font-size:14px; }
.shop-foot a { color:#6b7280; font-size:13px; display:block; padding:3px 0; }
.shop-foot a:hover { color:#0B895A; }
.foot-bar { background:#0f1c17; color:#9fb4a9; font-size:12px; }
</style>
