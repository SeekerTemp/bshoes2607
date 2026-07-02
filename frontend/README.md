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

Screens currently run on **mock data** via `src/composables/use*.js` (backed by
`src/composables/useCrud.js` over seed arrays in `src/mock/data.js`). Each composable has a
`// TODO(api)` marker showing where to swap in the REST calls.

The Vite dev server proxies `/api` → `http://localhost:8085` (see `vite.config.js`), and
`src/api/*.js` already maps to the Spring Boot Phase 2 endpoints (`/api/san-pham-ql`,
`/api/khach-hang`, `/api/hoa-don`, `/api/thong-ke`, …). To go live: start the Spring Boot
backend on :8085 (needs JPA-annotated entities + a SQL Server `BShoes` DB) and switch the
composables from `useCrud(mock)` to the `*Api` calls.

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
