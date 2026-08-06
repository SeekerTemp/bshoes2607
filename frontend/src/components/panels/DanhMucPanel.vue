<script setup>
// Category manager — faithful to ABCShops/6_demo_page_manageCategory.png.
// Left: category CRUD. Right: the products that belong to the selected category,
// with add / remove (assigns san_pham.id_loai_san_pham on the server).
import { ref, computed, onMounted } from 'vue'
import { loaiSanPhamApi } from '../../api/catalog'
import { sanPhamApi } from '../../api/sanPham'
import { useToast } from '../../composables/useToast'

const { notify } = useToast()
const cats = ref([])
const products = ref([])
const kw = ref('')
const form = ref(blank())
const selectedCat = ref(null)
const addProductId = ref('')
const fileInput = ref(null)

function blank() { return { id: null, ma: '', ten: '', moTa: '', trangThai: true } }
const filteredCats = computed(() => {
  const k = kw.value.trim().toLowerCase()
  return cats.value.filter(c => !k || (c.ma || '').toLowerCase().includes(k) || (c.ten || '').toLowerCase().includes(k))
})
function countIn(cat) { return products.value.filter(p => p.idLoaiSanPham === cat.id).length }
const productsInCat = computed(() => selectedCat.value ? products.value.filter(p => p.idLoaiSanPham === selectedCat.value.id) : [])
const productsNotInCat = computed(() => selectedCat.value ? products.value.filter(p => p.idLoaiSanPham !== selectedCat.value.id) : [])

// "Xem theo": Danh mục (CRUD + gán) hoặc Thương hiệu / Kiểu dáng (chỉ xem, gán ở màn Sản phẩm)
const dim = ref('category')   // 'category' | 'thuongHieu' | 'kieuDang'
const selectedVal = ref('')
const dimValues = computed(() =>
  dim.value === 'category' ? [] : [...new Set(products.value.map(p => p[dim.value]).filter(Boolean))].sort())
function countForVal(v) { return products.value.filter(p => p[dim.value] === v).length }
const productsForVal = computed(() => selectedVal.value ? products.value.filter(p => p[dim.value] === selectedVal.value) : [])
function pickDim(d) { dim.value = d; selectedVal.value = ''; selectedCat.value = null }

async function loadCats() { try { cats.value = await loaiSanPhamApi.findAll() } catch (e) { cats.value = [] } }
async function loadProducts() { try { products.value = await sanPhamApi.findAll() } catch (e) { products.value = [] } }

function selectCat(c) { selectedCat.value = c; form.value = { id: c.id, ma: c.ma, ten: c.ten, moTa: c.moTa || '', trangThai: c.trangThai !== false }; addProductId.value = '' }
function newCat() { selectedCat.value = null; form.value = blank() }

// Tạo mới / Lưu (Sửa) tách đôi — một nút tự đổi nghĩa theo dòng đang chọn khiến
// "chọn danh mục, đổi tên, tạo thành danh mục mới" ghi đè bản gốc thay vì tạo mới.
async function taoMoiCat() {
  if (!form.value.ten) { notify('Nhập tên danh mục', 'warning'); return }
  try {
    await loaiSanPhamApi.create({ ...form.value, id: null, ma: '' })
    notify('Đã thêm danh mục', 'success'); newCat(); await loadCats()
  } catch (e) { notify('Lưu thất bại (backend offline?)', 'warning') }
}
async function saveCat() {
  if (!form.value.id) { notify('Chọn danh mục trong danh sách để sửa', 'warning'); return }
  if (!form.value.ten) { notify('Nhập tên danh mục', 'warning'); return }
  try {
    await loaiSanPhamApi.update({ ...form.value })
    notify('Đã cập nhật danh mục', 'success'); await loadCats()
  } catch (e) { notify('Lưu thất bại (backend offline?)', 'warning') }
}
async function removeCat() {
  if (!form.value.id) { notify('Chọn danh mục để xoá', 'warning'); return }
  if (!confirm(`Xoá danh mục "${form.value.ten}"?`)) return
  try { await loaiSanPhamApi.remove(form.value.id); notify('Đã xoá', 'success'); newCat(); await loadCats() }
  catch (e) { notify('Xoá thất bại (có thể còn sản phẩm)', 'warning') }
}

