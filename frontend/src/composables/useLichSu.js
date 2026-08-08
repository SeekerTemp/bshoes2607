// Wired to /api/lich-su-hoa-don with a mock fallback when the backend is offline.
import { ref, computed, onMounted } from 'vue'
import { lichSuApi } from '../api/lichSu'
import { lichSu } from '../mock/data'

export function useLichSu() {
  const rows = ref([])
  const keyword = ref('')
  const trangThai = ref('')
  // See useCrud(): true while showing the offline mock seed instead of real data.
  const isDemo = ref(false)

  async function load() {
    try {
      rows.value = await lichSuApi.findAll()
      isDemo.value = false
    } catch (e) {
      console.warn('API offline, using mock', e)
      rows.value = JSON.parse(JSON.stringify(lichSu))
      isDemo.value = true
    }
  }
  onMounted(load)

  const filtered = computed(() => {
    const k = keyword.value.toLowerCase()
    return rows.value.filter(r =>
      (r.ma.toLowerCase().includes(k) || r.khach.toLowerCase().includes(k)) &&
      (!trangThai.value || r.trangThai === trangThai.value)
    )
  })

  return { rows, keyword, trangThai, filtered, load, isDemo }
}
