<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '../components/layout/AppShell.vue'
import PageHeader from '../components/ui/PageHeader.vue'
import AppButton from '../components/ui/AppButton.vue'
import ConfirmDialog from '../components/ui/ConfirmDialog.vue'

const router = useRouter()
const nguoiDung = 'admin'
const vaiTro = 'ADMIN'

const confirmOpen = ref(false)

function logout() {
  confirmOpen.value = true
}

function doLogout() {
  confirmOpen.value = false
  router.push('/login')
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

    <ConfirmDialog v-model:open="confirmOpen" title="Đăng xuất" message="Đăng xuất?" @confirm="doLogout" />
  </AppShell>
</template>
