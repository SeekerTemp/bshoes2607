# Quy tắc dùng checklist (bullet-proof rules)

Hai file trong thư mục này là **nguồn sự thật duy nhất** về "app có gì" và "cái gì đã test":

| File | Trả lời câu hỏi |
|---|---|
| [feature-backlog.csv](feature-backlog.csv) | App **có** những chức năng nào, chức năng nào **đã làm xong** |
| [testcase-checklist.csv](testcase-checklist.csv) | Mỗi chức năng **đã được kiểm chứng** tới đâu, **bằng chứng** ở đâu |

Mục tiêu của các quy tắc dưới đây: **không bao giờ để một chức năng "trông như chạy được" bị tính là chạy được.**

---

## R1 — Không có bằng chứng thì không được ghi Pass

Cột `Test Status` chỉ được đặt `Pass` khi có **một dòng dán được vào cột `Log Reference`**. Không có log reference ⇒ trạng thái là `Not run`.

Bằng chứng hợp lệ, theo thứ tự ưu tiên:

| Loại | Định dạng ghi vào `Log Reference` |
|---|---|
| Unit test frontend | `vitest <đường dẫn file test> (n/n) <ngày giờ>` |
| Unit test backend | `surefire target/surefire-reports/<TênLớp>.txt (n/n) <ngày giờ>` |
| Log server / client | `logs/bshoes.log <yyyy-MM-dd HH:mm:ss.SSS> <logger> <trích 1 dòng>` |
| Gọi API thủ công | `curl <method> <url> → <mã HTTP> <trích body>` |
| Kiểm tra tay trên UI | `manual <yyyy-MM-dd HH:mm> <tài khoản> <việc đã làm> → <kết quả quan sát được>` |

"Tôi thấy nó chạy", "hôm qua test rồi", ảnh chụp màn hình không kèm thời điểm ⇒ **không hợp lệ**.

## R2 — Từ vựng trạng thái cố định (không tự bịa giá trị mới)

`feature-backlog.csv` cột `Status`:

- `Implemented` — có code chạy thật, gọi API thật.
- `Partial` — có code nhưng thiếu một phần quan trọng (ví dụ có fallback mock che lỗi).
- `Not implemented` — chỉ có UI, nút disabled, không có handler, hoặc chưa viết.
- `Legacy` — còn trong repo nhưng không còn dùng.

`testcase-checklist.csv` cột `Test Status`:

- `Pass` — đã chạy, đúng kỳ vọng, **có** log reference.
- `Fail` — đã chạy, sai kỳ vọng, **có** log reference chỉ ra chỗ sai.
- `Blocked` — không chạy được vì môi trường (DB tắt, chưa có dữ liệu). Ghi rõ lý do vào `Log Reference`.
- `Not run` — chưa chạy.

## R3 — "Backend tắt vẫn thấy dữ liệu" là Fail, không phải Pass

App hiện có fallback mock ở nhiều chỗ (`HomeView`, `DashboardView`, `BaoHanhView`, `useCrud`, `useHoaDon`, `useKhachHang`, `useLichSu`, `useNhanVien`). Khi API lỗi, màn hình **vẫn hiện dữ liệu** và chỉ `console.warn`.

Vì vậy mọi test case đọc dữ liệu phải được chạy theo quy trình:

1. Backend **đang chạy** và **DB đang chạy**.
2. Sửa/tạo một bản ghi có giá trị **duy nhất, dễ nhận** (ví dụ `ZZTEST-<giờ phút>`).
3. **F5 lại trang** và tìm chính giá trị đó.

Thấy danh sách "có dữ liệu" mà không làm bước 2–3 ⇒ ghi `Not run`, không được ghi `Pass`.

## R4 — Ghi (create/update/delete) phải được xác nhận sau khi F5

Toast "Đã lưu" **không** phải bằng chứng. Một test case ghi chỉ `Pass` khi:

- Thao tác → toast thành công, **và**
- F5 (hoặc gọi lại API list) → dữ liệu vẫn đúng, **và**
- với xoá: bản ghi biến mất khỏi cả màn quản trị lẫn nơi khác dùng nó (POS, storefront).

