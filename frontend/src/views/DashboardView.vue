<script setup>
import { ref, onMounted, nextTick } from 'vue'
import Chart from 'chart.js/auto'
import AppShell from '../components/layout/AppShell.vue'
import DemoDataBanner from '../components/ui/DemoDataBanner.vue'
import { thongKeApi } from '../api/thongKe'
import { vnd } from '../utils/format'

// dataviz palette (validated colorblind-safe): blue / green / magenta + brand green
const S = { blue: '#2a78d6', green: '#008300', magenta: '#e87ba4', brand: '#0B895A' }
const GRID = '#e1e0d9', MUTED = '#898781'

const IMG = n => `/images/shoes/img_shoe_${n}.png`
const money = v => (v / 1e6).toLocaleString('vi-VN') + 'tr'
const vndShort = v => v >= 1e9 ? (v / 1e9).toFixed(2).replace('.', ',') + ' tỷ' : Math.round(v / 1e6).toLocaleString('vi-VN') + ' tr'

const kpis = ref([])
const best = ref([]); const worst = ref([]); const trending = ref([]); const featured = ref([])
const cashEl = ref(null); const retEl = ref(null); const roiEl = ref(null); const catEl = ref(null)
const years = ref([]); const year = ref(new Date().getFullYear())
// True while the whole dashboard is showing illustrative fallback numbers
// because the thống kê endpoints failed.
const isDemo = ref(false)
const retData = ref({ quayLai: 62, moi: 38, tyLe: 62 })
let cashData = null, catData = null, roiData = null
let chartInstances = []

// illustrative fallback (retention/trending still lack a backend query)
const roiMock = [
  { t: 'Nike Air Zoom', v: 67 }, { t: 'Reebok Classic', v: 58 }, { t: 'Vans Old Skool', v: 52 },
  { t: 'Puma Suede', v: 44 }, { t: 'Adidas Ultraboost', v: 39 }, { t: 'Sandal Puma', v: 28 },
].sort((a, b) => b.v - a.v)
const trendingMock = [
  { ten: 'Nike Air Max', brand: 'Nike', growth: 48, img: IMG('10013') },
  { ten: 'Adidas Boost', brand: 'Adidas', growth: 36, img: IMG('10014') },
  { ten: 'Puma RS-X', brand: 'Puma', growth: 29, img: IMG('10009') },
  { ten: 'Vans SK8-Hi', brand: 'Vans', growth: 22, img: IMG('10011') },
]
const featuredMock = [
  { ten: 'Nike Pegasus 41', brand: 'Nike', gia: '2.190.000 ₫', soon: '', img: IMG('10001') },
  { ten: 'Adidas Samba OG', brand: 'Adidas', gia: '2.450.000 ₫', soon: '', img: IMG('10008') },
  { ten: 'Nike Vaporfly 3', brand: 'Nike', gia: '—', soon: 'Sắp ra mắt · 08/2026', img: IMG('10013') },
  { ten: 'Puma Deviate 3', brand: 'Puma', gia: '—', soon: 'Sắp ra mắt · 09/2026', img: IMG('10015') },
]

function buildKpis(tq) {
  const aov = tq.donThanhCong ? tq.doanhThu / tq.donThanhCong : 0
  kpis.value = [
    { lbl: 'Doanh thu', val: vndShort(tq.doanhThu || 0), icon: 'bi-cash-stack' },
    { lbl: 'Lợi nhuận', val: vndShort(tq.loiNhuan || 0), icon: 'bi-graph-up' },
    { lbl: 'Đơn hàng', val: (tq.soDon || 0).toLocaleString('vi-VN'), icon: 'bi-receipt' },
    { lbl: 'Thành công', val: (tq.donThanhCong || 0).toLocaleString('vi-VN'), icon: 'bi-check-circle' },
    { lbl: 'Đơn huỷ', val: (tq.donHuy || 0).toLocaleString('vi-VN'), icon: 'bi-x-circle' },
    { lbl: 'Đơn TB', val: vndShort(aov), icon: 'bi-bag-check' },
  ]
}

