<script setup>
// Lưới phân quyền: dòng = nhân viên, cột = màn hình (SCREENS). Quyền hiệu lực nằm ở
// bảng nhan_vien_quyen; vai trò chỉ là template để chép sang.
// Dòng ADMIN khóa lại: vai trò toàn quyền tính động ở server, không đọc rows.
import { ref, computed, onMounted } from 'vue'
import { nhanVienApi } from '../../api/nhanVien'
import { SCREENS } from '../../config/screens'
import { useToast } from '../../composables/useToast'
import { useAuth } from '../../composables/useAuth'

const { notify } = useToast()
const { user } = useAuth()

const rows = ref([])          // [{ idNhanVien, ma, ten, vaiTro, toanQuyen, quyen: Set }]
const kw = ref('')
const dangTai = ref(false)
const dangLuu = ref(false)
const goc = ref({})           // ảnh chụp lúc tải, để biết dòng nào bẩn

function snapshot(list) {
  const m = {}
  list.forEach(r => { m[r.idNhanVien] = [...r.quyen].sort().join(',') })
  return m
}
function isDirty(r) {
  return goc.value[r.idNhanVien] !== [...r.quyen].sort().join(',')
}
const dirtyRows = computed(() => rows.value.filter(r => !r.toanQuyen && isDirty(r)))

const filtered = computed(() => {
  const k = kw.value.trim().toLowerCase()
  return rows.value.filter(r => !k
    || (r.ma || '').toLowerCase().includes(k)
    || (r.ten || '').toLowerCase().includes(k)
    || (r.vaiTro || '').toLowerCase().includes(k))
})

async function load() {
  dangTai.value = true
  try {
    const data = await nhanVienApi.bangQuyen()
    rows.value = data.map(d => ({ ...d, quyen: new Set(d.quyen || []) }))
    goc.value = snapshot(rows.value)
  } catch (e) {
    notify('Không tải được bảng phân quyền (backend offline?)', 'warning')
    rows.value = []
  } finally {
    dangTai.value = false
  }
}

function has(r, key) { return r.toanQuyen || r.quyen.has(key) }
function toggle(r, key) {
  if (r.toanQuyen) return
  const s = new Set(r.quyen)
  s.has(key) ? s.delete(key) : s.add(key)
  r.quyen = s
}
function toggleRow(r) {
  if (r.toanQuyen) return
  const all = SCREENS.every(s => r.quyen.has(s.key))
  r.quyen = all ? new Set() : new Set(SCREENS.map(s => s.key))
}

async function luuTatCa() {
  const ds = dirtyRows.value
  if (!ds.length) { notify('Không có thay đổi nào để lưu', 'info'); return }
  dangLuu.value = true
  let ok = 0
  try {
    for (const r of ds) {
      await nhanVienApi.luuQuyen(r.idNhanVien, [...r.quyen])
      ok++
    }
    goc.value = snapshot(rows.value)
    notify(`Đã lưu quyền cho ${ok} nhân viên`, 'success')
    // Quyền của chính mình chỉ nạp lại lúc đăng nhập — nói rõ kẻo tưởng UI hỏng.
    if (ds.some(r => user.value && r.idNhanVien === user.value.id)) {
      notify('Bạn vừa đổi quyền của chính mình — đăng nhập lại để áp dụng', 'info')
    }
  } catch (e) {
    notify(e?.response?.data?.message || 'Lưu thất bại', 'warning')
    await load()
  } finally {
    dangLuu.value = false
  }
}

