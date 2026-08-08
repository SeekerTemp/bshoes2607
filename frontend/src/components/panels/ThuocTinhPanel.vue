<script setup>
// Product-attribute manager — faithful to ABCShops/5_pannel_product_attribute.png.
// A "Loại thuộc tính" filter (Tất cả + each of the 8 catalogs) drives the list: pick
// a type and only that type's attributes show. Right panel is the add/edit form.
import { ref, computed, onMounted } from 'vue'
import { ATTR_TYPES, catalogApi } from '../../api/catalog'
import { useToast } from '../../composables/useToast'

const { notify } = useToast()
const filterKey = ref('all')          // 'all' | one of ATTR_TYPES[].key
const rows = ref([])
const kw = ref('')
const form = ref(blank())
const fileInput = ref(null)

function blank() { return { id: null, ma: '', ten: '', moTa: '', trangThai: true, loai: ATTR_TYPES[0].key } }
function typeOf(key) { return ATTR_TYPES.find(t => t.key === key) }
const activeLabel = computed(() => filterKey.value === 'all' ? 'Tất cả' : typeOf(filterKey.value)?.label)
const showLoaiColumn = computed(() => filterKey.value === 'all')
const formType = computed(() => typeOf(form.value.loai) || ATTR_TYPES[0])

const filtered = computed(() => {
  const k = kw.value.trim().toLowerCase()
  return rows.value.filter(r => !k || (r.ma || '').toLowerCase().includes(k) || (r.ten || '').toLowerCase().includes(k))
})

async function fetchType(t) {
  try {
    const list = await catalogApi(t.key).findAll()
    return list.map(r => ({ ...r, _typeKey: t.key, _typeLabel: t.label }))
  } catch (e) { return [] }
}
async function load() {
  if (filterKey.value === 'all') {
    const all = await Promise.all(ATTR_TYPES.map(fetchType))
    rows.value = all.flat()
  } else {
    rows.value = await fetchType(typeOf(filterKey.value))
  }
  form.value = blank()
  if (filterKey.value !== 'all') form.value.loai = filterKey.value
}
function pickFilter(key) { filterKey.value = key; kw.value = ''; load() }
function edit(r) { form.value = { id: r.id, ma: r.ma, ten: r.ten, moTa: r.moTa || '', trangThai: r.trangThai !== false, loai: r._typeKey } }
function reset() { form.value = blank(); if (filterKey.value !== 'all') form.value.loai = filterKey.value }

function payloadOf(id) {
  const p = { id, ten: form.value.ten, trangThai: form.value.trangThai }
  if (formType.value.moTa) p.moTa = form.value.moTa
  return p   // không gửi `ma`: server luôn tự sinh và ghi đè
}

// Tạo mới / Lưu (Sửa) tách đôi — trước đây một nút tự đổi nghĩa theo dòng đang chọn,
// nên "chọn thuộc tính, đổi tên, tạo thành thuộc tính mới" lại ghi đè bản gốc.
async function taoMoi() {
  if (!form.value.ten) { notify('Nhập tên thuộc tính', 'warning'); return }
  try {
    await catalogApi(form.value.loai).create(payloadOf(null))
    notify('Đã thêm ' + formType.value.label, 'success')
    reset(); await load()
  } catch (e) { notify('Lưu thất bại (backend offline?)', 'warning') }
}
async function save() {
  if (!form.value.id) { notify('Chọn thuộc tính trong danh sách để sửa', 'warning'); return }
  if (!form.value.ten) { notify('Nhập tên thuộc tính', 'warning'); return }
  try {
    await catalogApi(form.value.loai).update(payloadOf(form.value.id))
    notify('Đã cập nhật ' + formType.value.label, 'success')
    await load()
  } catch (e) { notify('Lưu thất bại (backend offline?)', 'warning') }
}
async function remove(r) {
  if (!confirm(`Xoá "${r.ten}" (${r._typeLabel})?`)) return
  try { await catalogApi(r._typeKey).remove(r.id); notify('Đã xoá', 'success'); await load() }
  catch (e) { notify('Xoá thất bại', 'warning') }
}

