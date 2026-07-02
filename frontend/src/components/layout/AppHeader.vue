<script setup>
</script>

<template>
  <header class="app-header d-flex align-items-center justify-content-between px-4">
    <div class="d-flex flex-column justify-content-center">
      <span class="brand">BShoes</span>
      <span class="subtitle">Quản lý cửa hàng giày</span>
    </div>

    <div class="user-chip d-flex align-items-center gap-2">
      <i class="bi bi-person-circle"></i>
      <span>admin</span>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  height: var(--header-h);
  min-height: var(--header-h);
  background: var(--c-primary);
  box-shadow: var(--shadow-sm);
  z-index: 10;
}

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
</style>
