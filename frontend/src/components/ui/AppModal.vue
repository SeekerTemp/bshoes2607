<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { Modal } from 'bootstrap'
const props = defineProps({ open: Boolean, title: { type: String, default: '' }, size: { type: String, default: '' } })
const emit = defineEmits(['update:open'])
const el = ref(null)
let modal
onMounted(() => {
  modal = new Modal(el.value)
  el.value.addEventListener('hidden.bs.modal', () => emit('update:open', false))
  if (props.open) modal.show()
})
watch(() => props.open, (v) => { v ? modal?.show() : modal?.hide() })

// Tearing the component down while bootstrap is still animating the hide left
// its transition callback running against a disposed instance:
//   Uncaught TypeError: Cannot read properties of null (reading 'style')
//       at _Modal._hideModal (bootstrap.js)
// (seen in logs/bshoes.log via the CLIENT logger when navigating away from a
// screen with an open modal). Dropping `fade` first makes bootstrap tear down
// synchronously, so no callback survives the unmount.
onBeforeUnmount(() => {
  try {
    el.value?.classList.remove('fade')
    modal?.hide()
    modal?.dispose()
  } catch {
    // teardown must never throw during unmount
  }
  // A modal unmounted mid-transition can also strand its backdrop, leaving a
  // grey overlay that swallows every click on the next screen.
  document.querySelectorAll('.modal-backdrop').forEach((b) => b.remove())
  document.body.classList.remove('modal-open')
  document.body.style.removeProperty('overflow')
  document.body.style.removeProperty('padding-right')
})
</script>

<template>
  <div class="modal fade" tabindex="-1" ref="el">
    <div class="modal-dialog" :class="size ? `modal-${size}` : ''">
      <div class="modal-content app-modal-content">
        <div class="modal-header app-modal-header">
          <h5 class="modal-title">{{ title }}</h5>
          <button type="button" class="btn-close" @click="emit('update:open', false)"></button>
        </div>
        <div class="modal-body"><slot /></div>
        <div class="modal-footer"><slot name="footer" /></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-modal-content {
  border: none;
  border-radius: var(--radius);
  box-shadow: var(--shadow-lg);
}
.app-modal-header {
  border-bottom: 1px solid var(--c-border);
}
</style>
