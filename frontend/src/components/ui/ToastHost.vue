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
      class="toast show align-items-center border-0 mb-2 app-toast"
      :class="`app-toast--${t.type}`"
      role="alert"
    >
      <div class="d-flex">
        <div class="toast-body d-flex align-items-center">
          <i :class="`bi bi-${iconFor(t.type)} me-2`"></i>{{ t.message }}
        </div>
        <button
          type="button"
          class="btn-close me-2 m-auto"
          @click="remove(t.id)"
        ></button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-toast {
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  font-weight: 500;
}

.app-toast--success {
  background: var(--c-success-subtle);
  color: var(--c-success-text);
}
.app-toast--warning {
  background: var(--c-warning-subtle);
  color: var(--c-warning-text);
}
.app-toast--danger {
  background: var(--c-danger-subtle);
  color: var(--c-danger-text);
}
.app-toast--info {
  background: var(--c-info-subtle);
  color: var(--c-info-text);
}
</style>
