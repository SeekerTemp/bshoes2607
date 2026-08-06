<script setup>
// Faithful to NetBeans Pnl_3_qlSanPham (see frontend/refui): a JTabbedPane with
// three tabs — "Sản phẩm", "Sản phẩm chi tiết", "Thuộc tính". Each tab is a
// master-detail screen: a table on the left and a green inspector panel on the
// right (NOT a modal popup). Selecting a row fills the inspector.
import { ref, computed } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import AppModal from '../components/ui/AppModal.vue'
import ImagePicker from '../components/ui/ImagePicker.vue'
import DemoDataBanner from '../components/ui/DemoDataBanner.vue'
import DanhMucPanel from '../components/panels/DanhMucPanel.vue'
import ThuocTinhPanel from '../components/panels/ThuocTinhPanel.vue'
import { useSanPham } from '../composables/useSanPham'
import { useToast } from '../composables/useToast'
import { crudErrorMessage } from '../composables/useCrud'
import { vnd } from '../utils/format'
import { validateImageFile } from '../utils/upload'
import { downloadJson, stamp } from '../utils/csv'
import { bienTheApi } from '../api/bienThe'
import { sanPhamApi } from '../api/sanPham'
import { uploadApi } from '../api/upload'
import {
  loaiSanPhamList, kieuDangList, kieuCoGiayList, kieuDayGiayList,
  xuatXuList, mauSacList, kichThuocList,
} from '../mock/data'

const { filtered, load, add, update, remove, thuongHieuList, chatLieuList, isDemo } = useSanPham()
const { notify } = useToast()

const tab = ref('sanpham') // 'sanpham' | 'chitiet' | 'danhmuc' | 'thuoctinh'

const PLACEHOLDER = '/images/shoes/img_shoe_10001.png'
function onImgError(e) { e.target.style.visibility = 'hidden' }

// grid-card image pickers (one per tab that carries an image)
const spImgOpen = ref(false)
const ctImgOpen = ref(false)

// "Tải ảnh từ máy" — upload a local file for the variant image, as an
// alternative to picking one from the built-in image library.
const ctFileInput = ref(null)
function ctTriggerUpload() { ctFileInput.value?.click() }
async function ctUploadImage(ev) {
  const file = ev.target.files && ev.target.files[0]
  ev.target.value = '' // allow re-selecting the same file later
  if (!file) return
  const { ok, error } = validateImageFile(file)
  if (!ok) { notify(error, 'warning'); return }
  try {
    const res = await uploadApi.image(file)
    ctForm.value.imageUrl = res.url
    notify('Đã tải ảnh lên', 'success')
  } catch (e) {
    notify(crudErrorMessage(e) || 'Tải ảnh thất bại', 'warning')
  }
}

/* ============================ TAB 1 — Sản phẩm ============================ */
const spSearch = ref('')
// "Xem theo": group/filter products by Danh mục / Thương hiệu (hãng) / Kiểu dáng
const spDim = ref('all')     // 'all' | 'loaiSP' | 'thuongHieu' | 'kieuDang'
const spDimVal = ref('')
const spDimValues = computed(() =>
  spDim.value === 'all' ? [] : [...new Set(filtered.value.map(p => p[spDim.value]).filter(Boolean))].sort())
function onSpDimChange() { spDimVal.value = '' }
const spRows = computed(() => {
  const k = spSearch.value.trim().toLowerCase()
  return filtered.value.filter(p =>
    (!k || p.ma.toLowerCase().includes(k) || p.ten.toLowerCase().includes(k)) &&
    (spDim.value === 'all' || !spDimVal.value || p[spDim.value] === spDimVal.value))
})

// Giá của sản phẩm cha là KHOẢNG giá của các biến thể (backend trả giaTu/giaDen).
// Trước đây cột giá lấy bienThe[0] nên sản phẩm nhiều biến thể hiển thị sai.
//
// Khi backend offline, useCrud rơi về mock data vốn không có các trường tổng hợp —
// nên tự tính lại từ bienThe để màn demo vẫn hiển thị đúng thay vì "chưa có biến thể".
function rollup(p) {
  const bt = p?.bienThe || []
  if (p?.soBienThe != null) {
    return { soBienThe: p.soBienThe, giaTu: p.giaTu, giaDen: p.giaDen, tongTon: p.tongTon, dangBan: p.dangBan }
  }
  const gias = bt.map(v => v.gia).filter(g => g != null)
  return {
    soBienThe: bt.length,
    giaTu: gias.length ? Math.min(...gias) : null,
    giaDen: gias.length ? Math.max(...gias) : null,
    tongTon: bt.reduce((s, v) => s + (Number(v.ton) || 0), 0),
    dangBan: bt.some(v => v.trangThai !== false),
  }
}

function khoangGia(p) {
  const r = rollup(p)
  if (!r.soBienThe) return 'Chưa có biến thể'
  if (r.giaTu == null) return '—'
  return r.giaTu === r.giaDen ? vnd(r.giaTu) : `${vnd(r.giaTu)} – ${vnd(r.giaDen)}`
}

