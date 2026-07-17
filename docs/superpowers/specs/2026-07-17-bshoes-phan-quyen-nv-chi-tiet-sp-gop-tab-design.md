# BShoes — Phân quyền theo nhân viên · Chi tiết sản phẩm · Gộp tab (Design)

**Date:** 2026-07-17
**Branch:** `test-api-connect`
**Repo:** `SeekerTemp/bshoes2607`

## Goal

Ba thay đổi, làm theo đúng thứ tự vì task 3 quyết định tập cột của task 1:

1. **Gộp tab** — Thuộc tính + Danh mục vào Quản lý sản phẩm; Phân quyền vào Quản lý nhân viên.
2. **Phân quyền theo từng nhân viên** — bảng phụ `nhan_vien_quyen`, vai trò chỉ còn là template.
3. **Trang chi tiết sản phẩm** ở storefront — tồn > 0 cho mua, tồn = 0 cho đặt trước.

Không có yêu cầu bảo mật (demo). Quyền ở đây chỉ là **ẩn/hiện màn hình UI**, không phải
authorization ở tầng API — backend vẫn mở như hiện tại.

## Bối cảnh hiện tại

- `vai_tro.quyen` = CSV các key màn hình (`*` = tất cả). Nhân viên **kế thừa cứng** theo vai trò.
- `PhanQuyenView.vue` là màn riêng, sửa quyền theo vai trò.
- `frontend/src/config/screens.js` (`SCREENS`) là registry duy nhất, chạy cả sidebar lẫn
  router guard lẫn bảng phân quyền.
- `KhachHangView.vue` đã có sẵn pattern tab (`kh-tabs`) → dùng lại, không phát minh kiểu mới.
- `GET /api/san-pham/{id}` đã trả `SanPhamDto` kèm `bienThe` → trang chi tiết **không cần**
  endpoint mới.
- `PosSanPhamDto` (dùng cho `/san-pham-chi-tiet/store`) **thiếu `idSanPham`** → card ở
  storefront chưa biết điều hướng đi đâu.

## Task 3 (làm trước) — Gộp tab

### Vì sao trước

`SCREENS` co từ 14 → 11 key. Các key `danh-muc`, `thuoc-tinh`, `phan-quyen` biến mất, mà
chúng chính là cột của bảng phân quyền ở task 1. Làm ngược thứ tự sẽ phải sửa 2 lần.

`SCREENS` sau khi gộp:

```
dashboard, san-pham, hoa-don, don-hang, dat-truoc,
nhan-vien, khach-hang, lich-su, bao-hanh, phieu-giam-gia, he-thong
```

### Cách làm

Không dồn ~800 dòng vào một file. Tách phần thân của mỗi màn thành **panel component**
(không có `AppShell`/`PageHeader`), rồi màn cha render trong tab:

| Component mới | Tách từ | Dùng ở |
|---|---|---|
| `components/panels/DanhMucPanel.vue` | `views/DanhMucView.vue` | tab trong `SanPhamView` |
| `components/panels/ThuocTinhPanel.vue` | `views/ThuocTinhView.vue` | tab trong `SanPhamView` |
| `components/panels/VaiTroPanel.vue` | `views/PhanQuyenView.vue` | tab trong `NhanVienView` |

- `SanPhamView` → 3 tab: **Sản phẩm | Danh mục | Thuộc tính**
- `NhanVienView` → 3 tab: **Danh sách | Phân quyền | Vai trò (template)**
- Xóa `views/DanhMucView.vue`, `views/ThuocTinhView.vue`, `views/PhanQuyenView.vue` và
  route `/danh-muc`, `/thuoc-tinh`, `/phan-quyen`.
- Dọn seed `vai_tro.quyen` trong `sqlBshoes.sql` — đang chứa key sắp chết.
  `NK` từ `san-pham,danh-muc,thuoc-tinh,dat-truoc` → `san-pham,dat-truoc`.

**Không** phân quyền riêng cho từng tab con — tab kế thừa quyền của màn cha. Giữ đúng phạm vi
"phân quyền access UI", tránh phình.

## Task 1 — Phân quyền theo nhân viên

### Dữ liệu

```sql
create table nhan_vien_quyen (
    id_nhan_vien int not null,
    man_hinh varchar(30) not null,
    primary key (id_nhan_vien, man_hinh),
    foreign key (id_nhan_vien) references nhan_vien(id_nhan_vien)
);
```

- **Rows = sự thật.** Một dòng = nhân viên đó vào được màn đó.
- `vai_tro.quyen` **giữ nguyên**, đổi vai trò thành **template**: nguồn để vật chất hóa rows,
  sửa được ở tab "Vai trò".
- Seed rows cho 6 nhân viên sẵn có từ template vai trò của họ — nếu không, bảng vừa ra đời là
  cả 6 người mất sạch quyền.

### Quy tắc

| Tình huống | Xử lý |
|---|---|
| Tạo NV mới | Vật chất hóa rows từ `vai_tro.quyen` của vai trò được gán |
| **Đổi vai trò của NV** | Xóa sạch rows cũ, chép lại từ template vai trò mới — **phần mở rộng trước đó mất**. Chủ ý: đổi vai trò nghĩa là đổi hẳn bộ quyền, giữ lại phần mở rộng của vai trò cũ sẽ thành quyền lạc chỗ khó truy vết |
| Muốn mở rộng | Tick thêm ô → thêm row. Bỏ tick → xóa row |
| Muốn về mặc định | Nút "Áp lại template vai trò" → xóa rows cũ, chép lại từ template |
| Vai trò ADMIN (`quyen = '*'`) | **Tính động = tất cả màn, không đọc rows** |

