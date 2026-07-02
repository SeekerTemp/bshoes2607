<script setup>
import { ref, watch } from 'vue'
const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: 'Tìm kiếm...' }
})
const emit = defineEmits(['update:modelValue'])
const local = ref(props.modelValue)
let t
watch(local, (v) => { clearTimeout(t); t = setTimeout(() => emit('update:modelValue', v), 250) })
watch(() => props.modelValue, (v) => { if (v !== local.value) local.value = v })
</script>

<template>
  <div class="input-group" style="max-width: 320px">
    <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
    <input class="form-control" v-model="local" :placeholder="placeholder" />
  </div>
</template>
