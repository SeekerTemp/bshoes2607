# BShoes — Vue SPA

A Vite + Vue 3 single-page app for the BShoes shoe-store management system. All 9 screens
(Login, Thống kê/Dashboard, Sản phẩm, Nhân viên, Khách hàng, Hóa đơn/POS, Lịch sử hóa đơn,
Phiếu giảm giá, Hệ thống) built on a shared, themed **design system**.

## Run

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```

- `npm run build` — production build to `dist/`
- `npm run preview` — serve the production build locally

Requires internet access on first `npm install` only.

## Data / backend

The composables in `src/composables/use*.js` now call the **live REST API** through the
axios modules in `src/api/*.js`, with a **graceful fallback to mock data** (seed arrays in
`src/mock/data.js`) whenever the backend is offline or a request fails. So the SPA runs
either way: against real data when the backend is up, and against the demo seed when it is
not (watch the console for `API offline, using mock` warnings).

The Vite dev server proxies `/api` → `http://localhost:8085` (see `vite.config.js`). The
api modules map to the Spring Boot DTO-shaped endpoints — `/api/khach-hang`, `/api/nhan-vien`,
`/api/phieu-giam-gia`, `/api/san-pham-ql`, `/api/lich-su-hoa-don`, `/api/hoa-don`,
`/api/thong-ke`, plus `/api/thuong-hieu` & `/api/chat-lieu` for the product-form selects —
whose JSON field names match the frontend shapes. To go live: start the Spring Boot backend
on :8085 (needs JPA-annotated entities + a SQL Server `BShoes` DB). CRUD writes (add/update/
delete) POST/PUT/DELETE to the API and re-load on success; when offline they mutate the local
rows so the UI stays interactive.

## Design system

The reusable UI vocabulary every screen is built from:

- **Tokens & theme:** `src/styles/_tokens.scss` (green brand `#0B895A`, semantic
  success/warning/danger/info with subtle tints, spacing/radius/shadow/focus-ring/type) and
  `src/styles/_bootstrap.scss` (Bootstrap 5 `$primary` override so the whole framework is
  themed green).
- **Components:** `src/components/ui/` — `PageHeader`, `AppButton` (variants + `loading`/
  states), `SearchBar`, `DataTable`, `AppModal`, `ConfirmDialog`, `FormField`, `AppSelect`,
  `StatusBadge` (soft pills), `StatCard`, `ToastHost` (+ `useToast`).
- **Layout:** `src/components/layout/` — `AppShell`, `AppHeader`, `AppSidebar`.
- **Style guide:** browse **`/style-guide`** to see every component, color, and interaction
  state in one place.

## Structure

```
src/
├─ main.js  App.vue  router/index.js
├─ styles/          design tokens + Bootstrap theme + global
├─ components/ui/   design-system primitives
├─ components/layout/  shell, header, sidebar
├─ composables/     use* (mock data, api-swappable) + useCrud + useToast
├─ api/             axios instance + endpoint modules (Phase 2 REST)
├─ mock/            seed data
├─ utils/           format helpers (vnd)
└─ views/           the 9 screens + LoginView + StyleGuideView
```