Lưới không phân trang — `nhan_vien` hiện 6 dòng, thêm phân trang lúc này là phí.

Chốt an toàn ADMIN là có chủ đích: thêm màn mới về sau, admin không bị khóa ngoài chính hệ
thống của mình. Đây là ngoại lệ duy nhất của "rows = sự thật".

### Backend

- Entity `NhanVienQuyen` + `@IdClass`/`@EmbeddedId` cho khóa kép; repo `NhanVienQuyenRepository`.
- `NhanVienQuyenService`:
  - `quyenCuaNhanVien(idNV)` → `List<String>` (ADMIN → toàn bộ `SCREENS`)
  - `luuQuyen(idNV, List<String>)` → xóa rows cũ, ghi rows mới
  - `apTemplate(idNV)` → chép từ `vai_tro.quyen`
  - `bangQuyen()` → dữ liệu cho lưới: mọi NV + rows của họ (1 lượt, tránh N+1)
- Controller gắn vào `NhanVienController`:
  - `GET /api/nhan-vien/quyen` → lưới
  - `PUT /api/nhan-vien/{id}/quyen` (body: mảng key)
  - `POST /api/nhan-vien/{id}/quyen/ap-template`
- `AuthServiceImpl.login` trả **quyền hiệu lực** (dựng từ rows) thay cho `vai_tro.quyen`.
  `LoginResponse.quyen` giữ nguyên kiểu CSV → `useAuth` không phải sửa.

### Frontend — tab "Phân quyền"

Lưới theo template ảnh người dùng gửi: dòng = nhân viên, cột = màn hình.

```
| ID | Tên NV | Vai trò | dashboard | san-pham | hoa-don | ... | Áp template |
|----|--------|---------|-----------|----------|---------|-----|-------------|
| NV001 | Nguyễn Văn A | ADMIN ▾ |  [x]  |  [x]  |  [x]  | ... |   [nút]     |
```

- Header xanh `#0B895A` sticky, ô tích bo góc, cột trái (ID/tên) đóng băng khi cuộn ngang.
- Dòng ADMIN: ô tích đầy đủ + **disabled**, có chú thích "ADMIN luôn có toàn quyền".
- Sửa nhiều dòng rồi **"Lưu thay đổi"** một lần (bulk), không auto-save từng ô.
- Đổi quyền của chính mình → toast nhắc đăng nhập lại (giữ hành vi `PhanQuyenView` cũ).

## Task 2 — Trang chi tiết sản phẩm

### Backend

Chỉ một thay đổi: thêm `idSanPham` vào `PosSanPhamDto`, set trong `SanPhamChiTietServiceImpl.toPos()`.
`GET /api/san-pham/{id}` đã đủ dùng cho trang chi tiết.

### Frontend

- Route công khai `/san-pham/:id` → `views/SanPhamDetailView.vue` (thêm vào `PUBLIC` của
  router guard, cạnh `home`/`gio-hang`).
- Card ở `HomeView` → `router.push('/san-pham/' + p.idSanPham)`.
- Trang chi tiết: ảnh, tên, thương hiệu, giá, mô tả, chọn **màu/size** (= chọn biến thể).
  - Biến thể `ton > 0` → ô số lượng + **Thêm vào giỏ** + **Mua ngay** (→ `/gio-hang`)
  - Biến thể `ton = 0` → **Đặt trước**
  - Trạng thái nút bám theo biến thể đang chọn, không phải theo sản phẩm.
- Tách modal đặt trước khỏi `HomeView` → `components/ui/DatTruocModal.vue`, dùng chung cho
  cả HomeView lẫn trang chi tiết (hiện đang nằm inline trong HomeView).
- `ToastHost` phải tự gắn (storefront không nằm trong `AppShell`).

## Không làm (YAGNI)

- Không authorization ở tầng API — đúng yêu cầu "no security method required yet".
- Không phân quyền mức thao tác (xem/sửa/xóa) — chỉ mức màn hình.
- Không phân quyền cho tab con.
- Không đụng `PhieuGiamGia`, `Dashboard`, `HoaDon`.

## Rủi ro / điểm cần canh

- **Khóa ngoài chính mình**: bỏ tick `nhan-vien` của tài khoản đang đăng nhập rồi lưu →
  mất đường vào màn phân quyền. Chốt an toàn ADMIN che phần lớn, nhưng QL tự bỏ tick vẫn tự
  khóa được. Chấp nhận (demo) + toast cảnh báo.
- **Không chạy thật được**: SQL Server chưa bật. Nghiệm thu tối đa ở mức mvn compile +
  npm build + context khởi động + endpoint trả 500 (DB tắt) thay vì 404. Phải nói rõ điều này
  khi báo cáo, không được tuyên bố "đã hoạt động".

## Nghiệm thu

- [ ] `SCREENS` còn 11 key; sidebar không còn Danh mục / Thuộc tính / Phân quyền
- [ ] `SanPhamView` 3 tab, `NhanVienView` 3 tab, dùng lại pattern `kh-tabs`
- [ ] Bảng `nhan_vien_quyen` + seed 6 NV; ADMIN vẫn full quyền khi rows rỗng
- [ ] Lưới phân quyền lưu bulk; login trả quyền hiệu lực từ rows
- [ ] `/san-pham/:id` công khai; tồn > 0 mua được, tồn = 0 hiện Đặt trước
- [ ] `mvn compile` + `npm run build` OK; context khởi động; endpoint mới 500 chứ không 404
