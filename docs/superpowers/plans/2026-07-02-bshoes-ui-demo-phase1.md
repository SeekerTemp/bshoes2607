# BShoes UI Demo (Phase 1) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a viewable HTML UI demo of all 9 BShoes screens, faithful to the legacy NetBeans flow, served by a single Spring Boot app on port 8085 using Thymeleaf + Vue 3 (CDN) + Bootstrap 5 (CDN) with inline mock data.

**Architecture:** One Spring Boot app. A thin view-only `PageController` maps each route to a Thymeleaf template. All templates reuse one layout fragment (`layout/main.html`) that renders the green header + sidebar. Each page hosts a self-contained Vue 3 app with inline mock data in `data()`. No database, no persistence, no REST, no automated tests (per spec: "no encoding or test run yet"). Verification is manual: start the app and browse `http://localhost:8085`.

**Tech Stack:** Spring Boot 4.1 (Java 17), Thymeleaf, Vue 3 (global build via CDN), Bootstrap 5.3 (CDN). No npm/Vite.

**Design spec:** `docs/superpowers/specs/2026-07-02-bshoes-spring-boot-port-design.md`

---

## Conventions used by every screen

- **Delimiters:** Thymeleaf processes only `th:*` attributes; Vue keeps its default `{{ }}` mustaches. They do not collide.
- **Vue load:** `https://unpkg.com/vue@3/dist/vue.global.prod.js` (global `Vue`). Each page ends with an inline `<script>` calling `Vue.createApp({ delimiters: ['[[', ']]'], data() {...}, methods: {...} }).mount('#app')`. **We deliberately set Vue delimiters to `[[ ]]`** to avoid any ambiguity with Thymeleaf and to make the demo copy-paste-safe. Use `[[ expr ]]` in markup.
- **Bootstrap:** CSS + `bootstrap.bundle.min.js` from jsDelivr (needed for modals/dropdowns).
- **Money/format:** mock data uses plain numbers; format inline with a `vnd` method (`n.toLocaleString('vi-VN')`).
- **Every page** is a Thymeleaf template that replaces the layout fragment and supplies `title`, `section` (content), and an `active` nav key.

---

## Task 1: Configure app port and Thymeleaf

**Files:**
- Modify: `src/main/resources/application.properties`

- [ ] **Step 1: Set port and dev-friendly Thymeleaf settings**

Replace the contents of `src/main/resources/application.properties` with:

```properties
spring.application.name=bshoes
server.port=8085

# Thymeleaf: disable template cache so edits show on refresh during the demo
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Phase 1 has no datasource yet; do not fail startup on missing DB config.
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
```

- [ ] **Step 2: Verify the app starts**

Run: `./mvnw spring-boot:run` (from `bshoes2607/`). On Windows PowerShell use `.\mvnw.cmd spring-boot:run`.
Expected: log line `Tomcat started on port 8085` and no `Failed to configure a DataSource` error. Stop with Ctrl+C.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/application.properties
git commit -m "chore: run on port 8085, exclude datasource for Phase 1 UI demo"
```

---

## Task 2: Page view controller

**Files:**
- Create: `src/main/java/com/vn/test/bshoes/controller/PageController.java`

This is a Phase-1 view-only controller (distinct from Phase-2 data controllers). It maps each route to a template name.

- [ ] **Step 1: Create the controller**

```java
package com.vn.test.bshoes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Phase 1 view-only controller. Maps each demo route to its Thymeleaf template.
 * Replaced/augmented by real data controllers in Phase 2.
 */
@Controller
public class PageController {

    @GetMapping("/")               public String dashboard() { return "dashboard"; }
    @GetMapping("/login")          public String login()     { return "login"; }
    @GetMapping("/san-pham")       public String sanPham()   { return "san-pham"; }
    @GetMapping("/nhan-vien")      public String nhanVien()  { return "nhan-vien"; }
    @GetMapping("/khach-hang")     public String khachHang() { return "khach-hang"; }
    @GetMapping("/hoa-don")        public String hoaDon()    { return "hoa-don"; }
    @GetMapping("/lich-su")        public String lichSu()    { return "lich-su"; }
    @GetMapping("/phieu-giam-gia") public String phieuGiam() { return "phieu-giam-gia"; }
    @GetMapping("/he-thong")       public String heThong()   { return "he-thong"; }
}
```

- [ ] **Step 2: Commit** (templates come next; app will 500 on these routes until Task 3–4 add templates — that's expected)

```bash
git add src/main/java/com/vn/test/bshoes/controller/PageController.java
git commit -m "feat: add Phase 1 view-only PageController with all demo routes"
```

---

## Task 3: Shared layout fragment + theme CSS

**Files:**
- Create: `src/main/resources/templates/layout/main.html`
- Create: `src/main/resources/static/css/app.css`

- [ ] **Step 1: Create the layout fragment**

`src/main/resources/templates/layout/main.html`:

```html
<!DOCTYPE html>
<html th:fragment="layout(title, section, active)" lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title th:replace="${title}">BShoes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" th:href="@{/css/app.css}">
</head>
<body>
    <header class="app-header d-flex align-items-center px-3">
        <span class="fs-4 fw-bold text-white">BShoes</span>
        <span class="ms-2 text-white-50">Quản lý cửa hàng giày</span>
    </header>
    <div class="app-shell">
        <nav class="app-sidebar">
            <a th:href="@{/}"               th:classappend="${active=='dashboard'}?'active'" class="nav-item">Doanh Thu</a>
            <a th:href="@{/san-pham}"       th:classappend="${active=='sanpham'}?'active'"   class="nav-item">Sản Phẩm</a>
            <a th:href="@{/hoa-don}"        th:classappend="${active=='hoadon'}?'active'"    class="nav-item">Hóa Đơn</a>
            <a th:href="@{/nhan-vien}"      th:classappend="${active=='nhanvien'}?'active'"  class="nav-item">Nhân Viên</a>
            <a th:href="@{/khach-hang}"     th:classappend="${active=='khachhang'}?'active'" class="nav-item">Khách Hàng</a>
            <a th:href="@{/lich-su}"        th:classappend="${active=='lichsu'}?'active'"    class="nav-item">Lịch Sử</a>
            <a th:href="@{/phieu-giam-gia}" th:classappend="${active=='phieu'}?'active'"     class="nav-item">Khuyến Mãi</a>
            <a th:href="@{/he-thong}"       th:classappend="${active=='hethong'}?'active'"   class="nav-item">Hệ Thống</a>
            <a th:href="@{/login}"          th:classappend="${active=='login'}?'active'"     class="nav-item mt-auto">Đăng Nhập</a>
        </nav>
        <main class="app-content">
            <div th:replace="${section}">content</div>
        </main>
    </div>
    <script src="https://unpkg.com/vue@3/dist/vue.global.prod.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
