<script setup>
const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  options: { type: Array, default: () => [] } // [{value,label}] or plain strings
})
defineEmits(['update:modelValue'])

function optValue(o) {
  return typeof o === 'object' && o !== null ? o.value : o
}
function optLabel(o) {
  return typeof o === 'object' && o !== null ? o.label : o
}
</script>

<template>
  <select
    class="form-select"
    :value="modelValue"
    @change="$emit('update:modelValue', $event.target.value)"
  >
    <option v-for="o in options" :key="optValue(o)" :value="optValue(o)">{{ optLabel(o) }}</option>
  </select>
</template>
