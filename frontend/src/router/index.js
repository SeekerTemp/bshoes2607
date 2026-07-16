import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import { SCREENS } from '../config/screens'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
  { path: '/home', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/', name: 'dashboard', component: () => import('../views/DashboardView.vue') },
  { path: '/san-pham', name: 'san-pham', component: () => import('../views/SanPhamView.vue') },
  { path: '/danh-muc', name: 'danh-muc', component: () => import('../views/DanhMucView.vue') },
  { path: '/thuoc-tinh', name: 'thuoc-tinh', component: () => import('../views/ThuocTinhView.vue') },
  { path: '/nhan-vien', name: 'nhan-vien', component: () => import('../views/NhanVienView.vue') },
  { path: '/khach-hang', name: 'khach-hang', component: () => import('../views/KhachHangView.vue') },
  { path: '/hoa-don', name: 'hoa-don', component: () => import('../views/HoaDonView.vue') },
  { path: '/don-hang', name: 'don-hang', component: () => import('../views/DonHangView.vue') },
  { path: '/dat-truoc', name: 'dat-truoc', component: () => import('../views/DatTruocView.vue') },
  { path: '/lich-su', name: 'lich-su', component: () => import('../views/LichSuView.vue') },
  { path: '/bao-hanh', name: 'bao-hanh', component: () => import('../views/BaoHanhView.vue') },
  { path: '/phieu-giam-gia', name: 'phieu-giam-gia', component: () => import('../views/PhieuGiamGiaView.vue') },
  { path: '/phan-quyen', name: 'phan-quyen', component: () => import('../views/PhanQuyenView.vue') },
  { path: '/he-thong', name: 'he-thong', component: () => import('../views/HeThongView.vue') },
  { path: '/style-guide', name: 'style-guide', component: () => import('../views/StyleGuideView.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({ history: createWebHistory(), routes })

// Public routes bypass auth; storefront + login are open to everyone.
const PUBLIC = ['login', 'home', 'style-guide']

router.beforeEach((to) => {
  const { isAuthed, allowed } = useAuth()
  if (PUBLIC.includes(to.name)) return true
  if (!isAuthed.value) return { name: 'login', query: { redirect: to.fullPath } }
  // permission gate: redirect to the first screen this role can access
  const keys = allowed.value
  if (to.name && keys.length && !keys.includes(to.name)) {
    const first = SCREENS.find(s => keys.includes(s.key))
    return first ? first.to : { name: 'login' }
  }
  return true
})

export default router
