# BShoes — NetBeans → Spring Boot Port (Design)

**Date:** 2026-07-02
**Branch:** `feature/spring-boot-port`
**Repo:** `SeekerTemp/bshoes2607`

## Goal

Port the legacy **BShoes** NetBeans Swing desktop application (a Vietnamese shoe-store
management system) to a Spring Boot web application using an MVC + JPA structure.

The work is split into two phases:

- **Phase 1 (this deliverable):** Build an HTML UI demo of **all** screens, faithful to the
  NetBeans flow, using Thymeleaf + Vue 3 + Bootstrap 5, served by a single Spring Boot app.
  No backend logic, no database, no test run — mock data only.
- **Phase 2 (later, after UI approval):** Convert the legacy `DAO`/`DAOImpl` raw-SQL logic and
  Swing controller logic into Spring `@Service` + Spring Data JPA `repository` (`@Query`) +
  web `@Controller`/`@RestController`. This will **not run** until the user adds JPA
  annotations to the existing entities (done separately by the user).

## Legacy app summary (source: `ref/BShoes_lts`)

- **UI:** NetBeans Swing (`ui/Form_Home.java` + `Pnl_*` panels). Green theme `#0B895A`,
  1280×720, header + left sidebar navigation.
- **Data layer:** raw SQL via `util/XJdbc` (JDBC to SQL Server `BShoes`) and `util/XQuery`
  (reflection-based `ResultSet` → bean mapping). `dao/*` interfaces (`CrudDAO<T,ID>`),
  `daoipml/*` implementations.
- **Entities:** plain POJOs in `entity/*` with Vietnamese snake_case fields (e.g.
  `id_san_pham`, `ten_san_pham`, `trang_thai`). **Not yet JPA-annotated** — the user will map
  these later.
- **Controllers:** `controller/*` are Swing form-controller interfaces (not web controllers).

### Domain modules / screens

| # | Screen (VN) | Legacy panel | Route |
|---|-------------|--------------|-------|
| 1 | Đăng nhập (Login) | `pnl_DangNhap` | `/login` |
| 2 | Thống kê (Dashboard/Statistics) | `Pnl_2_ThongKeSanPham` | `/` |
| 3 | Sản phẩm (Products) | `Pnl_3_qlSanPham` | `/san-pham` |
| 4 | Nhân viên (Employees) | `pnl_4_NhanVien` | `/nhan-vien` |
| 5 | Khách hàng (Customers) | `Pnl_5_qlKhachHang` | `/khach-hang` |
| 6 | Hóa đơn / POS (Invoices) | `Pnl_6_qlHoaDon` | `/hoa-don` |
| 7 | Lịch sử hóa đơn (Invoice history) | `Pnl_7_LichSuHoaDon` | `/lich-su` |
| 8 | Phiếu giảm giá (Vouchers) | `Pnl_8_PhieuGiamGia` | `/phieu-giam-gia` |
| 9 | Hệ thống (System) | sidebar `btn_formHeThong` | `/he-thong` |

Notes:
- The **Hệ thống** screen's primary function is **Logout** (per user clarification).
- Sidebar order mirrors the Swing `Form_Home`: Doanh Thu, Sản Phẩm, Hóa Đơn, Nhân Viên,
  Khách Hàng, Lịch Sử, Khuyến Mãi, Hệ Thống, Đăng nhập.
- Default landing screen for an ADMIN role is Thống kê (`/`).

## Stack decision

**Thymeleaf + Vue 3 (CDN) + Bootstrap 5 (CDN), one Spring Boot app on port 8085.**

Rationale:
- Single app / single port (`server.port=8085`) — satisfies the "run local port 8085 only"
  constraint. A separate Vite SPA would need a second dev server or an npm build step.
- `spring-boot-starter-thymeleaf` is already in `pom.xml`; no new dependencies.
- Server-rendered layout + Vue for progressive interactivity (reactive tables, filters, form
  binding, modals) is a standard pattern for internal back-office tools like this.
- No npm/build toolchain; viewable as soon as Spring Boot starts.
- Phase 2 friendly: templates later swap inline mock JSON for controller model data or
  `fetch()` calls to REST endpoints.

## Architecture

