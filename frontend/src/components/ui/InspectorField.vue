<script setup>
/* Một dòng nhãn + ô nhập trong InspectorPanel. Cùng lưới 120px như panel Sản phẩm. */
defineProps({
  label: { type: String, required: true },
  /** Hiện dấu * đỏ nhạt sau nhãn. */
  required: { type: Boolean, default: false },
})
</script>

<template>
  <div class="if-row">
    <dt>{{ label }} <span v-if="required" class="if-req">*</span></dt>
    <dd><slot /></dd>
  </div>
</template>

<style scoped>
.if-row { display: grid; grid-template-columns: 120px 1fr; align-items: center; gap: 8px; }
.if-row dt { font-weight: 500; font-size: 13px; opacity: .95; }
.if-row dd { margin: 0; }
.if-req { color: #ffd9d9; font-weight: 700; }

/* Giá trị chỉ đọc do server sinh (mã). Là <p>, không phải input đã disable: ô nhập
   xám vẫn mời người dùng bấm vào gõ, rồi nuốt mất thứ họ gõ. */
.if-row :deep(.ins-static) {
  margin: 0; padding: 6px 0; font-size: 13px; font-weight: 600;
  letter-spacing: .3px; word-break: break-all;
}
.if-row :deep(.ins-static.chua-co) { font-weight: 400; opacity: .75; font-style: italic; }
</style>