async function addProductToCat() {
  if (!selectedCat.value || !addProductId.value) { notify('Chọn sản phẩm để thêm', 'warning'); return }
  try {
    await sanPhamApi.setDanhMuc(addProductId.value, selectedCat.value.id)
    notify('Đã thêm sản phẩm vào danh mục', 'success')
    addProductId.value = ''; await loadProducts()
  } catch (e) { notify('Thêm thất bại (backend offline?)', 'warning') }
}
async function removeProductFromCat(p) {
  try { await sanPhamApi.setDanhMuc(p.id, null); notify('Đã bỏ khỏi danh mục', 'success'); await loadProducts() }
  catch (e) { notify('Thao tác thất bại', 'warning') }
}

// ---- category CSV ----
function exportCsv() {
  const lines = ['ma,ten,moTa'].concat(cats.value.map(r => ['ma', 'ten', 'moTa'].map(h => `"${(r[h] ?? '').toString().replace(/"/g, '""')}"`).join(',')))
  const blob = new Blob(['﻿' + lines.join('\n')], { type: 'text/csv;charset=utf-8' })
  const a = document.createElement('a'); a.href = URL.createObjectURL(blob); a.download = 'danh-muc.csv'; a.click(); URL.revokeObjectURL(a.href)
}
function triggerImport() { fileInput.value?.click() }
async function importCsv(ev) {
  const file = ev.target.files[0]; if (!file) return
  const lines = (await file.text()).replace(/^﻿/, '').split(/\r?\n/).filter(Boolean); lines.shift()
  let ok = 0
  for (const line of lines) {
    const cells = line.match(/("([^"]|"")*"|[^,]*)/g).filter((_, i) => i % 2 === 0).map(c => c.replace(/^"|"$/g, '').replace(/""/g, '"'))
    if (!cells[1]) continue
    try { await loaiSanPhamApi.create({ ma: cells[0], ten: cells[1], moTa: cells[2], trangThai: true }); ok++ } catch (e) { /* skip */ }
  }
  notify(`Đã nhập ${ok} danh mục`, 'success'); ev.target.value = ''; await loadCats()
}

onMounted(async () => { await Promise.all([loadCats(), loadProducts()]) })
</script>