```

- [ ] **Step 2: Create the theme CSS**

`src/main/resources/static/css/app.css`:

```css
:root { --bs-green: #0B895A; --bs-green-2: #0E9F67; }
* { box-sizing: border-box; }
body { margin: 0; font-family: "Segoe UI", Roboto, sans-serif; background: #f4f6f8; }
.app-header { height: 48px; background: var(--bs-green); }
.app-shell { display: flex; min-height: calc(100vh - 48px); }
.app-sidebar { width: 210px; background: var(--bs-green); display: flex; flex-direction: column; padding-top: 8px; }
.app-sidebar .nav-item { color: #fff; padding: 12px 18px; text-decoration: none; font-weight: 600; border: none; }
.app-sidebar .nav-item:hover { background: var(--bs-green-2); }
.app-sidebar .nav-item.active { background: #fff; color: var(--bs-green); }
.app-sidebar .mt-auto { margin-top: auto; }
.app-content { flex: 1; padding: 20px; overflow: auto; }
.card-stat { border-left: 4px solid var(--bs-green); }
.pos-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 16px; }
```

- [ ] **Step 3: Verify** — done together with Task 4 (need a page to render the layout).

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/templates/layout/main.html src/main/resources/static/css/app.css
git commit -m "feat: add shared Thymeleaf layout fragment and green theme CSS"
```

---

## Task 4: Login screen (`/login`) — validates layout wiring

**Files:**
- Create: `src/main/resources/templates/login.html`

- [ ] **Step 1: Create the login template**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/main :: layout(~{::title}, ~{::section}, 'login')}">
<head><title>Đăng nhập — BShoes</title></head>
<body>
<section>
    <div id="app" class="d-flex justify-content-center align-items-center" style="min-height:70vh">
        <div class="card shadow-sm" style="width:360px">
            <div class="card-body">
                <h4 class="text-center mb-3" style="color:var(--bs-green)">Đăng nhập</h4>
                <div class="mb-3">
                    <label class="form-label">Tài khoản</label>
                    <input class="form-control" v-model="taiKhoan" placeholder="admin">
                </div>
                <div class="mb-3">
                    <label class="form-label">Mật khẩu</label>
                    <input type="password" class="form-control" v-model="matKhau">
                </div>
                <button class="btn w-100 text-white" style="background:var(--bs-green)" @click="dangNhap">Đăng nhập</button>
                <p class="text-muted small mt-3 mb-0">Demo: vai trò ADMIN vào thẳng màn hình Thống kê.</p>
                <p v-if="thongBao" class="text-danger small mt-2">[[ thongBao ]]</p>
            </div>
        </div>
    </div>
</section>
<script>
Vue.createApp({
    delimiters: ['[[', ']]'],
    data() { return { taiKhoan: '', matKhau: '', thongBao: '' }; },
    methods: {
        dangNhap() {
            if (!this.taiKhoan || !this.matKhau) { this.thongBao = 'Nhập tài khoản và mật khẩu.'; return; }
            window.location.href = '/';   // demo: go to dashboard
        }
    }
}).mount('#app');
</script>
</body>
</html>
```

- [ ] **Step 2: Verify layout + login render**

Run: `.\mvnw.cmd spring-boot:run`, open `http://localhost:8085/login`.
Expected: green header + sidebar; centered login card; sidebar "Đăng Nhập" highlighted. In a second terminal (optional) `curl -s -o NUL -w "%{http_code}" http://localhost:8085/login` → `200`.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/login.html
git commit -m "feat: add login demo screen and verify layout wiring"
```

---

## Task 5: Dashboard / Thống kê (`/`)

**Files:**
- Create: `src/main/resources/templates/dashboard.html`

Content from `ThongKeDoanhThu` (thang, soSanPhamBan, tongDoanhThu, donCho, soDon, donThanhCong, donHuy, doanhThuNgay/Thang/Nam) and `ThongKeSanPham` (maSP, tenSP, loaiSP, chatLieu, mauSac, kichThuoc, soLuongTon, soLuongBan, doanhThu).

- [ ] **Step 1: Create the template**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/main :: layout(~{::title}, ~{::section}, 'dashboard')}">
<head><title>Thống kê — BShoes</title></head>
<body>
<section>
    <div id="app">
        <h4 class="mb-3">Thống kê doanh thu</h4>
        <div class="row g-3 mb-4">
            <div class="col" v-for="c in cards" :key="c.label">
                <div class="card card-stat shadow-sm"><div class="card-body">
                    <div class="text-muted small">[[ c.label ]]</div>
                    <div class="fs-4 fw-bold">[[ c.value ]]</div>
                </div></div>
            </div>
        </div>
        <h5 class="mb-2">Sản phẩm bán chạy</h5>
        <table class="table table-striped bg-white">
            <thead><tr>
                <th>Mã SP</th><th>Tên</th><th>Loại</th><th>Chất liệu</th><th>Màu</th>
                <th>Size</th><th class="text-end">Tồn</th><th class="text-end">Đã bán</th><th class="text-end">Doanh thu</th>
            </tr></thead>
            <tbody>
                <tr v-for="p in sanPham" :key="p.maSP">
                    <td>[[ p.maSP ]]</td><td>[[ p.tenSP ]]</td><td>[[ p.loaiSP ]]</td>
                    <td>[[ p.chatLieu ]]</td><td>[[ p.mauSac ]]</td><td>[[ p.kichThuoc ]]</td>
                    <td class="text-end">[[ p.soLuongTon ]]</td><td class="text-end">[[ p.soLuongBan ]]</td>
                    <td class="text-end">[[ vnd(p.doanhThu) ]]</td>
                </tr>
            </tbody>
        </table>
    </div>
</section>
<script>
Vue.createApp({
    delimiters: ['[[', ']]'],
    data() { return {
        cards: [
            { label: 'Doanh thu tháng', value: '128.500.000 ₫' },
            { label: 'Số đơn', value: 342 },
            { label: 'Đơn thành công', value: 310 },
            { label: 'Đơn chờ', value: 18 },
            { label: 'Đơn huỷ', value: 14 },
        ],
        sanPham: [
            { maSP:'SP1', tenSP:'Nike Air Zoom', loaiSP:'Thể thao', chatLieu:'Vải', mauSac:'Đen', kichThuoc:'42', soLuongTon:35, soLuongBan:120, doanhThu:240000000 },
            { maSP:'SP2', tenSP:'Adidas Ultraboost', loaiSP:'Chạy bộ', chatLieu:'Primeknit', mauSac:'Trắng', kichThuoc:'41', soLuongTon:22, soLuongBan:98, doanhThu:196000000 },
            { maSP:'SP3', tenSP:'Converse Classic', loaiSP:'Lifestyle', chatLieu:'Canvas', mauSac:'Đỏ', kichThuoc:'40', soLuongTon:60, soLuongBan:75, doanhThu:90000000 },
        ]
    }; },
    methods: { vnd(n){ return (n||0).toLocaleString('vi-VN') + ' ₫'; } }
}).mount('#app');
</script>
</body>
</html>
```

- [ ] **Step 2: Verify** — open `http://localhost:8085/` → cards + product table; "Doanh Thu" nav active.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/dashboard.html
git commit -m "feat: add Thong Ke dashboard demo screen"
```

---

## Canonical CRUD screen pattern (used by Tasks 6, 8, 9, 11)

Tasks 8 (Khách hàng), 9 handled separately (POS differs), and the catalog-style CRUD screens follow **this exact pattern**: a search bar, a data table, and a Bootstrap modal add/edit form bound to a `form` object. **Task 11 below is the fully-worked reference implementation.** For Tasks 6, 7, 8 you reproduce the same structure (copy Task 11's markup/script) substituting the column list, form fields, and mock rows given in each task. Do not abbreviate — paste the full pattern and swap the fields.

---

## Task 6: Phiếu giảm giá / Vouchers (`/phieu-giam-gia`) — reference CRUD implementation

**Files:**
- Create: `src/main/resources/templates/phieu-giam-gia.html`

Fields from `PhieuGiamGia`: ma_phieu_giam, ten_phieu_giam, loai_giam_gia (0=%,1=tiền), gia_tri_giam, don_toi_thieu, giam_toi_da, so_luong, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai.

- [ ] **Step 1: Create the template**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/main :: layout(~{::title}, ~{::section}, 'phieu')}">
<head><title>Phiếu giảm giá — BShoes</title></head>
<body>
<section>
    <div id="app">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="mb-0">Phiếu giảm giá</h4>
            <button class="btn text-white" style="background:var(--bs-green)" @click="moThem">+ Thêm phiếu</button>
        </div>
        <input class="form-control mb-3" style="max-width:320px" v-model="tuKhoa" placeholder="Tìm theo mã / tên...">
        <table class="table table-hover bg-white align-middle">
            <thead><tr>
                <th>Mã</th><th>Tên</th><th>Loại</th><th class="text-end">Giá trị</th>
                <th class="text-end">Đơn tối thiểu</th><th class="text-end">SL</th><th>Bắt đầu</th><th>Kết thúc</th>
                <th>Trạng thái</th><th></th>
            </tr></thead>
            <tbody>
                <tr v-for="p in ketQua" :key="p.id">
                    <td>[[ p.ma ]]</td><td>[[ p.ten ]]</td>
                    <td>[[ p.loai === 0 ? '% ' : 'Tiền' ]]</td>
                    <td class="text-end">[[ p.loai === 0 ? p.giaTri + '%' : vnd(p.giaTri) ]]</td>
                    <td class="text-end">[[ vnd(p.donToiThieu) ]]</td>
                    <td class="text-end">[[ p.soLuong ]]</td>
                    <td>[[ p.batDau ]]</td><td>[[ p.ketThuc ]]</td>
                    <td><span class="badge" :class="p.trangThai ? 'bg-success' : 'bg-secondary'">[[ p.trangThai ? 'Hoạt động' : 'Ngừng' ]]</span></td>
                    <td class="text-end">
                        <button class="btn btn-sm btn-outline-secondary" @click="moSua(p)">Sửa</button>
                        <button class="btn btn-sm btn-outline-danger" @click="xoa(p)">Xoá</button>
                    </td>
                </tr>
                <tr v-if="ketQua.length === 0"><td colspan="10" class="text-center text-muted">Không có dữ liệu</td></tr>
            </tbody>
        </table>

        <!-- Modal add/edit -->
        <div class="modal fade" ref="modalEl" tabindex="-1">
            <div class="modal-dialog"><div class="modal-content">
                <div class="modal-header"><h5 class="modal-title">[[ form.id ? 'Sửa' : 'Thêm' ]] phiếu giảm giá</h5>
                    <button class="btn-close" data-bs-dismiss="modal"></button></div>
                <div class="modal-body">
                    <div class="mb-2"><label class="form-label">Tên</label><input class="form-control" v-model="form.ten"></div>
                    <div class="mb-2"><label class="form-label">Loại</label>
                        <select class="form-select" v-model.number="form.loai"><option :value="0">Phần trăm</option><option :value="1">Số tiền</option></select></div>
                    <div class="row">
                        <div class="col mb-2"><label class="form-label">Giá trị giảm</label><input type="number" class="form-control" v-model.number="form.giaTri"></div>
                        <div class="col mb-2"><label class="form-label">Số lượng</label><input type="number" class="form-control" v-model.number="form.soLuong"></div>
                    </div>
                    <div class="row">
                        <div class="col mb-2"><label class="form-label">Đơn tối thiểu</label><input type="number" class="form-control" v-model.number="form.donToiThieu"></div>
                        <div class="col mb-2"><label class="form-label">Giảm tối đa</label><input type="number" class="form-control" v-model.number="form.giamToiDa"></div>
                    </div>
                    <div class="row">
                        <div class="col mb-2"><label class="form-label">Bắt đầu</label><input type="date" class="form-control" v-model="form.batDau"></div>
                        <div class="col mb-2"><label class="form-label">Kết thúc</label><input type="date" class="form-control" v-model="form.ketThuc"></div>
                    </div>
                    <div class="form-check"><input class="form-check-input" type="checkbox" v-model="form.trangThai" id="tt"><label class="form-check-label" for="tt">Hoạt động</label></div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" data-bs-dismiss="modal">Huỷ</button>
                    <button class="btn text-white" style="background:var(--bs-green)" @click="luu">Lưu</button>
                </div>
            </div></div>
        </div>
    </div>
</section>
<script>
Vue.createApp({
    delimiters: ['[[', ']]'],
    data() { return {
        tuKhoa: '', modal: null, seq: 3,
        form: { id: null, ma: '', ten: '', loai: 0, giaTri: 0, donToiThieu: 0, giamToiDa: 0, soLuong: 0, batDau: '', ketThuc: '', trangThai: true },
        rows: [
            { id:1, ma:'PGG1', ten:'Giảm 10% toàn shop', loai:0, giaTri:10, donToiThieu:500000, giamToiDa:100000, soLuong:50, batDau:'2026-07-01', ketThuc:'2026-07-31', trangThai:true },
            { id:2, ma:'PGG2', ten:'Giảm 50k', loai:1, giaTri:50000, donToiThieu:300000, giamToiDa:50000, soLuong:100, batDau:'2026-07-01', ketThuc:'2026-08-15', trangThai:true },
            { id:3, ma:'PGG3', ten:'Flash sale', loai:0, giaTri:25, donToiThieu:1000000, giamToiDa:300000, soLuong:20, batDau:'2026-06-01', ketThuc:'2026-06-30', trangThai:false },
        ]
    }; },
    computed: {
        ketQua() { const k = this.tuKhoa.toLowerCase();
            return this.rows.filter(r => r.ma.toLowerCase().includes(k) || r.ten.toLowerCase().includes(k)); }
    },
    mounted() { this.modal = new bootstrap.Modal(this.$refs.modalEl); },
    methods: {
        vnd(n){ return (n||0).toLocaleString('vi-VN') + ' ₫'; },
        moThem() { this.form = { id:null, ma:'', ten:'', loai:0, giaTri:0, donToiThieu:0, giamToiDa:0, soLuong:0, batDau:'', ketThuc:'', trangThai:true }; this.modal.show(); },
        moSua(p) { this.form = JSON.parse(JSON.stringify(p)); this.modal.show(); },
        luu() {
            if (this.form.id) { const i = this.rows.findIndex(r => r.id === this.form.id); this.rows.splice(i, 1, this.form); }
            else { this.form.id = ++this.seq; this.form.ma = 'PGG' + this.form.id; this.rows.push(this.form); }
            this.modal.hide();
        },
        xoa(p) { if (confirm('Xoá phiếu ' + p.ma + '?')) this.rows = this.rows.filter(r => r.id !== p.id); }
    }
}).mount('#app');
</script>
</body>
</html>
```

- [ ] **Step 2: Verify** — open `/phieu-giam-gia`: table renders, search filters, "+ Thêm" opens modal, save adds a row, edit/delete work.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/phieu-giam-gia.html
git commit -m "feat: add Phieu Giam Gia CRUD demo (reference pattern)"
```

---

## Task 7: Khách hàng / Customers (`/khach-hang`)

**Files:**
- Create: `src/main/resources/templates/khach-hang.html`

Reproduce the Task 6 pattern (search + table + modal, `active='khachhang'`, title "Khách hàng — BShoes"). Fields from `KhachHang`: ma_khach_hang, ten_khach_hang, gioi_tinh, so_dien_thoai, email, dia_chi, trang_thai.

- [ ] **Step 1: Create the template** — copy the full structure from Task 6 and substitute:
  - **Table columns:** Mã KH, Tên, Giới tính, SĐT, Email, Địa chỉ, Trạng thái, (actions).
  - **Form fields:** `ten` (text), `gioiTinh` (`<select>` Nam/Nữ), `sdt` (text), `email` (email), `diaChi` (text), `trangThai` (checkbox).
  - **`form` object:** `{ id:null, ma:'', ten:'', gioiTinh:'Nam', sdt:'', email:'', diaChi:'', trangThai:true }`.
  - **Code prefix:** `'KH' + id`.
  - **`rows` mock (3 rows):**
```javascript
[
  { id:1, ma:'KH1', ten:'Nguyễn Văn A', gioiTinh:'Nam', sdt:'0901111111', email:'a@gmail.com', diaChi:'Hà Nội', trangThai:true },
  { id:2, ma:'KH2', ten:'Trần Thị B', gioiTinh:'Nữ', sdt:'0902222222', email:'b@gmail.com', diaChi:'Đà Nẵng', trangThai:true },
  { id:3, ma:'KH3', ten:'Lê Văn C', gioiTinh:'Nam', sdt:'0903333333', email:'c@gmail.com', diaChi:'TP.HCM', trangThai:false },
]
```
  - **`computed.ketQua` filter:** match on `ma`, `ten`, or `sdt`.

- [ ] **Step 2: Verify** — `/khach-hang`: table, search, add/edit/delete via modal.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/khach-hang.html
git commit -m "feat: add Khach Hang CRUD demo screen"
```

---

## Task 8: Nhân viên / Employees (`/nhan-vien`)

**Files:**
- Create: `src/main/resources/templates/nhan-vien.html`

Reproduce the Task 6 pattern (`active='nhanvien'`, title "Nhân viên — BShoes"). Fields from `NhanVien` + `VaiTro`: ma_nhan_vien, ten_nhan_vien, tai_khoan, email, so_dien_thoai, cccd, chuc_vu, gioi_tinh, ten_vai_tro, trang_thai.

- [ ] **Step 1: Create the template** — copy Task 6 structure and substitute:
  - **Table columns:** Mã NV, Tên, Tài khoản, Email, SĐT, CCCD, Chức vụ, Vai trò, Trạng thái, (actions).
  - **Form fields:** `ten`, `taiKhoan`, `email`, `sdt`, `cccd`, `chucVu` (text), `gioiTinh` (select Nam/Nữ), `vaiTro` (`<select>` with options `ADMIN`, `NHÂN VIÊN`), `trangThai` (checkbox).
  - **`form` object:** `{ id:null, ma:'', ten:'', taiKhoan:'', email:'', sdt:'', cccd:'', chucVu:'', gioiTinh:'Nam', vaiTro:'NHÂN VIÊN', trangThai:true }`.
  - **Code prefix:** `'NV' + id`.
  - **`rows` mock (3 rows):**
```javascript
[
  { id:1, ma:'NV1', ten:'Phạm Quản Trị', taiKhoan:'admin', email:'admin@bshoes.vn', sdt:'0900000001', cccd:'012345678901', chucVu:'Quản lý', gioiTinh:'Nam', vaiTro:'ADMIN', trangThai:true },
  { id:2, ma:'NV2', ten:'Hoàng Bán Hàng', taiKhoan:'hoangbh', email:'hoang@bshoes.vn', sdt:'0900000002', cccd:'012345678902', chucVu:'Bán hàng', gioiTinh:'Nam', vaiTro:'NHÂN VIÊN', trangThai:true },
  { id:3, ma:'NV3', ten:'Vũ Thu Ngân', taiKhoan:'vungan', email:'ngan@bshoes.vn', sdt:'0900000003', cccd:'012345678903', chucVu:'Thu ngân', gioiTinh:'Nữ', vaiTro:'NHÂN VIÊN', trangThai:false },
]
```
  - **`computed.ketQua` filter:** match on `ma`, `ten`, or `taiKhoan`.

- [ ] **Step 2: Verify** — `/nhan-vien`: table, search, add/edit/delete.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/nhan-vien.html
git commit -m "feat: add Nhan Vien CRUD demo screen"
```

---

## Task 9: Sản phẩm / Products (`/san-pham`)

**Files:**
- Create: `src/main/resources/templates/san-pham.html`

This screen is richer than the basic CRUD pattern: a product master list plus a product-detail (variant) sub-table and attribute selectors. Fields from `SanPham` (ten_san_pham, mo_ta, + attribute FKs) and `SanPhamChiTiet` (ma, id_mau_sac, id_kich_co, so_luong_ton, don_gia).

- [ ] **Step 1: Create the template**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/main :: layout(~{::title}, ~{::section}, 'sanpham')}">
<head><title>Sản phẩm — BShoes</title></head>
<body>
<section>
    <div id="app">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="mb-0">Quản lý sản phẩm</h4>
            <button class="btn text-white" style="background:var(--bs-green)" @click="moThem">+ Thêm sản phẩm</button>
        </div>
        <div class="d-flex gap-2 mb-3">
            <input class="form-control" style="max-width:280px" v-model="tuKhoa" placeholder="Tìm mã / tên...">
            <select class="form-select" style="max-width:200px" v-model="locThuongHieu">
                <option value="">-- Thương hiệu --</option>
                <option v-for="t in thuongHieu" :key="t" :value="t">[[ t ]]</option>
            </select>
        </div>
        <div class="row g-3">
            <div class="col-7">
                <table class="table table-hover bg-white align-middle">
                    <thead><tr><th>Mã</th><th>Tên</th><th>Thương hiệu</th><th>Chất liệu</th><th class="text-end">Giá</th><th></th></tr></thead>
                    <tbody>
                        <tr v-for="p in ketQua" :key="p.id" :class="{'table-success': p.id === chon?.id}" @click="chonSP(p)" style="cursor:pointer">
                            <td>[[ p.ma ]]</td><td>[[ p.ten ]]</td><td>[[ p.thuongHieu ]]</td><td>[[ p.chatLieu ]]</td>
                            <td class="text-end">[[ vnd(p.gia) ]]</td>
                            <td class="text-end"><button class="btn btn-sm btn-outline-secondary" @click.stop="moSua(p)">Sửa</button></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="col-5">
                <div class="card"><div class="card-body">
                    <h6 v-if="!chon" class="text-muted">Chọn 1 sản phẩm để xem biến thể</h6>
                    <div v-else>
                        <h6 class="mb-2">Biến thể: [[ chon.ten ]]</h6>
                        <table class="table table-sm">
                            <thead><tr><th>Màu</th><th>Size</th><th class="text-end">Tồn</th><th class="text-end">Đơn giá</th></tr></thead>
                            <tbody>
                                <tr v-for="v in chon.bienThe" :key="v.ma">
                                    <td>[[ v.mau ]]</td><td>[[ v.size ]]</td><td class="text-end">[[ v.ton ]]</td><td class="text-end">[[ vnd(v.gia) ]]</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div></div>
            </div>
        </div>

        <div class="modal fade" ref="modalEl" tabindex="-1">
            <div class="modal-dialog"><div class="modal-content">
                <div class="modal-header"><h5 class="modal-title">[[ form.id ? 'Sửa' : 'Thêm' ]] sản phẩm</h5>
                    <button class="btn-close" data-bs-dismiss="modal"></button></div>
                <div class="modal-body">
                    <div class="mb-2"><label class="form-label">Tên</label><input class="form-control" v-model="form.ten"></div>
                    <div class="row">
                        <div class="col mb-2"><label class="form-label">Thương hiệu</label>
                            <select class="form-select" v-model="form.thuongHieu"><option v-for="t in thuongHieu" :key="t">[[ t ]]</option></select></div>
                        <div class="col mb-2"><label class="form-label">Chất liệu</label>
                            <select class="form-select" v-model="form.chatLieu"><option v-for="c in chatLieu" :key="c">[[ c ]]</option></select></div>
                    </div>
                    <div class="mb-2"><label class="form-label">Giá</label><input type="number" class="form-control" v-model.number="form.gia"></div>
                    <div class="mb-2"><label class="form-label">Mô tả</label><textarea class="form-control" v-model="form.moTa"></textarea></div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" data-bs-dismiss="modal">Huỷ</button>
                    <button class="btn text-white" style="background:var(--bs-green)" @click="luu">Lưu</button>
                </div>
            </div></div>
        </div>
    </div>
</section>
<script>
Vue.createApp({
    delimiters: ['[[', ']]'],
    data() { return {
        tuKhoa: '', locThuongHieu: '', chon: null, modal: null, seq: 3,
        thuongHieu: ['Nike','Adidas','Converse'], chatLieu: ['Vải','Da','Canvas','Primeknit'],
        form: { id:null, ma:'', ten:'', thuongHieu:'Nike', chatLieu:'Vải', gia:0, moTa:'' },
        rows: [
            { id:1, ma:'SP1', ten:'Nike Air Zoom', thuongHieu:'Nike', chatLieu:'Vải', gia:2000000, moTa:'Giày chạy bộ',
              bienThe:[{ma:'SP1-D42',mau:'Đen',size:'42',ton:12,gia:2000000},{ma:'SP1-T41',mau:'Trắng',size:'41',ton:8,gia:2000000}] },
            { id:2, ma:'SP2', ten:'Adidas Ultraboost', thuongHieu:'Adidas', chatLieu:'Primeknit', gia:2500000, moTa:'Đệm Boost',
              bienThe:[{ma:'SP2-T40',mau:'Trắng',size:'40',ton:5,gia:2500000}] },
            { id:3, ma:'SP3', ten:'Converse Classic', thuongHieu:'Converse', chatLieu:'Canvas', gia:1200000, moTa:'Cổ điển',
              bienThe:[{ma:'SP3-D39',mau:'Đỏ',size:'39',ton:20,gia:1200000}] },
        ]
    }; },
    computed: {
        ketQua() { const k = this.tuKhoa.toLowerCase();
            return this.rows.filter(r => (r.ma.toLowerCase().includes(k) || r.ten.toLowerCase().includes(k))
                && (!this.locThuongHieu || r.thuongHieu === this.locThuongHieu)); }
    },
    mounted() { this.modal = new bootstrap.Modal(this.$refs.modalEl); },
    methods: {
        vnd(n){ return (n||0).toLocaleString('vi-VN') + ' ₫'; },
        chonSP(p) { this.chon = p; },
        moThem() { this.form = { id:null, ma:'', ten:'', thuongHieu:'Nike', chatLieu:'Vải', gia:0, moTa:'' }; this.modal.show(); },
        moSua(p) { this.form = { id:p.id, ma:p.ma, ten:p.ten, thuongHieu:p.thuongHieu, chatLieu:p.chatLieu, gia:p.gia, moTa:p.moTa }; this.modal.show(); },
        luu() {
            if (this.form.id) { const i = this.rows.findIndex(r => r.id === this.form.id);
                this.rows[i].ten = this.form.ten; this.rows[i].thuongHieu = this.form.thuongHieu;
                this.rows[i].chatLieu = this.form.chatLieu; this.rows[i].gia = this.form.gia; this.rows[i].moTa = this.form.moTa; }
            else { const id = ++this.seq; this.rows.push({ id, ma:'SP'+id, ten:this.form.ten, thuongHieu:this.form.thuongHieu,
                chatLieu:this.form.chatLieu, gia:this.form.gia, moTa:this.form.moTa, bienThe:[] }); }
            this.modal.hide();
        }
    }
}).mount('#app');
</script>
</body>
</html>
```

- [ ] **Step 2: Verify** — `/san-pham`: list + brand filter + search; clicking a row shows its variants; add/edit via modal.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/san-pham.html
git commit -m "feat: add San Pham product management demo screen"
```

---

## Task 10: Hóa đơn / POS (`/hoa-don`)

**Files:**
- Create: `src/main/resources/templates/hoa-don.html`

POS layout from `Pnl_6_qlHoaDon`: left = product picker, right = current invoice (`HoaDon` + `HoaDonChiTiet` lines) with customer, voucher, totals.

- [ ] **Step 1: Create the template**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/main :: layout(~{::title}, ~{::section}, 'hoadon')}">
<head><title>Bán hàng — BShoes</title></head>
<body>
<section>
    <div id="app">
        <h4 class="mb-3">Bán hàng tại quầy</h4>
        <div class="pos-grid">
            <div class="card"><div class="card-body">
                <input class="form-control mb-3" v-model="tuKhoa" placeholder="Tìm sản phẩm...">
                <div class="row g-2">
                    <div class="col-6" v-for="p in ketQua" :key="p.id">
                        <div class="card h-100"><div class="card-body p-2">
                            <div class="fw-bold">[[ p.ten ]]</div>
                            <div class="small text-muted">[[ p.mau ]] • Size [[ p.size ]] • Tồn [[ p.ton ]]</div>
                            <div class="d-flex justify-content-between align-items-center mt-1">
                                <span>[[ vnd(p.gia) ]]</span>
                                <button class="btn btn-sm text-white" style="background:var(--bs-green)" @click="them(p)">Thêm</button>
                            </div>
                        </div></div>
                    </div>
                </div>
            </div></div>

            <div class="card"><div class="card-body">
                <h6>Hoá đơn hiện tại</h6>
                <div class="mb-2">
                    <label class="form-label small mb-0">Khách hàng</label>
                    <select class="form-select form-select-sm" v-model="khachHang">
                        <option>Khách lẻ</option><option>Nguyễn Văn A</option><option>Trần Thị B</option>
                    </select>
                </div>
                <table class="table table-sm align-middle">
                    <thead><tr><th>SP</th><th class="text-end">SL</th><th class="text-end">Đơn giá</th><th class="text-end">TT</th><th></th></tr></thead>
                    <tbody>
                        <tr v-for="l in gio" :key="l.id">
                            <td>[[ l.ten ]]<div class="small text-muted">[[ l.mau ]]/[[ l.size ]]</div></td>
                            <td class="text-end" style="width:70px"><input type="number" min="1" class="form-control form-control-sm text-end" v-model.number="l.soLuong"></td>
                            <td class="text-end">[[ vnd(l.gia) ]]</td>
                            <td class="text-end">[[ vnd(l.gia * l.soLuong) ]]</td>
                            <td class="text-end"><button class="btn btn-sm btn-outline-danger" @click="xoa(l)">×</button></td>
                        </tr>
                        <tr v-if="gio.length===0"><td colspan="5" class="text-center text-muted">Chưa có sản phẩm</td></tr>
                    </tbody>
                </table>
                <div class="mb-2">
                    <label class="form-label small mb-0">Phiếu giảm giá</label>
                    <select class="form-select form-select-sm" v-model="voucher">
                        <option :value="0">Không</option><option :value="0.1">Giảm 10%</option><option :value="50000">Giảm 50k</option>
                    </select>
                </div>
                <ul class="list-group list-group-flush">
                    <li class="list-group-item d-flex justify-content-between"><span>Tạm tính</span><b>[[ vnd(tamTinh) ]]</b></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Giảm giá</span><b>-[[ vnd(giamGia) ]]</b></li>
                    <li class="list-group-item d-flex justify-content-between fs-5"><span>Phải trả</span><b style="color:var(--bs-green)">[[ vnd(phaiTra) ]]</b></li>
                </ul>
                <button class="btn w-100 text-white mt-3" style="background:var(--bs-green)" :disabled="gio.length===0" @click="thanhToan">Thanh toán</button>
            </div></div>
        </div>
    </div>
</section>
<script>
Vue.createApp({
    delimiters: ['[[', ']]'],
    data() { return {
        tuKhoa: '', khachHang: 'Khách lẻ', voucher: 0, seq: 0,
        sanPham: [
            { id:1, ten:'Nike Air Zoom', mau:'Đen', size:'42', ton:12, gia:2000000 },
            { id:2, ten:'Adidas Ultraboost', mau:'Trắng', size:'40', ton:5, gia:2500000 },
            { id:3, ten:'Converse Classic', mau:'Đỏ', size:'39', ton:20, gia:1200000 },
        ],
        gio: []
    }; },
    computed: {
        ketQua() { const k = this.tuKhoa.toLowerCase(); return this.sanPham.filter(p => p.ten.toLowerCase().includes(k)); },
        tamTinh() { return this.gio.reduce((s,l) => s + l.gia * l.soLuong, 0); },
        giamGia() { if (this.voucher === 0) return 0; return this.voucher < 1 ? Math.round(this.tamTinh * this.voucher) : this.voucher; },
        phaiTra() { return Math.max(0, this.tamTinh - this.giamGia); }
    },
    methods: {
        vnd(n){ return (n||0).toLocaleString('vi-VN') + ' ₫'; },
        them(p) { const found = this.gio.find(l => l.spId === p.id);
            if (found) found.soLuong++; else this.gio.push({ id:++this.seq, spId:p.id, ten:p.ten, mau:p.mau, size:p.size, gia:p.gia, soLuong:1 }); },
        xoa(l) { this.gio = this.gio.filter(x => x.id !== l.id); },
        thanhToan() { alert('Thanh toán (demo): ' + this.vnd(this.phaiTra) + ' cho ' + this.khachHang); this.gio = []; this.voucher = 0; }
    }
}).mount('#app');
</script>
</body>
</html>
```

- [ ] **Step 2: Verify** — `/hoa-don`: add products to cart, change qty, apply voucher, totals recompute, checkout clears cart.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/hoa-don.html
git commit -m "feat: add Hoa Don POS demo screen"
```

---

## Task 11: Lịch sử hóa đơn / Invoice history (`/lich-su`)

**Files:**
- Create: `src/main/resources/templates/lich-su.html`

From `HoaDon`/`LichSuHoaDon`: invoice list with status filter + detail view. Columns: Mã HĐ, Khách, Nhân viên, Ngày tạo, Tổng tiền, Trạng thái.

- [ ] **Step 1: Create the template**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/main :: layout(~{::title}, ~{::section}, 'lichsu')}">
<head><title>Lịch sử hoá đơn — BShoes</title></head>
<body>
<section>
    <div id="app">
        <h4 class="mb-3">Lịch sử hoá đơn</h4>
        <div class="d-flex gap-2 mb-3">
            <input class="form-control" style="max-width:280px" v-model="tuKhoa" placeholder="Tìm mã HĐ / khách...">
            <select class="form-select" style="max-width:200px" v-model="locTrangThai">
                <option value="">-- Trạng thái --</option>
                <option>Chờ</option><option>Thành công</option><option>Huỷ</option>
            </select>
        </div>
        <table class="table table-hover bg-white align-middle">
            <thead><tr><th>Mã HĐ</th><th>Khách</th><th>Nhân viên</th><th>Ngày tạo</th><th class="text-end">Tổng tiền</th><th>Trạng thái</th><th></th></tr></thead>
            <tbody>
                <tr v-for="h in ketQua" :key="h.id">
                    <td>[[ h.ma ]]</td><td>[[ h.khach ]]</td><td>[[ h.nhanVien ]]</td><td>[[ h.ngayTao ]]</td>
                    <td class="text-end">[[ vnd(h.tongTien) ]]</td>
                    <td><span class="badge" :class="badge(h.trangThai)">[[ h.trangThai ]]</span></td>
                    <td class="text-end"><button class="btn btn-sm btn-outline-secondary" @click="xem(h)">Chi tiết</button></td>
                </tr>
            </tbody>
        </table>

        <div class="modal fade" ref="modalEl" tabindex="-1">
            <div class="modal-dialog modal-lg"><div class="modal-content" v-if="chon">
                <div class="modal-header"><h5 class="modal-title">Hoá đơn [[ chon.ma ]]</h5><button class="btn-close" data-bs-dismiss="modal"></button></div>
                <div class="modal-body">
                    <p class="mb-1"><b>Khách:</b> [[ chon.khach ]] — <b>NV:</b> [[ chon.nhanVien ]] — <b>Ngày:</b> [[ chon.ngayTao ]]</p>
                    <table class="table table-sm">
                        <thead><tr><th>Sản phẩm</th><th class="text-end">SL</th><th class="text-end">Đơn giá</th><th class="text-end">Thành tiền</th></tr></thead>
                        <tbody>
                            <tr v-for="d in chon.chiTiet" :key="d.ten">
                                <td>[[ d.ten ]]</td><td class="text-end">[[ d.soLuong ]]</td>
                                <td class="text-end">[[ vnd(d.donGia) ]]</td><td class="text-end">[[ vnd(d.donGia*d.soLuong) ]]</td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="text-end fs-5">Tổng: <b style="color:var(--bs-green)">[[ vnd(chon.tongTien) ]]</b></div>
                </div>
            </div></div>
        </div>
    </div>
</section>
<script>
Vue.createApp({
    delimiters: ['[[', ']]'],
    data() { return {
        tuKhoa: '', locTrangThai: '', chon: null, modal: null,
        rows: [
            { id:1, ma:'HD1', khach:'Nguyễn Văn A', nhanVien:'admin', ngayTao:'2026-07-01', tongTien:2000000, trangThai:'Thành công',
              chiTiet:[{ten:'Nike Air Zoom (Đen/42)', soLuong:1, donGia:2000000}] },
            { id:2, ma:'HD2', khach:'Khách lẻ', nhanVien:'hoangbh', ngayTao:'2026-07-01', tongTien:3700000, trangThai:'Chờ',
              chiTiet:[{ten:'Adidas Ultraboost (Trắng/40)', soLuong:1, donGia:2500000},{ten:'Converse Classic (Đỏ/39)', soLuong:1, donGia:1200000}] },
            { id:3, ma:'HD3', khach:'Trần Thị B', nhanVien:'vungan', ngayTao:'2026-06-30', tongTien:1200000, trangThai:'Huỷ',
              chiTiet:[{ten:'Converse Classic (Đỏ/39)', soLuong:1, donGia:1200000}] },
        ]
    }; },
    computed: {
        ketQua() { const k = this.tuKhoa.toLowerCase();
            return this.rows.filter(r => (r.ma.toLowerCase().includes(k) || r.khach.toLowerCase().includes(k))
                && (!this.locTrangThai || r.trangThai === this.locTrangThai)); }
    },
    mounted() { this.modal = new bootstrap.Modal(this.$refs.modalEl); },
    methods: {
        vnd(n){ return (n||0).toLocaleString('vi-VN') + ' ₫'; },
        badge(t){ return t==='Thành công'?'bg-success':(t==='Chờ'?'bg-warning text-dark':'bg-danger'); },
        xem(h){ this.chon = h; this.modal.show(); }
    }
}).mount('#app');
</script>
</body>
</html>
```

- [ ] **Step 2: Verify** — `/lich-su`: list + search + status filter; "Chi tiết" opens invoice detail modal.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/lich-su.html
git commit -m "feat: add Lich Su Hoa Don demo screen"
```

---

## Task 12: Hệ thống / System (`/he-thong`) — logout

**Files:**
- Create: `src/main/resources/templates/he-thong.html`

Per spec, this screen's primary action is Logout.

- [ ] **Step 1: Create the template**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/main :: layout(~{::title}, ~{::section}, 'hethong')}">
<head><title>Hệ thống — BShoes</title></head>
<body>
<section>
    <div id="app" style="max-width:520px">
        <h4 class="mb-3">Hệ thống</h4>
        <div class="card"><div class="card-body">
            <p class="mb-1"><b>Người dùng:</b> [[ nguoiDung ]]</p>
            <p class="mb-1"><b>Vai trò:</b> [[ vaiTro ]]</p>
            <p class="mb-3"><b>Phiên bản:</b> BShoes 1.0 (demo)</p>
            <button class="btn btn-danger" @click="dangXuat">Đăng xuất</button>
        </div></div>
    </div>
</section>
<script>
Vue.createApp({
    delimiters: ['[[', ']]'],
    data() { return { nguoiDung: 'admin', vaiTro: 'ADMIN' }; },
    methods: { dangXuat() { if (confirm('Đăng xuất?')) window.location.href = '/login'; } }
}).mount('#app');
</script>
</body>
</html>
```

- [ ] **Step 2: Verify** — `/he-thong`: info card + "Đăng xuất" → confirms → redirects to `/login`.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/he-thong.html
git commit -m "feat: add He Thong system/logout demo screen"
```

---

## Task 13: Full smoke verification + push branch

**Files:** none (verification + git)

- [ ] **Step 1: Start the app**

Run: `.\mvnw.cmd spring-boot:run`
Expected: `Tomcat started on port 8085`.

- [ ] **Step 2: Verify all routes return 200**

In a second terminal (PowerShell):
```powershell
'/','/login','/san-pham','/nhan-vien','/khach-hang','/hoa-don','/lich-su','/phieu-giam-gia','/he-thong' |
  ForEach-Object { "$_ -> " + (Invoke-WebRequest -UseBasicParsing "http://localhost:8085$_").StatusCode }
```
Expected: every route prints `200`.

- [ ] **Step 3: Manual click-through**

In a browser, visit each screen from the sidebar. Confirm: green theme, active-nav highlight, tables render, search/filter works, modals open, POS totals compute, logout redirects. Stop the app (Ctrl+C).

- [ ] **Step 4: Push the branch**

```bash
git push -u origin feature/spring-boot-port
```
Expected: branch published to `origin`.

---

## Self-review notes (author)

- **Spec coverage:** All 9 screens (spec table) → Tasks 4–12. Port 8085 → Task 1. Green theme/sidebar layout → Task 3. Inline mock data → every screen task. Thymeleaf + Vue CDN + Bootstrap → Task 3 + all pages. No DB / no REST / no tests → enforced by Task 1 datasource exclusion and manual-only verification. Phase 2 intentionally out of scope.
- **Placeholders:** Tasks 7 & 8 reference the Task 6 reference pattern but give complete concrete substitutions (columns, form fields, full mock `rows`, code prefix, filter keys) — no "TBD"/"implement later".
- **Consistency:** All pages use Vue delimiters `[[ ]]`, method `vnd(n)`, layout fragment signature `layout(title, section, active)`, and `active` keys matching the sidebar `th:classappend` keys in Task 3 (`dashboard, sanpham, hoadon, nhanvien, khachhang, lichsu, phieu, hethong, login`).
```