## R5 — Mỗi chức năng phải có ít nhất 1 test case tiêu cực

Với mọi chức năng `Implemented` có thao tác ghi hoặc gọi API, checklist phải có tối thiểu một test case dạng:

- dữ liệu sai / thiếu bắt buộc → bị chặn, có thông báo rõ;
- backend tắt → toast **lỗi**, không phải "Đã lưu";
- vượt ràng buộc nghiệp vụ (vượt tồn kho, phiếu hết hạn, ngày bắt đầu > ngày kết thúc) → bị chặn.

Chức năng chỉ có test case "đường sung sướng" ⇒ coi như **chưa đủ test**, không được đóng.

## R6 — Nút bị `disabled` hoặc không có handler = `Not implemented`

Không được ghi `Implemented` cho một nút chỉ vì nó hiển thị trên màn hình. Trước khi đặt `Implemented`, phải chỉ ra được:

- handler (`@click`, `@submit`) trỏ tới một hàm có thật, **và**
- hàm đó gọi tới API hoặc thực hiện logic thật (không phải chỉ `notify('… (demo)')`).

## R7 — ID không bao giờ được tái sử dụng

- Feature ID: `F-<MÃ MÀN>-<số 2 chữ số>` (ví dụ `F-POS-14`).
- Testcase ID: `TC-<MÃ MÀN>-<số feature>-<số 2 chữ số>` (ví dụ `TC-POS-14-02`).

Bỏ một chức năng ⇒ đổi `Status` thành `Legacy` hoặc xoá dòng, **nhưng số ID đó không được cấp lại cho chức năng khác**. Sửa tên chức năng thì giữ nguyên ID.

## R8 — Mỗi feature phải có ít nhất 1 test case, mỗi test case phải trỏ về feature có thật

Kiểm tra nhanh bằng PowerShell trước khi nộp / merge:

```powershell
cd docs\checklist
$f = Import-Csv feature-backlog.csv
$t = Import-Csv testcase-checklist.csv
# feature chưa có test case nào
$f | Where-Object { $_.'Feature ID' -notin $t.'Feature ID' } | Format-Table 'Feature ID',Feature
# test case trỏ tới feature không tồn tại
$t | Where-Object { $_.'Feature ID' -notin $f.'Feature ID' } | Format-Table 'Testcase ID','Feature ID'
# Pass mà không có bằng chứng  ->  vi phạm R1
$t | Where-Object { $_.'Test Status' -eq 'Pass' -and -not $_.'Log Reference' } | Format-Table 'Testcase ID'
```

Cả ba lệnh phải trả về **rỗng**.

## R9 — Thu log sau mỗi lượt test, đính kèm khi báo lỗi

- Log server + log client nằm chung một file: `logs/bshoes.log` (xem `logback-spring.xml`; đổi chỗ bằng `-DLOG_DIR=<path>`). Dòng do frontend gửi lên mang logger `CLIENT`.
- Log client trong trình duyệt: **Hệ Thống → Tải file log** (`bshoes-log-YYYYMMDD-HHmm.json`).
- Khi ghi `Fail`, `Log Reference` phải trỏ tới **dòng log cụ thể** hoặc **file:dòng trong source**, không chỉ mô tả cảm tính.

## R10 — Trạng thái hết hạn sau mỗi lần đổi code

Sau khi merge bất kỳ thay đổi nào chạm tới một màn hình:

1. Mọi test case của các feature thuộc màn đó chuyển về `Not run` (trừ test case được tự động hoá).
2. Chạy lại 2 bộ test tự động và cập nhật lại:

```powershell
cd frontend; npm test -- --run
cd ..; $env:JAVA_HOME='C:\Users\DREAMSTORE\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.19.10-hotspot'; .\mvnw test
```

3. Chạy lại các test case tay của màn đó.

Test case tự động hoá được thì **phải** tự động hoá — chỉ giữ ở dạng tay khi thật sự cần mắt người (in ấn, quét QR, camera).

## R12 — Mỗi vòng review phải XOÁ TRẮNG kết quả cũ rồi chạy lại regression

