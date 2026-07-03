import { ref, computed, onMounted } from 'vue'

// Generic CRUD composable: loads rows from a REST api module, falling back to a
// local mock seed when the backend is offline. Public interface is unchanged:
// { rows, keyword, filtered, add, update, remove }.
//
// `api` is an object exposing findAll()/create()/update()/remove() (each returns
// a promise). `seed` is the mock array used both as offline data and as the
// fallback when a network call fails.
export function useCrud(api, seed, { searchKeys = [], codePrefix = '', codeField = '' } = {}) {
  const rows = ref([])
  const keyword = ref('')
  let seq = 0

  function syncSeq() {
    seq = rows.value.reduce((m, r) => Math.max(m, r.id || 0), 0)
  }

  async function load() {
    try {
      rows.value = await api.findAll()
    } catch (e) {
      console.warn('API offline, using mock', e)
      rows.value = JSON.parse(JSON.stringify(seed))
    }
    syncSeq()
  }
  onMounted(load)

  const filtered = computed(() => {
    const k = keyword.value.toLowerCase()
    if (!k) return rows.value
    return rows.value.filter(r => searchKeys.some(key => String(r[key] ?? '').toLowerCase().includes(k)))
  })

  function localInsert(item) {
    item.id = ++seq
    if (codePrefix && codeField) item[codeField] = codePrefix + item.id
    rows.value.push(item)
  }

  async function add(item) {
    try {
      await api.create(item)
      await load()
    } catch (e) {
      console.warn('API offline, adding locally', e)
      localInsert(item)
    }
  }

  async function update(item) {
    try {
      await api.update(item)
      await load()
    } catch (e) {
      console.warn('API offline, updating locally', e)
      const i = rows.value.findIndex(r => r.id === item.id)
      if (i !== -1) rows.value.splice(i, 1, item)
    }
  }

  async function remove(id) {
    try {
      await api.remove(id)
      await load()
    } catch (e) {
      console.warn('API offline, removing locally', e)
      rows.value = rows.value.filter(r => r.id !== id)
    }
  }

  return { rows, keyword, filtered, add, update, remove, load }
}