// ---- Export / Import (CSV, only for a single selected type) ----
function exportCsv() {
  const head = ['loai', 'ma', 'ten', 'moTa']
  const lines = [head.join(',')].concat(rows.value.map(r => [r._typeLabel, r.ma, r.ten, r.moTa].map(c => `"${(c ?? '').toString().replace(/"/g, '""')}"`).join(',')))
  const blob = new Blob(['﻿' + lines.join('\n')], { type: 'text/csv;charset=utf-8' })
  const a = document.createElement('a'); a.href = URL.createObjectURL(blob)
  a.download = 'thuoc-tinh-' + filterKey.value + '.csv'; a.click(); URL.revokeObjectURL(a.href)
}
function triggerImport() {
  if (filterKey.value === 'all') { notify('Chọn 1 loại thuộc tính để nhập', 'warning'); return }
  fileInput.value?.click()
}
async function importCsv(ev) {
  const file = ev.target.files[0]; if (!file) return
  const lines = (await file.text()).replace(/^﻿/, '').split(/\r?\n/).filter(Boolean); const head = lines.shift().split(',').map(s => s.trim())
  const api = catalogApi(filterKey.value); let ok = 0
  for (const line of lines) {
    const cells = line.match(/("([^"]|"")*"|[^,]*)/g).filter((_, i) => i % 2 === 0).map(c => c.replace(/^"|"$/g, '').replace(/""/g, '"'))
    const obj = {}; head.forEach((h, i) => obj[h] = cells[i])
    if (!obj.ten) continue
    try { await api.create({ ma: obj.ma, ten: obj.ten, moTa: obj.moTa, trangThai: true }); ok++ } catch (e) { /* skip */ }
  }
  notify(`Đã nhập ${ok} dòng`, 'success'); ev.target.value = ''; await load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="tt-grid">
      <!-- list + type filter -->
      <div class="card"><div class="card-body">
        <div class="d-flex gap-2 mb-3 flex-wrap align-items-center">
          <input class="form-control form-control-sm" style="max-width:220px" v-model="kw" placeholder="Tìm mã / tên...">
          <button class="btn btn-sm btn-outline-secondary" @click="triggerImport"><i class="bi bi-upload"></i> Import</button>
          <button class="btn btn-sm btn-outline-secondary" @click="exportCsv"><i class="bi bi-download"></i> Export</button>
          <input ref="fileInput" type="file" accept=".csv" class="d-none" @change="importCsv">
          <span class="badge bg-light text-dark border">{{ activeLabel }} · {{ filtered.length }}</span>
          <button class="btn btn-sm btn-success ms-auto" @click="reset"><i class="bi bi-plus-lg"></i> Thêm</button>
        </div>

        <div class="d-flex gap-3">
          <!-- table -->
          <div class="flex-grow-1" style="max-height:520px;overflow:auto;border:1px solid #e5e9ef;border-radius:8px">
            <table class="table table-sm table-hover align-middle mb-0 tt-table">
              <thead><tr>
                <th style="width:44px">STT</th><th v-if="showLoaiColumn">Loại thuộc tính</th><th>Mã</th><th>Tên</th><th class="text-center">TT</th><th></th>
              </tr></thead>
              <tbody>
                <tr v-for="(r,i) in filtered" :key="r._typeKey + '-' + r.id" @click="edit(r)" style="cursor:pointer" :class="{ sel: r.id === form.id && r._typeKey === form.loai }">
                  <td>{{ i+1 }}</td>
                  <td v-if="showLoaiColumn"><span class="badge bg-primary-subtle text-primary-emphasis border">{{ r._typeLabel }}</span></td>
                  <td class="fw-semibold">{{ r.ma }}</td><td>{{ r.ten }}</td>
                  <td class="text-center"><i class="bi" :class="r.trangThai !== false ? 'bi-eye text-success' : 'bi-eye-slash text-muted'"></i></td>
                  <td class="text-end"><button class="btn btn-sm btn-outline-danger py-0 px-1" @click.stop="remove(r)"><i class="bi bi-trash"></i></button></td>
                </tr>
                <tr v-if="filtered.length === 0"><td :colspan="showLoaiColumn ? 6 : 5" class="text-center text-muted py-4">Chưa có dữ liệu</td></tr>
              </tbody>
            </table>
          </div>
          <!-- type filter buttons (5_pannel: Tất cả / Màu sắc / Hãng / ...) -->
          <div class="tt-filters d-flex flex-column gap-1">
            <button class="btn btn-sm" :class="filterKey === 'all' ? 'btn-success' : 'btn-outline-secondary'" @click="pickFilter('all')">Tất cả</button>
            <button v-for="t in ATTR_TYPES" :key="t.key" class="btn btn-sm text-nowrap" :class="filterKey === t.key ? 'btn-success' : 'btn-outline-secondary'" @click="pickFilter(t.key)">{{ t.label }}</button>
          </div>
        </div>
      </div></div>

      <!-- form -->
      <div class="card"><div class="card-body">
        <h6 class="fw-bold mb-3">{{ form.id ? 'Sửa' : 'Thêm' }} thuộc tính</h6>
        <div class="mb-2">
          <label class="form-label small mb-1">Loại thuộc tính</label>
          <select class="form-select form-select-sm" v-model="form.loai" :disabled="!!form.id">
            <option v-for="t in ATTR_TYPES" :key="t.key" :value="t.key">{{ t.label }}</option>
          </select>
        </div>
        <!-- Mã luôn do server sinh ("TH"/"CL"/"KD"… + id). Ô nhập cũ là ảo: giá trị gõ
             vào bị service create() ghi đè, người dùng không hề biết. -->
        <div class="mb-2"><label class="form-label small mb-1">Mã</label>
          <p class="ma-static" :class="{ 'chua-co': !form.ma }">{{ form.ma || 'Tự sinh khi lưu' }}</p>
        </div>
        <div class="mb-2"><label class="form-label small mb-1">Tên thuộc tính *</label><input class="form-control form-control-sm" v-model="form.ten"></div>
        <div class="mb-2" v-if="formType.moTa"><label class="form-label small mb-1">Mô tả</label><textarea class="form-control form-control-sm" rows="3" maxlength="300" v-model="form.moTa"></textarea></div>
        <div class="mb-3">
          <label class="form-label small mb-1 d-block">Trạng thái</label>
          <div class="form-check form-check-inline"><input class="form-check-input" type="radio" :value="true" v-model="form.trangThai" id="tt-on"><label class="form-check-label" for="tt-on">Hiện</label></div>
          <div class="form-check form-check-inline"><input class="form-check-input" type="radio" :value="false" v-model="form.trangThai" id="tt-off"><label class="form-check-label" for="tt-off">Ẩn</label></div>
        </div>
        <div class="d-flex gap-2">
          <button class="btn btn-outline-secondary flex-fill" @click="reset">Làm mới</button>
          <button class="btn btn-success flex-fill" @click="taoMoi">Tạo mới</button>
          <button class="btn btn-success flex-fill" :disabled="!form.id" @click="save">Lưu (Sửa)</button>
        </div>
      </div></div>
    </div>
  </div>
</template>

<style scoped>
/* Mã do server sinh: hiển thị dạng chữ, không phải ô nhập bị vô hiệu hoá. */
.ma-static { margin: 0; padding: 6px 0; font-size: 13px; font-weight: 600; letter-spacing: .3px; }
.ma-static.chua-co { font-weight: 400; opacity: .7; font-style: italic; }

.tt-grid { display: grid; grid-template-columns: minmax(0, 1fr) 340px; gap: 16px; align-items: start; }
.tt-table thead th { position: sticky; top: 0; background: var(--c-primary, #0B895A); color: #fff; font-size: 12px; white-space: nowrap; }
.tt-table tbody tr.sel > td { background: var(--c-primary-subtle, #E7F4EF); }
.tt-filters { min-width: 132px; }
.badge.bg-primary-subtle { background: #E7F4EF; }
.text-primary-emphasis { color: #0A6E48; }
@media (max-width: 992px) { .tt-grid { grid-template-columns: 1fr; } }
</style>
