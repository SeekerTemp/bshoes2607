<script setup>
import { useToast } from '../../composables/useToast'

const { toasts, remove } = useToast()

const iconFor = (type) => ({
  success: 'check-circle-fill',
  danger: 'x-circle-fill',
  warning: 'exclamation-triangle-fill',
  info: 'info-circle-fill'
}[type] || 'check-circle-fill')
</script>

<template>
  <div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 1080">
    <div
      v-for="t in toasts"
      :key="t.id"
      class="toast show align-items-center text-white border-0 mb-2"
      :class="`bg-${t.type}`"
      role="alert"
    >
      <div class="d-flex">
        <div class="toast-body">
          <i :class="`bi bi-${iconFor(t.type)} me-2`"></i>{{ t.message }}
        </div>
        <button
          type="button"
          class="btn-close btn-close-white me-2 m-auto"
          @click="remove(t.id)"
        ></button>
      </div>
    </div>
  </div>
</template>
