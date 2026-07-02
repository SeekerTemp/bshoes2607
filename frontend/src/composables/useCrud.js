import { ref, computed } from 'vue'
export function useCrud(seed, { searchKeys = [], codePrefix = '', codeField = '' } = {}) {
  const rows = ref(JSON.parse(JSON.stringify(seed)))
  const keyword = ref('')
  let seq = rows.value.reduce((m, r) => Math.max(m, r.id || 0), 0)
  const filtered = computed(() => {
    const k = keyword.value.toLowerCase()
    if (!k) return rows.value
    return rows.value.filter(r => searchKeys.some(key => String(r[key] ?? '').toLowerCase().includes(k)))
  })
  function add(item) {
    item.id = ++seq
    if (codePrefix && codeField) item[codeField] = codePrefix + item.id
    rows.value.push(item)
  }
  function update(item) {
    const i = rows.value.findIndex(r => r.id === item.id)
    if (i !== -1) rows.value.splice(i, 1, item)
  }
  function remove(id) { rows.value = rows.value.filter(r => r.id !== id) }
  return { rows, keyword, filtered, add, update, remove }
}
