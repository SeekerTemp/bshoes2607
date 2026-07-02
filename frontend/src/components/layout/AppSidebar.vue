<script setup>
const navItems = [
  { to: '/', icon: 'bi-graph-up', label: 'Doanh Thu', exact: true },
  { to: '/san-pham', icon: 'bi-box-seam', label: 'Sản Phẩm' },
  { to: '/hoa-don', icon: 'bi-cart', label: 'Hóa Đơn' },
  { to: '/nhan-vien', icon: 'bi-people', label: 'Nhân Viên' },
  { to: '/khach-hang', icon: 'bi-person-badge', label: 'Khách Hàng' },
  { to: '/lich-su', icon: 'bi-clock-history', label: 'Lịch Sử' },
  { to: '/phieu-giam-gia', icon: 'bi-ticket-perforated', label: 'Khuyến Mãi' },
  { to: '/he-thong', icon: 'bi-gear', label: 'Hệ Thống' }
]
</script>

<template>
  <aside class="app-sidebar d-flex flex-column">
    <nav class="d-flex flex-column flex-grow-1">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="nav-link-item"
        :class="{ 'exact-link': item.exact }"
        active-class="active"
        :exact-active-class="item.exact ? 'active' : undefined"
      >
        <i :class="['bi', item.icon]"></i>
        <span>{{ item.label }}</span>
      </router-link>
    </nav>

    <router-link to="/login" class="nav-link-item mt-auto" active-class="active">
      <i class="bi bi-box-arrow-right"></i>
      <span>Đăng Nhập</span>
    </router-link>
  </aside>
</template>

<style scoped>
.app-sidebar {
  width: var(--sidebar-w);
  min-width: var(--sidebar-w);
  background: var(--c-primary);
  padding: var(--sp-3) var(--sp-2);
}

.nav-link-item {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: 10px var(--sp-4);
  margin-bottom: 4px;
  border-radius: var(--radius);
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-weight: 500;
  transition: background-color var(--transition), color var(--transition), transform var(--transition);
}

.nav-link-item i {
  font-size: 1.05rem;
  width: 20px;
  text-align: center;
}

.nav-link-item:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.nav-link-item.active {
  background: #fff;
  color: var(--c-primary);
  box-shadow: var(--shadow-sm);
}
</style>
