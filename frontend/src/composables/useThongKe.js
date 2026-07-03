// Wired to /api/thong-ke/san-pham for the best-selling table with a mock
// fallback when the backend is offline. `cards` stay from mock (no single
// endpoint provides the dashboard summary tiles).
import { ref, onMounted } from 'vue'
import { thongKeApi } from '../api/thongKe'
import { dashboardCards, dashboardSanPham } from '../mock/data'

export function useThongKe() {
  const cards = ref(JSON.parse(JSON.stringify(dashboardCards)))
  const sanPham = ref(JSON.parse(JSON.stringify(dashboardSanPham)))

  async function load() {
    try {
      const data = await thongKeApi.tatCaSanPham()
      // ThongKeSanPhamDto has no soLuongBan/doanhThu; default them so the view
      // (which renders both columns) stays happy.
      sanPham.value = data.map(r => ({
        soLuongBan: 0,
        doanhThu: 0,
        ...r,
      }))
    } catch (e) {
      console.warn('API offline, using mock', e)
      sanPham.value = JSON.parse(JSON.stringify(dashboardSanPham))
    }
  }
  onMounted(load)

  return { cards, sanPham }
}
