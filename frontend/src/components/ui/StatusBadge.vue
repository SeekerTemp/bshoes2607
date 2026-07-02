<script setup>
defineProps({
  active: { type: Boolean, default: false },
  activeText: { type: String, default: 'Hoạt động' },
  inactiveText: { type: String, default: 'Ngừng' }
})
</script>

<template>
  <span class="badge" :class="active ? 'bg-success' : 'bg-secondary'">
    {{ active ? activeText : inactiveText }}
  </span>
</template>
