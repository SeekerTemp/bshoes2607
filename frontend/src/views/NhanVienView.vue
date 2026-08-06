<script setup>
import { ref, onMounted } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import DemoDataBanner from '../components/ui/DemoDataBanner.vue'
import SearchBar from '../components/ui/SearchBar.vue'
import ConfirmDialog from '../components/ui/ConfirmDialog.vue'
import AppSelect from '../components/ui/AppSelect.vue'
import InspectorPanel from '../components/ui/InspectorPanel.vue'
import InspectorField from '../components/ui/InspectorField.vue'
import PhanQuyenPanel from '../components/panels/PhanQuyenPanel.vue'
import VaiTroPanel from '../components/panels/VaiTroPanel.vue'
import { useNhanVien } from '../composables/useNhanVien'
import { useToast } from '../composables/useToast'
import { crudErrorMessage } from '../composables/useCrud'
import { vaiTroApi } from '../api/vaiTro'

const { keyword, filtered, add, update, remove, load, isDemo } = useNhanVien()
const { notify } = useToast()

const tab = ref('ds')   // 'ds' | 'quyen' | 'vaitro'

const gioiTinhOptions = ['Nam', 'Nữ']
const vaiTroOptions = ref([])

onMounted(async () => {
  try {
    const list = await vaiTroApi.findAll()
    vaiTroOptions.value = list.map(v => ({ value: v.id, label: v.ten }))
  } catch (e) {
    console.warn('Không tải được danh sách vai trò', e)
  }
})

// Chức vụ KHÔNG còn là ô nhập tự do: vai trò là nguồn sự thật duy nhất, cột "Chức vụ"
// hiển thị tên vai trò. Hai ô nhập cùng nghĩa trước đây chỉ tạo dữ liệu mâu thuẫn.
function tenVaiTro(row) {
  const opt = vaiTroOptions.value.find(v => v.value === row.idVaiTro)
  return opt?.label || row.vaiTro || '—'
}

function blankForm() {
  return { id: null, ma: '', ten: '', taiKhoan: '', email: '', sdt: '', cccd: '', gioiTinh: 'Nam', idVaiTro: null, matKhau: '', trangThai: true }
}

const form = ref(blankForm())
const selectedId = ref(null)
const confirmOpen = ref(false)
const target = ref(null)

function selectRow(row) {
  selectedId.value = row.id
  form.value = { ...blankForm(), ...JSON.parse(JSON.stringify(row)) }
  form.value.matKhau = ''   // để trống = giữ nguyên mật khẩu hiện tại
}

// "Làm mới" = XOÁ TRẮNG inspector (trước đây nó nạp lại dòng đang chọn).
function lamMoi() {
  selectedId.value = null
  form.value = blankForm()
}

// Bắt buộc: SĐT và CCCD (ngoài tên / tài khoản vốn đã cần để đăng nhập).
//
// `batBuocMatKhau` do người gọi quyết định, không suy ra từ form.id: tạo mới TỪ một
// nhân viên đang chọn thì form.id vẫn còn nhưng đây là bản ghi mới nên vẫn cần mật khẩu.
function loiForm({ batBuocMatKhau }) {
  if (!form.value.ten?.trim()) return 'Nhập tên nhân viên'
  if (!form.value.taiKhoan?.trim()) return 'Nhập tài khoản đăng nhập'
  if (!form.value.sdt?.trim()) return 'Số điện thoại là bắt buộc'
  if (!/^0\d{8,10}$/.test(form.value.sdt.trim())) return 'Số điện thoại không hợp lệ (bắt đầu bằng 0, 9–11 chữ số)'
  if (!form.value.cccd?.trim()) return 'CCCD là bắt buộc'
  if (!/^\d{9,12}$/.test(form.value.cccd.trim())) return 'CCCD phải là 9–12 chữ số'
  if (batBuocMatKhau && !form.value.matKhau) return 'Nhập mật khẩu cho nhân viên mới'
  return ''
}

/*
 * Tạo mới và Lưu (Sửa) là hai nút riêng. Một nút tự đổi nghĩa theo trạng thái chọn
 * dòng khiến thao tác "chọn nhân viên, đổi tên, tạo thành nhân viên mới" không thể
 * thực hiện — nút lặng lẽ trở thành Lưu (Sửa) và ghi đè bản gốc.
 */

