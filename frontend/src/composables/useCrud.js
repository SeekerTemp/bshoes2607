import { ref, computed, onMounted } from 'vue'

// Generic CRUD composable: loads rows from a REST api module, falling back to a
// local mock seed when the backend is offline (read-only display only). Public
// interface is unchanged: { rows, keyword, filtered, add, update, remove }.
//
// `api` is an object exposing findAll()/create()/update()/remove() (each returns
// a promise). `seed` is the mock array used only as offline fallback for `load()`.
//
// Writes (add/update/remove) are HONEST: they await the API call and let errors
// propagate to the caller. There is no silent local-memory fallback for writes —
// a failed write must reject so the UI can surface the failure instead of
// reporting fake success.
export function useCrud(api, seed, { searchKeys = [] } = {}) {
  const rows = ref([])
  const keyword = ref('')

  async function load() {
    try {
      rows.value = await api.findAll()
    } catch (e) {
      console.warn('API offline, using mock', e)
      rows.value = JSON.parse(JSON.stringify(seed))
    }
  }
  onMounted(load)

  const filtered = computed(() => {
    const k = keyword.value.toLowerCase()
    if (!k) return rows.value
    return rows.value.filter(r => searchKeys.some(key => String(r[key] ?? '').toLowerCase().includes(k)))
  })

  async function add(item) {
    await api.create(item)
    await load()
  }

  async function update(item) {
    await api.update(item)
    await load()
  }

  async function remove(id) {
    await api.remove(id)
    await load()
  }

  return { rows, keyword, filtered, add, update, remove, load }
}

// Extracts a human-readable message from an API error, in priority order:
// backend-provided message, then the error's own message, then a fallback.
export function crudErrorMessage(err) {
  return err?.response?.data?.message || err?.message || 'Lỗi không xác định'
}
