# CRUD fixes + missing features (branch test-auth)

Batch of fixes/features reported broken, plus a regression requirement that every
UI's CRUD (create/read/update/delete) works. Root causes were investigated
before this design (see per-area notes). Backend + SQL Server are **down** in the
dev session, so backend-dependent parts are verified by build + unit tests here
and by a manual checklist against the live stack.

## Decisions (from the user)

1. **CRUD reliability:** honest save — `await` the API, show success only on server
   confirmation, surface real errors; drop the silent local-memory fallback.
2. **Image upload:** real backend upload endpoint (MultipartFile, jpg/png, <1MB),
   wired into the **admin** product-variant image field (`san_pham_chi_tiet.image_url`).
3. **Invoice print:** add an explicit "In hóa đơn" button to Lịch Sử + Đơn Hàng
   (browser print preview, reusing the receipt builder) AND harden the fragile
   existing print.
4. **Add employee password:** add tài khoản + mật khẩu to the add-employee form +
   backend so new staff can log in.
5. **Vai trò (chosen default):** the add-employee role dropdown fetches real roles
   from `/api/vai-tro`; backend resolves the role by id (not by display string).
6. **Category (chosen default):** wire the existing `loai_san_pham` table into the
   storefront — no new table. Storefront fetches real categories and filters by them.

## Area 1 — CRUD honesty (foundational, frontend)

**Root cause:** `useCrud.js:42-71` catches all API errors on add/update/remove and
falls back to mutating local memory; views call `add()` without `await` and show
success unconditionally (`NhanVienView.vue:57-65`, `KhachHangView.vue:53-61`). With
the backend down every write fakes success then vanishes on reload.

**Change:**
- `useCrud.js`: `add`/`update`/`remove` **await** and **throw** on failure (no local
  fallback). `load()` keeps its mock fallback for read-only offline display (so
  screens still render sample rows when browsing offline) — only **writes** become
  honest. Document this split clearly in the file.
- Every caller of `add/update/remove` (all list screens) wraps the call in
  try/catch: success → toast + close modal; failure → red toast with the server
  message, modal stays open. Audit all callers (grep `\.add(`, `\.update(`,
  `\.remove(`, and the CRUD composables) and update each to the new contract.
- Extract a tiny helper for the error message (`err.response?.data?.message || err.message`).

**Test:** Vitest unit tests for `useCrud` with a fake `api` (create resolves →
rows reload; create rejects → throws, rows unchanged, no phantom insert).

## Area 2 — Add nhân viên (frontend + backend)

**Root causes:** (a) Area 1 masking; (b) role dropdown values `['ADMIN','NHÂN VIÊN']`
never match `ten_vai_tro` in `NhanVienServiceImpl.applyVaiTro` → `id_vai_tro` NULL;
(c) no password set on create → new staff can't log in.

**Change:**
- Frontend `NhanVienView.vue`: replace hardcoded `vaiTroOptions` with roles fetched
  from `vaiTroApi` (`/api/vai-tro`); bind the selected role by **id**. Add `taiKhoan`
  + `matKhau` fields to the add/edit form.