function blankSP() {
  return {
    id: null, ma: '', ten: '', loaiSP: '', chatLieu: chatLieuList[0] || '',
    kieuDang: '', coGiay: '', dayGiay: '', thuongHieu: thuongHieuList[0] || '',
    xuatXu: '', moTa: '', imageUrl: '', bienThe: [],
  }
}
const spForm = ref(blankSP())
const spSelectedId = ref(null)
function selectSP(p) {
  spSelectedId.value = p.id
  spForm.value = { ...blankSP(), ...JSON.parse(JSON.stringify(p)) }
}
// double-click a product → jump to its variants (Sản phẩm chi tiết tab), filtered by product id
function openBienThe(p) {
  selectSP(p)
  ctFilterProductId.value = p.id
  ctSearch.value = ''
  ctSelectedKey.value = null
  tab.value = 'chitiet'
}
// Từ inspector sản phẩm → sang tab biến thể, form đã điền sẵn sản phẩm cha.
function themBienTheCho(p) {
  ctFilterProductId.value = p.id
  ctSearch.value = ''
  ctLamMoi()
  ctForm.value.idSanPham = p.id
  tab.value = 'chitiet'
}
// "Làm mới" = XOÁ TRẮNG inspector (trước đây nó nạp lại sản phẩm đang chọn).
function spLamMoi() {
  spSelectedId.value = null
  spForm.value = blankSP()
}

