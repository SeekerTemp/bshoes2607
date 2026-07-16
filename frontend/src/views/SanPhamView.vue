<script setup>
// Faithful to NetBeans Pnl_3_qlSanPham (see frontend/refui): a JTabbedPane with
// three tabs — "Sản phẩm", "Sản phẩm chi tiết", "Thuộc tính". Each tab is a
// master-detail screen: a table on the left and a green inspector panel on the
// right (NOT a modal popup). Selecting a row fills the inspector.
import { ref, computed } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import ImagePicker from '../components/ui/ImagePicker.vue'
import { useSanPham } from '../composables/useSanPham'
import { useToast } from '../composables/useToast'
import { vnd } from '../utils/format'
import { bienTheApi } from '../api/bienThe'
import {
  loaiSanPhamList, kieuDangList, kieuCoGiayList, kieuDayGiayList,
  xuatXuList, mauSacList, kichThuocList,
} from '../mock/data'

const { filtered, load, add, update, remove, thuongHieuList, chatLieuList } = useSanPham()
const { notify } = useToast()

const tab = ref('sanpham') // 'sanpham' | 'chitiet'  (attributes now live in the /thuoc-tinh screen)

const PLACEHOLDER = '/images/shoes/img_shoe_10001.png'
function onImgError(e) { e.target.style.visibility = 'hidden' }

// grid-card image pickers (one per tab that carries an image)
const spImgOpen = ref(false)
const ctImgOpen = ref(false)

/* ============================ TAB 1 — Sản phẩm ============================ */
const spSearch = ref('')
const spRows = computed(() => {
  const k = spSearch.value.trim().toLowerCase()
  return filtered.value.filter(p =>
    !k || p.ma.toLowerCase().includes(k) || p.ten.toLowerCase().includes(k))
})
function firstVariant(p) { return (p.bienThe && p.bienThe[0]) || {} }

function blankSP() {
  return {
    id: null, ma: '', ten: '', loaiSP: '', chatLieu: chatLieuList[0] || '',
    kieuDang: '', coGiay: '', dayGiay: '', thuongHieu: thuongHieuList[0] || '',
    xuatXu: '', gia: 0, moTa: '', imageUrl: '', bienThe: [],
  }
}
const spForm = ref(blankSP())
const spSelectedId = ref(null)
function selectSP(p) {
  spSelectedId.value = p.id
  spForm.value = { ...blankSP(), ...JSON.parse(JSON.stringify(p)) }
}
function spThem() { spSelectedId.value = null; spForm.value = blankSP() }
// double-click a product → jump to its variants (Sản phẩm chi tiết tab), filtered
function openBienThe(p) {
  selectSP(p)
  ctSearch.value = p.ten
  ctSelectedKey.value = null
  tab.value = 'chitiet'
}
function spLamMoi() {
  if (spSelectedId.value) { const p = filtered.value.find(x => x.id === spSelectedId.value); if (p) selectSP(p) }
  else spForm.value = blankSP()
}
function spLuu() {
  if (!spForm.value.ten) { notify('Nhập tên sản phẩm', 'warning'); return }
  if (spForm.value.id) { update({ ...spForm.value }); notify('Đã cập nhật sản phẩm', 'success') }
  else { add({ ...spForm.value }); notify('Đã thêm sản phẩm', 'success'); spThem() }
}
function spAn() {
  if (!spForm.value.id) { notify('Chọn sản phẩm để ẩn', 'warning'); return }
  if (confirm(`Ẩn (xóa mềm) sản phẩm ${spForm.value.ma}?`)) { remove(spForm.value.ma); spThem() }
}

/* ============= TAB 2 — Sản phẩm chi tiết (biến thể), wired to backend ============= */
const ctSearch = ref('')
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
  return rows.filter(r => !k || (r.ma || '').toLowerCase().includes(k) || (r.tenSP || '').toLowerCase().includes(k))
})
const ctSelectedKey = ref(null)
const ctNhap = ref(0)
function blankCT() {
  return { idSpct: null, idSanPham: '', ma: '', tenSP: '', mau: mauSacList[0] || '', size: kichThuocList[0] || '', gia: 0, giaNhap: 0, ton: 0, trangThai: true, imageUrl: '' }
}
const ctForm = ref(blankCT())
function selectCT(r) { ctSelectedKey.value = r.key; ctForm.value = { ...r }; ctNhap.value = 0 }
function ctThem() { ctSelectedKey.value = null; ctForm.value = blankCT() }
function ctLamMoi() { const r = ctRows.value.find(x => x.key === ctSelectedKey.value); ctForm.value = r ? { ...r } : blankCT() }
async function ctLuu() {
  const f = ctForm.value
  const dto = { ma: f.ma, mau: f.mau, size: f.size, gia: f.gia, giaNhap: f.giaNhap, ton: f.ton, trangThai: f.trangThai, imageUrl: f.imageUrl }
  try {
    if (f.idSpct) { await bienTheApi.update(f.idSpct, dto); notify('Đã cập nhật biến thể', 'success') }
    else {
      if (!f.idSanPham) { notify('Chọn sản phẩm cho biến thể', 'warning'); return }
      await bienTheApi.create(f.idSanPham, dto); notify('Đã thêm biến thể', 'success')
    }
    await load(); ctThem()
  } catch (e) { notify('Lưu thất bại (backend offline?)', 'warning') }
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
  try { await bienTheApi.remove(ctForm.value.idSpct); notify('Đã ẩn biến thể', 'success'); await load(); ctThem() }
  catch (e) { notify('Thao tác thất bại', 'warning') }
}
</script>

