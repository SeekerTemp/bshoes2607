import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
  { path: '/', name: 'dashboard', component: () => import('../views/DashboardView.vue') },
  { path: '/san-pham', name: 'san-pham', component: () => import('../views/SanPhamView.vue') },
  { path: '/nhan-vien', name: 'nhan-vien', component: () => import('../views/NhanVienView.vue') },
  { path: '/khach-hang', name: 'khach-hang', component: () => import('../views/KhachHangView.vue') },
  { path: '/hoa-don', name: 'hoa-don', component: () => import('../views/HoaDonView.vue') },
  { path: '/lich-su', name: 'lich-su', component: () => import('../views/LichSuView.vue') },
  { path: '/phieu-giam-gia', name: 'phieu-giam-gia', component: () => import('../views/PhieuGiamGiaView.vue') },
  { path: '/he-thong', name: 'he-thong', component: () => import('../views/HeThongView.vue') },
  { path: '/style-guide', name: 'style-guide', component: () => import('../views/StyleGuideView.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

export default createRouter({ history: createWebHistory(), routes })