/** Luôn TẠO MỚI từ nội dung inspector, bỏ id + mã + biến thể của bản gốc. */
async function spTaoMoi() {
  if (!spForm.value.ten) { notify('Nhập tên sản phẩm', 'warning'); return }
  try {
    await add({ ...spForm.value, id: null, ma: '', bienThe: [] })
    notify('Đã thêm sản phẩm', 'success')
    spLamMoi()
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

/** Chỉ CẬP NHẬT sản phẩm đang chọn. */
async function spLuu() {
  if (!spForm.value.id) { notify('Chọn sản phẩm trong danh sách để sửa', 'warning'); return }
  if (!spForm.value.ten) { notify('Nhập tên sản phẩm', 'warning'); return }
  try {
    await update({ ...spForm.value })
    notify('Đã cập nhật sản phẩm', 'success')
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}
async function spAn() {
  if (!spForm.value.id) { notify('Chọn sản phẩm để ẩn', 'warning'); return }
  if (!confirm(`Ẩn (xóa mềm) sản phẩm ${spForm.value.ma}?`)) return
  try {
    await remove(spForm.value.ma)
    spLamMoi()
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

/* ---- Thùng rác: xem sản phẩm đã ẩn và khôi phục (GET /recycle, POST /restore/{ma}) ---- */
const recycleOpen = ref(false)
const recycleRows = ref([])
const recycleLoading = ref(false)

async function openRecycle() {
  recycleOpen.value = true
  recycleLoading.value = true
  try {
    recycleRows.value = (await sanPhamApi.recycle()) || []
  } catch (e) {
    recycleRows.value = []
    notify(crudErrorMessage(e) || 'Không tải được danh sách bị ẩn', 'warning')
  } finally {
    recycleLoading.value = false
  }
}

async function restoreSP(row) {
  try {
    await sanPhamApi.restore(row.ma)
    notify(`Đã khôi phục ${row.ma}`, 'success')
    // Refresh both the bin and the main list so neither shows a stale row.
    await Promise.all([openRecycle(), load()])
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

/* ---- Xuất / nhập JSON danh sách sản phẩm ---- */
function spXuatJson() {
  const rows = spRows.value
  if (!rows.length) { notify('Không có sản phẩm nào để xuất', 'warning'); return }
  const ok = downloadJson(`san-pham-${stamp()}.json`, {
    exportedAt: new Date().toISOString(),
    count: rows.length,
    sanPham: rows,
  })
  notify(ok ? `Đã xuất ${rows.length} sản phẩm` : 'Không xuất được file', ok ? 'success' : 'warning')
}

const spJsonInput = ref(null)
function spTriggerImport() { spJsonInput.value?.click() }

async function spNhapJson(ev) {
  const file = ev.target.files?.[0]
  if (!file) return
  ev.target.value = ''

  let items
  try {
    const parsed = JSON.parse(await file.text())
    // Accept both the shape spXuatJson() writes and a bare array.
    items = Array.isArray(parsed) ? parsed : parsed?.sanPham
  } catch (e) {
    notify('File JSON không hợp lệ', 'warning')
    return
  }
  if (!Array.isArray(items) || !items.length) { notify('File JSON không có sản phẩm nào', 'warning'); return }

  let ok = 0
  const loi = []
  for (const [i, item] of items.entries()) {
    if (!item?.ten) { loi.push(`mục ${i + 1}: thiếu tên`); continue }
    try {
      // Drop server-side identity so an import always creates new rows rather
      // than silently overwriting whatever happens to share an id.
      const { id, bienThe, ...rest } = item
      await add({ ...blankSP(), ...rest, id: null, bienThe: [] })
      ok++
    } catch (e) {
      loi.push(`mục ${i + 1} (${item.ten}): ${crudErrorMessage(e)}`)
    }
  }
  if (ok) notify(`Đã nhập ${ok}/${items.length} sản phẩm`, 'success')
  if (loi.length) notify(`${loi.length} mục lỗi — ${loi.slice(0, 3).join('; ')}`, 'warning')
}

/* ============= TAB 2 — Sản phẩm chi tiết (biến thể), wired to backend ============= */
const ctSearch = ref('')
// scope the variant list to a single product (set via the dropdown or by
// double-clicking a product row); null = no filter, show every product's variants
const ctFilterProductId = ref(null)
const ctFilterProductLabel = computed(() => {
  if (ctFilterProductId.value == null) return ''
  const p = filtered.value.find(x => x.id === ctFilterProductId.value)
  return p ? p.ten : ''
})
function ctClearProductFilter() { ctFilterProductId.value = null }
// flatten every product's variants; each row carries the server variant id (idSpct)
const ctRows = computed(() => {
  const k = ctSearch.value.trim().toLowerCase()
  const rows = []
  filtered.value.forEach(p => (p.bienThe || []).forEach(v => {
    rows.push({
      key: v.id != null ? 'v' + v.id : (p.id + '-' + v.ma), idSpct: v.id, idSanPham: p.id,
      ma: v.ma, tenSP: p.ten, mau: v.mau, size: v.size, gia: v.gia, giaNhap: v.giaNhap,
      ton: v.ton, trangThai: v.trangThai !== false, imageUrl: v.imageUrl || p.imageUrl,
    })
  }))
  return rows
    .filter(r => ctFilterProductId.value == null || r.idSanPham === ctFilterProductId.value)
    .filter(r => !k || (r.ma || '').toLowerCase().includes(k) || (r.tenSP || '').toLowerCase().includes(k))
})
const ctSelectedKey = ref(null)
const ctNhap = ref(0)
function blankCT() {
  return { idSpct: null, idSanPham: '', ma: '', tenSP: '', mau: mauSacList[0] || '', size: kichThuocList[0] || '', gia: 0, giaNhap: 0, ton: 0, trangThai: true, imageUrl: '' }
}
const ctForm = ref(blankCT())
function selectCT(r) { ctSelectedKey.value = r.key; ctForm.value = { ...r }; ctNhap.value = 0 }
// "Làm mới" = XOÁ TRẮNG inspector (trước đây nó nạp lại biến thể đang chọn).
function ctLamMoi() { ctSelectedKey.value = null; ctForm.value = blankCT() }

// Cùng bộ ràng buộc mà entity SanPhamChiTiet enforce ở backend — chặn sớm ở đây để
// người dùng thấy lỗi ngay tại ô nhập thay vì đợi một toast 400 từ server.
function ctLoi(f, { batBuocTon }) {
  if (!f.mau) return 'Chọn màu sắc cho biến thể'
  if (!f.size) return 'Chọn kích cỡ cho biến thể'
  if (!(Number(f.gia) > 0)) return 'Đơn giá bán phải lớn hơn 0'
  if (Number(f.ton) < 0) return 'Số lượng tồn không được âm'
  if (batBuocTon && !(Number(f.ton) > 0)) return 'Biến thể mới phải có số lượng > 0 thì mới ở trạng thái Đang bán'
  return ''
}
// Không gửi `ma`: mã là của server (SPCT<id>-<màu>-<cỡ>), client gửi lên cũng bị bỏ qua.
function ctDto(f) {
  return { mau: f.mau, size: f.size, gia: f.gia, giaNhap: f.giaNhap, ton: f.ton, trangThai: f.trangThai, imageUrl: f.imageUrl }
}

/** Luôn TẠO MỚI biến thể từ nội dung inspector, kể cả khi đang chọn một biến thể khác. */
async function ctTaoMoi() {
  const f = ctForm.value
  // selectCT() giữ lại idSanPham của dòng đang chọn, nên "chọn một biến thể, đổi màu,
  // Tạo mới" tạo đúng biến thể mới cho cùng sản phẩm cha.
  if (!f.idSanPham) { notify('Chọn sản phẩm cho biến thể', 'warning'); return }
  const loi = ctLoi(f, { batBuocTon: true })
  if (loi) { notify(loi, 'warning'); return }
  try {
    await bienTheApi.create(f.idSanPham, ctDto(f))
    notify('Đã thêm biến thể', 'success')
    await load(); ctLamMoi()
  } catch (e) { notify(crudErrorMessage(e), 'warning') }
}

/** Chỉ CẬP NHẬT biến thể đang chọn. */
async function ctLuu() {
  const f = ctForm.value
  if (!f.idSpct) { notify('Chọn biến thể trong danh sách để sửa', 'warning'); return }
  const loi = ctLoi(f, { batBuocTon: false })
  if (loi) { notify(loi, 'warning'); return }
  try {
    await bienTheApi.update(f.idSpct, ctDto(f))
    notify('Đã cập nhật biến thể', 'success')
    await load()
  } catch (e) { notify(crudErrorMessage(e), 'warning') }
}
async function ctNhapKho() {
  if (!ctForm.value.idSpct) { notify('Chọn biến thể để nhập kho', 'warning'); return }
  const sl = Number(ctNhap.value) || 0
  if (sl <= 0) { notify('Nhập số lượng > 0', 'warning'); return }
  try { await bienTheApi.nhapKho(ctForm.value.idSpct, sl); notify(`Đã nhập ${sl} vào kho`, 'success'); ctNhap.value = 0; await load(); ctLamMoi() }
  catch (e) { notify('Nhập kho thất bại', 'warning') }
}
async function ctAn() {
  if (!ctForm.value.idSpct) { notify('Chọn biến thể để ẩn', 'warning'); return }
  if (!confirm('Ẩn (xóa mềm) biến thể ' + ctForm.value.ma + '?')) return
  try { await bienTheApi.remove(ctForm.value.idSpct); notify('Đã ẩn biến thể', 'success'); await load(); ctLamMoi() }
  catch (e) { notify('Thao tác thất bại', 'warning') }
}
</script>

<template>
  <AppShell>
    <DemoDataBanner v-if="isDemo" what="danh sách sản phẩm" @retry="load" />
    <PageHeader title="Quản lý sản phẩm" />

    <!-- JTabbedPane section_1: Sản phẩm | Sản phẩm chi tiết | Thuộc tính -->
    <div class="sp-tabs">
      <button class="sp-tab" :class="{ active: tab === 'sanpham' }" @click="tab = 'sanpham'">Sản phẩm</button>
      <button class="sp-tab" :class="{ active: tab === 'chitiet' }" @click="tab = 'chitiet'">Sản phẩm chi tiết</button>
      <button class="sp-tab" :class="{ active: tab === 'danhmuc' }" @click="tab = 'danhmuc'">Danh mục</button>
      <button class="sp-tab" :class="{ active: tab === 'thuoctinh' }" @click="tab = 'thuoctinh'">Thuộc tính</button>
    </div>

    <!-- ==================== TAB 1: SẢN PHẨM ==================== -->
    <div v-show="tab === 'sanpham'" class="sp-grid">
      <div class="sp-master">
        <div class="card sp-panel">
          <header class="sp-panel-head flex-wrap gap-2">
            <h6 class="sp-title">SẢN PHẨM</h6>
            <div class="d-flex gap-2 align-items-center flex-wrap">
              <span class="small text-muted">Xem theo:</span>
              <select class="form-select form-select-sm" style="width:140px" v-model="spDim" @change="onSpDimChange">
                <option value="all">Tất cả</option>
                <option value="loaiSP">Danh mục</option>
                <option value="thuongHieu">Thương hiệu</option>
                <option value="kieuDang">Kiểu dáng</option>
              </select>
              <select v-if="spDim !== 'all'" class="form-select form-select-sm" style="width:150px" v-model="spDimVal">
                <option value="">-- Tất cả --</option>
                <option v-for="v in spDimValues" :key="v" :value="v">{{ v }}</option>
              </select>
              <div class="input-group input-group-sm sp-search">
                <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                <input class="form-control" v-model="spSearch" placeholder="Tìm kiếm sản phẩm" />
              </div>
            </div>
          </header>
          <div class="table-scroll" style="height: 460px">
            <table class="table table-sm table-hover align-middle sp-table mb-0">
              <!-- Không còn cột Màu sắc / Kích thước: đó là thuộc tính của BIẾN THỂ.
                   Thay bằng khoảng giá + số biến thể + tồn, tổng hợp từ các con. -->
              <thead><tr>
                <th class="text-center">STT</th><th>Mã SP</th><th>Tên sp</th><th>Chất liệu</th>
                <th>Kiểu dáng</th><th>Thương hiệu</th>
                <th class="text-end">Giá bán</th><th class="text-center">Biến thể</th>
                <th class="text-end">Tồn</th><th>Trạng thái</th>
              </tr></thead>
              <tbody>
                <tr v-for="(p, i) in spRows" :key="p.id" :class="{ 'row-active': p.id === spSelectedId }" @click="selectSP(p)" @dblclick="openBienThe(p)" title="Double-click để xem biến thể">
                  <td class="text-center">{{ i + 1 }}</td><td class="fw-medium">{{ p.ma }}</td><td>{{ p.ten }}</td>
                  <td>{{ p.chatLieu }}</td><td>{{ p.kieuDang || '—' }}</td><td>{{ p.thuongHieu }}</td>
                  <td class="text-end" :class="{ 'text-danger': !rollup(p).soBienThe }">{{ khoangGia(p) }}</td>
                  <td class="text-center">{{ rollup(p).soBienThe }}</td>
                  <td class="text-end">{{ rollup(p).tongTon }}</td>
                  <td>{{ rollup(p).dangBan ? 'Đang bán' : 'Chưa bán' }}</td>
                </tr>
                <tr v-if="spRows.length === 0"><td colspan="10" class="text-center text-muted py-3">Không có sản phẩm</td></tr>
              </tbody>
            </table>
          </div>
          <footer class="sp-master-foot"><button class="btn btn-sm btn-outline-secondary" @click="openRecycle">Xem danh sách bị ẩn</button></footer>
        </div>
      </div>

      <div class="sp-detail">
        <div class="sp-tab-r">Chi tiết</div>
        <div class="sp-panel-green">
          <h6 class="green-title">Thông tin sản phẩm</h6>
          <div class="green-img">
            <button type="button" class="img-choose" @click="spImgOpen = true" title="Chọn ảnh">
              <img :src="spForm.imageUrl || PLACEHOLDER" alt="" @error="onImgError">
              <span class="img-choose-overlay"><i class="bi bi-images"></i> Chọn ảnh</span>
            </button>
          </div>
          <dl class="green-fields">
            <!-- Mã do server sinh ("SP" + id) khi tạo — hiển thị chữ, không phải ô nhập. -->
            <div class="gf"><dt>Mã sản phẩm</dt><dd>
              <p class="ins-static" :class="{ 'chua-co': !spForm.ma }">{{ spForm.ma || 'Tự sinh khi lưu' }}</p>
            </dd></div>
            <div class="gf"><dt>Tên sản phẩm</dt><dd><input class="form-control form-control-sm" v-model="spForm.ten"></dd></div>
            <div class="gf"><dt>Loại sản phẩm</dt><dd><AppSelect v-model="spForm.loaiSP" :options="loaiSanPhamList" /></dd></div>
            <div class="gf"><dt>Chất liệu</dt><dd><AppSelect v-model="spForm.chatLieu" :options="chatLieuList" /></dd></div>
            <div class="gf"><dt>Kiểu dáng</dt><dd><AppSelect v-model="spForm.kieuDang" :options="kieuDangList" /></dd></div>
            <div class="gf"><dt>Kiểu cỡ giày</dt><dd><AppSelect v-model="spForm.coGiay" :options="kieuCoGiayList" /></dd></div>
            <div class="gf"><dt>Kiểu dây giày</dt><dd><AppSelect v-model="spForm.dayGiay" :options="kieuDayGiayList" /></dd></div>
            <div class="gf"><dt>Thương hiệu</dt><dd><AppSelect v-model="spForm.thuongHieu" :options="thuongHieuList" /></dd></div>
            <div class="gf"><dt>Xuất xứ</dt><dd><AppSelect v-model="spForm.xuatXu" :options="xuatXuList" /></dd></div>
          </dl>

          <!-- Giá / tồn / trạng thái chỉ đọc: chúng thuộc về biến thể, sửa ở tab
               "Sản phẩm chi tiết". Sản phẩm cha không có ô nhập giá. -->
          <div class="sp-rollup" v-if="spForm.id">
            <div class="sp-rollup-row"><span>Giá bán</span><b>{{ khoangGia(spForm) }}</b></div>
            <div class="sp-rollup-row"><span>Tổng tồn</span><b>{{ rollup(spForm).tongTon }}</b></div>
            <div class="sp-rollup-row"><span>Số biến thể</span><b>{{ rollup(spForm).soBienThe }}</b></div>
            <div class="sp-rollup-row"><span>Trạng thái</span><b>{{ rollup(spForm).dangBan ? 'Đang bán' : 'Chưa bán' }}</b></div>
            <p v-if="!rollup(spForm).soBienThe" class="sp-rollup-warn">
              Sản phẩm chưa có biến thể nào nên chưa có giá và chưa thể bán.
              Thêm biến thể (màu, kích cỡ, giá, số lượng) để mở bán.
            </p>
            <button class="btn btn-sm btn-light w-100 mt-2" @click="themBienTheCho(spForm)">
              <i class="bi bi-plus-lg"></i> Thêm biến thể cho sản phẩm này
            </button>
          </div>
        </div>
        <div class="green-actions">
          <button class="btn btn-success w-100" @click="spTaoMoi">Tạo mới</button>
          <button class="btn btn-success w-100" :disabled="!spForm.id" @click="spLuu">Lưu (Sửa)</button>
          <button class="btn btn-success w-100" @click="spLamMoi">Làm mới</button>
          <button class="btn btn-outline-danger w-100" :disabled="!spForm.id" @click="spAn">Ẩn (Xóa mềm)</button>
          <button class="btn btn-light w-100" @click="spXuatJson">Xuất json thông tin sản phẩm</button>
          <button class="btn btn-light w-100" @click="spTriggerImport">Import sản phẩm bằng list json</button>
          <input ref="spJsonInput" type="file" accept=".json,application/json" class="d-none" @change="spNhapJson">
        </div>
      </div>
    </div>

    <!-- ==================== TAB 2: SẢN PHẨM CHI TIẾT ==================== -->
    <div v-show="tab === 'chitiet'" class="sp-grid">
      <div class="sp-master">
        <div class="card sp-panel">
          <header class="sp-panel-head flex-wrap gap-2">
            <h6 class="sp-title">CHI TIẾT SẢN PHẨM</h6>
            <div class="d-flex gap-2 align-items-center flex-wrap">
              <select class="form-select form-select-sm" style="width:220px" v-model="ctFilterProductId">
                <option :value="null">Tất cả sản phẩm</option>
                <option v-for="p in filtered" :key="p.id" :value="p.id">{{ p.ten }}</option>
              </select>
              <span v-if="ctFilterProductId != null" class="badge bg-primary-subtle text-primary-emphasis border ct-filter-chip">
                {{ ctFilterProductLabel }}
                <button type="button" class="ct-filter-chip-x" @click="ctClearProductFilter" title="Bỏ lọc theo sản phẩm">✕</button>
              </span>
              <div class="input-group input-group-sm sp-search">
                <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                <input class="form-control" v-model="ctSearch" placeholder="Tìm sản phẩm chi tiết" />
              </div>
            </div>
          </header>
          <div class="table-scroll" style="height: 460px">
            <table class="table table-sm table-hover align-middle sp-table mb-0">
              <thead><tr>
                <th class="text-center">STT</th><th>Mã SP</th><th>Tên sp</th><th>Màu sắc</th>
                <th>Kích cỡ</th><th class="text-end">Đơn giá</th><th class="text-end">Giá nhập</th><th class="text-end">Tồn</th><th>Trạng thái</th>
              </tr></thead>
              <tbody>
                <tr v-for="(r, i) in ctRows" :key="r.key" :class="{ 'row-active': r.key === ctSelectedKey }" @click="selectCT(r)">
                  <td class="text-center">{{ i + 1 }}</td><td class="fw-medium">{{ r.ma }}</td><td>{{ r.tenSP }}</td>
                  <td>{{ r.mau }}</td><td>{{ r.size }}</td><td class="text-end">{{ vnd(r.gia) }}</td>
                  <td class="text-end">{{ vnd(r.giaNhap) }}</td><td class="text-end">{{ r.ton }}</td><td>{{ r.trangThai ? 'Đang bán' : 'Ngừng bán' }}</td>
                </tr>
                <tr v-if="ctRows.length === 0"><td colspan="9" class="text-center text-muted py-3">Không có biến thể</td></tr>
              </tbody>
            </table>
          </div>
          <footer class="sp-master-foot">
            <button class="btn btn-sm btn-outline-secondary" disabled
                    title="Backend chưa có endpoint recycle/restore cho biến thể (chỉ có cho sản phẩm)">
              Xem danh sách bị ẩn
            </button>
          </footer>
        </div>
      </div>

      <div class="sp-detail">
        <div class="sp-tab-r">{{ ctForm.idSpct ? 'Sửa biến thể' : 'Thêm biến thể' }}</div>
        <div class="sp-panel-green">
          <h6 class="green-title">Thông tin biến thể</h6>
          <div class="green-img">
            <button type="button" class="img-choose" @click="ctImgOpen = true" title="Chọn ảnh">
              <img :src="ctForm.imageUrl || PLACEHOLDER" alt="" @error="onImgError">
              <span class="img-choose-overlay"><i class="bi bi-images"></i> Chọn ảnh</span>
            </button>
            <div class="mt-2">
              <button type="button" class="btn btn-sm btn-light" @click="ctTriggerUpload">
                <i class="bi bi-upload"></i> Tải ảnh từ máy
              </button>
              <input ref="ctFileInput" type="file" accept="image/png,image/jpeg" class="d-none" @change="ctUploadImage">
            </div>
          </div>
          <dl class="green-fields">
            <!-- Luôn cho chọn, kể cả khi đang chọn một biến thể: nếu khoá lại thì không
                 thể "Tạo mới" một biến thể cho sản phẩm khác từ nội dung đang có. -->
            <div class="gf"><dt>Sản phẩm <span class="req">*</span></dt><dd>
              <select class="form-select form-select-sm" v-model="ctForm.idSanPham">
                <option value="">-- Chọn sản phẩm --</option>
                <option v-for="p in filtered" :key="p.id" :value="p.id">{{ p.ma }} · {{ p.ten }}</option>
              </select>
            </dd></div>
            <!-- Mã biến thể do server sinh: SPCT<id>-<mã màu>-<mã cỡ>. Trước đây là ô
                 nhập tự do và mọi biến thể của cùng sản phẩm dùng chung một mã. -->
            <div class="gf"><dt>Mã biến thể</dt><dd>
              <p class="ins-static" :class="{ 'chua-co': !ctForm.ma }">{{ ctForm.ma || 'Tự sinh khi lưu' }}</p>
            </dd></div>
            <div class="gf"><dt>Màu sắc <span class="req">*</span></dt><dd><AppSelect v-model="ctForm.mau" :options="mauSacList" /></dd></div>
            <div class="gf"><dt>Kích cỡ <span class="req">*</span></dt><dd><AppSelect v-model="ctForm.size" :options="kichThuocList" /></dd></div>
            <div class="gf"><dt>Đơn giá (bán) <span class="req">*</span></dt><dd><input type="number" min="1" class="form-control form-control-sm text-end" :class="{ 'is-invalid': !(Number(ctForm.gia) > 0) }" v-model.number="ctForm.gia"></dd></div>
            <div class="gf"><dt>Giá nhập (vốn)</dt><dd><input type="number" min="0" class="form-control form-control-sm text-end" v-model.number="ctForm.giaNhap"></dd></div>
            <div class="gf"><dt>Số lượng tồn <span class="req">*</span></dt><dd><input type="number" min="0" class="form-control form-control-sm text-end" :class="{ 'is-invalid': !ctForm.idSpct && !(Number(ctForm.ton) > 0) }" v-model.number="ctForm.ton"></dd></div>
            <div class="gf"><dt>Trạng thái</dt><dd class="d-flex gap-3 align-items-center">
              <label class="green-radio"><input type="radio" :value="true" v-model="ctForm.trangThai"> Đang bán</label>
              <label class="green-radio"><input type="radio" :value="false" v-model="ctForm.trangThai"> Ngừng bán</label>
            </dd></div>
          </dl>
          <p class="ct-hint">
            Biến thể chỉ được mở bán khi có đủ màu, kích cỡ, đơn giá &gt; 0 và số lượng &gt; 0.
            Hết hàng vẫn giữ trạng thái Đang bán để khách đặt trước.
          </p>
          <div class="ct-nhapkho" v-if="ctForm.idSpct">
            <span>Nhập kho:</span>
            <input type="number" min="1" class="form-control form-control-sm text-end" style="width:90px" v-model.number="ctNhap" placeholder="SL">
            <button class="btn btn-sm btn-light" @click="ctNhapKho"><i class="bi bi-box-arrow-in-down"></i> Nhập</button>
          </div>
        </div>
        <div class="green-actions">
          <button class="btn btn-success w-100" @click="ctTaoMoi">Tạo mới</button>
          <button class="btn btn-success w-100" :disabled="!ctForm.idSpct" @click="ctLuu">Lưu (Sửa)</button>
          <button class="btn btn-success w-100" @click="ctLamMoi">Làm mới</button>
          <button class="btn btn-outline-danger w-100" :disabled="!ctForm.idSpct" @click="ctAn">Ẩn (Xóa mềm)</button>
        </div>
      </div>
    </div>

    <!-- Danh mục / Thuộc tính: gộp vào đây thay vì màn riêng. Lazy bằng v-if để
         panel chỉ gọi API khi người dùng thực sự mở tab. -->
    <div v-if="tab === 'danhmuc'"><DanhMucPanel /></div>
    <div v-if="tab === 'thuoctinh'"><ThuocTinhPanel /></div>

    <ImagePicker v-model:open="spImgOpen" v-model="spForm.imageUrl" />
    <ImagePicker v-model:open="ctImgOpen" v-model="ctForm.imageUrl" />

    <!-- Thùng rác: sản phẩm đã ẩn (xóa mềm) -->
    <AppModal v-model:open="recycleOpen" title="Sản phẩm đã ẩn">
      <div v-if="recycleLoading" class="text-muted py-3">Đang tải…</div>
      <div v-else-if="!recycleRows.length" class="text-muted py-3">Không có sản phẩm nào bị ẩn.</div>
      <div v-else style="max-height:420px;overflow:auto">
        <table class="table table-sm table-hover align-middle mb-0">
          <thead><tr><th style="width:44px">STT</th><th>Mã</th><th>Tên sản phẩm</th><th>Thương hiệu</th><th></th></tr></thead>
          <tbody>
            <tr v-for="(r, i) in recycleRows" :key="r.id ?? r.ma">
              <td>{{ i + 1 }}</td>
              <td class="fw-semibold">{{ r.ma }}</td>
              <td>{{ r.ten }}</td>
              <td>{{ r.thuongHieu }}</td>
              <td class="text-end">
                <button class="btn btn-sm btn-success py-0 px-2" @click="restoreSP(r)">
                  <i class="bi bi-arrow-counterclockwise"></i> Khôi phục
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </AppModal>
  </AppShell>
</template>

<style scoped>
.sp-tabs { display: flex; gap: 4px; margin-bottom: 12px; }
.sp-tab {
  padding: 8px 20px;
  border: 1px solid var(--c-border);
  border-bottom: none;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
  background: #eef1f4; color: var(--c-text-muted); font-weight: 500; cursor: pointer;
}
.sp-tab.active { background: var(--c-primary); color: #fff; border-color: var(--c-primary); }
.sp-tab-link { text-decoration: none; color: var(--c-primary); }
.sp-tab-link:hover { background: var(--c-primary-subtle); }
.req { color: #ffd9d9; font-weight: 700; }
/* Giá trị chỉ đọc do server sinh (mã). Là <p>, không phải input đã disable. */
.ins-static { margin: 0; padding: 6px 0; font-size: 13px; font-weight: 600; letter-spacing: .3px; word-break: break-all; }
.ins-static.chua-co { font-weight: 400; opacity: .75; font-style: italic; }
.ct-hint { margin: 10px 0 0; font-size: 12px; line-height: 1.45; opacity: .9; }

/* tóm tắt chỉ đọc trong inspector sản phẩm cha */
.sp-rollup { margin-top: 12px; padding-top: 10px; border-top: 1px solid rgba(255,255,255,.25); font-size: 13px; }
.sp-rollup-row { display: flex; justify-content: space-between; gap: 8px; padding: 2px 0; }
.sp-rollup-warn { margin: 8px 0 0; font-size: 12px; line-height: 1.45; color: #ffe08a; }

.ct-nhapkho { display: flex; align-items: center; gap: 8px; margin-top: 12px; padding-top: 10px; border-top: 1px solid rgba(255,255,255,.25); font-size: 13px; }

.sp-grid { display: grid; grid-template-columns: minmax(0, 1fr) 400px; gap: 16px; align-items: start; }
.sp-master { min-width: 0; }
.sp-panel { padding: 10px 12px; }
.sp-panel-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 8px; }
.sp-title { margin: 0; font-weight: 700; letter-spacing: .3px; }
.sp-search { max-width: 280px; }
.ct-filter-chip { display: inline-flex; align-items: center; gap: 6px; font-weight: 500; }
.ct-filter-chip-x { border: none; background: none; padding: 0; line-height: 1; color: inherit; cursor: pointer; font-size: 12px; }

.table-scroll { overflow: auto; border: 1px solid var(--c-border); border-radius: var(--radius-sm); }
.sp-table { min-width: 640px; }
.sp-table thead th {
  position: sticky; top: 0; z-index: 1;
  background: var(--c-primary); color: #fff; font-weight: 600; font-size: 12px; white-space: nowrap;
}
.sp-table tbody td { font-size: 13px; }
.sp-table tbody tr { cursor: pointer; }
.row-active > td { background: var(--c-primary); color: #fff; }
.sp-master-foot { display: flex; justify-content: center; margin-top: 8px; }

/* right inspector */
.sp-detail { position: sticky; top: 16px; }
.sp-tab-r {
  display: inline-block; padding: 6px 18px; background: #eef1f4; color: var(--c-text-muted);
  border: 1px solid var(--c-border); border-bottom: none;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0; font-weight: 500;
}
.sp-panel-green {
  background: var(--c-primary); color: #fff; padding: 14px;
  border-radius: 0 var(--radius) var(--radius) var(--radius);
}
.green-title { font-weight: 700; margin: 0 0 12px; }
.green-img { text-align: center; margin-bottom: 12px; }
.img-choose {
  position: relative; display: inline-block; border: none; padding: 0;
  background: #fff; border-radius: var(--radius-sm); cursor: pointer; overflow: hidden;
}
.img-choose img { max-height: 120px; max-width: 100%; padding: 6px; display: block; }
.img-choose-overlay {
  position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; gap: 6px;
  background: rgba(11, 137, 90, 0.72); color: #fff; font-size: 13px; font-weight: 600;
  opacity: 0; transition: opacity var(--transition);
}
.img-choose:hover .img-choose-overlay { opacity: 1; }

.green-fields { display: flex; flex-direction: column; gap: 8px; margin: 0; }
.gf { display: grid; grid-template-columns: 120px 1fr; align-items: center; gap: 8px; }
.gf dt { font-weight: 500; font-size: 13px; opacity: .95; }
.gf dd { margin: 0; }

.green-radio-group { margin-top: 10px; display: flex; flex-direction: column; gap: 6px; }
.green-radio-label { font-weight: 500; font-size: 13px; }
.green-radio { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; cursor: pointer; }

.green-actions { display: flex; flex-direction: column; gap: 8px; margin-top: 14px; }
.green-actions .btn-success { background: #fff; color: var(--c-primary); border-color: #fff; font-weight: 600; }
.green-actions .btn-success:hover:not(:disabled) { background: #f0f0f0; }
.green-actions .btn-outline-danger { background: #fff; color: var(--c-danger); border-color: var(--c-danger); font-weight: 600; }
.green-actions .btn-light:disabled { opacity: .5; }

@media (max-width: 992px) {
  .sp-grid { grid-template-columns: 1fr; }
  .sp-detail { position: static; }
}
</style>
