import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '../composables/useAuth'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
  { path: '/home', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/gio-hang', name: 'gio-hang', component: () => import('../views/GioHangView.vue') },
  // chi tiết SP ở storefront (công khai). Không đụng /san-pham của màn quản trị:
  // '/san-pham' và '/san-pham/:id' là 2 path khác nhau nên không che nhau.
  { path: '/san-pham/:id', name: 'san-pham-detail', component: () => import('../views/SanPhamDetailView.vue') },
  { path: '/', name: 'dashboard', component: () => import('../views/DashboardView.vue') },
  { path: '/san-pham', name: 'san-pham', component: () => import('../views/SanPhamView.vue') },
  { path: '/nhan-vien', name: 'nhan-vien', component: () => import('../views/NhanVienView.vue') },
  { path: '/khach-hang', name: 'khach-hang', component: () => import('../views/KhachHangView.vue') },
  { path: '/hoa-don', name: 'hoa-don', component: () => import('../views/HoaDonView.vue') },
  { path: '/don-hang', name: 'don-hang', component: () => import('../views/DonHangView.vue') },
  { path: '/dat-truoc', name: 'dat-truoc', component: () => import('../views/DatTruocView.vue') },
  { path: '/lich-su', name: 'lich-su', component: () => import('../views/LichSuView.vue') },
  { path: '/bao-hanh', name: 'bao-hanh', component: () => import('../views/BaoHanhView.vue') },
  { path: '/phieu-giam-gia', name: 'phieu-giam-gia', component: () => import('../views/PhieuGiamGiaView.vue') },
  { path: '/he-thong', name: 'he-thong', component: () => import('../views/HeThongView.vue') },
  { path: '/style-guide', name: 'style-guide', component: () => import('../views/StyleGuideView.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({ history: createWebHistory(), routes })

// Public routes bypass auth; storefront + chi tiết SP + giỏ hàng + login are open to everyone.
const PUBLIC = ['login', 'home', 'gio-hang', 'san-pham-detail', 'style-guide']

router.beforeEach((to) => {
  const { isAuthed, allowed, landingRoute } = useAuth()
  if (PUBLIC.includes(to.name)) return true
  if (!isAuthed.value) return { name: 'login', query: { redirect: to.fullPath } }
  // permission gate: a screen this role can't access -> send to its role landing
  // (landingRoute is '/login' when the role has no permissions at all).
  if (to.name && !allowed.value.includes(to.name)) {
    return landingRoute.value
  }
  return true
})

export default router
