<script setup>
// Grid card selector for product images. Opens as a modal, shows a searchable
// grid of image cards; clicking a card selects it.
import { ref, computed, watch } from 'vue'
import AppModal from './AppModal.vue'
import AppButton from './AppButton.vue'
import { shoeImages } from '../../mock/shoeImages'

const props = defineProps({
  open: { type: Boolean, default: false },
  modelValue: { type: String, default: '' },
})
const emit = defineEmits(['update:open', 'update:modelValue'])

const keyword = ref('')
const picked = ref(props.modelValue)
watch(() => props.open, (v) => { if (v) picked.value = props.modelValue })

const filtered = computed(() => {
  const k = keyword.value.trim().toLowerCase()
  return k ? shoeImages.filter(u => u.toLowerCase().includes(k)) : shoeImages
})

function choose(url) { picked.value = url }
function apply() {
  emit('update:modelValue', picked.value)
  emit('update:open', false)
}
</script>

<template>
  <AppModal :open="open" @update:open="v => emit('update:open', v)" title="Chọn ảnh sản phẩm" size="lg">
    <div class="input-group input-group-sm mb-3" style="max-width: 260px">
      <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
      <input class="form-control" v-model="keyword" placeholder="Tìm ảnh theo mã..." />
    </div>
    <div class="img-grid">
      <button
        v-for="url in filtered"
        :key="url"
        type="button"
        class="img-card"
        :class="{ picked: url === picked }"
        @click="choose(url)"
      >
        <img :src="url" alt="" loading="lazy" />
        <i v-if="url === picked" class="bi bi-check-circle-fill tick"></i>
      </button>
      <p v-if="filtered.length === 0" class="text-muted">Không tìm thấy ảnh</p>
    </div>
    <template #footer>
      <AppButton variant="secondary" @click="emit('update:open', false)">Huỷ</AppButton>
      <AppButton :disabled="!picked" @click="apply">Chọn ảnh</AppButton>
    </template>
  </AppModal>
</template>

<style scoped>
.img-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(96px, 1fr));
  gap: 10px;
  max-height: 60vh;
  overflow-y: auto;
  padding: 2px;
}
.img-card {
  position: relative;
  aspect-ratio: 1;
  border: 2px solid var(--c-border);
  border-radius: var(--radius-sm);
  background: #f5f7fa;
  padding: 6px;
  cursor: pointer;
  transition: var(--transition);
}
.img-card:hover { border-color: var(--c-primary-light); box-shadow: var(--shadow-sm); }
.img-card.picked { border-color: var(--c-primary); box-shadow: 0 0 0 2px var(--c-primary-subtle); }
.img-card img { width: 100%; height: 100%; object-fit: contain; }
.tick { position: absolute; top: 4px; right: 4px; color: var(--c-primary); font-size: 1.1rem; background: #fff; border-radius: 50%; }
</style>
