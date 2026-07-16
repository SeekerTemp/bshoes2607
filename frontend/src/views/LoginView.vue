<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import FormField from '../components/ui/FormField.vue'
import AppButton from '../components/ui/AppButton.vue'
import { useAuth } from '../composables/useAuth'

const router = useRouter()
const route = useRoute()
const { login: doLogin, loginDemo } = useAuth()
const username = ref('')
const password = ref('')
const err = ref('')
const busy = ref(false)

function go() { router.push(route.query.redirect || '/') }

async function login() {
  err.value = ''
  if (!username.value) { err.value = 'Nhập tài khoản'; return }
  busy.value = true
  try {
    await doLogin(username.value, password.value)
    go()
  } catch (e) {
    if (e?.response?.status === 401) err.value = 'Sai tài khoản hoặc mật khẩu'
    else err.value = 'Không kết nối được máy chủ — dùng "Vào demo" bên dưới'
  } finally { busy.value = false }
}
function demo() { loginDemo(); go() }
</script>

<template>
  <div class="login-page d-flex align-items-center justify-content-center">
    <div class="login-card">
      <h4 class="text-center fw-bold mb-4" style="color: var(--c-primary)">Đăng nhập</h4>

      <form @submit.prevent="login">
        <FormField label="Tài khoản">
          <input v-model="username" type="text" class="form-control" placeholder="vd: vanan" />
        </FormField>

        <FormField label="Mật khẩu">
          <input v-model="password" type="password" class="form-control" placeholder="vd: 123456" />
        </FormField>

        <div v-if="err" class="alert alert-danger py-2 small mb-2">{{ err }}</div>

        <AppButton type="submit" variant="primary" class="w-100 mt-2" :disabled="busy">
          {{ busy ? 'Đang đăng nhập...' : 'Đăng nhập' }}
        </AppButton>
      </form>

      <button class="btn btn-outline-secondary w-100 mt-2" @click="demo">Vào demo (Admin)</button>

      <p class="text-muted small text-center mt-3 mb-0">
        Tài khoản mẫu: <b>vanan / 123456</b> (ADMIN). Quyền theo vai trò được cấu hình ở màn Phân quyền.
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
