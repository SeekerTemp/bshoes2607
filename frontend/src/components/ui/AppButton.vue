<script setup>
defineProps({
  variant: { type: String, default: 'primary' },   // primary | secondary | outline-secondary | outline-danger | danger
  size: { type: String, default: '' },              // '' | sm | lg
  icon: { type: String, default: '' },              // bootstrap-icons name, e.g. 'plus-lg'
  type: { type: String, default: 'button' }
})
</script>

<template>
  <button :type="type" class="btn" :class="[`btn-${variant}`, size ? `btn-${size}` : '']">
    <i v-if="icon" :class="`bi bi-${icon}`" class="me-1"></i>
    <slot />
  </button>
</template>
