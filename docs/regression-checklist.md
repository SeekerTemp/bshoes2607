# Regression checklist — CRUD + fixed features (branch test-auth)

What's automatically verified here vs. what YOU must verify on the live stack.

## Auto-verified in this session
- Frontend: 33 Vitest unit tests pass (`cd frontend && npm test`).
- Frontend: production build succeeds (`npm run build`).
- Backend: compiles (`./mvnw -q -o compile`); `NhanVienServiceImplTest` 5/5 passes.

## Must verify on the LIVE stack (backend + SQL Server up)
Because SQL Server was down during development, real persistence could not be
exercised. Bring the stack up first:
1. Start SQL Server; run the updated `sqlBshoes.sql` (creates roles, staff incl.
   `banhang`/`quanly`, categories `loai_san_pham`).
2. Backend: `./mvnw spring-boot:run` (port 8085).
3. Frontend: `cd frontend && npm run dev`.
4. Keep the browser console open — with the honesty fix, a failed save now shows a
   real error toast instead of a fake "Đã lưu".

### 1. CRUD honesty — every list screen
For **Nhân Viên, Khách Hàng, Khuyến Mãi (phiếu giảm giá), Sản Phẩm**:
- [ ] Add a row → success toast → **reload the page** → row still there (persisted).
- [ ] Edit a row → success toast → reload → change persisted.
- [ ] Delete a row → success toast → reload → row gone (soft-deleted).
- [ ] Stop the backend, try to add → you get a **red/warning error toast** (NOT a
      green success, NOT a phantom row that vanishes on reload).

### 2. Thêm nhân viên (add employee)
- [ ] The "Vai trò" dropdown lists the **real roles** from the DB (Quản trị, Quản lý,
      Nhân viên bán hàng, …) — not "ADMIN / NHÂN VIÊN".
- [ ] Add an employee with a role + a password → reload → the grid shows the correct
      **Vai trò**, and the role persisted (not blank).
- [ ] Log out and log in with that new employee's tài khoản + password → it works
      and lands per role (see login flow).
- [ ] Edit the employee, leave password blank, save → existing password still works
      (not wiped). Set a new password → new one works.

### 3. Thêm khách hàng (add customer)
- [ ] Add a customer on the Khách Hàng screen → reload → persisted.
- [ ] The POS quick-add customer modal (from Bán Hàng / Đơn Hàng) still works.

### 4. Product image upload (admin Sản Phẩm → biến thể)
- [ ] In the variant (chi tiết) form, click **"Tải ảnh từ máy"**, pick a JPG or PNG
      < 1MB → it previews immediately on the page.
- [ ] Pick a non-image or a file ≥ 1MB → clear warning toast, no upload.
- [ ] Save the variant → reload → the uploaded image still shows (persisted to
      `san_pham_chi_tiet.image_url`, served from `/api/uploads/...`).
- [ ] "Chọn từ kho" (pick from catalog) still works alongside the upload button.
- [ ] Confirm the backend `uploads/` dir is created and writable where the app runs.

### 5. In hóa đơn (invoice print)
- [ ] Bán Hàng: paying / "Phiếu tạm tính" opens a **print preview** reliably.
- [ ] Lịch Sử → open an invoice → **"In hóa đơn"** → print preview with correct
      mã, ngày, khách, line items, totals.
- [ ] Đơn Hàng → open an order → **"In hóa đơn"** → print preview.
- [ ] If printing fails, you get an error toast (no more silent nothing).

### 6. Storefront category sort (Home)
- [ ] The category nav / tiles show **real categories** from `loai_san_pham`.
- [ ] Clicking a category **filters** the product grid to that category (not just a
      highlight). "Tất cả" shows everything.
- [ ] Price / best-seller sort still works on top of the category filter.
- [ ] Assign a product to a category in admin (Danh Mục) → it appears under that
      category on the storefront.

## Known non-goals (not fixed in this batch — by design)
- Passwords are still plaintext; API endpoints are not yet role-protected server-side
  (separate "auth hardening").
- No product-level image column added (upload attaches to the variant).
- Receipts use browser print, not generated PDF.
