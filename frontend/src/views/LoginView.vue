<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import FormField from '../components/ui/FormField.vue'
import AppButton from '../components/ui/AppButton.vue'

const router = useRouter()
const username = ref('')
const password = ref('')

function login() {
  router.push('/')
}
</script>

<template>
  <div class="login-page d-flex align-items-center justify-content-center">
    <div class="login-card">
      <h4 class="text-center fw-bold mb-4" style="color: var(--c-primary)">Đăng nhập</h4>

      <form @submit.prevent="login">
        <FormField label="Tài khoản">
          <input v-model="username" type="text" class="form-control" placeholder="Nhập tài khoản" />
        </FormField>

        <FormField label="Mật khẩu">
          <input v-model="password" type="password" class="form-control" placeholder="Nhập mật khẩu" />
        </FormField>

        <AppButton type="submit" variant="primary" class="w-100 mt-2">Đăng nhập</AppButton>
      </form>

      <p class="text-muted small text-center mt-3 mb-0">
        Demo: vai trò ADMIN vào thẳng màn hình Thống kê.
      </p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  background: var(--c-bg);
}

.login-card {
  width: 380px;
  max-width: 90vw;
  background: var(--c-surface);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: var(--sp-6) var(--sp-5);
}
</style>