<template>
  <AppShell>
    <PageHeader title="Quản lý sản phẩm" />

    <!-- JTabbedPane section_1: Sản phẩm | Sản phẩm chi tiết | Thuộc tính -->
    <div class="sp-tabs">
      <button class="sp-tab" :class="{ active: tab === 'sanpham' }" @click="tab = 'sanpham'">Sản phẩm</button>
      <button class="sp-tab" :class="{ active: tab === 'chitiet' }" @click="tab = 'chitiet'">Sản phẩm chi tiết</button>
      <router-link to="/thuoc-tinh" class="sp-tab sp-tab-link">Thuộc tính ↗</router-link>
    </div>

    <!-- ==================== TAB 1: SẢN PHẨM ==================== -->
    <div v-show="tab === 'sanpham'" class="sp-grid">
      <div class="sp-master">
        <div class="card sp-panel">
          <header class="sp-panel-head">
            <h6 class="sp-title">SẢN PHẨM</h6>
            <div class="input-group input-group-sm sp-search">
              <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
              <input class="form-control" v-model="spSearch" placeholder="Tìm kiếm sản phẩm" />
            </div>
          </header>
          <div class="table-scroll" style="height: 460px">
            <table class="table table-sm table-hover align-middle sp-table mb-0">
              <thead><tr>
                <th class="text-center">STT</th><th>Mã SP</th><th>Tên sp</th><th>Chất liệu</th>
                <th>Màu sắc</th><th>Kích thước</th><th>Kiểu dáng</th><th>Kiểu cỡ giày</th>
                <th>Kiểu dây giày</th><th>Thương hiệu</th>
              </tr></thead>
              <tbody>
                <tr v-for="(p, i) in spRows" :key="p.id" :class="{ 'row-active': p.id === spSelectedId }" @click="selectSP(p)" @dblclick="openBienThe(p)" title="Double-click để xem biến thể">
                  <td class="text-center">{{ i + 1 }}</td><td class="fw-medium">{{ p.ma }}</td><td>{{ p.ten }}</td>
                  <td>{{ p.chatLieu }}</td><td>{{ firstVariant(p).mau || '—' }}</td><td>{{ firstVariant(p).size || '—' }}</td>
                  <td>{{ p.kieuDang || '—' }}</td><td>{{ p.coGiay || '—' }}</td><td>{{ p.dayGiay || '—' }}</td><td>{{ p.thuongHieu }}</td>
                </tr>
                <tr v-if="spRows.length === 0"><td colspan="10" class="text-center text-muted py-3">Không có sản phẩm</td></tr>
              </tbody>
            </table>
          </div>
          <footer class="sp-master-foot"><button class="btn btn-sm btn-outline-secondary" disabled>Xem danh sách bị ẩn</button></footer>
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
            <div class="gf"><dt>Mã sản phẩm</dt><dd><input class="form-control form-control-sm" v-model="spForm.ma" readonly></dd></div>
            <div class="gf"><dt>Tên sản phẩm</dt><dd><input class="form-control form-control-sm" v-model="spForm.ten"></dd></div>
            <div class="gf"><dt>Loại sản phẩm</dt><dd><AppSelect v-model="spForm.loaiSP" :options="loaiSanPhamList" /></dd></div>
            <div class="gf"><dt>Chất liệu</dt><dd><AppSelect v-model="spForm.chatLieu" :options="chatLieuList" /></dd></div>
            <div class="gf"><dt>Kiểu dáng</dt><dd><AppSelect v-model="spForm.kieuDang" :options="kieuDangList" /></dd></div>
            <div class="gf"><dt>Kiểu cỡ giày</dt><dd><AppSelect v-model="spForm.coGiay" :options="kieuCoGiayList" /></dd></div>
            <div class="gf"><dt>Kiểu dây giày</dt><dd><AppSelect v-model="spForm.dayGiay" :options="kieuDayGiayList" /></dd></div>
            <div class="gf"><dt>Thương hiệu</dt><dd><AppSelect v-model="spForm.thuongHieu" :options="thuongHieuList" /></dd></div>
            <div class="gf"><dt>Xuất xứ</dt><dd><AppSelect v-model="spForm.xuatXu" :options="xuatXuList" /></dd></div>
          </dl>
        </div>
        <div class="green-actions">
          <button class="btn btn-success w-100" @click="spThem">Thêm</button>
          <button class="btn btn-success w-100" :disabled="!spForm.id" @click="spLuu">Sửa</button>
          <button class="btn btn-success w-100" @click="spLamMoi">Làm mới</button>
          <button class="btn btn-outline-danger w-100" :disabled="!spForm.id" @click="spAn">Ẩn (Xóa mềm)</button>
          <button class="btn btn-light w-100" disabled>Xuất json thông tin sản phẩm</button>
          <button class="btn btn-light w-100" disabled>Import sản phẩm bằng list json/csv/excel</button>
        </div>
      </div>
    </div>

    <!-- ==================== TAB 2: SẢN PHẨM CHI TIẾT ==================== -->
    <div v-show="tab === 'chitiet'" class="sp-grid">
      <div class="sp-master">
        <div class="card sp-panel">
          <header class="sp-panel-head">
            <h6 class="sp-title">CHI TIẾT SẢN PHẨM</h6>
            <div class="input-group input-group-sm sp-search">
              <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
              <input class="form-control" v-model="ctSearch" placeholder="Tìm sản phẩm chi tiết" />
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
          <footer class="sp-master-foot"><button class="btn btn-sm btn-outline-secondary" disabled>Xem danh sách bị ẩn</button></footer>
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
          </div>
          <dl class="green-fields">
            <div class="gf" v-if="!ctForm.idSpct"><dt>Sản phẩm</dt><dd>
              <select class="form-select form-select-sm" v-model="ctForm.idSanPham">
                <option value="">-- Chọn sản phẩm --</option>
                <option v-for="p in filtered" :key="p.id" :value="p.id">{{ p.ma }} · {{ p.ten }}</option>
              </select>
            </dd></div>
            <div class="gf" v-else><dt>Sản phẩm</dt><dd><input class="form-control form-control-sm" :value="ctForm.tenSP" readonly></dd></div>
            <div class="gf"><dt>Mã biến thể</dt><dd><input class="form-control form-control-sm" v-model="ctForm.ma" placeholder="Tự sinh nếu bỏ trống"></dd></div>
            <div class="gf"><dt>Màu sắc</dt><dd><AppSelect v-model="ctForm.mau" :options="mauSacList" /></dd></div>
            <div class="gf"><dt>Kích cỡ</dt><dd><AppSelect v-model="ctForm.size" :options="kichThuocList" /></dd></div>
            <div class="gf"><dt>Đơn giá (bán)</dt><dd><input type="number" class="form-control form-control-sm text-end" v-model.number="ctForm.gia"></dd></div>
            <div class="gf"><dt>Giá nhập (vốn)</dt><dd><input type="number" class="form-control form-control-sm text-end" v-model.number="ctForm.giaNhap"></dd></div>
            <div class="gf"><dt>Số lượng tồn</dt><dd><input type="number" class="form-control form-control-sm text-end" v-model.number="ctForm.ton"></dd></div>
            <div class="gf"><dt>Trạng thái</dt><dd class="d-flex gap-3 align-items-center">
              <label class="green-radio"><input type="radio" :value="true" v-model="ctForm.trangThai"> Đang bán</label>
              <label class="green-radio"><input type="radio" :value="false" v-model="ctForm.trangThai"> Ngừng bán</label>
            </dd></div>
          </dl>
          <div class="ct-nhapkho" v-if="ctForm.idSpct">
            <span>Nhập kho:</span>
            <input type="number" min="1" class="form-control form-control-sm text-end" style="width:90px" v-model.number="ctNhap" placeholder="SL">
            <button class="btn btn-sm btn-light" @click="ctNhapKho"><i class="bi bi-box-arrow-in-down"></i> Nhập</button>
          </div>
        </div>
        <div class="green-actions">
          <button class="btn btn-success w-100" @click="ctThem">Thêm mới</button>
          <button class="btn btn-success w-100" @click="ctLuu">{{ ctForm.idSpct ? 'Lưu (Sửa)' : 'Tạo biến thể' }}</button>
          <button class="btn btn-success w-100" @click="ctLamMoi">Làm mới</button>
          <button class="btn btn-outline-danger w-100" :disabled="!ctForm.idSpct" @click="ctAn">Ẩn (Xóa mềm)</button>
        </div>
      </div>
    </div>

    <ImagePicker v-model:open="spImgOpen" v-model="spForm.imageUrl" />
    <ImagePicker v-model:open="ctImgOpen" v-model="ctForm.imageUrl" />
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
.ct-nhapkho { display: flex; align-items: center; gap: 8px; margin-top: 12px; padding-top: 10px; border-top: 1px solid rgba(255,255,255,.25); font-size: 13px; }

.sp-grid { display: grid; grid-template-columns: minmax(0, 1fr) 400px; gap: 16px; align-items: start; }
.sp-master { min-width: 0; }
.sp-panel { padding: 10px 12px; }
.sp-panel-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 8px; }
.sp-title { margin: 0; font-weight: 700; letter-spacing: .3px; }
.sp-search { max-width: 280px; }

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
