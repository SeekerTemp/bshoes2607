// TODO(api): swap mock data for thongKeApi calls when the backend runs.
import { ref } from 'vue'
import { dashboardCards, dashboardSanPham } from '../mock/data'

export function useThongKe() {
  const cards = ref(JSON.parse(JSON.stringify(dashboardCards)))
  const sanPham = ref(JSON.parse(JSON.stringify(dashboardSanPham)))
  return { cards, sanPham }
}
