<script setup>
defineProps({
  label: { type: String, default: '' },
  error: { type: String, default: '' }
})
</script>

<template>
  <div class="mb-3">
    <label v-if="label" class="form-label">{{ label }}</label>
    <slot />
    <div v-if="error" class="text-danger small">{{ error }}</div>
  </div>
</template>