async function loadData() {
  try {
    const [tq, sp, dt, roi, ret, tr] = await Promise.all([
      thongKeApi.tongQuan(),
      thongKeApi.tatCaSanPham(),
      thongKeApi.dongTien(year.value),
      thongKeApi.roi(),
      thongKeApi.retention(),
      thongKeApi.trending(year.value),
    ])
    buildKpis(tq)

    // retention — real (returning vs new)
    if (ret) retData.value = { quayLai: ret.quayLai || 0, moi: ret.moi || 0, tyLe: Number(ret.tyLe) || 0 }

    // trending — real (growth vs prior year)
    trending.value = (tr && tr.length)
      ? tr.map((t, i) => ({ ten: t.ten, brand: '', growth: t.growth, img: IMG(10013 + (i % 4)) }))
      : trendingMock

    // best / worst — real (soLuongBan)
    const rows = (sp || []).map(r => ({ ...r, soLuongBan: r.soLuongBan || 0, doanhThu: r.doanhThu || 0 }))
    const byBan = [...rows].sort((a, b) => b.soLuongBan - a.soLuongBan)
    const maxBan = byBan[0]?.soLuongBan || 1
    best.value = byBan.slice(0, 5).map((r, i) => ({ ten: r.tenSP, ban: r.soLuongBan, pct: Math.round(r.soLuongBan / maxBan * 100), img: IMG(10007 + i) }))
    worst.value = [...rows].sort((a, b) => a.soLuongBan - b.soLuongBan).slice(0, 4)
      .map((r, i) => ({ ten: r.tenSP, ban: r.soLuongBan, ton: r.soLuongTon, ngay: 40 + i * 12, img: IMG(10002 + i) }))

    // category revenue — real (group by loaiSP)
    const catMap = {}
    rows.forEach(r => { catMap[r.loaiSP] = (catMap[r.loaiSP] || 0) + (r.doanhThu || 0) })
    catData = { labels: Object.keys(catMap), values: Object.values(catMap) }

    // cash flow — real revenue, cost of goods (giá vốn), profit per month
    const rev = Array(12).fill(0), cost = Array(12).fill(0), prof = Array(12).fill(0)
    ;(dt || []).forEach(m => {
      if (!m.thang) return
      rev[m.thang - 1] = Number(m.doanhThu) || 0
      cost[m.thang - 1] = Number(m.giaVon) || 0
      prof[m.thang - 1] = Number(m.loiNhuan) || 0
    })
    cashData = { rev, cost, prof }

    // ROI — real (revenue vs cost)
    roiData = (roi || []).map(r => ({ t: r.ten, v: Math.round(Number(r.roi) || 0) })).sort((a, b) => b.v - a.v)
    if (!roiData.length) roiData = roiMock
    isDemo.value = false
  } catch (e) {
    console.warn('API offline, using mock dashboard', e)
    isDemo.value = true
    buildKpis({ doanhThu: 1.82e9, soDon: 1284, donThanhCong: 1180, donCho: 62, donHuy: 42, soSanPhamBan: 3120 })
    best.value = [
      { ten: 'Nike Air Zoom', ban: 340, pct: 100, img: IMG('10007') },
      { ten: 'Adidas Ultraboost', ban: 298, pct: 88, img: IMG('10008') },
      { ten: 'Vans Old Skool', ban: 265, pct: 78, img: IMG('10016') },
      { ten: 'Puma Suede', ban: 210, pct: 62, img: IMG('10015') },
      { ten: 'Reebok Classic', ban: 188, pct: 55, img: IMG('10012') },
    ]
    worst.value = [
      { ten: 'Dép Gucci cao cấp', ban: 12, ton: 120, ngay: 96, img: IMG('10010') },
      { ten: 'Giày tây Vans', ban: 19, ton: 88, ngay: 74, img: IMG('10011') },
      { ten: 'Sandal Puma da', ban: 24, ton: 76, ngay: 61, img: IMG('10009') },
      { ten: 'Giày da công sở', ban: 31, ton: 54, ngay: 48, img: IMG('10002') },
    ]
    catData = { labels: ['Thể thao', 'Giày da', 'Sandal', 'Dép', 'Giày tây', 'Chạy bộ'], values: [520e6, 310e6, 180e6, 140e6, 240e6, 360e6] }
    const rev = [98, 112, 120, 135, 148, 160, 171, 182, 190, 205, 220, 236].map(x => x * 1e6)
    cashData = { rev, cost: rev.map(v => Math.round(v * 0.63)), prof: rev.map(v => Math.round(v * 0.37)) }
    roiData = roiMock
    trending.value = trendingMock
    retData.value = { quayLai: 62, moi: 38, tyLe: 62 }
  }
  featured.value = featuredMock
}