Đây là quy tắc quan trọng nhất của tài liệu này. Một vòng review (mỗi lần rà soát
lại toàn bộ hệ thống) **không được** giữ lại trạng thái của vòng trước.

**Bắt buộc, theo đúng thứ tự:**

1. **Xoá trắng**: đặt lại `Test Status` = `Not run` và **xoá sạch** cột `Log Reference`
   của **toàn bộ** test case. Không có ngoại lệ, kể cả test case vòng trước ghi `Pass`.
2. **Chạy lại regression** trong MỘT lượt duy nhất:
   ```powershell
   cd frontend; npm test -- --run; npm run build
   cd ..; $env:JAVA_HOME='...'; .\mvnw.cmd -o test
   ```
3. **Ghi lại trạng thái mới** — mọi bằng chứng phải mang **cùng một mã vòng review**
   (`R<n> <ngày giờ>`), để nhìn là biết ngay kết quả thuộc vòng nào.
   Bằng chứng của vòng cũ là **vô giá trị**: code đã đổi từ lúc đó.
4. **Không được suy diễn**: `Pass` chỉ ghi khi lượt chạy MỚI thật sự bao phủ test case
   đó. Kết quả curl / kiểm tra tay của vòng trước phải chạy lại, nếu không thì để
   `Not run`.

**Danh sách chức năng (`feature-backlog.csv`) thì KHÔNG bị xoá trắng.**
Chức năng tồn tại độc lập với việc đã kiểm thử hay chưa; giữ nguyên `Feature ID` và
`Status` nếu code không đổi. Chỉ sửa dòng nào có code thay đổi thật.

**Khi danh sách chức năng thay đổi, phải BÁO cho người dùng** — nêu rõ:

- chức năng nào **mới thêm** (kèm Feature ID);
- chức năng nào **đổi mô tả hoặc đổi `Status`**, và vì sao;
- chức năng nào **bị gỡ / chuyển `Legacy`**.

Không được lặng lẽ thêm/sửa dòng trong backlog rồi chỉ báo "đã cập nhật checklist".

**Nhật ký các vòng review** phải được ghi ở cuối tài liệu này (mục "Lịch sử vòng review"),
mỗi vòng một khối: mã vòng, ngày, số test đã chạy, số chức năng thay đổi.

## R11 — Không đóng feature khi còn test case Fail hoặc Blocked

Một feature chỉ được coi là xong khi **toàn bộ** test case của nó ở trạng thái `Pass`. Còn `Blocked` thì lý do phải nằm trong `Log Reference` và phải có người chịu trách nhiệm gỡ (ví dụ: bật SQL Server).

---

## Trạng thái lần chạy gần nhất — 2026-08-04 (sau đợt sửa)

Quy mô: **169 chức năng**, **224 test case** (vòng R3).
Phân bố test: `Pass` 62 · `Fail` 2 · `Blocked` 127 · `Not run` 33.
Test tự động: frontend **132/132** (12 file), backend **48/48** (6 lớp).

Ý nghĩa hai nhóm chưa chạy — dùng để chia việc kiểm thử tay:

- `Blocked` (127) — **cần SQL Server**. Đây là nhóm bạn sẽ tự test.
- `Not run` (33) — **chỉ cần mở trình duyệt**, không cần DB (tải file, xem trước khi in,
  banner, toast, thu gọn sidebar…).

### Vòng rà soát 2 — lỗi tìm được bằng đọc code + test, không cần DB

