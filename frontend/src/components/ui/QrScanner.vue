<script setup>
import { ref, watch, nextTick, onBeforeUnmount } from 'vue'
import { BrowserMultiFormatReader } from '@zxing/browser'
import AppModal from './AppModal.vue'
import AppButton from './AppButton.vue'

const props = defineProps({ open: Boolean })
const emit = defineEmits(['update:open', 'detected'])

const videoEl = ref(null)
const error = ref('')
let controls = null

async function start() {
  error.value = ''
  try {
    const reader = new BrowserMultiFormatReader()
    controls = await reader.decodeFromVideoDevice(undefined, videoEl.value, (result) => {
      if (result) {
        const text = result.getText()
        stop()
        emit('detected', text)
        emit('update:open', false)
      }
    })
  } catch (e) {
    error.value = 'Không truy cập được camera: ' + (e && e.message ? e.message : e)
  }
}

function stop() {
  try {
    if (controls) controls.stop()
  } catch (e) { /* ignore */ }
  controls = null
  const s = videoEl.value && videoEl.value.srcObject
  if (s) {
    s.getTracks().forEach(t => t.stop())
    videoEl.value.srcObject = null
  }
}

watch(() => props.open, (v) => { if (v) nextTick(start); else stop() })
onBeforeUnmount(stop)
</script>

<template>
  <AppModal :open="open" @update:open="v => emit('update:open', v)" title="Quét mã QR / Barcode">
    <video ref="videoEl" style="width:100%; border-radius:8px; background:#000" muted autoplay playsinline></video>
    <p class="text-muted small mt-2 mb-0">Đưa mã QR/vạch của sản phẩm vào khung hình</p>
    <p class="text-danger small" v-if="error">{{ error }}</p>
    <template #footer>
      <AppButton variant="secondary" @click="emit('update:open', false)">Đóng</AppButton>
    </template>
  </AppModal>
</template>