async function apTemplate(r) {
  if (!confirm(`Áp lại template vai trò "${r.vaiTro}" cho ${r.ten}? Phần quyền mở rộng riêng sẽ mất.`)) return
  try {
    const d = await nhanVienApi.apTemplate(r.idNhanVien)
    r.quyen = new Set(d.quyen || [])
    goc.value[r.idNhanVien] = [...r.quyen].sort().join(',')
    notify(`Đã áp template vai trò cho ${r.ten}`, 'success')
  } catch (e) {
    notify(e?.response?.data?.message || 'Áp template thất bại', 'warning')
  }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="d-flex gap-2 mb-3 flex-wrap align-items-center">
      <input class="form-control form-control-sm" style="max-width:260px" v-model="kw"
             placeholder="Tìm mã / tên / vai trò...">
      <span class="badge bg-light text-dark border">{{ filtered.length }} nhân viên</span>
      <span v-if="dirtyRows.length" class="badge" style="background:#FFF4E0;color:#9A6700">
        {{ dirtyRows.length }} dòng chưa lưu
      </span>
      <button class="btn btn-sm btn-success ms-auto px-3" :disabled="dangLuu || !dirtyRows.length" @click="luuTatCa">
        <i class="bi bi-save"></i> {{ dangLuu ? 'Đang lưu...' : 'Lưu thay đổi' }}
      </button>
    </div>

    <div class="pq-wrap">
      <table class="table table-sm align-middle mb-0 pq-table">
        <thead>
          <tr>
            <th class="fx fx-1">Mã NV</th>
            <th class="fx fx-2">Tên nhân viên</th>
            <th class="fx fx-3">Vai trò</th>
            <th v-for="s in SCREENS" :key="s.key" class="th-screen" :title="s.key">
              <i class="bi d-block mb-1" :class="s.icon"></i>{{ s.label }}
            </th>
            <th class="text-center">Template</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in filtered" :key="r.idNhanVien" :class="{ dirty: !r.toanQuyen && isDirty(r), admin: r.toanQuyen }">
            <td class="fx fx-1 fw-semibold">{{ r.ma }}</td>
            <td class="fx fx-2">
              {{ r.ten }}
              <span v-if="user && r.idNhanVien === user.id" class="badge bg-light text-dark border ms-1">bạn</span>
            </td>
            <td class="fx fx-3">
              <span class="badge-role" :class="{ full: r.toanQuyen }">{{ r.vaiTro || '—' }}</span>
            </td>
            <td v-for="s in SCREENS" :key="s.key" class="text-center">
              <button class="cb" :class="{ on: has(r, s.key), lock: r.toanQuyen }"
                      :disabled="r.toanQuyen"
                      :title="r.toanQuyen ? 'Vai trò quản trị luôn có toàn quyền' : s.label"
                      @click="toggle(r, s.key)">
                <i v-if="has(r, s.key)" class="bi bi-check-lg"></i>
              </button>
            </td>
            <td class="text-center">
              <button class="btn btn-sm btn-outline-secondary py-0 px-2" :disabled="r.toanQuyen"
                      title="Chép lại bộ quyền mặc định của vai trò" @click="apTemplate(r)">
                <i class="bi bi-arrow-counterclockwise"></i>
              </button>
              <button class="btn btn-sm btn-outline-secondary py-0 px-2 ms-1" :disabled="r.toanQuyen"
                      title="Chọn / bỏ tất cả" @click="toggleRow(r)">
                <i class="bi bi-check2-square"></i>
              </button>
            </td>
          </tr>
          <tr v-if="!filtered.length">
            <td :colspan="SCREENS.length + 4" class="text-center text-muted py-4">
              {{ dangTai ? 'Đang tải...' : 'Không có nhân viên' }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="small text-muted mt-2">
      <i class="bi bi-info-circle"></i>
      Vai trò <b>Quản trị</b> luôn toàn quyền (tính động, không lưu ở bảng phân quyền) — thêm màn hình mới
      admin vẫn vào được. Các vai trò khác: tick ô = cho vào màn đó; nút
      <i class="bi bi-arrow-counterclockwise"></i> chép lại bộ quyền mặc định của vai trò.
    </div>
  </div>
</template>

<style scoped>
.pq-wrap { overflow: auto; max-height: 560px; border: 1px solid #e5e9ef; border-radius: 8px; }
.pq-table { min-width: 900px; }
.pq-table thead th {
  position: sticky; top: 0; z-index: 3;
  background: var(--c-primary, #0B895A); color: #fff;
  font-size: 11px; font-weight: 600; text-align: center; white-space: nowrap; vertical-align: middle;
}
.pq-table .th-screen { min-width: 76px; font-size: 10.5px; line-height: 1.2; }
.pq-table .th-screen i { font-size: 13px; }
/* đóng băng 3 cột trái để cuộn ngang vẫn biết đang sửa ai */
.fx { position: sticky; background: #fff; z-index: 2; text-align: left !important; }
.fx-1 { left: 0; min-width: 78px; }
.fx-2 { left: 78px; min-width: 160px; }
.fx-3 { left: 238px; min-width: 120px; box-shadow: 2px 0 0 #e5e9ef; }
.pq-table thead .fx { z-index: 4; background: var(--c-primary, #0B895A); }
tbody tr.dirty .fx, tbody tr.dirty > td { background: #FFFBF0; }
tbody tr.admin .fx, tbody tr.admin > td { background: #F4F7FA; }
.badge-role { font-size: 11px; font-weight: 600; padding: 3px 8px; border-radius: 999px; background: #eceff3; color: #556; }
.badge-role.full { background: #E7F4EF; color: #0A6E48; }
.cb {
  width: 26px; height: 26px; border-radius: 7px; border: 2px solid #c3ccd6;
  background: #fff; color: #fff; display: inline-flex; align-items: center; justify-content: center;
  cursor: pointer; padding: 0; font-size: 14px;
}
.cb.on { background: var(--c-primary, #0B895A); border-color: var(--c-primary, #0B895A); }
.cb.lock { opacity: .55; cursor: not-allowed; }
.cb:disabled { cursor: not-allowed; }
</style>
