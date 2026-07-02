<script setup>
defineProps({
  columns: { type: Array, required: true }, // [{ key, label, align }]
  rows: { type: Array, default: () => [] },
  rowKey: { type: String, default: 'id' },
  emptyText: { type: String, default: 'Không có dữ liệu' }
})
</script>

<template>
  <table class="table table-hover bg-white align-middle mb-0">
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
</template>
