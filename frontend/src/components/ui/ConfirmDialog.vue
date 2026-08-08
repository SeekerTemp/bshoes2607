<script setup>
import AppModal from './AppModal.vue'
import AppButton from './AppButton.vue'

defineProps({
  open: Boolean,
  title: { type: String, default: 'Xác nhận' },
  message: { type: String, default: '' },
  // The confirm button used to always read "Xoá", whatever it was confirming —
  // so the logout dialog asked "Đăng xuất?" and offered [Huỷ] [Xoá]. Callers
  // that are not deleting anything should pass their own verb.
  confirmText: { type: String, default: 'Xoá' },
  confirmVariant: { type: String, default: 'danger' },
})
const emit = defineEmits(['update:open', 'confirm'])
</script>

<template>
  <AppModal :open="open" :title="title" @update:open="emit('update:open', $event)">
    <p class="mb-0">{{ message }}</p>
    <template #footer>
      <AppButton variant="secondary" @click="emit('update:open', false)">Huỷ</AppButton>
      <AppButton :variant="confirmVariant" @click="emit('confirm')">{{ confirmText }}</AppButton>
    </template>
  </AppModal>
</template>
