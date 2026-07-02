<script setup>
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'
import ToastHost from '../ui/ToastHost.vue'
</script>

<template>
  <div class="app-shell d-flex flex-column">
    <AppHeader />
    <div class="d-flex flex-grow-1">
      <AppSidebar />
      <main class="flex-grow-1 p-4" style="min-height: calc(100vh - var(--header-h)); overflow: auto">
        <slot />
      </main>
    </div>
    <ToastHost />
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}
</style>
