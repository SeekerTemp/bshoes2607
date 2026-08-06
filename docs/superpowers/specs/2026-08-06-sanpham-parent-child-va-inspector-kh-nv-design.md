# Tách sản phẩm cha/con + inspector cho Khách hàng / Nhân viên

Ngày: 2026-08-06 · Nhánh: `test-auth`

## Vấn đề

1. **Sản phẩm cha mang dữ liệu của con.** `SanPhamDto` bịa ra một trường `gia` bằng
   cách lấy `bienThe.get(0).getGia()`, và bảng tab "Sản phẩm" hiển thị cột *Màu sắc* /
   *Kích thước* cũng lấy từ biến thể đầu tiên. Sản phẩm nhiều biến thể vì thế hiển thị
   sai; sản phẩm chưa có biến thể nào hiển thị giá rỗng nhưng vẫn nằm trong danh sách
   như thể đang bán. Không có ràng buộc nào bắt biến thể phải có giá > 0 và số lượng
   để được coi là *Đang bán*.
2. **Khách hàng / Nhân viên nhập liệu qua modal**, chậm hơn hẳn màn Sản phẩm vốn đã
   dùng master-detail. Form nhân viên còn có ô text *Chức vụ* trùng vai trò với
   dropdown *Vai trò*.

## Thiết kế

### 1. Bất biến "bán được" đặt ở tầng entity

Ràng buộc nằm trong `SanPhamChiTiet`, không nằm ở service hay ở form — mọi đường ghi
đều đi qua nó.

- Thêm `spring-boot-starter-validation`. Có provider trên classpath thì Hibernate tự
  chạy Bean Validation ở thời điểm flush.
- `SanPhamChiTiet`: `@NotNull @DecimalMin("1") donGia`, `@NotNull @Min(0) soLuongTon`,
  `@NotNull idMauSac`, `@NotNull idKichCo`. Biến thể không thể tồn tại nếu thiếu màu,
  kích cỡ hoặc giá thật.
- `@PrePersist` / `@PreUpdate` suy ra `trangThai`: **Đang bán** khi và chỉ khi
  `donGia > 0 && soLuongTon > 0`, ngược lại Ngừng bán. Bán hết pair cuối thì tự
  chuyển; `nhapKho` tự bật lại.
- `GlobalExceptionHandler` map `ConstraintViolationException` → 400 kèm thông điệp
  tiếng Việt, thay vì 500 như hiện tại.

### 2. Sản phẩm cha chỉ còn thuộc tính của cha

- `SanPhamDto` bỏ `gia`; thay bằng các trường dẫn xuất chỉ đọc `giaTu`, `giaDen`,
  `tongTon`, `soBienThe`. Cha không bao giờ nhận giá ghi vào.
- Entity `SanPham` vốn đã không có màu / kích cỡ — giữ nguyên.
- `SanPhamView` tab 1: bỏ cột *Màu sắc* / *Kích thước*, thêm *Giá bán* (khoảng),
  *Biến thể*, *Trạng thái*. Inspector cha có tóm tắt giá/tồn chỉ đọc và nút nhảy sang
  tab biến thể. Inspector biến thể đánh dấu màu/size/giá/số lượng là bắt buộc và chặn
  lưu khi `giá <= 0`.

### 3. Dữ liệu mẫu

`sqlBshoes.sql` §2.8 / §2.11 viết lại: 30 sản phẩm (5 × 6 danh mục), mỗi sản phẩm 2–3
biến thể, tất cả `don_gia > 0` và `so_luong_ton > 0` — trừ 2 dòng cố ý hết hàng để
giữ demo *Đặt trước*.

### 4. Inspector cho Khách hàng / Nhân viên

Cả hai màn chuyển sang master-detail giống `SanPhamView`: bảng bên trái, panel xanh
sticky bên phải, bỏ `AppModal`. `ConfirmDialog` giữ nguyên cho thao tác xoá.