/** Luôn TẠO MỚI từ nội dung inspector hiện tại, bỏ id + mã. */
async function taoMoi() {
  const loi = loiForm({ batBuocMatKhau: true })
  if (loi) { notify(loi, 'warning'); return }
  try {
    await add({ ...form.value, id: null, ma: '' })
    notify('Đã thêm nhân viên', 'success')
    lamMoi()
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

/** Chỉ CẬP NHẬT dòng đang chọn. */
async function luuSua() {
  if (!form.value.id) { notify('Chọn nhân viên trong danh sách để sửa', 'warning'); return }
  const loi = loiForm({ batBuocMatKhau: false })
  if (loi) { notify(loi, 'warning'); return }
  try {
    await update(form.value)
    notify('Đã cập nhật nhân viên', 'success')
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}

function askDelete(row) {
  target.value = row
  confirmOpen.value = true
}

async function doDelete() {
  try {
    await remove(target.value.id)
    confirmOpen.value = false
    if (target.value.id === selectedId.value) lamMoi()
    notify('Đã xoá', 'success')
  } catch (e) {
    notify(crudErrorMessage(e), 'warning')
  }
}
</script>

<template>
  <AppShell>
    <DemoDataBanner v-if="isDemo" what="danh sách nhân viên" @retry="load" />
    <PageHeader title="Nhân viên" />

    <div class="nv-tabs mb-3">
      <button class="nv-tab" :class="{ active: tab === 'ds' }" @click="tab = 'ds'">Danh sách</button>
      <button class="nv-tab" :class="{ active: tab === 'quyen' }" @click="tab = 'quyen'">Phân quyền</button>
      <button class="nv-tab" :class="{ active: tab === 'vaitro' }" @click="tab = 'vaitro'">Vai trò (template)</button>
    </div>

    <!-- Master-detail: bảng bên trái, inspector bên phải (bỏ modal) -->
    <div v-show="tab === 'ds'" class="nv-grid">
      <div class="nv-master">
        <SearchBar v-model="keyword" class="mb-2" placeholder="Tìm theo mã / tên / tài khoản..." />
        <div class="table-scroll">
          <table class="table table-sm table-hover align-middle mb-0 nv-table">
            <thead><tr>
              <th class="text-center">STT</th><th>Mã NV</th><th>Tên</th><th>Tài khoản</th>
              <th>Email</th><th>SĐT</th><th>CCCD</th><th>Chức vụ (vai trò)</th><th>Trạng thái</th><th></th>
            </tr></thead>
            <tbody>
              <tr v-for="(n, i) in filtered" :key="n.id" :class="{ 'row-active': n.id === selectedId }" @click="selectRow(n)">
                <td class="text-center">{{ i + 1 }}</td>
                <td class="fw-medium">{{ n.ma }}</td><td>{{ n.ten }}</td><td>{{ n.taiKhoan }}</td>
                <td>{{ n.email }}</td><td>{{ n.sdt }}</td><td>{{ n.cccd }}</td>
                <td>{{ tenVaiTro(n) }}</td>
                <td>{{ n.trangThai ? 'Hoạt động' : 'Ngừng' }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-danger py-0 px-2" @click.stop="askDelete(n)" title="Xoá">
                    <i class="bi bi-trash"></i>
                  </button>
                </td>
              </tr>
              <tr v-if="!filtered.length"><td colspan="10" class="text-center text-muted py-4">Không có nhân viên</td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <InspectorPanel :tab-label="form.id ? 'Sửa nhân viên' : 'Thêm nhân viên'" title="Thông tin nhân viên">
        <!-- Mã do server sinh ("NV" + id) khi tạo, nên hiển thị dạng chữ chứ không phải ô nhập. -->
        <InspectorField label="Mã NV">
          <p class="ins-static" :class="{ 'chua-co': !form.ma }">{{ form.ma || 'Tự sinh khi lưu' }}</p>
        </InspectorField>
        <InspectorField label="Tên" required>
          <input class="form-control form-control-sm" v-model="form.ten">
        </InspectorField>
        <InspectorField label="Tài khoản" required>
          <input class="form-control form-control-sm" v-model="form.taiKhoan">
        </InspectorField>
        <InspectorField label="Email">
          <input type="email" class="form-control form-control-sm" v-model="form.email">
        </InspectorField>
        <InspectorField label="Số điện thoại" required>
          <input class="form-control form-control-sm" v-model="form.sdt" :class="{ 'is-invalid': !form.sdt }" placeholder="0xxxxxxxxx">
        </InspectorField>
        <InspectorField label="CCCD" required>
          <input class="form-control form-control-sm" v-model="form.cccd" :class="{ 'is-invalid': !form.cccd }" placeholder="9–12 chữ số">
        </InspectorField>
        <InspectorField label="Giới tính">
          <AppSelect v-model="form.gioiTinh" :options="gioiTinhOptions" />
        </InspectorField>
        <!-- Không còn ô "Chức vụ" nhập tay: vai trò quyết định chức vụ. -->
        <InspectorField label="Vai trò">
          <AppSelect v-model.number="form.idVaiTro" :options="vaiTroOptions" />
        </InspectorField>
        <!-- Bắt buộc khi Tạo mới (kể cả đang chọn một nhân viên), để trống khi Lưu (Sửa)
             nghĩa là giữ nguyên mật khẩu cũ. -->
        <InspectorField label="Mật khẩu" required>
          <input type="password" class="form-control form-control-sm" v-model="form.matKhau"
                 :placeholder="form.id ? 'Để trống nếu không đổi' : ''">
        </InspectorField>
        <InspectorField label="Trạng thái">
          <label class="nv-radio"><input type="radio" :value="true" v-model="form.trangThai"> Hoạt động</label>
          <label class="nv-radio ms-3"><input type="radio" :value="false" v-model="form.trangThai"> Ngừng</label>
        </InspectorField>

        <template #actions>
          <button class="btn btn-success w-100" @click="taoMoi">Tạo mới</button>
          <button class="btn btn-success w-100" :disabled="!form.id" @click="luuSua">Lưu (Sửa)</button>
          <button class="btn btn-success w-100" @click="lamMoi">Làm mới</button>
          <button class="btn btn-outline-danger w-100" :disabled="!form.id" @click="askDelete(form)">Xoá</button>
        </template>
      </InspectorPanel>
    </div>

    <!-- v-if để panel chỉ gọi API khi thực sự mở tab -->
    <PhanQuyenPanel v-if="tab === 'quyen'" />
    <VaiTroPanel v-if="tab === 'vaitro'" />

    <ConfirmDialog v-model:open="confirmOpen" :message="`Xoá nhân viên ${target?.ma}?`" @confirm="doDelete" />
  </AppShell>
</template>

<style scoped>
/* cùng pattern tab với KhachHangView / SanPhamView */
.nv-tabs { display: flex; gap: 4px; }
.nv-tab {
  border: 1px solid #e5e9ef; background: #eef1f4; color: #6b7280;
  border-radius: 8px; padding: 8px 20px; font-size: 13px; font-weight: 600; cursor: pointer;
}
.nv-tab.active { background: var(--c-primary, #0B895A); color: #fff; border-color: var(--c-primary, #0B895A); }

.nv-grid { display: grid; grid-template-columns: minmax(0, 1fr) 380px; gap: 16px; align-items: start; }
.nv-master { min-width: 0; }
.table-scroll { overflow: auto; max-height: 560px; border: 1px solid var(--c-border); border-radius: var(--radius-sm); }
.nv-table { min-width: 860px; }
.nv-table thead th {
  position: sticky; top: 0; z-index: 1;
  background: var(--c-primary); color: #fff; font-weight: 600; font-size: 12px; white-space: nowrap;
}
.nv-table tbody td { font-size: 13px; }
.nv-table tbody tr { cursor: pointer; }
.nv-table tbody tr.row-active > td { background: var(--c-primary); color: #fff; }
.nv-radio { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; cursor: pointer; }

@media (max-width: 992px) { .nv-grid { grid-template-columns: 1fr; } }
</style>
