// TODO(api): swap mock rows for lichSuApi calls when the backend runs.
import { ref, computed } from 'vue'
import { lichSu } from '../mock/data'

export function useLichSu() {
  const rows = ref(JSON.parse(JSON.stringify(lichSu)))
  const keyword = ref('')
  const trangThai = ref('')

  const filtered = computed(() => {
    const k = keyword.value.toLowerCase()
    return rows.value.filter(r =>
      (r.ma.toLowerCase().includes(k) || r.khach.toLowerCase().includes(k)) &&
      (!trangThai.value || r.trangThai === trangThai.value)
    )
  })

  return { rows, keyword, trangThai, filtered }
}