| Lỗi | Ảnh hưởng | Trạng thái |
|---|---|---|
| `vitest.setup.js` stub `localStorage` trả null vĩnh viễn | Mọi thứ lưu qua localStorage (giỏ hàng, phiên, log) **không thể test được** | Sửa: mock in-memory thật |
| `useCart` / `useAuth` `JSON.parse` không bọc try/catch, chạy lúc import module | localStorage hỏng → **trắng trang toàn app**, không vào lại được | Sửa: `readCart()` / `readUser()` |
| `vnd('250000')` → `250000 ₫` | Giá đến dạng chuỗi hiển thị **mất dấu phân cách** | Sửa: ép `Number()` trước |
| `thanhToan()` áp phiếu giảm giá chỉ bằng `findById` | Phiếu **hết hạn / bị tắt / đã xoá / hết lượt** vẫn giảm được nếu post thẳng id; `so_luong` không bao giờ bị trừ | Sửa: `VoucherRules` + `consumeOne()` |
| Tổng tiền POS / giỏ hàng cộng thẳng `l.gia * l.soLuong` | 1 dòng thiếu giá → **toàn bộ hoá đơn thành NaN** | Sửa: `Number(...) \|\| 0` |
| `hoanTra()` ở POS | Chỉ đổi nhãn dòng, **không cộng lại kho** — TC-POS-07-01 để `Fail` | Ghi nhận, chưa sửa |

Hai test case `Fail` còn lại đều là **cố ý ghi nhận**, không phải bug chưa biết:
`TC-AUTH-08-01` (không hash mật khẩu — theo yêu cầu) và `TC-POS-07-01` (hoàn trả tại quầy
mới là nhãn hiển thị; muốn trả hàng thật thì dùng F-POS-20).

### Lỗi đọc được từ `logs/` — đã sửa hết phần nguyên nhân

Nguồn: `logs/bshoes-2026-07-31.0.log` (lần chạy có DB thật) + `logs/bshoes-log-20260731-1425.json`.

| Lỗi trong log | Số lần | Nguyên nhân | Trạng thái |
|---|---|---|---|
| `500 GET /api/hoa-don/vouchers-active` | 45 client / 2 server | View `view_phieu_giam_gia_hoat_dong` bị map vào **entity** `PhieuGiamGia`; view thiếu 5 cột audit → `Unable to find column position by name: ngay_cap_nhat` | Sửa: projection `VoucherActiveView` |
| `500 GET /api/nhan-vien` | 78 client / 5 server | `NhanVien.idVaiTro` LAZY + `open-in-view=false` → `LazyInitializationException` | Sửa: `left join fetch` + `@Transactional(readOnly)` |
| `404 GET /san-pham-chi-tiet/by-ma/sp1` | 77 | `PosSanPhamDto` không có `ma`, POS tự bịa `SP1/SP2…` rồi tra mã đó | Sửa: thêm `ma` thật vào DTO |
| Dropdown khuyến mãi luôn rỗng | — | Mọi phiếu seed hết hạn 11/2025 | Sửa: sinh phiếu 2025-2030 (`sqlBshoes.sql` §2.16b) |
| Phiếu giảm giá không giảm gì | — | `giamToiDa \|\| 0` biến "không giới hạn" thành "trần 0đ"; cột `loai_giam_gia` trong seed bị đảo | Sửa: `utils/voucher.js` + hàm SQL + seed |
| `WARN HHH90000025` mỗi lần khởi động | mỗi lần | Pin `hibernate.dialect` không cần thiết | Sửa: bỏ ở bản chạy thật |
| `401 POST /auth/login` | 1 | Sai mật khẩu — **không phải lỗi** | Không sửa |

Toàn bộ nguyên nhân đã sửa trong code, nhưng **chưa chứng minh được bằng chạy thật** vì
chưa có DB ⇒ các test case tương ứng để `Blocked`, không phải `Pass` (quy tắc R1).

Đã chạy và có bằng chứng:

- Frontend: `npm test -- --run` → **90/90 pass**, 8 file test (trước đợt sửa: 62/62).
- Frontend: `npm run build` → thành công.
- Backend: `mvnw test` → **37/37 pass** (trước đợt sửa: 21/21). Mới thêm `ScreenPermissionsTest` 16/16.
- Backend chạy ở cổng 8085; kiểm tra bộ lọc quyền bằng curl:
  `/api/ping` → 200 · `/api/nhan-vien` → **401** · `/api/san-pham-ql` → **401** ·
  `/api/thong-ke/hom-nay` → **401** · token rác → **401** · `/api/san-pham` (công khai) → 500 vì lỗi DB, **không** phải 401.

