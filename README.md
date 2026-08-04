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

- **JDK 17** with `JAVA_HOME` pointing at it. If `java -version` already reports 17 you are done;
  otherwise set `JAVA_HOME` for the shell you build in (see below).
- **Node 20+ / npm 10+** (developed on Node 24 / npm 11).
- **Internet access on the first build** — the Maven wrapper downloads its jar and the
  dependencies; `npm ci` downloads the SPA packages. Later builds work offline (`./mvnw -o`).
- **SQL Server** with the `BShoes` database created from `sqlBshoes.sql` — only needed to *run*
  the app, not to build or test it. Default creds in `application.properties`: `sa` / `123` on
  `localhost:1433`.

## Build & run

### Backend (Spring Boot API, :8085)
```bash
export JAVA_HOME="/path/to/jdk-17"          # bash;  PowerShell: $env:JAVA_HOME='C:\path\to\jdk-17'
./mvnw -DskipTests compile                  # PowerShell: .\mvnw.cmd
./mvnw spring-boot:run                      # starts even if the DB is down (degraded)
```

### Frontend (Vue SPA, :5173)
```bash
cd frontend
npm ci                 # reproducible install from package-lock.json
npm run dev            # http://localhost:5173  (proxies /api -> :8085)
npm run build          # production build to dist/
```
When an API call fails, the affected screen falls back to local mock data **and shows a red
"ĐANG HIỂN THỊ DỮ LIỆU DEMO" banner**, so demo data is never mistaken for real data.

### Run the tests
```bash
cd frontend && npm test -- --run     # Vitest — 132 tests, no DB required
cd ..      && ./mvnw test            # JUnit  —  48 tests, no DB required
```
Both suites are DB-free: the backend test profile keeps the Hibernate dialect pinned and lets
Hikari defer connecting, so the Spring context loads (validating every entity, repository query
and controller mapping) without SQL Server.

Verified from a clean `git clone` on 2026-08-04: `npm ci` + build + 132/132, and `./mvnw test`
48/48 `BUILD SUCCESS`.

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
- **`asm-doc`** — coursework documents under `doc-school/`.
- **`test-api-connect`** — API wiring experiments.
- **`test-auth`** — **the current line, most complete**: Vue 3 SPA + reconciled backend, QR
  scanner, server-side auth, and the feature/test checklist under `docs/checklist/`.

## Design system

The SPA is built on a documented design system (green-themed Bootstrap): tokens in
`frontend/src/styles/`, reusable primitives in `frontend/src/components/ui/`, all demoed at the
`/style-guide` route.

## Logging — testing on another machine

Clone, run, reproduce the bug, then send back **one file**: `logs/bshoes.log`.

The backend writes a rolling log; the frontend **auto-ships** its captured browser events to
`POST /api/logs/client`, which appends them to that same file. So server stack traces and what
the browser saw sit in one time-ordered timeline:

```
10:15:30.118 INFO  CLIENT - [api] POST /api/khach-hang -> 500 | route=/khach-hang user=admin
10:15:30.121 ERROR GlobalExceptionHandler - 500 on POST /api/khach-hang
    java.lang.NullPointerException: ...
```

**Where the file is.** `logs/bshoes.log`, relative to the directory the backend was started from.
The absolute path is printed to the console at startup, so you never have to guess. Pin it
anywhere with:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DLOG_DIR=/abs/path"
java -DLOG_DIR=/abs/path -jar target/*.jar          # packaged
```

Rotates daily and at 10MB (`bshoes-YYYY-MM-DD.N.log`), 7 days / 100MB kept. `logs/` is
gitignored — created at runtime, never committed.

**What is captured:** every `/api/**` request (method, URI, status, duration), all 4xx/5xx with
the request body, full stack traces via a catch-all handler, plus browser-side API failures,
Vue errors and unhandled rejections. Passwords/tokens are redacted (`***`).

**If the API is on another host/port**, point the frontend proxy at it (works for both
`npm run dev` and `npm run preview`):

```bash
VITE_API_TARGET=http://192.168.1.50:8085 npm run dev
```

**Fallback:** if the backend is unreachable the browser log can't be shipped — grab it from the
*Hệ thống* page → **"Tải file log"** (downloads a JSON). That's the only place network-level
failures the server never saw will show up.

## Authentication

`POST /api/auth/login` returns a session **token**; the SPA stores it and sends it back as the
`X-Auth-Token` header on every call. `ApiAuthFilter` checks that token server-side and compares
the requested path against the employee's per-screen permissions in `nhan_vien_quyen`
(`ScreenPermissions`), so an admin endpoint cannot be reached by curl-ing it directly.

Public without a token: login, `/api/ping`, `/api/logs/client`, storefront browsing, the customer
cart/checkout, and placing a pre-order.

CORS for `/api/**` is declared in `WebConfig` and allows `localhost:5173` / `:4173` by default —
override with `-Dapp.cors.origins=http://host:port`. Not needed for the normal dev flow, because
Vite proxies `/api` to :8085.

> **Prototype scope:** passwords are compared in **plaintext** and tokens live in memory only
> (a restart logs everyone out). Deliberate for user testing — both must change before this
> handles real accounts.

## Status & known gaps

- ✅ Backend compiles and tests (48/48). ✅ Frontend builds and tests (132/132). Both verified
  from a clean clone, **without** a database.
- ⏳ Full end-to-end run requires the `BShoes` SQL Server DB up on :1433. Re-run `sqlBshoes.sql`
  after pulling — the voucher seed, the active-voucher view usage and `dbo.tinh_tien_giam_gia`
  all changed.
- Passwords are plaintext (see above).
- POS **"Hoàn trả"** on a cart line only toggles a label; it does not return stock. Real returns
  go through `POST /api/hoa-don/{id}/tra-hang`.
- Import supports **JSON only** for products / POS lines (CSV/Excel not implemented); Bảo Hành
  import is CSV.
- ~126 test cases still need a live database — tracked in `docs/checklist/`.

## Docs

- **Feature + test checklist:** `docs/checklist/` — `feature-backlog.csv` (what exists),
  `testcase-checklist.csv` (what is verified, with evidence), `CHECKLIST-RULES.md` (rules R1–R12;
  R12 requires wiping all results and re-running the full regression each review round).
- Manual regression list: `docs/regression-checklist.md`
- Design spec: `docs/superpowers/specs/2026-07-02-bshoes-spring-boot-port-design.md`
- Phase plans: `docs/superpowers/plans/` (Phase 1 UI, Phase 2 backend, Vue SPA, Phase 3 DTO reconcile)
