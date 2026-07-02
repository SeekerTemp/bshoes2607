<script setup>
import AppModal from './AppModal.vue'
import AppButton from './AppButton.vue'

defineProps({
  open: Boolean,
  title: { type: String, default: 'Xác nhận' },
  message: { type: String, default: '' }
})
const emit = defineEmits(['update:open', 'confirm'])
</script>

<template>
  <AppModal :open="open" :title="title" @update:open="emit('update:open', $event)">
    <p class="mb-0">{{ message }}</p>
    <template #footer>
      <AppButton variant="secondary" @click="emit('update:open', false)">Huỷ</AppButton>
      <AppButton variant="danger" @click="emit('confirm')">Xoá</AppButton>
    </template>
  </AppModal>
</template>