<template>
  <div>

    <!-- Xem theo: Danh mục (quản lý) / Thương hiệu / Kiểu dáng (xem theo) -->
    <div class="btn-group btn-group-sm mb-3">
      <button class="btn" :class="dim === 'category' ? 'btn-success' : 'btn-outline-secondary'" @click="pickDim('category')">Danh mục</button>
      <button class="btn" :class="dim === 'thuongHieu' ? 'btn-success' : 'btn-outline-secondary'" @click="pickDim('thuongHieu')">Thương hiệu (hãng giày)</button>
      <button class="btn" :class="dim === 'kieuDang' ? 'btn-success' : 'btn-outline-secondary'" @click="pickDim('kieuDang')">Kiểu dáng</button>
    </div>

    <div class="dm-grid">
      <!-- ===== left (category mode): category list + form ===== -->
      <div v-if="dim === 'category'" class="d-flex flex-column gap-3">
        <div class="card"><div class="card-body">
          <div class="d-flex gap-2 mb-3 flex-wrap">
            <input class="form-control form-control-sm" style="max-width:180px" v-model="kw" placeholder="Tìm danh mục...">
            <button class="btn btn-sm btn-outline-secondary" @click="triggerImport" title="Import"><i class="bi bi-upload"></i></button>
            <button class="btn btn-sm btn-outline-secondary" @click="exportCsv" title="Export"><i class="bi bi-download"></i></button>
            <button class="btn btn-sm btn-success ms-auto" @click="newCat"><i class="bi bi-plus-lg"></i> Thêm</button>
            <input ref="fileInput" type="file" accept=".csv" class="d-none" @change="importCsv">
          </div>
          <div style="max-height:300px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
            <table class="table table-sm table-hover align-middle mb-0 dm-table">
              <thead><tr><th style="width:44px">STT</th><th>Mã</th><th>Tên danh mục</th><th class="text-center">SP</th><th class="text-center">TT</th></tr></thead>
              <tbody>
                <tr v-for="(c,i) in filteredCats" :key="c.id" @click="selectCat(c)" style="cursor:pointer" :class="{ sel: selectedCat && c.id === selectedCat.id }">
                  <td>{{ i+1 }}</td><td class="fw-semibold">{{ c.ma }}</td><td>{{ c.ten }}</td>
                  <td class="text-center">{{ countIn(c) }}</td>
                  <td class="text-center"><i class="bi" :class="c.trangThai !== false ? 'bi-toggle-on text-success' : 'bi-toggle-off text-muted'"></i></td>
                </tr>
                <tr v-if="filteredCats.length === 0"><td colspan="5" class="text-center text-muted py-4">Chưa có danh mục</td></tr>
              </tbody>
            </table>
          </div>
        </div></div>

        <div class="card"><div class="card-body">
          <h6 class="fw-bold mb-3">Nhóm danh mục</h6>
          <!-- Mã luôn do server sinh ("LSP" + id). Ô nhập cũ là ảo: giá trị gõ vào bị
               LoaiSanPhamServiceImpl.create() ghi đè, người dùng không hề biết. -->
          <div class="mb-2"><label class="form-label small mb-1">Mã</label>
            <p class="ma-static" :class="{ 'chua-co': !form.ma }">{{ form.ma || 'Tự sinh khi lưu' }}</p>
          </div>
          <div class="mb-2"><label class="form-label small mb-1">Tên danh mục *</label><input class="form-control form-control-sm" v-model="form.ten"></div>
          <div class="mb-2"><label class="form-label small mb-1">Mô tả</label><textarea class="form-control form-control-sm" rows="2" v-model="form.moTa"></textarea></div>
          <div class="mb-3">
            <label class="form-label small mb-1 d-block">Trạng thái</label>
            <div class="form-check form-check-inline"><input class="form-check-input" type="radio" :value="true" v-model="form.trangThai" id="dm-on"><label class="form-check-label" for="dm-on">Bật</label></div>
            <div class="form-check form-check-inline"><input class="form-check-input" type="radio" :value="false" v-model="form.trangThai" id="dm-off"><label class="form-check-label" for="dm-off">Tắt</label></div>
          </div>
          <div class="d-flex gap-2 flex-wrap">
            <button class="btn btn-outline-secondary flex-fill" @click="newCat">Làm mới</button>
            <button class="btn btn-success flex-fill" @click="taoMoiCat">Tạo mới</button>
            <button class="btn btn-success flex-fill" :disabled="!form.id" @click="saveCat">Lưu (Sửa)</button>
            <button class="btn btn-outline-danger flex-fill" :disabled="!form.id" @click="removeCat">Xoá danh mục</button>
          </div>
        </div></div>
      </div>

      <!-- ===== left (brand/style mode): distinct values ===== -->
      <div v-else class="card"><div class="card-body">
        <h6 class="fw-bold mb-3">{{ dim === 'thuongHieu' ? 'Thương hiệu (hãng giày)' : 'Kiểu dáng' }}</h6>
        <div style="max-height:520px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
          <table class="table table-sm table-hover align-middle mb-0 dm-table">
            <thead><tr><th style="width:44px">STT</th><th>Tên</th><th class="text-center">SP</th></tr></thead>
            <tbody>
              <tr v-for="(v,i) in dimValues" :key="v" @click="selectedVal = v" style="cursor:pointer" :class="{ sel: selectedVal === v }">
                <td>{{ i+1 }}</td><td class="fw-semibold">{{ v }}</td><td class="text-center">{{ countForVal(v) }}</td>
              </tr>
              <tr v-if="dimValues.length === 0"><td colspan="3" class="text-center text-muted py-4">Chưa có dữ liệu</td></tr>
            </tbody>
          </table>
        </div>
      </div></div>

      <!-- ===== right: products ===== -->
      <div class="card"><div class="card-body">
       <template v-if="dim === 'category'">
        <div v-if="!selectedCat" class="text-muted text-center py-5">
          <i class="bi bi-diagram-3 fs-1 d-block mb-2 opacity-50"></i>
          Chọn một danh mục để xếp sản phẩm vào (hiển thị ở trang chủ)
        </div>
        <div v-else>
          <h6 class="fw-bold mb-1">Sản phẩm thuộc: <span style="color:#0B895A">{{ selectedCat.ten }}</span></h6>
          <div class="text-muted small mb-3">{{ productsInCat.length }} sản phẩm — dùng để xếp danh mục preview ở trang chủ</div>

          <!-- add product -->
          <div class="d-flex gap-2 mb-3">
            <select class="form-select form-select-sm" v-model="addProductId">
              <option value="">-- Chọn sản phẩm để thêm --</option>
              <option v-for="p in productsNotInCat" :key="p.id" :value="p.id">{{ p.ma }} · {{ p.ten }}{{ p.loaiSP ? ' (đang ở: ' + p.loaiSP + ')' : '' }}</option>
            </select>
            <button class="btn btn-sm btn-success text-nowrap" @click="addProductToCat"><i class="bi bi-plus-lg"></i> Thêm sản phẩm</button>
          </div>

          <div style="max-height:420px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
            <table class="table table-sm table-hover align-middle mb-0 dm-table">
              <thead><tr><th style="width:44px">STT</th><th>Mã SP</th><th>Tên sản phẩm</th><th>Thương hiệu</th><th></th></tr></thead>
              <tbody>
                <tr v-for="(p,i) in productsInCat" :key="p.id">
                  <td>{{ i+1 }}</td><td class="fw-semibold">{{ p.ma }}</td><td>{{ p.ten }}</td><td class="text-muted">{{ p.thuongHieu }}</td>
                  <td class="text-end"><button class="btn btn-sm btn-outline-danger py-0 px-2" @click="removeProductFromCat(p)" title="Bỏ khỏi danh mục"><i class="bi bi-x-lg"></i></button></td>
                </tr>
                <tr v-if="productsInCat.length === 0"><td colspan="5" class="text-center text-muted py-4">Chưa có sản phẩm trong danh mục này</td></tr>
              </tbody>
            </table>
          </div>
        </div>
       </template>
       <template v-else>
        <div v-if="!selectedVal" class="text-muted text-center py-5">
          <i class="bi bi-bookmark-star fs-1 d-block mb-2 opacity-50"></i>
          Chọn một {{ dim === 'thuongHieu' ? 'thương hiệu' : 'kiểu dáng' }} để xem sản phẩm
        </div>
        <div v-else>
          <h6 class="fw-bold mb-1">Sản phẩm — <span style="color:#0B895A">{{ selectedVal }}</span></h6>
          <div class="text-muted small mb-3">{{ productsForVal.length }} sản phẩm · gán {{ dim === 'thuongHieu' ? 'hãng' : 'kiểu dáng' }} tại màn Sản phẩm</div>
          <div style="max-height:460px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
            <table class="table table-sm table-hover align-middle mb-0 dm-table">
              <thead><tr><th style="width:44px">STT</th><th>Mã SP</th><th>Tên sản phẩm</th><th>Danh mục</th></tr></thead>
              <tbody>
                <tr v-for="(p,i) in productsForVal" :key="p.id">
                  <td>{{ i+1 }}</td><td class="fw-semibold">{{ p.ma }}</td><td>{{ p.ten }}</td><td class="text-muted">{{ p.loaiSP || '—' }}</td>
                </tr>
                <tr v-if="productsForVal.length === 0"><td colspan="4" class="text-center text-muted py-4">Không có sản phẩm</td></tr>
              </tbody>
            </table>
          </div>
        </div>
       </template>
      </div></div>
    </div>
  </div>
</template>

<style scoped>
/* Mã do server sinh: hiển thị dạng chữ, không phải ô nhập bị vô hiệu hoá. */
.ma-static { margin: 0; padding: 6px 0; font-size: 13px; font-weight: 600; letter-spacing: .3px; }
.ma-static.chua-co { font-weight: 400; opacity: .7; font-style: italic; }

.dm-grid { display: grid; grid-template-columns: 380px minmax(0, 1fr); gap: 16px; align-items: start; }
.dm-table thead th { position: sticky; top: 0; background: var(--c-primary, #0B895A); color: #fff; font-size: 12px; white-space: nowrap; }
.dm-table tbody tr.sel > td { background: var(--c-primary-subtle, #E7F4EF); }
@media (max-width: 992px) { .dm-grid { grid-template-columns: 1fr; } }
</style>
