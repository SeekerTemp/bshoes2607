<script setup>
defineProps({
  label: { type: String, default: '' },
  value: { type: [String, Number], default: '' },
  icon: { type: String, default: '' },       // bootstrap-icons name, e.g. 'cash-stack'
  variant: { type: String, default: 'primary' } // primary | success | warning | info | danger
})
</script>

<template>
  <div class="stat-card h-100" :class="`stat-card--${variant}`">
    <div class="stat-card__body">
      <div class="stat-card__label">{{ label }}</div>
      <div class="stat-card__value">{{ value }}</div>
    </div>
    <div v-if="icon" class="stat-card__icon">
      <i :class="`bi bi-${icon}`"></i>
    </div>
  </div>
</template>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-3);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-left: 4px solid var(--c-primary);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  padding: var(--sp-4);
}

.stat-card__label {
  color: var(--c-text-muted);
  text-transform: uppercase;
  font-size: .72rem;
  letter-spacing: .04em;
  font-weight: 600;
  margin-bottom: 4px;
}

.stat-card__value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--c-text);
  line-height: 1.2;
}

.stat-card__icon {
  flex: none;
  width: 44px;
  height: 44px;
  border-radius: var(--radius);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
}

.stat-card--primary { border-left-color: var(--c-primary); }
.stat-card--primary .stat-card__icon {
  background: var(--c-primary-subtle);
  color: var(--c-primary-subtle-text);
}

.stat-card--success { border-left-color: var(--c-success); }
.stat-card--success .stat-card__icon {
  background: var(--c-success-subtle);
  color: var(--c-success-text);
}

.stat-card--warning { border-left-color: var(--c-warning); }
.stat-card--warning .stat-card__icon {
  background: var(--c-warning-subtle);
  color: var(--c-warning-text);
}

.stat-card--info { border-left-color: var(--c-info); }
.stat-card--info .stat-card__icon {
  background: var(--c-info-subtle);
  color: var(--c-info-text);
}

.stat-card--danger { border-left-color: var(--c-danger); }
.stat-card--danger .stat-card__icon {
  background: var(--c-danger-subtle);
  color: var(--c-danger-text);
}
</style>
