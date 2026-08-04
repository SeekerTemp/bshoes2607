<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppButton from '../components/ui/AppButton.vue'
import ConfirmDialog from '../components/ui/ConfirmDialog.vue'
import { getLogs, downloadLogs, clearLogs } from '../utils/logger'
import { useAuth } from '../composables/useAuth'

const router = useRouter()
const { user, logout: doLogoutAuth } = useAuth()
// Show who is actually signed in, not a hardcoded 'admin'.
const nguoiDung = computed(() => user.value?.ten || user.value?.ma || '—')
const vaiTro = computed(() => user.value?.vaiTro || '—')

const confirmOpen = ref(false)

function logout() {
  confirmOpen.value = true
}

async function doLogout() {
  confirmOpen.value = false
  // Actually end the session — before this it only navigated away, leaving the
  // user (and their token) in localStorage, so going back in re-entered the app.
  await doLogoutAuth()
  router.push('/login')
}

/* ===== Nhật ký sự kiện (log) ===== */
const logs = ref([])

function refreshLogs() {
  logs.value = getLogs()
}
onMounted(refreshLogs)

const logCount = computed(() => logs.value.length)
const recentLogs = computed(() => logs.value.slice(-50).reverse())

function formatTime(iso) {
  try {
    return new Date(iso).toLocaleTimeString('vi-VN', { hour12: false })
  } catch {
    return iso
  }
}

function levelClass(level) {
  if (level === 'error') return 'log-badge log-badge--danger'
  if (level === 'warn') return 'log-badge log-badge--warning'
  return 'log-badge log-badge--info'
}

function onDownload() {
  downloadLogs()
}

const clearConfirmOpen = ref(false)
function askClear() {
  clearConfirmOpen.value = true
}
function doClear() {
  clearLogs()
  clearConfirmOpen.value = false
  refreshLogs()
}
</script>

<template>
  <AppShell>
    <PageHeader title="Hệ thống" />

    <div style="max-width: 520px">
      <div class="card">
        <div class="card-body">
          <p class="mb-1"><b>Người dùng:</b> {{ nguoiDung }}</p>
          <p class="mb-1"><b>Vai trò:</b> {{ vaiTro }}</p>
          <p class="mb-3"><b>Phiên bản:</b> BShoes 1.0 (demo)</p>
          <AppButton variant="danger" @click="logout">Đăng xuất</AppButton>
        </div>
      </div>
    </div>

    <div class="card mt-4">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-2">
          <h6 class="fw-bold mb-0">Nhật ký sự kiện (log)</h6>
          <div class="d-flex gap-2">
            <AppButton size="sm" variant="outline-secondary" icon="download" @click="onDownload">Tải file log</AppButton>
            <AppButton size="sm" variant="outline-danger" icon="trash" @click="askClear">Xoá log</AppButton>
          </div>
        </div>

        <p class="small text-muted mb-3">
          Đang lưu <b>{{ logCount }}</b> mục (tối đa 400, tự động xoá mục cũ nhất). Log cũng được tự động gửi lên máy
          chủ để lưu chung vào file log server. Nếu máy chủ không truy cập được, hãy bấm "Tải file log" và gửi file
          đó kèm mô tả lỗi để được hỗ trợ.
        </p>

        <div class="log-table-wrap">
          <table class="table table-sm align-middle mb-0 log-table">
            <thead>
              <tr>
                <th style="width:90px">Giờ</th>
                <th style="width:80px">Mức</th>
                <th style="width:90px">Nhóm</th>
                <th>Thông điệp</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(l, i) in recentLogs" :key="i">
                <td class="text-muted">{{ formatTime(l.t) }}</td>
                <td><span :class="levelClass(l.level)">{{ l.level }}</span></td>
                <td class="text-muted">{{ l.category }}</td>
                <td class="log-msg">{{ l.message }}</td>
              </tr>
              <tr v-if="!recentLogs.length">
                <td colspan="4" class="text-center text-muted py-4">Chưa có log nào</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <ConfirmDialog v-model:open="confirmOpen" title="Đăng xuất" message="Đăng xuất khỏi hệ thống?"
                   confirm-text="Đăng xuất" @confirm="doLogout" />
    <ConfirmDialog
      v-model:open="clearConfirmOpen"
      title="Xoá log"
      message="Xoá toàn bộ nhật ký sự kiện đang lưu trên máy này?"
      @confirm="doClear"
    />
  </AppShell>
</template>

<style scoped>
.log-table-wrap {
  max-height: 420px;
  overflow: auto;
  border: 1px solid var(--c-border);
  border-radius: 8px;
}
.log-table thead th {
  position: sticky;
  top: 0;
  background: #fafbfc;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: .04em;
  color: var(--c-text-muted);
  white-space: nowrap;
  z-index: 1;
}
.log-msg {
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: .8rem;
  word-break: break-word;
}
.log-badge {
  display: inline-block;
  padding: .2em .6em;
  border-radius: 999px;
  font-size: .72rem;
  font-weight: 600;
  text-transform: uppercase;
}
.log-badge--danger { background: var(--c-danger-subtle); color: var(--c-danger-text); }
.log-badge--warning { background: var(--c-warning-subtle); color: var(--c-warning-text); }
.log-badge--info { background: var(--c-info-subtle); color: var(--c-info-text); }
</style>
