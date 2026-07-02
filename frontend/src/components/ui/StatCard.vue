<script setup>
defineProps({
  label: { type: String, default: '' },
  value: { type: [String, Number], default: '' }
})
</script>

<template>
  <div class="stat-card bg-white p-3 h-100">
    <div class="text-muted small">{{ label }}</div>
    <div class="fs-4 fw-bold">{{ value }}</div>
  </div>
</template>

<style scoped>
.stat-card {
  border-left: 4px solid var(--c-primary);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
}
</style>
