<script setup>
import { computed } from 'vue'

const props = defineProps({
  // Preferred: explicit semantic status
  status: { type: String, default: '' }, // success | warning | danger | info | neutral
  // Convenience: boolean active/inactive (kept for backward compatibility)
  active: { type: Boolean, default: false },
  activeText: { type: String, default: 'Hoạt động' },
  inactiveText: { type: String, default: 'Ngừng' },
  label: { type: String, default: '' }
})

const resolvedStatus = computed(() => {
  if (props.status) return props.status
  return props.active ? 'success' : 'neutral'
})

const resolvedLabel = computed(() => {
  if (props.label) return props.label
  if (!props.status) return props.active ? props.activeText : props.inactiveText
  return {
    success: 'Hoạt động',
    warning: 'Cảnh báo',
    danger: 'Ngừng',
    info: 'Thông tin',
    neutral: 'Ngừng'
  }[props.status] || props.status
})
</script>

<template>
  <span class="badge rounded-pill status-badge" :class="`status-badge--${resolvedStatus}`">
    <span class="status-badge__dot"></span>
    <slot>{{ resolvedLabel }}</slot>
  </span>
</template>

<style scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  font-size: .78rem;
  padding: .35em .75em;
}
.status-badge__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex: none;
}

.status-badge--success {
  background: var(--c-success-subtle);
  color: var(--c-success-text);
}
.status-badge--success .status-badge__dot { background: var(--c-success); }

.status-badge--warning {
  background: var(--c-warning-subtle);
  color: var(--c-warning-text);
}
.status-badge--warning .status-badge__dot { background: var(--c-warning); }

.status-badge--danger {
  background: var(--c-danger-subtle);
  color: var(--c-danger-text);
}
.status-badge--danger .status-badge__dot { background: var(--c-danger); }

.status-badge--info {
  background: var(--c-info-subtle);
  color: var(--c-info-text);
}
.status-badge--info .status-badge__dot { background: var(--c-info); }

.status-badge--neutral {
  background: #EEF1F4;
  color: var(--c-text-muted);
}
.status-badge--neutral .status-badge__dot { background: var(--c-text-muted); }
</style>
