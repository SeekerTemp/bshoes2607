<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useLayout } from '../../composables/useLayout'
import { useAuth } from '../../composables/useAuth'
import { SCREENS } from '../../config/screens'

const { sidebarCollapsed } = useLayout()
const { allowed, logout } = useAuth()
const router = useRouter()

// only the screens this role is permitted to see (vai_tro.quyen)
const navItems = computed(() =>
  SCREENS.filter(s => allowed.value.includes(s.key))
    .map(s => ({ to: s.to, icon: s.icon, label: s.label, exact: s.to === '/' }))
)
function doLogout() { logout(); router.push('/login') }
</script>

<template>
  <aside class="app-sidebar d-flex flex-column" :class="{ collapsed: sidebarCollapsed }">
    <nav class="side-nav d-flex flex-column">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="nav-link-item"
        :class="{ 'exact-link': item.exact }"
        active-class="active"
        :exact-active-class="item.exact ? 'active' : undefined"
        :title="sidebarCollapsed ? item.label : undefined"
      >
        <i :class="['bi', item.icon]"></i>
        <span class="nav-label">{{ item.label }}</span>
      </router-link>
    </nav>

    <a href="#" class="nav-link-item logout-link" @click.prevent="doLogout" :title="sidebarCollapsed ? 'Đăng xuất' : undefined">
      <i class="bi bi-box-arrow-right"></i>
      <span class="nav-label">Đăng xuất</span>
    </a>
  </aside>
</template>

<style scoped>
.app-sidebar {
  width: var(--sidebar-w);
  min-width: var(--sidebar-w);
  background: var(--c-primary);
  padding: var(--sp-3) var(--sp-2);
  height: 100%;
  overflow: hidden;                /* the nav scrolls, not the whole sidebar */
  transition: width var(--transition), min-width var(--transition);
}
.app-sidebar.collapsed {
  width: 64px;
  min-width: 64px;
}

/* nav list scrolls on its own if it ever overflows; logout stays pinned below */
.side-nav {
  flex: 1 1 auto;
  overflow-y: auto;
  min-height: 0;
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
  white-space: nowrap;
  transition: background-color var(--transition), color var(--transition);
}

.nav-link-item i {
  font-size: 1.05rem;
  width: 20px;
  text-align: center;
  flex-shrink: 0;
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

.logout-link {
  flex-shrink: 0;
  margin-top: var(--sp-2);
}

/* collapsed: hide labels, center icons */
.collapsed .nav-label { display: none; }
.collapsed .nav-link-item { justify-content: center; padding: 10px 0; gap: 0; }
</style>