### Target package layout
```
src/main/java/com/vn/test/bshoes/
  ├─ BshoesApplication.java        (exists)
  ├─ controller/                   Phase 2: @Controller (pages) + @RestController (data)
  ├─ service/                      Phase 2: interfaces + impl (from DAOImpl logic)
  ├─ repository/                   Phase 2: Spring Data JPA repos w/ @Query
  ├─ entity/                       user's existing POJOs (JPA annotations added later)
  └─ config/                       (minimal)
src/main/resources/
  ├─ application.properties        server.port=8085
  ├─ templates/
  │   ├─ layout/main.html          Thymeleaf layout fragment: header + green sidebar
  │   ├─ login.html
  │   ├─ dashboard.html            (Thống kê, route /)
  │   ├─ san-pham.html
  │   ├─ nhan-vien.html
  │   ├─ khach-hang.html
  │   ├─ hoa-don.html
  │   ├─ lich-su.html
  │   ├─ phieu-giam-gia.html
  │   └─ he-thong.html
  └─ static/
      ├─ css/app.css               green theme + shared styles
      └─ js/                       (optional per-page Vue app scripts; inline is fine)
```

### Phase 1 page structure (each screen)
- Extends the shared `layout/main.html` fragment (header + sidebar with active-link
  highlighting).
- A page-scoped Vue 3 app (`Vue.createApp({...}).mount('#app')`) with **inline mock data in
  `data()`** (per user: "inline, will be clear later").
- Bootstrap 5 for layout, tables, forms, modals, buttons.
- Interactions demonstrated client-side against mock data: search/filter, row selection,
  add/edit/delete via modal forms, navigation between screens, and the POS cart flow.

### Screen content (fields sourced from legacy entities)
- **Login:** username, password, submit; shows role concept (ADMIN vs staff).
- **Thống kê:** summary cards (revenue, product counts) + a statistics table
  (`ThongKeDoanhThu`, `ThongKeSanPham`).
- **Sản phẩm:** product list (`SanPham`/`SanPham_ql`) + product-detail (`SanPhamChiTiet`)
  with attribute pickers — chất liệu, màu sắc, kích cỡ, thương hiệu, xuất xứ, kiểu dáng,
  kiểu cổ giày, kiểu dây giày, loại sản phẩm. Search/filter + CRUD modal.
- **Nhân viên:** staff table (`NhanVien`) + CRUD form, vai trò (`VaiTro`), địa chỉ (`DiaChi`).
- **Khách hàng:** customer table (`KhachHang`) + CRUD form + địa chỉ.
- **Hóa đơn (POS):** product grid, invoice-detail cart (`HoaDon` + `HoaDonChiTiet`), customer
  select, voucher apply (`PhieuGiamGia`), totals.
- **Lịch sử hóa đơn:** invoice list with filters + detail view (`LichSuHoaDon`).
- **Phiếu giảm giá:** voucher table + CRUD form (`PhieuGiamGia`).
- **Hệ thống:** system info panel; primary action **Logout**.

### Theme
Green `#0B895A` header + sidebar, white content area, Segoe UI. Reproduces the Swing look.

## Phase 2 (outline only — not built in this pass)

For each `DAOImpl_*`, produce:
1. A Spring Data JPA `repository` interface; raw SQL translated to `@Query` (native where
   needed). Methods mirror the DAO (`findAll`, `findById`, `findByName`, custom queries).
2. A `@Service` interface + implementation holding logic currently in the DAOImpl and the
   Swing `Controller_*`/`Pnl_*` classes (e.g. auto-generating codes like `"CL" + id`).
3. A web `@Controller` (returns Thymeleaf views) and/or `@RestController` (JSON for Vue),
   replacing inline mock data with real data.

Blocked on: user adding JPA annotations to `entity/*`. Accepted — won't run until then.

## Testing / running

- **Phase 1:** No automated tests, no DB. Manual verification = start Spring Boot, browse
  screens on `http://localhost:8085`. (Per user: "no encoding or test run yet.")
- **Phase 2:** Deferred until entities are mapped.

## Scope guardrails (YAGNI)

- No authentication/authorization implementation in Phase 1 (login is a mock form).
- No database, no real persistence, no REST endpoints in Phase 1.
- No Vite/npm/SPA build.
- No changes to the legacy `ref/` code.
- No JPA entity annotation work (user owns that).

## Git

- Branch `feature/spring-boot-port` off `main`, pushed to `origin`.
- Phase 1 committed as its own set of commits.
