# BShoes

Shoe-store management system, ported from a legacy **NetBeans Swing** desktop app to a
**Spring Boot (JPA/MVC) backend + Vue 3 SPA** frontend. Vietnamese domain (sản phẩm, hóa đơn,
khách hàng, nhân viên, phiếu giảm giá, thống kê).

The original desktop app lives under `ref/BShoes_lts/` for reference only.

## Repository layout

```
bshoes2607/
├─ src/main/java/com/vn/test/bshoes/
│  ├─ entity/        JPA entities (reverse-engineered from sqlBshoes.sql)
│  ├─ repository/    Spring Data JPA repositories (derived / JPQL / native queries)
│  ├─ service/       service interfaces + service/impl/ implementations
│  ├─ dto/           frontend-shaped DTOs (the REST contract)
│  ├─ controller/    @RestController APIs under /api/... (+ Phase-1 PageController)
│  └─ BshoesApplication.java
├─ src/main/resources/
│  ├─ templates/     Thymeleaf UI demo (Phase 1) + layout fragment
│  ├─ static/css/    demo theme
│  └─ application.properties   (port 8085, SQL Server datasource, JPA)
├─ frontend/         Vite + Vue 3 SPA (the primary UI) — see frontend/README.md
├─ preview/          standalone no-build HTML preview of the screens (open in a browser)
├─ sqlBshoes.sql     SQL Server schema + seed (database name: BShoes)
├─ ref/BShoes_lts/   legacy NetBeans app (reference only, not built)
└─ docs/superpowers/ design spec + phase plans
```

## Tech stack

- **Backend:** Spring Boot 4.1 (Java 17), Spring Data JPA / Hibernate, Microsoft SQL Server
  (mssql-jdbc), Lombok, Thymeleaf (Phase-1 demo). Runs on **port 8085**.
- **Frontend:** Vite 6 + Vue 3 (`<script setup>`), Vue Router, Bootstrap 5 + bootstrap-icons
  (SCSS-themed green `#0B895A`), axios, `@zxing/browser` (QR/barcode). Dev server **:5173**.

## Prerequisites

- **JDK 17** — installed at `C:\Users\DREAMSTORE\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.19.10-hotspot`
  (not on PATH; export `JAVA_HOME` before building — see below).
- **Node 24 / npm 11** (for the SPA).
- **SQL Server** with the `BShoes` database created from `sqlBshoes.sql` (default creds in
  `application.properties`: `sa` / `123` on `localhost:1433`).

## Build & run

### Backend (Spring Boot API, :8085)
```bash
export JAVA_HOME="/c/Users/DREAMSTORE/AppData/Local/Programs/Eclipse Adoptium/jdk-17.0.19.10-hotspot"
./mvnw -q -DskipTests compile        # BUILD SUCCESS (verified)
./mvnw spring-boot:run               # needs the BShoes DB reachable on :1433
```
On Windows PowerShell use `.\mvnw.cmd`. First run downloads dependencies (online).

### Frontend (Vue SPA, :5173)
```bash
cd frontend
npm install
npm run dev            # http://localhost:5173  (proxies /api -> :8085)
npm run build          # production build to dist/
```
The SPA calls the live `/api/...` endpoints, with a **graceful fallback to mock data** when the
backend is offline (watch the console for `API offline, using mock`). See `frontend/README.md`.

### No-build preview (no backend, no npm)
Open `preview/index.html` in a browser to click through all screens on mock data (needs internet
for the Bootstrap/Vue CDN scripts).

## REST API (under `/api`)

DTO-shaped JSON whose field names match the frontend. Main endpoints:

| Domain | Base path |
|---|---|
| Catalog attrs | `/api/chat-lieu`, `/api/mau-sac`, `/api/kich-co`, `/api/kieu-co-giay`, `/api/kieu-dang`, `/api/kieu-day-giay`, `/api/thuong-hieu`, `/api/xuat-su`, `/api/loai-san-pham`, `/api/vai-tro` |
| Products | `/api/san-pham`, `/api/san-pham-ql` (admin), `/api/san-pham-chi-tiet`, `/api/san-pham-chi-tiet-ql` |
| Customers / staff | `/api/khach-hang`, `/api/nhan-vien`, `/api/dia-chi`, `/api/auth/login` |
| Vouchers | `/api/phieu-giam-gia` |
| Invoices / POS | `/api/hoa-don` (+ `/cart`, `/pos-products`, `/vouchers-active`, `/{idPhieu}/giam-gia`), `/api/hoa-don-chi-tiet` |
| Invoice history | `/api/lich-su-hoa-don` |
| Statistics | `/api/thong-ke` (`/hom-nay`, `/theo-ngay`, `/theo-thang`, `/theo-nam`, `/san-pham`, `/nam`) |
| **QR scan lookup** | `GET /api/san-pham-chi-tiet/by-ma/{ma}` → cart-ready item or 404 |

Faithful DB objects kept as native queries: function `dbo.tinh_tien_giam_gia`, view
`view_phieu_giam_gia_hoat_dong`.

## QR / barcode scanning

The legacy desktop webcam scanner (Swing + sarxos + ZXing) is replaced by a **browser** scanner:
`frontend/src/components/ui/QrScanner.vue` (via `@zxing/browser`) opens the device camera in the
POS screen; on decode it calls `/api/san-pham-chi-tiet/by-ma/{ma}` and adds the item to the cart.
Requires a secure origin (localhost or HTTPS) and camera permission.

## Branches

- **`main`** — initial import.
- **`feature/spring-boot-port`** — Thymeleaf UI demo (Phase 1), `preview/`, and the Phase-2
  backend scaffolding.
- **`feature/vue-spa`** — the current line: Vue 3 SPA + Phase-3 backend reconciled to the new JPA
  entities (DTOs) + wired frontend + QR scanner. **Most complete branch.**

## Design system

The SPA is built on a documented design system (green-themed Bootstrap): tokens in
`frontend/src/styles/`, reusable primitives in `frontend/src/components/ui/`, all demoed at the
`/style-guide` route.

## Status & known gaps

- ✅ Backend **compiles** (JDK 17). ✅ Frontend **builds**. Verified locally.
- ⏳ Full end-to-end **run** requires the `BShoes` SQL Server DB up on :1433.
- POS **checkout** persistence is minimal (create-invoice endpoint exists; full stock/line write
  flow is client-side for now).
- Dashboard **stat cards** use mock values (no single aggregate endpoint yet; sourceable from
  `/api/thong-ke/theo-thang`).
- **Auth** compares plaintext passwords (legacy parity) — replace with Spring Security + BCrypt.

## Docs

- Design spec: `docs/superpowers/specs/2026-07-02-bshoes-spring-boot-port-design.md`
- Phase plans: `docs/superpowers/plans/` (Phase 1 UI, Phase 2 backend, Vue SPA, Phase 3 DTO reconcile)
