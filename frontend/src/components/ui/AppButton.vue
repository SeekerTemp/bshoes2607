<script setup>
defineProps({
  variant: { type: String, default: 'primary' },   // primary | secondary | outline-secondary | outline-danger | danger | success | warning | info
  size: { type: String, default: '' },              // '' | sm | lg
  icon: { type: String, default: '' },              // bootstrap-icons name, e.g. 'plus-lg'
  type: { type: String, default: 'button' },
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false }
})
</script>

<template>
  <button
    :type="type"
    class="btn app-btn"
    :class="[`btn-${variant}`, size ? `btn-${size}` : '']"
    :disabled="disabled || loading"
  >
    <span v-if="loading" class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
    <i v-else-if="icon" :class="`bi bi-${icon}`" class="me-1"></i>
    <slot />
  </button>
</template>

<style scoped>
.app-btn {
  transition: var(--transition);
}
.app-btn:hover:not(:disabled) {
  filter: brightness(0.94);
  box-shadow: var(--shadow-sm);
}
.app-btn:active:not(:disabled) {
  transform: translateY(1px);
}
.app-btn:focus-visible {
  box-shadow: var(--focus-ring);
  outline: none;
}
.app-btn:disabled {
  opacity: .55;
  cursor: not-allowed;
}
</style>
