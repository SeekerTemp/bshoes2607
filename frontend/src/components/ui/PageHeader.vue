<script setup>
defineProps({ title: { type: String, required: true } })
</script>

<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h4 class="mb-0 fw-bold" style="color: var(--c-text)">{{ title }}</h4>
    <div class="d-flex gap-2"><slot name="actions" /></div>
  </div>
</template>
