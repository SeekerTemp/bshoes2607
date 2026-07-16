<script setup>
// Phân quyền: configure which UI screens each vai trò (role) can access.
// Each nhân viên inherits their role's permission set (vai_tro.quyen).
import { ref, computed, onMounted } from 'vue'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import { vaiTroApi } from '../api/vaiTro'
import { SCREENS } from '../config/screens'
import { useToast } from '../composables/useToast'
import { useAuth } from '../composables/useAuth'

const { notify } = useToast()
const { user } = useAuth()
const roles = ref([])
const selected = ref(null)
const checked = ref(new Set())

const allKeys = SCREENS.map(s => s.key)
const isAll = computed(() => allKeys.every(k => checked.value.has(k)))

async function load() {
  try { roles.value = await vaiTroApi.quyen() }
  catch (e) { notify('Không tải được vai trò (backend offline?)', 'warning'); roles.value = [] }
  if (roles.value.length) selectRole(roles.value[0])
}
function selectRole(r) {
  selected.value = r
  const q = (r.quyen || '').trim()
  checked.value = new Set(q === '*' ? allKeys : q.split(',').map(s => s.trim()).filter(Boolean))
}
function toggle(key) {
  const s = new Set(checked.value)
  s.has(key) ? s.delete(key) : s.add(key)
  checked.value = s
}
function toggleAll() {
  checked.value = isAll.value ? new Set() : new Set(allKeys)
}
async function save() {
  if (!selected.value) return
  const quyen = isAll.value ? '*' : allKeys.filter(k => checked.value.has(k)).join(',')
  try {
    await vaiTroApi.updateQuyen(selected.value.id, quyen)
    selected.value.quyen = quyen
    notify('Đã lưu quyền cho ' + selected.value.ten, 'success')
    if (user.value && user.value.idVaiTro === selected.value.id) {
      notify('Bạn vừa đổi quyền vai trò của chính mình — đăng nhập lại để áp dụng', 'info')
    }
  } catch (e) { notify('Lưu thất bại', 'warning') }
}
onMounted(load)
</script>

<template>
  <AppShell>
    <PageHeader title="Phân quyền nhân viên" />
    <div class="pq-grid">
      <!-- roles -->
      <div class="card"><div class="card-body">
        <h6 class="fw-bold mb-3">Vai trò</h6>
        <div class="list-group">
          <button v-for="r in roles" :key="r.id" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                  :class="{ active: selected && r.id === selected.id }" @click="selectRole(r)">
            <span><i class="bi bi-person-badge me-2"></i>{{ r.ten }}</span>
            <span class="badge bg-light text-dark">{{ r.quyen === '*' ? 'Tất cả' : (r.quyen || '').split(',').filter(Boolean).length + ' màn' }}</span>
          </button>
          <div v-if="roles.length === 0" class="text-muted text-center py-3">Không có vai trò</div>
        </div>
      </div></div>

      <!-- screens for selected role -->
      <div class="card"><div class="card-body">
        <div v-if="!selected" class="text-muted text-center py-5"><i class="bi bi-shield-lock fs-1 d-block mb-2 opacity-50"></i>Chọn một vai trò để phân quyền</div>
        <div v-else>
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0">Màn hình cho phép truy cập — <span style="color:#0B895A">{{ selected.ten }}</span></h6>
            <div class="form-check form-switch">
              <input class="form-check-input" type="checkbox" id="all" :checked="isAll" @change="toggleAll">
              <label class="form-check-label small" for="all">Chọn tất cả</label>
            </div>
          </div>
          <div class="row row-cols-1 row-cols-md-2 g-2">
            <div class="col" v-for="s in SCREENS" :key="s.key">
              <label class="pq-item" :class="{ on: checked.has(s.key) }">
                <input type="checkbox" :checked="checked.has(s.key)" @change="toggle(s.key)">
                <i class="bi" :class="s.icon"></i>
                <span>{{ s.label }}</span>
                <small class="text-muted ms-auto">{{ s.key }}</small>
              </label>
            </div>
          </div>
          <div class="d-flex justify-content-end mt-3">
            <button class="btn btn-success px-4" @click="save"><i class="bi bi-save"></i> Lưu quyền</button>
          </div>
        </div>
      </div></div>
    </div>
  </AppShell>
</template>

<style scoped>
.pq-grid { display: grid; grid-template-columns: 300px minmax(0, 1fr); gap: 16px; align-items: start; }
.list-group-item.active { background: var(--c-primary, #0B895A); border-color: var(--c-primary, #0B895A); }
.pq-item { display: flex; align-items: center; gap: 8px; width: 100%; padding: 8px 12px; border: 1px solid #e5e9ef; border-radius: 8px; cursor: pointer; font-size: 14px; }
.pq-item.on { border-color: #0B895A; background: #E7F4EF; }
.pq-item input { accent-color: #0B895A; }
.pq-item i { color: #0B895A; }
@media (max-width: 992px) { .pq-grid { grid-template-columns: 1fr; } }
</style>
