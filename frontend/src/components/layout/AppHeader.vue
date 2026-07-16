<script setup>
import { RouterLink } from 'vue-router'
import { useLayout } from '../../composables/useLayout'
import { useAuth } from '../../composables/useAuth'
const { toggleSidebar } = useLayout()
const { user } = useAuth()
</script>

<template>
  <header class="app-header d-flex align-items-center justify-content-between px-3">
    <div class="d-flex align-items-center gap-2">
      <button class="hamburger" type="button" title="Thu gọn / mở menu" @click="toggleSidebar">
        <i class="bi bi-list"></i>
      </button>
      <div class="d-flex flex-column justify-content-center">
        <span class="brand">BShoes</span>
        <span class="subtitle">Quản lý cửa hàng giày</span>
      </div>
    </div>

    <div class="d-flex align-items-center gap-2">
      <RouterLink to="/home" class="store-link d-none d-sm-inline-flex align-items-center gap-1" title="Xem cửa hàng">
        <i class="bi bi-shop"></i> <span>Xem cửa hàng</span>
      </RouterLink>
      <div class="user-chip d-flex align-items-center gap-2">
        <i class="bi bi-person-circle"></i>
        <span>{{ user?.ten || 'Khách' }}</span>
        <span v-if="user?.vaiTro" class="badge bg-light text-dark ms-1">{{ user.vaiTro }}</span>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  height: var(--header-h);
  min-height: var(--header-h);
  background: var(--c-primary);
  box-shadow: var(--shadow-sm);
  z-index: 20;
}

.hamburger {
  border: none;
  background: transparent;
  color: #fff;
  font-size: 1.4rem;
  line-height: 1;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background-color var(--transition);
}
.hamburger:hover { background: rgba(255, 255, 255, 0.15); }

.brand {
  color: #fff;
  font-weight: 700;
  font-size: 1.05rem;
  line-height: 1.1;
}

.subtitle {
  color: rgba(255, 255, 255, 0.75);
  font-size: 0.72rem;
  line-height: 1.1;
}

.user-chip {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.85rem;
  background: rgba(255, 255, 255, 0.12);
  padding: 4px 12px;
  border-radius: var(--radius-pill);
}

.user-chip i {
  font-size: 1rem;
}

.store-link {
  color: #fff;
  text-decoration: none;
  font-size: 0.82rem;
  background: rgba(255, 255, 255, 0.12);
  padding: 4px 12px;
  border-radius: var(--radius-pill);
  transition: background-color var(--transition);
}
.store-link:hover { background: rgba(255, 255, 255, 0.22); }
</style>
