<script setup>
defineProps({
  columns: { type: Array, required: true }, // [{ key, label, align }]
  rows: { type: Array, default: () => [] },
  rowKey: { type: String, default: 'id' },
  emptyText: { type: String, default: 'Không có dữ liệu' }
})
</script>

<template>
  <div class="dt-card">
    <table class="table align-middle mb-0 dt-table">
      <thead>
        <tr>
          <th v-for="c in columns" :key="c.key" :class="c.align === 'end' ? 'text-end' : ''">{{ c.label }}</th>
          <th v-if="$slots.actions"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in rows" :key="row[rowKey]">
          <td v-for="c in columns" :key="c.key" :class="c.align === 'end' ? 'text-end' : ''">
            <slot :name="`cell-${c.key}`" :row="row" :value="row[c.key]">{{ row[c.key] }}</slot>
          </td>
          <td v-if="$slots.actions" class="text-end"><slot name="actions" :row="row" /></td>
        </tr>
        <tr v-if="!rows.length">
          <td :colspan="columns.length + ($slots.actions ? 1 : 0)" class="text-center text-muted py-4">{{ emptyText }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.dt-card {
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.dt-table {
  margin-bottom: 0;
}

.dt-table thead th {
  text-transform: uppercase;
  font-size: .72rem;
  letter-spacing: .04em;
  color: var(--c-text-muted);
  background: #FAFBFC;
  font-weight: 600;
  padding: 12px 16px;
  border-bottom: 1px solid var(--c-border);
  white-space: nowrap;
}

.dt-table tbody td {
  padding: 12px 16px;
  border-bottom: 1px solid var(--c-border);
  border-top: none;
}

.dt-table tbody tr:last-child td {
  border-bottom: none;
}

.dt-table tbody tr {
  transition: background var(--transition);
}

.dt-table tbody tr:hover {
  background: var(--c-primary-subtle);
}
</style>
