<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { Modal } from 'bootstrap'
const props = defineProps({ open: Boolean, title: { type: String, default: '' } })
const emit = defineEmits(['update:open'])
const el = ref(null)
let modal
onMounted(() => {
  modal = new Modal(el.value)
  el.value.addEventListener('hidden.bs.modal', () => emit('update:open', false))
  if (props.open) modal.show()
})
watch(() => props.open, (v) => { v ? modal?.show() : modal?.hide() })
onBeforeUnmount(() => modal?.dispose())
</script>

<template>
  <div class="modal fade" tabindex="-1" ref="el">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ title }}</h5>
          <button type="button" class="btn-close" @click="emit('update:open', false)"></button>
        </div>
        <div class="modal-body"><slot /></div>
        <div class="modal-footer"><slot name="footer" /></div>
      </div>
    </div>
  </div>
</template>
