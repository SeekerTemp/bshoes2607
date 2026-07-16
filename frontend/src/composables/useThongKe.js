// Wired to /api/thong-ke: `san-pham` feeds the best-selling table and
// `tong-quan` feeds the summary tiles. Mock fallback when the backend is offline.
import { ref, onMounted } from 'vue'
import { thongKeApi } from '../api/thongKe'
import { dashboardCards, dashboardSanPham } from '../mock/data'
import { vnd } from '../utils/format'

export function useThongKe() {
  const cards = ref(JSON.parse(JSON.stringify(dashboardCards)))
  const sanPham = ref(JSON.parse(JSON.stringify(dashboardSanPham)))

  async function load() {
    try {
      const [rows, tq] = await Promise.all([
        thongKeApi.tatCaSanPham(),
        thongKeApi.tongQuan(),
      ])
      // The DTO now carries soLuongBan/doanhThu; default only if missing.
      sanPham.value = rows.map(r => ({ soLuongBan: 0, doanhThu: 0, ...r }))
      cards.value = [
        { label: 'Doanh thu', value: vnd(tq.doanhThu) },
        { label: 'Số đơn', value: tq.soDon },
        { label: 'Đơn thành công', value: tq.donThanhCong },
        { label: 'Đơn chờ', value: tq.donCho },
        { label: 'Đơn huỷ', value: tq.donHuy },
      ]
    } catch (e) {
      console.warn('API offline, using mock', e)
      sanPham.value = JSON.parse(JSON.stringify(dashboardSanPham))
      cards.value = JSON.parse(JSON.stringify(dashboardCards))
    }
  }
  onMounted(load)

  return { cards, sanPham }
}
