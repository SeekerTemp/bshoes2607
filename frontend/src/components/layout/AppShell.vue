<script setup>
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'
import ToastHost from '../ui/ToastHost.vue'
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <div class="app-body">
      <AppSidebar />
      <main class="app-main">
        <slot />
      </main>
    </div>
    <ToastHost />
  </div>
</template>

<style scoped>
/* Fixed viewport: header stays on top, sidebar stays put, only <main> scrolls. */
.app-shell {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.app-body {
  flex: 1 1 auto;
  display: flex;
  min-height: 0;                 /* let the row shrink so children can scroll */
}
.app-main {
  flex: 1 1 auto;
  min-width: 0;
  overflow: auto;                /* content scroll is isolated from the sidebar */
  padding: var(--sp-4);
}
</style>
