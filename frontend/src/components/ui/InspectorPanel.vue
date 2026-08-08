<script setup>
/*
 * Panel xanh master-detail bên phải bảng — cùng hình dáng với inspector đã có sẵn ở
 * màn Sản phẩm (NetBeans Pnl_3_qlSanPham). Tách ra thành component vì Khách hàng và
 * Nhân viên giờ cũng dùng: nhập/sửa ngay tại chỗ, không phải mở modal.
 *
 * Slot:
 *   default  — các dòng <InspectorField>
 *   actions  — các nút xếp dọc dưới panel
 */
defineProps({
  /** Nhãn của "tab" nhỏ phía trên panel, ví dụ 'Chi tiết' / 'Thêm khách hàng'. */
  tabLabel: { type: String, default: 'Chi tiết' },
  /** Tiêu đề bên trong panel. */
  title: { type: String, default: '' },
})
</script>

<template>
  <div class="ins">
    <div class="ins-tab">{{ tabLabel }}</div>
    <div class="ins-panel">
      <h6 v-if="title" class="ins-title">{{ title }}</h6>
      <dl class="ins-fields"><slot /></dl>
      <slot name="extra" />
    </div>
    <div v-if="$slots.actions" class="ins-actions"><slot name="actions" /></div>
  </div>
</template>

<style scoped>
.ins { position: sticky; top: 16px; }
.ins-tab {
  display: inline-block; padding: 6px 18px; background: #eef1f4; color: var(--c-text-muted);
  border: 1px solid var(--c-border); border-bottom: none;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0; font-weight: 500;
}
.ins-panel {
  background: var(--c-primary); color: #fff; padding: 14px;
  border-radius: 0 var(--radius) var(--radius) var(--radius);
}
.ins-title { font-weight: 700; margin: 0 0 12px; }
.ins-fields { display: flex; flex-direction: column; gap: 8px; margin: 0; }

.ins-actions { display: flex; flex-direction: column; gap: 8px; margin-top: 14px; }
.ins-actions :deep(.btn-success) { background: #fff; color: var(--c-primary); border-color: #fff; font-weight: 600; }
.ins-actions :deep(.btn-success:hover:not(:disabled)) { background: #f0f0f0; }
.ins-actions :deep(.btn-outline-danger) { background: #fff; color: var(--c-danger); border-color: var(--c-danger); font-weight: 600; }
.ins-actions :deep(.btn:disabled) { opacity: .5; }

@media (max-width: 992px) { .ins { position: static; } }
</style>