Đã sửa trong đợt này (6 nhóm vấn đề của lần chạy trước):

1. **Bảo vệ API phía server** — thêm `security/ApiAuthFilter` + `ScreenPermissions` + `SessionRegistry`:
   đăng nhập cấp token, mọi request mang `X-Auth-Token`, server đối chiếu quyền màn hình từ `nhan_vien_quyen`.
   *Lưu ý:* Spring Security **không** được dùng — `spring-boot-starter-security` đang bị comment trong `pom.xml:37-40`.
2. **Fallback mock không còn im lặng** — `DemoDataBanner.vue` + cờ `isDemo` ở `useCrud`, `useLichSu`,
   `useHoaDon`, `HomeView`, `DashboardView`, `BaoHanhView`, `SanPhamView`.
3. **Ô tìm kiếm storefront** — `filterByKeyword()` (bỏ dấu tiếng Việt), nút + phím Enter.
4. **Bảo Hành** — nút Tìm kiếm, Export CSV, Import CSV đều hoạt động thật.
5. **Xuất / Import JSON** ở Sản Phẩm và POS (chỉ JSON; CSV/Excel vẫn chưa hỗ trợ — nhãn nút đã sửa cho đúng).
6. **Thùng rác sản phẩm** — "Xem danh sách bị ẩn" mở modal, khôi phục qua `/recycle` + `/restore`.

Phát hiện thêm và đã sửa: **nút Đăng xuất ở màn Hệ Thống trước đây không xoá phiên**, chỉ chuyển trang.

Còn lại:

- **SQL Server localhost:1433 vẫn chưa chạy** ⇒ 108 test case nghiệp vụ ở `Blocked`.
  Bật SQL Server + nạp `sqlBshoes.sql` là việc phải làm trước tiên.
- 45 test case `Not run` cần bấm tay trên trình duyệt (banner demo, tải file, in ấn).
- 1 test case `Fail` **cố ý**: `TC-AUTH-08-01` — mật khẩu vẫn lưu plaintext theo yêu cầu
  (prototype dùng cho user testing). Giữ nguyên trạng thái `Fail` để không quên trước khi lên bản thật.

Xem thêm [../regression-checklist.md](../regression-checklist.md) cho danh sách kiểm thử tay của đợt sửa CRUD trước.

---

## Lịch sử vòng review

### R3 — 2026-08-04 12:05 (vòng đầu tiên áp dụng R12)

- **Xoá trắng** toàn bộ 218 test case rồi chạy lại từ đầu. Mọi bằng chứng trong
  `testcase-checklist.csv` giờ đều mang tiền tố `R3 2026-08-04 12:05`.
- Regression đã chạy: frontend **132/132** (12 file) + `npm run build` OK ·
  backend **48/48** (6 lớp) · curl cổng bảo vệ + CORS · Edge headless (CDP) cho
  storefront.
- Kết quả: `Pass` 62 · `Fail` 2 · `Blocked` 127 · `Not run` 33 / **224** test case.
- **Chức năng**: 165 → **169**. Thêm F-SYS-19 (CORS), F-NAV-01, F-NAV-02, F-SYS-20.
  Sửa mô tả F-SYS-11 và F-SYS-15.
- **Lỗi hồi quy do chính vòng trước gây ra**: bỏ pin `hibernate.dialect` (định để hết
  cảnh báo HHH90000025) làm backend **không khởi động được** khi SQL Server chưa chạy.
  Đã revert, thêm TC-SYS-15-02 để không tái diễn.

### R2 — 2026-08-04 11:40 (trước khi có R12)

Rà soát code không cần DB: sửa `vitest.setup.js`, `readCart`/`readUser`, `vnd()`,
`VoucherRules`, tổng tiền NaN. Kết quả của vòng này **đã bị xoá** theo R12.

### R1 — 2026-08-04 10:00 (trước khi có R12)

Lập checklist lần đầu + sửa 6 nhóm lỗi (bảo mật server, banner dữ liệu demo, nút chết,
thùng rác, xuất/nhập JSON) và 4 lỗi đọc từ `logs/`. Kết quả **đã bị xoá** theo R12.