- Backend: `NhanVienDto` gains `taiKhoan`, `matKhau`, `idVaiTro`. `NhanVienServiceImpl`
  resolves the role by `idVaiTro` (`vaiTroRepository.findById`) instead of
  `findByTenVaiTro`; sets `matKhau` on create; on update only overwrites `matKhau`
  when a new value is provided (don't wipe existing password on edit).
- Keep code-generation for `ma_nhan_vien` as-is.

**Test:** Backend — a unit/service test that create with a valid `idVaiTro` + password
persists role and password (run when DB available; otherwise assert mapping logic).
Frontend — no new unit test beyond Area 1 CRUD contract (form wiring verified by build
+ manual checklist).

## Area 3 — Add khách hàng (frontend only)

**Root cause:** Area 1 masking only; backend sound.

**Change:** covered by the Area 1 caller fix in `KhachHangView.vue`. No backend change.

## Area 4 — Product image upload (backend + frontend)

**Root cause:** no upload exists; `ImagePicker` only picks static files;
`san_pham_chi_tiet.image_url varchar(255)` is the only persisted image field.

**Change:**
- Backend: new `UploadController` `POST /api/upload` (consumes multipart), param
  `MultipartFile file`. Validate content type ∈ {image/jpeg, image/png} and size
  < 1 MB; reject others with 400 + message. Save to a configured uploads dir
  (`app.upload.dir`, default `uploads/`), random filename keeping extension; return
  `{ url: "/uploads/<name>" }`. Add a static resource handler mapping `/uploads/**`
  to that dir, and `spring.servlet.multipart.max-file-size=1MB` /
  `max-request-size=1MB` in `application.properties`.
- Frontend: in `SanPhamView.vue` variant form, next to the existing "Chọn từ kho"
  (`ImagePicker`) add a **"Tải ảnh từ máy"** button → hidden `<input type=file
  accept="image/png,image/jpeg">`. On pick: client-side validate type + `size <
  1MB` (reject with toast), POST multipart to `/api/upload`, set the returned `url`
  into `ctForm.imageUrl`, and show it in the existing preview `<img>` ("download to
  current page" = immediate preview).
- Add `uploads/` to `.gitignore`.

**Test:** Frontend — Vitest unit test for a pure `validateImageFile(file)` helper
(type + size). Backend upload verified by build + manual checklist (needs running
server).

## Area 5 — Invoice print (frontend)

**Root cause:** print only fires as a checkout side-effect via a hidden iframe with a
silent `try/catch` + 300ms timer (`HoaDonView.vue:145-206`); no reprint action on
history/order screens.

**Change:**
- Extract `buildReceipt`/`printReceipt` from `HoaDonView.vue` into a reusable
  composable `useReceipt.js` (or `utils/receipt.js`). Harden `printReceipt`: drop
  the silent catch (log + notify on failure), and wait for the iframe document to be
  ready (`onload` + `readyState`/`requestAnimationFrame`) instead of a bare 300ms.
- Add an "In hóa đơn" button to the invoice-detail views: `LichSuView.vue` and
  `DonHangView.vue`, mapping their invoice row (`chon.chiTiet`, totals, khách, nhân
  viên) into the receipt shape and calling the shared `printReceipt`.

**Test:** Vitest unit test that `buildReceipt(invoice)` produces the expected HTML
fields (ma, ngày, line items, totals). Print dialog itself verified manually.

## Area 6 — Storefront category sort (backend + frontend)

**Root cause:** storefront nav is cosmetic (`activeCat` never read); `PosSanPhamDto`
carries no category; `loai_san_pham` + FK + seed + admin manager already exist,
just not wired to the storefront.

**Change:**
- Backend: add `idLoaiSanPham` + `loaiSP` to `PosSanPhamDto`; populate in the store
  mapper (`SanPhamChiTietServiceImpl.toPos`) by joining
  `SanPhamChiTiet → SanPham → LoaiSanPham`.
- Frontend `HomeView.vue`: fetch real categories from `loaiSanPhamApi.findAll()`
  (replace the hardcoded 6), and make `activeCat` actually filter the product grid by
  `idLoaiSanPham`. Add an "All" option. Keep the existing price/best-seller sort;
  category is a filter on top.

**Test:** Vitest unit test for the pure category-filter function
(`filterByCategory(products, activeCatId)`).

## Regression requirement

After each area, regression-test that screen's CRUD. Because the backend/DB is down
here, "regression testing" in this session = (a) Vitest unit tests for the extracted
pure logic, (b) `npm run build` green, (c) a written **manual regression checklist**
(per screen: add persists after reload, edit persists, delete persists, list loads;
plus the feature-specific checks). The user runs the checklist against the live
stack to confirm 100%.

## Out of scope

- Password hashing / server-side endpoint authorization (separate "auth hardening").
- Changing the product-level image model (no `san_pham` image column added).
- PDF generation for receipts (browser print only).
- Concurrent-create unique-NULL edge case on `ma_*` (noted, needs live load test).

## Verification honesty

Backend-dependent behavior (real persistence, upload endpoint, category projection)
cannot be runtime-verified in this session (SQL Server + Spring are down). Those are
delivered as code + unit tests + build-green + manual checklist, and explicitly
flagged as "verify on live stack", never claimed as end-to-end confirmed.
