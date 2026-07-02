<script setup>
defineProps({ title: { type: String, required: true } })
</script>

<template>
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h4 class="mb-0" style="color: var(--c-primary)">{{ title }}</h4>
    <div class="d-flex gap-2"><slot name="actions" /></div>
  </div>
</template>