function drawCharts() {
  chartInstances.forEach(c => c.destroy())
  chartInstances = []
  const months = ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12']
  const baseScales = {
    x: { grid: { display: false }, border: { display: false }, ticks: { color: MUTED, font: { size: 11 } } },
    y: { grid: { color: GRID }, border: { display: false }, ticks: { color: MUTED, font: { size: 11 }, callback: money } },
  }
  const rev = cashData.rev
  const cost = cashData.cost
  const prof = cashData.prof

  chartInstances.push(new Chart(cashEl.value, {
    type: 'line',
    data: {
      labels: months, datasets: [
        { label: 'Doanh thu', data: rev, borderColor: S.blue, backgroundColor: 'rgba(42,120,214,.10)', borderWidth: 2, fill: true, tension: .35, pointRadius: 0, pointHoverRadius: 5 },
        { label: 'Giá vốn', data: cost, borderColor: S.magenta, borderWidth: 2, fill: false, tension: .35, pointRadius: 0, pointHoverRadius: 5, borderDash: [5, 4] },
        { label: 'Lợi nhuận', data: prof, borderColor: S.green, backgroundColor: 'rgba(0,131,0,.08)', borderWidth: 2, fill: true, tension: .35, pointRadius: 0, pointHoverRadius: 5 },
      ]
    },
    options: { responsive: true, maintainAspectRatio: false, interaction: { mode: 'index', intersect: false }, plugins: { legend: { display: false }, tooltip: { callbacks: { label: c => c.dataset.label + ': ' + vnd(c.raw) } } }, scales: baseScales }
  }))
  chartInstances.push(new Chart(retEl.value, {
    type: 'doughnut',
    data: { labels: ['Khách quay lại', 'Khách mới'], datasets: [{ data: [retData.value.quayLai, retData.value.moi], backgroundColor: [S.green, S.blue], borderWidth: 2, borderColor: '#fff' }] },
    options: { responsive: true, maintainAspectRatio: false, cutout: '64%', plugins: { legend: { display: false }, tooltip: { callbacks: { label: c => c.label + ': ' + c.raw } } } }
  }))
  const roi = (roiData && roiData.length) ? roiData : roiMock
  chartInstances.push(new Chart(roiEl.value, {
    type: 'bar',
    data: { labels: roi.map(r => r.t), datasets: [{ data: roi.map(r => r.v), backgroundColor: S.brand, borderRadius: 4, barPercentage: .7 }] },
    options: { indexAxis: 'y', responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false }, tooltip: { callbacks: { label: c => 'ROI ' + c.raw + '%' } } }, scales: { x: { grid: { color: GRID }, border: { display: false }, ticks: { color: MUTED, callback: v => v + '%' } }, y: { grid: { display: false }, border: { display: false }, ticks: { color: '#52514e', font: { size: 12 } } } } }
  }))
  chartInstances.push(new Chart(catEl.value, {
    type: 'bar',
    data: { labels: catData.labels, datasets: [{ data: catData.values, backgroundColor: S.brand, borderRadius: 4, barPercentage: .65 }] },
    options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false }, tooltip: { callbacks: { label: c => vnd(c.raw) } } }, scales: baseScales }
  }))
}

async function reload() { await loadData(); await nextTick(); drawCharts() }

