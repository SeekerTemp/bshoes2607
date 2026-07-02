# BShoes Vue SPA Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development. Steps use checkbox (`- [ ]`) syntax. **Review checkpoint after Task 2 (design system) before building screens.**

**Goal:** Build a Vite + Vue 3 single-page app in `frontend/` that reproduces all 9 BShoes screens on a shared, documented **design system** (Bootstrap 5 themed green), running on mock data with an API seam ready to point at the Phase 2 `/api/...` endpoints.

**Architecture:** Vite + Vue 3 `<script setup>` SFCs + Vue Router. A design-system layer (SCSS tokens + Bootstrap theme + reusable `components/ui` primitives) is built and reviewed first; screens are composed only from those primitives. Data comes from `composables/useXxx()` that currently return in-memory mock data but are shaped so swapping to the `api/` axios modules is a one-line change. Dev server on :5173 proxies `/api` → :8085.

**Tech Stack:** Vue 3.5, Vue Router 4, Bootstrap 5.3 + bootstrap-icons (npm, SCSS-themed), axios, Vite 6, sass. Node 24 / npm 11 confirmed available.

**Design spec:** `docs/superpowers/specs/2026-07-02-bshoes-spring-boot-port-design.md`. Screen field lists / mock data already exist in `preview/*.html` and `src/main/resources/templates/*.html` — reuse them.

**Green theme reference:** primary `#0B895A`, dark `#0A6E48`, light `#0E9F67`, app bg `#f4f6f8`.

---

## Task 1: Scaffold the Vite project

**Files (all under `frontend/`):** `package.json`, `vite.config.js`, `index.html`, `.gitignore`, `src/main.js`, `src/App.vue`, `src/router/index.js`, `src/styles/{_tokens.scss,_bootstrap.scss,app.scss}`.

- [ ] **Step 1: package.json**

```json
{
  "name": "bshoes-frontend",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "axios": "^1.7.9",
    "bootstrap": "^5.3.3",
    "bootstrap-icons": "^1.11.3",
    "vue": "^3.5.13",
    "vue-router": "^4.5.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.2.1",
    "sass": "^1.83.0",
    "vite": "^6.0.7"
  }
}
```

- [ ] **Step 2: vite.config.js**

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: { '/api': { target: 'http://localhost:8085', changeOrigin: true } }
  }
})
```

- [ ] **Step 3: index.html**

```html
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>BShoes</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 4: frontend/.gitignore**

```
node_modules
dist
*.local
.vite
```

- [ ] **Step 5: src/styles/_tokens.scss** (design tokens as CSS custom properties)

```scss
:root {
  --c-primary: #0B895A;
  --c-primary-dark: #0A6E48;
  --c-primary-light: #0E9F67;
  --c-success: #198754;
  --c-warning: #f0ad4e;
  --c-danger: #dc3545;
  --c-info: #0dcaf0;
  --c-bg: #f4f6f8;
  --c-surface: #ffffff;
  --c-border: #e3e6ea;
  --c-text: #212529;
  --c-text-muted: #6c757d;

  --sp-1: 4px;  --sp-2: 8px;  --sp-3: 12px;
  --sp-4: 16px; --sp-5: 24px; --sp-6: 32px;

  --radius: 8px;
  --shadow-sm: 0 1px 2px rgba(0,0,0,.06);
  --shadow: 0 2px 8px rgba(0,0,0,.08);

  --font-base: "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;

  --sidebar-w: 210px;
  --header-h: 48px;
}
```

- [ ] **Step 6: src/styles/_bootstrap.scss** (theme Bootstrap to the green palette)

```scss
// Override Bootstrap Sass variables BEFORE importing Bootstrap.
$primary: #0B895A;
$success: #198754;
$warning: #f0ad4e;
$danger:  #dc3545;
$info:    #0dcaf0;
$border-radius: 8px;
$font-family-sans-serif: "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;

@import "bootstrap/scss/bootstrap";
```

- [ ] **Step 7: src/styles/app.scss**

```scss
@import "./tokens";
@import "./bootstrap";
@import "bootstrap-icons/font/bootstrap-icons.css";

body { margin: 0; background: var(--c-bg); color: var(--c-text); font-family: var(--font-base); }
```

- [ ] **Step 8: src/main.js**

```js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './styles/app.scss'

createApp(App).use(router).mount('#app')
```

- [ ] **Step 9: src/App.vue**

```vue
<template>
  <router-view />
</template>
```