- `NhanVienView`: bỏ ô text *Chức vụ*; dropdown *Vai trò* là nguồn sự thật duy nhất,
  cột *Chức vụ* render từ vai trò.
- Bắt buộc: **SĐT + CCCD** cho nhân viên, **SĐT** cho khách hàng. Bảng `khach_hang`
  không có cột `cccd` và ta không đổi schema cho việc này (quyết định của người dùng).

## 5. Những gì `logs/bshoes260805.log` thực sự chứa

Lưu ý đọc log: dấu thời gian của client là UTC, của server là UTC+7. "10:41" phía
client và "17:41" phía server là CÙNG một sự kiện.

Ba lỗi, hai trong số đó đã sửa:

1. **File log phồng gấp ~66 lần** — 6.902 dòng nhưng chỉ ~100 sự kiện thật.
   `flushLogs()` đọc lại buffer bằng `readBuffer()` bên trong `.then()`, nên
   `markSent()` (so sánh theo tham chiếu) không khớp gì cả và không entry nào được
   đánh dấu đã gửi; mọi lần flush gửi lại toàn bộ lịch sử. Đã sửa: mỗi entry có `id`
   bền vững, `markSent()` khớp theo `id`.

2. **Bão 401 không bao giờ tự thoát** (17:41–17:42) — không hề có `POST /auth/login`
   nào trong ngày 2026-08-05: người dùng mở app với `bshoes_user` còn lại từ
   2026-07-31, tức là từ trước khi có `ApiAuthFilter` và token (commit 84da22b,
   2026-08-04). Object đó không có trường `token`, nên:
   - request không gắn `X-Auth-Token` → server trả 401 "**thiếu** token";
   - `http.js` dùng `storedUser()?.token` để nhận diện phiên demo, nên phiên thật
     không token bị xếp nhầm là demo → không xoá session, không chuyển về `/login`;
   - Dashboard bắn lại cả 7 request `/thong-ke` hết vòng này đến vòng khác trong 46
     giây, màn hình vẫn hiện "Nguyễn Văn A".

   Đã sửa: phiên demo được đánh dấu bằng cờ TƯỜNG MINH `demo: true`; mọi 401 của
   phiên không-demo đều xoá session và chuyển về `/login?expired=1`. Test hồi quy
   `src/api/http.session.test.js` — đã xác nhận fail trên code cũ, pass trên code mới.

3. **`/thong-ke/*` trả 500 lúc 17:41:37–38** — CHƯA sửa, và không chẩn đoán được từ
   file này. Chúng xảy ra khi backend đang khởi động lại (`Started BshoesApplication`
   lúc 17:41:47), và instance ghi ra file log này bắt đầu SAU đó nên không có stack
   trace tương ứng; toàn bộ 151 request mà nó phục vụ đều trả 200. `/thong-ke/nam`
   chỉ là `SELECT DISTINCT YEAR(ngay_tao_ma) FROM hoa_don` mà cũng 500, nên nguyên
   nhân gần như chắc chắn là hạ tầng (DB/kết nối lúc restart) chứ không phải lỗi
   truy vấn. Cần log của tiến trình phục vụ lúc 17:41 mới truy tiếp được.

Lỗi `/nhan-vien` 500 và `/hoa-don/vouchers-active` 500 trong log đều mang dấu thời
gian 2026-07-31, tức là trước các commit đã sửa chúng (fetch-join ở
`NhanVienRepository.findActive()` và 84da22b); phiên 2026-08-05 không tái hiện.

## 6. Mã (ID) do server sinh, hiển thị dạng chữ

Toàn bộ 22 entity đã có `@GeneratedValue(IDENTITY)` sẵn (trừ `NhanVienQuyen` dùng
`@IdClass` — đúng cho bảng nối). Vấn đề nằm ở tầng **mã nghiệp vụ**:

- `SanPhamChiTiet` đặt mã là `"SPCT" + mã sản phẩm cha`, nên MỌI biến thể của cùng
  một sản phẩm mang đúng một mã; `findByMaSanPhamChiTiet` (quét QR) trả về một biến
  thể bất kỳ. Nay là `SPCT<id 3 chữ số>-<mã màu>-<mã cỡ>` (ví dụ `SPCT079-BK-M`):
  phần id đảm bảo duy nhất, phần màu/cỡ để đọc được trên nhãn. Sinh lại khi màu hoặc
  kích cỡ đổi — nhãn/QR đã in của biến thể đó phải in lại.
- Ô "Mã" ở Danh mục và Thuộc tính là ô nhập **ảo**: `create()` luôn ghi đè bằng
  `"LSP"/"TH"/"CL"… + id`, giá trị người dùng gõ bị nuốt mất không báo gì.

Nay mã là `<p>` chỉ đọc ở cả sáu chỗ (Sản phẩm, Sản phẩm chi tiết, Khách hàng, Nhân
viên, Danh mục, Thuộc tính), hiện "Tự sinh khi lưu" khi đang tạo. Client không gửi
`ma` nữa và server bỏ qua nếu có. Bảng Danh mục được thêm lại cột Mã.

## 7. "Tạo mới" và "Làm mới"

Mỗi màn master-detail trước đây chỉ có MỘT nút lưu tự đổi nghĩa:
`{{ form.id ? 'Lưu (Sửa)' : 'Tạo …' }}`. Chọn một dòng là nút lặng lẽ thành "sửa",
nên thao tác "chọn một bản ghi, đổi tên, tạo thành bản ghi mới" là không thể — nó ghi
đè bản gốc. `lamMoi()` thì nạp lại dòng đang chọn chứ không xoá trắng form.

Quy ước thống nhất cho Sản phẩm, Sản phẩm chi tiết, Khách hàng, Địa chỉ, Nhân viên,
Danh mục, Thuộc tính:

| Nút | Việc | Vô hiệu khi |
|---|---|---|
| Tạo mới | Luôn TẠO, lấy toàn bộ inspector, bỏ `id` + `ma` | không bao giờ |
| Lưu (Sửa) | Chỉ CẬP NHẬT dòng đang chọn | chưa chọn dòng |
| Làm mới | XOÁ TRẮNG inspector + bỏ chọn | không bao giờ |

Dropdown "Sản phẩm" ở inspector biến thể không còn bị khoá khi đang chọn một biến thể
— khoá thì không tạo được biến thể cho sản phẩm khác từ nội dung đang có.

Hệ quả: tạo nhân viên mới từ một nhân viên đang chọn vẫn phải nhập **mật khẩu** (
`selectRow` xoá `matKhau`, không có gì để sao chép) và phải đổi **tài khoản**.
`existsByTaiKhoan` có trong repository từ trước nhưng KHÔNG nơi nào gọi, và cột
`tai_khoan` không có ràng buộc unique trong DDL — trùng tài khoản sẽ khiến
`findByTaiKhoanAndMatKhau` trả về một người bất kỳ. Nay chặn ở `create()` và
`update()`.

## 8. Còn tồn đọng

- `/thong-ke/*` 500 (mục 5.3) — cần log của tiến trình phục vụ lúc 17:41.
- Dữ liệu mẫu 30 sản phẩm mới chỉ nằm trong `sqlBshoes.sql`; phải chạy lại file này
  trên DB `BShoes` mới thì mới hiển thị (sẽ mất hoá đơn/đơn hàng hiện có).
- Chưa chạy end-to-end lần nào: SQL Server không bật trong suốt phiên làm việc. Mọi
  kiểm chứng dựa trên compile, 138 unit test, và thao tác thật trên DOM ở chế độ demo.
- Biến thể cũ có màu/kích cỡ/đơn giá NULL sẽ trả 400 khi sửa cho tới khi điền đủ.
  Ẩn (xoá mềm) vẫn luôn thực hiện được (bulk update, không qua validation).