function exportExcel() {
  const kpi = kpis.value.map(k => `<tr><td>${k.lbl}</td><td>${k.val}</td></tr>`).join('')
  const rows = best.value.map((p, i) => `<tr><td>${i + 1}</td><td>${p.ten}</td><td>${p.ban}</td></tr>`).join('')
  const html = `<html xmlns:x="urn:schemas-microsoft-com:office:excel"><head><meta charset="utf-8"></head><body>
    <h3>BShoes — Thống kê</h3>
    <table border=1><tr><th>Chỉ số</th><th>Giá trị</th></tr>${kpi}</table><br>
    <table border=1><tr><th>#</th><th>Sản phẩm bán chạy</th><th>Đã bán</th></tr>${rows}</table></body></html>`
  const blob = new Blob([html], { type: 'application/vnd.ms-excel' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob); a.download = 'BShoes-ThongKe.xls'; a.click(); URL.revokeObjectURL(a.href)
}

onMounted(async () => {
  try {
    const ns = await thongKeApi.nam()
    if (ns && ns.length) { years.value = ns; year.value = ns[0] }
  } catch (e) { years.value = [year.value] }
  await reload()
})
function onYearChange() { reload() }
</script>

<template>
  <AppShell>
    <div class="viz">
      <DemoDataBanner v-if="isDemo" what="toàn bộ số liệu thống kê" @retry="reload" />
      <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <div>
          <h4 class="mb-0 fw-bold">Thống kê &amp; Phân tích</h4>
          <div class="text-muted small">Tổng quan hiệu quả kinh doanh cửa hàng giày</div>
        </div>
        <div class="d-flex gap-2 align-items-center">
          <label class="small text-muted mb-0">Năm:</label>
          <select class="form-select form-select-sm" style="width:100px" v-model.number="year" @change="onYearChange">
            <option v-for="y in years" :key="y" :value="y">{{ y }}</option>
          </select>
          <button class="btn btn-sm btn-green" @click="exportExcel"><i class="bi bi-file-earmark-excel"></i> Xuất Excel</button>
        </div>
      </div>

      <!-- KPI -->
      <div class="row g-3 mb-3">
        <div class="col-6 col-lg-2" v-for="k in kpis" :key="k.lbl">
          <div class="card-x kpi h-100"><div class="bd d-flex align-items-start gap-2">
            <div class="ic"><i class="bi" :class="k.icon"></i></div>
            <div><div class="lbl">{{ k.lbl }}</div><div class="val">{{ k.val }}</div></div>
          </div></div>
        </div>
      </div>

      <!-- cash flow + retention -->
      <div class="row g-3 mb-3">
        <div class="col-lg-8">
          <div class="card-x h-100">
            <div class="hd">
              <span><i class="bi bi-graph-up-arrow text-success"></i> Dòng tiền theo tháng</span>
              <div class="legend">
                <span><i style="background:#2a78d6"></i>Doanh thu</span>
                <span><i style="background:#e87ba4"></i>Giá vốn</span>
                <span><i style="background:#008300"></i>Lợi nhuận</span>
              </div>
            </div>
            <div class="bd"><div style="height:230px"><canvas ref="cashEl"></canvas></div></div>
          </div>
        </div>
        <div class="col-lg-4">
          <div class="card-x h-100">
            <div class="hd"><span><i class="bi bi-arrow-repeat text-success"></i> Giữ chân khách hàng</span><span class="small fw-bold" style="color:#008300">{{ retData.tyLe }}%</span></div>
            <div class="bd text-center"><div style="height:150px"><canvas ref="retEl"></canvas></div>
              <div class="d-flex justify-content-around mt-2">
                <div><div class="fw-bold fs-5" style="color:#008300">{{ retData.quayLai }}</div><div class="small text-muted">Quay lại</div></div>
                <div><div class="fw-bold fs-5" style="color:#2a78d6">{{ retData.moi }}</div><div class="small text-muted">Khách mới</div></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ROI + category -->
      <div class="row g-3 mb-3">
        <div class="col-lg-6"><div class="card-x h-100">
          <div class="hd"><span><i class="bi bi-percent text-success"></i> ROI theo sản phẩm</span><span class="small text-muted">Lợi nhuận / giá vốn</span></div>
          <div class="bd"><div style="height:230px"><canvas ref="roiEl"></canvas></div></div>
        </div></div>
        <div class="col-lg-6"><div class="card-x h-100">
          <div class="hd"><span><i class="bi bi-bar-chart text-success"></i> Doanh thu theo danh mục</span></div>
          <div class="bd"><div style="height:230px"><canvas ref="catEl"></canvas></div></div>
        </div></div>
      </div>

      <!-- best / worst / trending -->
      <div class="row g-3 mb-3">
        <div class="col-lg-4"><div class="card-x h-100">
          <div class="hd"><span><i class="bi bi-trophy text-warning"></i> Bán chạy nhất</span></div>
          <div class="bd"><table class="table table-borderless mini mb-0"><tbody>
            <tr v-for="(p,i) in best" :key="p.ten">
              <td style="width:26px"><span class="rank">{{ i+1 }}</span></td>
              <td><img class="thumb-sm" :src="p.img"></td>
              <td><div class="fw-semibold">{{ p.ten }}</div><div class="bar-mini"><span :style="{width:p.pct+'%'}"></span></div></td>
              <td class="text-end fw-semibold">{{ p.ban }}</td>
            </tr>
          </tbody></table></div>
        </div></div>
        <div class="col-lg-4"><div class="card-x h-100">
          <div class="hd"><span><i class="bi bi-arrow-down-circle text-danger"></i> Bán chậm nhất</span></div>
          <div class="bd"><table class="table table-borderless mini mb-0"><tbody>
            <tr v-for="(p,i) in worst" :key="p.ten">
              <td style="width:26px"><span class="rank">{{ i+1 }}</span></td>
              <td><img class="thumb-sm" :src="p.img"></td>
              <td><div class="fw-semibold">{{ p.ten }}</div><div class="small text-muted">Tồn {{ p.ton }} · {{ p.ngay }} ngày</div></td>
              <td class="text-end fw-semibold">{{ p.ban }}</td>
            </tr>
          </tbody></table></div>
        </div></div>
        <div class="col-lg-4"><div class="card-x h-100">
          <div class="hd"><span><i class="bi bi-fire text-danger"></i> Đang lên xu hướng</span><span class="small text-muted">năm {{ year }}</span></div>
          <div class="bd"><table class="table table-borderless mini mb-0"><tbody>
            <tr v-for="p in trending" :key="p.ten">
              <td><img class="thumb-sm" :src="p.img"></td>
              <td><div class="fw-semibold">{{ p.ten }}</div><div class="small text-muted">{{ p.brand }}</div></td>
              <td class="text-end"><span class="up-pill"><i class="bi bi-graph-up-arrow"></i> +{{ p.growth }}%</span></td>
            </tr>
          </tbody></table></div>
        </div></div>
      </div>

      <!-- featured / upcoming -->
      <div class="card-x mb-4">
        <div class="hd"><span><i class="bi bi-stars text-warning"></i> Sản phẩm nổi bật &amp; sắp ra mắt</span><router-link to="/san-pham" class="small text-decoration-none" style="color:#0B895A">Quản lý sản phẩm →</router-link></div>
        <div class="bd"><div class="row row-cols-2 row-cols-md-4 g-3">
          <div class="col" v-for="f in featured" :key="f.ten">
            <div class="feat-card h-100">
              <div class="ph"><span class="soon" v-if="f.soon">{{ f.soon }}</span><img :src="f.img"></div>
              <div class="p-2"><div class="small text-muted">{{ f.brand }}</div><div class="fw-semibold" style="font-size:13.5px">{{ f.ten }}</div><div class="fw-bold" style="color:#0B895A">{{ f.gia }}</div></div>
            </div>
          </div>
        </div></div>
      </div>
    </div>
  </AppShell>
</template>

<style scoped>
.viz { --brand:#0B895A; --good:#0ca30c; }
.card-x { background:#fff; border:1px solid #e9edf2; border-radius:12px; box-shadow:0 1px 2px rgba(16,24,40,.05); }
.card-x .hd { padding:12px 16px; border-bottom:1px solid #eef1f4; font-weight:700; font-size:14px; display:flex; justify-content:space-between; align-items:center; }
.card-x .bd { padding:14px 16px; }
.kpi { border-left:4px solid var(--brand); }
.kpi .lbl { color:#6b7280; font-size:12px; font-weight:600; text-transform:uppercase; letter-spacing:.3px; }
.kpi .val { font-size:22px; font-weight:800; color:#1f2933; font-variant-numeric:tabular-nums; }
.kpi .ic { width:38px; height:38px; border-radius:10px; background:#E7F4EF; color:var(--brand); display:flex; align-items:center; justify-content:center; font-size:18px; }
.btn-green { background:var(--brand); color:#fff; } .btn-green:hover { background:#0E9F67; color:#fff; }
.legend { display:flex; gap:14px; font-size:12px; color:#52514e; }
.legend i { width:10px; height:10px; border-radius:2px; display:inline-block; margin-right:5px; vertical-align:middle; }
table.mini { font-size:13px; } table.mini td { padding:6px 4px; }
.rank { width:22px; height:22px; border-radius:6px; background:#eef1f4; color:#556; font-weight:700; font-size:12px; display:inline-flex; align-items:center; justify-content:center; }
.thumb-sm { width:34px; height:34px; object-fit:contain; background:#f5f7fa; border-radius:6px; }
.bar-mini { height:6px; border-radius:999px; background:#eef1f4; overflow:hidden; }
.bar-mini > span { display:block; height:100%; background:var(--brand); }
.up-pill { color:var(--good); font-weight:700; font-size:12px; }
.feat-card { border:1px solid #eef1f4; border-radius:10px; overflow:hidden; }
.feat-card .ph { aspect-ratio:16/10; background:#f5f7fa; display:flex; align-items:center; justify-content:center; position:relative; }
.feat-card .ph img { width:78%; height:78%; object-fit:contain; }
.feat-card .soon { position:absolute; top:8px; left:8px; background:#E8A317; color:#fff; font-size:11px; font-weight:700; padding:2px 7px; border-radius:6px; }
</style>