- [ ] **Step 10: src/router/index.js** (routes point to lazy-loaded views; views created in later tasks — for now only StyleGuide + a placeholder will exist, so include just the style-guide route and a catch-all redirect; full routes added in Task 3)

```js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/style-guide', name: 'style-guide', component: () => import('../views/StyleGuideView.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/style-guide' }
]

export default createRouter({ history: createWebHistory(), routes })
```

- [ ] **Step 11: install + verify**

Run (from `frontend/`): `npm install`
Expected: dependencies install without error. Do NOT run build yet (StyleGuideView doesn't exist until Task 2).

- [ ] **Step 12: Commit** (node_modules is gitignored)

```bash
git add frontend/package.json frontend/package-lock.json frontend/vite.config.js frontend/index.html frontend/.gitignore frontend/src
git commit -m "feat(spa): scaffold Vite + Vue 3 project with Bootstrap-themed design tokens"
```

---

## Task 2: Design system — base UI components + style guide  ⟵ REVIEW CHECKPOINT

**Files:** `src/components/ui/*.vue` (11 components) + `src/views/StyleGuideView.vue`.

All components use `<script setup>`, Bootstrap classes, and the tokens. Below, canonical components are fully coded; the rest have exact prop/slot/emit specs — follow the canonical style.

- [ ] **Step 1: `ui/PageHeader.vue`** (canonical — full code)

```vue
<script setup>
defineProps({ title: { type: String, required: true } })
</script>

<template>
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h4 class="mb-0" style="color: var(--c-primary)">{{ title }}</h4>
    <div class="d-flex gap-2"><slot name="actions" /></div>
  </div>
</template>
```

- [ ] **Step 2: `ui/AppButton.vue`** (canonical — full code)

```vue
<script setup>
defineProps({
  variant: { type: String, default: 'primary' },   // primary | secondary | outline-secondary | outline-danger | danger
  size: { type: String, default: '' },              // '' | sm | lg
  icon: { type: String, default: '' },              // bootstrap-icons name, e.g. 'plus-lg'
  type: { type: String, default: 'button' }
})
</script>

<template>
  <button :type="type" class="btn" :class="[`btn-${variant}`, size ? `btn-${size}` : '']">
    <i v-if="icon" :class="`bi bi-${icon}`" class="me-1"></i>
    <slot />
  </button>
</template>
```

- [ ] **Step 3: `ui/SearchBar.vue`** (canonical — full code, debounced, v-model)

```vue
<script setup>
import { ref, watch } from 'vue'
const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: 'Tìm kiếm...' }
})
const emit = defineEmits(['update:modelValue'])
const local = ref(props.modelValue)
let t
watch(local, (v) => { clearTimeout(t); t = setTimeout(() => emit('update:modelValue', v), 250) })
watch(() => props.modelValue, (v) => { if (v !== local.value) local.value = v })
</script>

<template>
  <div class="input-group" style="max-width: 320px">
    <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
    <input class="form-control" v-model="local" :placeholder="placeholder" />
  </div>
</template>
```

- [ ] **Step 4: `ui/DataTable.vue`** (canonical — full code; column-config + slots + empty state)

```vue
<script setup>
defineProps({
  columns: { type: Array, required: true }, // [{ key, label, align }]
  rows: { type: Array, default: () => [] },
  rowKey: { type: String, default: 'id' },
  emptyText: { type: String, default: 'Không có dữ liệu' }
})
</script>

<template>
  <table class="table table-hover bg-white align-middle mb-0">
    <thead>
      <tr>
        <th v-for="c in columns" :key="c.key" :class="c.align === 'end' ? 'text-end' : ''">{{ c.label }}</th>
        <th v-if="$slots.actions"></th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="row in rows" :key="row[rowKey]">
        <td v-for="c in columns" :key="c.key" :class="c.align === 'end' ? 'text-end' : ''">
          <slot :name="`cell-${c.key}`" :row="row" :value="row[c.key]">{{ row[c.key] }}</slot>
        </td>
        <td v-if="$slots.actions" class="text-end"><slot name="actions" :row="row" /></td>
      </tr>
      <tr v-if="!rows.length">
        <td :colspan="columns.length + ($slots.actions ? 1 : 0)" class="text-center text-muted py-4">{{ emptyText }}</td>
      </tr>
    </tbody>
  </table>
</template>
```

- [ ] **Step 5: `ui/AppModal.vue`** (canonical — full code; wraps Bootstrap Modal JS, v-model:open)

```vue
<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { Modal } from 'bootstrap'
const props = defineProps({ open: Boolean, title: { type: String, default: '' } })
const emit = defineEmits(['update:open'])
const el = ref(null)
let modal
onMounted(() => {
  modal = new Modal(el.value)
  el.value.addEventListener('hidden.bs.modal', () => emit('update:open', false))
  if (props.open) modal.show()
})
watch(() => props.open, (v) => { v ? modal?.show() : modal?.hide() })
onBeforeUnmount(() => modal?.dispose())
</script>

<template>
  <div class="modal fade" tabindex="-1" ref="el">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ title }}</h5>
          <button type="button" class="btn-close" @click="emit('update:open', false)"></button>
        </div>
        <div class="modal-body"><slot /></div>
        <div class="modal-footer"><slot name="footer" /></div>
      </div>
    </div>
  </div>
</template>
```

- [ ] **Step 6: the remaining 6 components** — build each following the canonical style, with these exact contracts:

  - **`ui/ConfirmDialog.vue`** — props `{ open:Boolean, title:String='Xác nhận', message:String }`; emits `update:open`, `confirm`. Uses `AppModal`; footer has a "Huỷ" (`update:open=false`) and a `danger` "Xoá" button (emits `confirm`).
  - **`ui/FormField.vue`** — props `{ label:String, error:String='' }`; renders `<label class="form-label">` + a default `<slot>` for the control + a `text-danger small` error line when `error`.
  - **`ui/AppSelect.vue`** — props `{ modelValue, options:Array }` (options = `[{value,label}]` or plain strings); emits `update:modelValue`; renders `<select class="form-select">`. Support v-model.
  - **`ui/StatusBadge.vue`** — props `{ active:Boolean, activeText:String='Hoạt động', inactiveText:String='Ngừng' }`; renders `<span class="badge">` with `bg-success` when active else `bg-secondary`.
  - **`ui/StatCard.vue`** — props `{ label:String, value:[String,Number] }`; a card with `border-left: 4px solid var(--c-primary)` (add a `.stat-card` class in the component `<style>` or reuse a token), showing label (muted small) + value (fs-4 fw-bold).
  - **`ui/ToastHost.vue`** — a global toast container. Export a composable `src/composables/useToast.js` with a reactive `toasts` array and `notify(message, type='success')`; `ToastHost` renders stacked Bootstrap toasts (position-fixed bottom-end) and auto-removes after 3s. (Simple: a module-level reactive array.)

- [ ] **Step 7: `views/StyleGuideView.vue`** — a page (no shell needed, or plain container) that renders EVERY ui component with sample data: a PageHeader with actions, all AppButton variants/sizes, a SearchBar, a DataTable with 3 mock rows + an `#actions` slot (edit/delete AppButtons), a button that opens an AppModal containing FormField + AppSelect, a ConfirmDialog trigger, StatusBadge (active + inactive), 4 StatCards in a row, and a "show toast" button calling `useToast().notify`. This is the visual contract to review.

- [ ] **Step 8: verify build**

Run (from `frontend/`): `npm run build`
Expected: `✓ built in ...` with no errors. Then `npm run dev` and open `http://localhost:5173/style-guide` to eyeball the components.

- [ ] **Step 9: Commit**

```bash
git add frontend/src
git commit -m "feat(spa): design system — themed base UI components + style guide"
```

**⟵ STOP: review checkpoint. Confirm the style guide looks right before Task 3.**

---

## Task 3: App shell, routing, and Login

**Files:** `src/components/layout/{AppShell,AppHeader,AppSidebar}.vue`, `src/views/LoginView.vue`, updated `src/router/index.js`, placeholder views for the 8 shell routes.

- [ ] **Step 1: `layout/AppSidebar.vue`** — the green sidebar. Nav items (route, label): `/` Doanh Thu, `/san-pham` Sản Phẩm, `/hoa-don` Hóa Đơn, `/nhan-vien` Nhân Viên, `/khach-hang` Khách Hàng, `/lich-su` Lịch Sử, `/phieu-giam-gia` Khuyến Mãi, `/he-thong` Hệ Thống, and `/login` Đăng Nhập pushed to the bottom (`margin-top:auto`). Use `<router-link>` with `active-class` styling (white bg + green text on active), width `var(--sidebar-w)`, bg `var(--c-primary)`.

- [ ] **Step 2: `layout/AppHeader.vue`** — green bar height `var(--header-h)`, "BShoes" bold white + muted subtitle "Quản lý cửa hàng giày".

- [ ] **Step 3: `layout/AppShell.vue`** — flex layout: `<AppHeader/>` on top, then a row of `<AppSidebar/>` + `<main class="p-4 flex-grow-1">` with a default `<slot/>`. Also render `<ToastHost/>` here so toasts are global.

- [ ] **Step 4: `views/LoginView.vue`** — centered card (no shell): tài khoản + mật khẩu fields (FormField + inputs), an AppButton "Đăng nhập" that on click routes to `/`. Reuse the demo's copy.

- [ ] **Step 5: placeholder views** — create `src/views/{DashboardView,SanPhamView,NhanVienView,KhachHangView,HoaDonView,LichSuView,PhieuGiamGiaView,HeThongView}.vue`, each temporarily rendering `<AppShell><PageHeader title="..."/><p>Đang xây dựng…</p></AppShell>` so routes resolve.

- [ ] **Step 6: full router** — replace `router/index.js` routes with: `/login`→LoginView (no shell); `/`→DashboardView; `/san-pham`,`/nhan-vien`,`/khach-hang`,`/hoa-don`,`/lich-su`,`/phieu-giam-gia`,`/he-thong`→their views; keep `/style-guide`; catch-all → `/`. All lazy-loaded.

- [ ] **Step 7: build + commit**

Run: `npm run build` (expect success).
```bash
git add frontend/src
git commit -m "feat(spa): app shell, sidebar navigation, routing, login"
```

---

## Task 4: Data seam — mock + composables + api modules

**Files:** `src/mock/data.js`, `src/api/http.js`, `src/api/*.js`, `src/composables/useCrud.js` + per-domain composables.

- [ ] **Step 1: `src/api/http.js`** — an axios instance:

```js
import axios from 'axios'
export const http = axios.create({ baseURL: '/api', headers: { 'Content-Type': 'application/json' } })
```

- [ ] **Step 2: `src/mock/data.js`** — export the seed arrays for each screen, copied from the existing `preview/*.html` / templates (products, product variants, customers, employees, vouchers, invoices+detail, history, dashboard cards+stats, POS products). Keep Vietnamese content intact.

- [ ] **Step 3: `src/composables/useCrud.js`** — a generic factory returning `{ rows, keyword, filtered, add, update, remove }` over a reactive copy of a mock array (id/seq bookkeeping, case-insensitive search over given keys). This is the mock implementation.

```js
import { ref, computed } from 'vue'
export function useCrud(seed, { searchKeys = [], codePrefix = '', codeField = '' } = {}) {
  const rows = ref(JSON.parse(JSON.stringify(seed)))
  const keyword = ref('')
  let seq = rows.value.reduce((m, r) => Math.max(m, r.id || 0), 0)
  const filtered = computed(() => {
    const k = keyword.value.toLowerCase()
    if (!k) return rows.value
    return rows.value.filter(r => searchKeys.some(key => String(r[key] ?? '').toLowerCase().includes(k)))
  })
  function add(item) {
    item.id = ++seq
    if (codePrefix && codeField) item[codeField] = codePrefix + item.id
    rows.value.push(item)
  }
  function update(item) {
    const i = rows.value.findIndex(r => r.id === item.id)
    if (i !== -1) rows.value.splice(i, 1, item)
  }
  function remove(id) { rows.value = rows.value.filter(r => r.id !== id) }
  return { rows, keyword, filtered, add, update, remove }
}
```

- [ ] **Step 4: per-domain composables + api stubs** — for each domain (`sanPham`, `khachHang`, `nhanVien`, `phieuGiamGia`, `lichSu`, `hoaDon`, `thongKe`), create:
  - `src/api/<domain>.js` — functions mapping to Phase 2 endpoints, e.g. `export const sanPhamApi = { findAll: () => http.get('/san-pham-ql').then(r=>r.data), create: (e)=>http.post('/san-pham-ql', e), ... }`. (These are the swap targets; not called yet.)
  - `src/composables/use<Domain>.js` — wraps `useCrud(seed, opts)` for the screen, exposing the reactive state + ops. Add a top comment: `// TODO(api): replace mock useCrud with <domain>Api calls when backend runs.`

- [ ] **Step 5: build + commit**

```bash
git add frontend/src
git commit -m "feat(spa): mock data, useCrud composable, per-domain composables + api stubs"
```

---

## Task 5: Screens group A — Dashboard, Vouchers, Customers, Employees

**Files:** flesh out `DashboardView`, `PhieuGiamGiaView`, `KhachHangView`, `NhanVienView`.

Each screen: `<AppShell>` + `<PageHeader :title>` (with a "+ Thêm" AppButton in `#actions` for CRUD screens) + `<SearchBar v-model="keyword">` + `<DataTable :columns :rows="filtered">` with `#cell-*` slots (e.g. StatusBadge for trạng thái) and an `#actions` slot (edit/delete AppButtons) + an `<AppModal v-model:open>` add/edit form built from `FormField`/`AppSelect` + a `<ConfirmDialog>` for delete + `useToast().notify` on save/delete. Data via the domain composable.

- [ ] **Step 1: DashboardView** — 5 `StatCard`s (from mock `cards`) + a `DataTable` of best-selling products (columns: Mã SP, Tên, Loại, Chất liệu, Màu, Size, Tồn, Đã bán, Doanh thu with `text-end` + a `vnd` formatter). No CRUD.
- [ ] **Step 2: PhieuGiamGiaView** — columns + form fields exactly per `preview/phieu-giam-gia.html` (Mã, Tên, Loại %/tiền, Giá trị, Đơn tối thiểu, SL, Bắt đầu, Kết thúc, Trạng thái). Code prefix `PGG`.
- [ ] **Step 3: KhachHangView** — per `preview/khach-hang.html` (Mã KH, Tên, Giới tính, SĐT, Email, Địa chỉ, Trạng thái). Code prefix `KH`.
- [ ] **Step 4: NhanVienView** — per `preview/nhan-vien.html` (Mã NV, Tên, Tài khoản, Email, SĐT, CCCD, Chức vụ, Vai trò, Trạng thái). Code prefix `NV`.
- [ ] **Step 5: build (`npm run build`) + commit**

```bash
git add frontend/src
git commit -m "feat(spa): dashboard, vouchers, customers, employees screens"
```

---

## Task 6: Screens group B — Products, POS, Invoice history, System

**Files:** flesh out `SanPhamView`, `HoaDonView`, `LichSuView`, `HeThongView`.

- [ ] **Step 1: SanPhamView** — per `preview/san-pham.html`: left product `DataTable` + brand `AppSelect` filter + SearchBar; clicking a row shows its variants in a side card; add/edit via AppModal. Row-select interaction.
- [ ] **Step 2: HoaDonView (POS)** — per `preview/hoa-don.html`: two-column `pos-grid` (product picker cards left; current invoice cart right with qty inputs, customer AppSelect, voucher AppSelect, computed tạm tính/giảm giá/phải trả, "Thanh toán" AppButton → toast + clear). Computeds mirror the demo.
- [ ] **Step 3: LichSuView** — per `preview/lich-su.html`: DataTable + status AppSelect filter + SearchBar + a detail AppModal (invoice lines).
- [ ] **Step 4: HeThongView** — per `preview/he-thong.html`: info card (user, role, version) + a `danger` AppButton "Đăng xuất" → ConfirmDialog → route to `/login`.
- [ ] **Step 5: build + commit**

```bash
git add frontend/src
git commit -m "feat(spa): products, POS, invoice history, system screens"
```

---

## Task 7: Final build verify + README + push

- [ ] **Step 1: production build**

Run (from `frontend/`): `npm run build`
Expected: `✓ built` with no errors/warnings that fail the build.

- [ ] **Step 2: `frontend/README.md`** — how to run: `cd frontend && npm install && npm run dev` → http://localhost:5173. Note the `/api` proxy to :8085 (backend must run for live data; otherwise mock data is used via composables). Note the design system lives in `src/styles` + `src/components/ui`, demoed at `/style-guide`.

- [ ] **Step 3: commit + push**

```bash
git add frontend/README.md
git commit -m "docs(spa): frontend README (run instructions, design system, api seam)"
git push -u origin feature/vue-spa
```

---

## Self-review notes (author)

- **Design-system-first:** Task 2 builds all 11 ui primitives + a style-guide page and STOPS for review before any screen (Task 5/6) is composed — matches the user's explicit requirement for a consistent interaction & UI theme.
- **Runs on mock now, API-ready:** composables use `useCrud` over mock (`Task 4`); `api/*.js` axios modules + the `TODO(api)` comments are the documented swap seam to the Phase 2 REST endpoints; Vite proxies `/api`→:8085.
- **All 9 screens covered:** Dashboard, Vouchers, Customers, Employees (Task 5); Products, POS, Invoice history, System (Task 6); Login + shell (Task 3). Field lists/mock reused from `preview/*.html`.
- **Consistency:** every screen composes only `components/ui` + `layout` primitives; theme flows from `_tokens.scss` + Bootstrap `$primary` override so the green is defined once.
- **Verifiable here:** Node/npm present → each task ends with `npm run build`; the JVM backend is not required to view the SPA.
```
