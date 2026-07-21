# Chuẩn hóa bộ tài liệu Workshop 1-2-3 theo Scrum (BShoes)

Ngày: 2026-07-21
Nguồn chuẩn (bắt buộc bám sát): `D:\temp_fpt\temp_AGILE\doc-school-request`

## 1. Mục tiêu và 4 vấn đề cần fix

Làm lại bộ tài liệu workshop BShoes cho đúng chuẩn Scrum của môn học. Bốn vấn đề của bản hiện tại:

1. **ID chưa chuẩn hóa**: PB và Task (T01..T90) đánh số theo thứ tự dữ liệu, không chạy theo Sprint. PB-30 bị tách task làm 2 chỗ (v1.0 và v1.2).
2. **Task không khớp story/product backlog**: hệ quả của (1), không gom sạch được theo story để lên Sprint Backlog từng sprint.
3. **Thiếu request use case**: có REQ nhưng chưa trình bày ở cấp request dạng "As a/an [role], I want [goal], so that [reason]" như sheet Product Backlog của file mẫu.
4. **Thiếu chia sprint theo product backlog**: chỉ có bảng tóm tắt, chưa có Release Backlog (map story vào Sprint#) và Sprint Backlog tách riêng từng Sprint.

Khung chuẩn: **phần mềm bán tại quầy (POS) là lõi, làm sớm**; customer view & order online là **mở rộng ở sprint sau**.

## 2. Chuẩn tham chiếu đã rút ra từ doc-school-request

File `Workshop 1 guide/Dach sach cong viec trong Sprint, Release backlog, product backlog.xlsx` có 3 sheet định nghĩa chính xác cột của 3 artifact. Phải theo đúng cột:

- **Product Backlog** (cấp Request): `ID | As a/an [User role] | I want to [Goal] | So that [reason] | Priority | Business Value | Acceptance Criteria | State | Note`. ID = RQ01..
- **Release Backlog** (cấp Backlog item): `Backlog ID | Backlog | As a/an [role] | I want to [goal] | So that [reason] | Story ID | Priority | Business Value | Sprint# | State | Note`.
- **Sprint Backlog** (cấp Task): `Task ID | Task | Description | Story ID | Backlog ID | Sprint# | State | Estimate Time (Hours) | Assign to | Note`. Task gom theo module, mỗi nhóm có Draw Usecase / Analyse & design / Code / Integrate / Test.

WS2 guide PDF: Product Backlog (`ID | User Story | Mô tả | Độ ưu tiên | Story Points`) → Sprint Planning (`Sprint | Mục tiêu`) → Sprint Backlog từng sprint (`Task ID | Product Backlog | Task | Mô tả công việc | Estimate (giờ) | Trạng thái`, Trạng thái = To Do).

WS2 guide `uoc luong so luong story.docx`: công thức UP = Loại tương tác + Quy tắc nghiệp vụ + Số thực thể + Thao tác dữ liệu (mỗi mục 1..3); AP = UP × C; PPS = (AP × ED)/36; ED = 18 yếu tố × (0/2), tối đa 36. Story Point quy về Fibonacci.

WS3 (`Yêu cầu workshop 3.docx`): (1) tầm nhìn kiến trúc (khái niệm, nội dung, đặc điểm, vai trò, ví dụ, lợi ích); (2) báo cáo; (3) ảnh Trello gồm cột Thành viên, Product Backlog, và 6 cột Sprint 1..6.

## 3. Mô hình ID 3 cấp cho BShoes

Quan hệ **1 RQ → nhiều PB → nhiều Task** (theo yêu cầu người dùng).

| Cấp | Mã | Ngữ nghĩa | Artifact |
|---|---|---|---|
| 1 | RQ-xx | Request/user story gốc từ actor, dạng As a/I want/So that | Product Backlog |
| 2 | PB-xx | Product Backlog Item (user story cụ thể) bóc từ RQ; có Priority, Sprint#, Story Point | Release Backlog |
| 3 | T-xx | Task cho Dev | Sprint Backlog (từng Sprint) |

Mỗi Task mang: `Story ID = PB-xx`, `Backlog ID = RQ-xx`, `Sprint#`. Truy vết trọn chuỗi RQ → PB → Task.

Quy ước (bám file mẫu): **Business Value = High/Medium/Low**; **Priority = số 1..4** (1 cao nhất); **State**: Product/Release Backlog = "New", Sprint Backlog task = "To Do".

### 3.1. Danh sách RQ (9 request theo 6+3 chức năng)

Định dạng: RQ | role | goal | so that | priority | business value | sprint.

- RQ-01 Đăng nhập | nhân viên | đăng nhập và được cấp đúng quyền theo vai trò | dùng hệ thống an toàn, đúng phạm vi | 1 | High | S1
- RQ-02 Nhân viên & Phân quyền | quản trị viên | quản lý nhân viên và phân quyền truy cập theo vai trò và theo từng người | kiểm soát ai được dùng chức năng nào | 1 | High | S1
- RQ-03 Quản lý sản phẩm & danh mục | quản lý | quản lý sản phẩm, biến thể màu/size, tồn kho, danh mục và thuộc tính | quản trị được toàn bộ hàng hóa | 2 | High | S2
- RQ-04 Bán hàng tại quầy (POS) | nhân viên bán hàng | bán tại quầy, tự trừ kho, thanh toán và in hóa đơn | phục vụ khách nhanh và chính xác | 1 | High | S3
- RQ-05 Đơn đặt hàng & Giao hàng | nhân viên giao hàng | tạo và theo dõi đơn giao, xử lý trả hàng và hoàn kho | giao hàng tận nơi có kiểm soát | 2 | Medium | S4
- RQ-06 Quản lý khách hàng | quản lý | quản lý khách hàng và địa chỉ giao hàng | chăm sóc khách và giao đúng địa chỉ | 2 | Medium | S4
- RQ-07 Cửa hàng online & Preorder (mở rộng) | khách hàng | xem cửa hàng online, mua giao tận nhà và đặt trước mẫu hết hàng | mua sắm online thuận tiện | 3 | Medium | S5
- RQ-08 Dashboard & Báo cáo doanh thu (mở rộng) | kế toán/chủ shop | xem KPI, doanh thu, lợi nhuận, ROI và xuất báo cáo | ra quyết định kinh doanh dựa trên dữ liệu | 3 | High | S6
- RQ-09 Sau bán hàng: Bảo hành & Khuyến mãi (mở rộng) | nhân viên bảo hành/quản lý | tiếp nhận và xử lý bảo hành, quản lý phiếu giảm giá/khuyến mãi | chăm sóc khách sau bán và thúc đẩy doanh số | 3 | Medium | S6

### 3.2. Kế hoạch 6 Sprint (POS lõi sớm, online sau)

| Sprint | Mục tiêu | RQ |
|---|---|---|
| S1 | Nền tảng: đăng nhập + Nhân viên & Phân quyền | RQ-01, RQ-02 |
| S2 | Quản lý sản phẩm, biến thể, danh mục, thuộc tính | RQ-03 |
| S3 | POS bán tại quầy + hóa đơn (KHUNG CHUẨN) | RQ-04 |
| S4 | Đơn đặt hàng + giao hàng + khách hàng | RQ-05, RQ-06 |
| S5 | Cửa hàng online (mở rộng): xem SP + mua COD + preorder | RQ-07 |
| S6 | Dashboard doanh thu + bảo hành + khuyến mãi | RQ-08, RQ-09 |

### 3.3. Ánh xạ 40 PB hiện có sang RQ và Sprint

(id cũ trong bshoes_data.py → RQ mới → Sprint). Renumber PB/Task sẽ tính tự động, KHÔNG hardcode.

- S1: cũ PB-01 (đăng nhập)→RQ-01; PB-29 (CRUD NV), PB-30 (bộ quyền vai trò), PB-38 (phân quyền từng NV)→RQ-02
- S2 (RQ-03): PB-10, PB-11, PB-12, PB-13, PB-14, PB-40
- S3 (RQ-04): PB-02, PB-03, PB-04, PB-05, PB-06, PB-07, PB-08, PB-09
- S4: PB-15, PB-16, PB-17→RQ-05; PB-21, PB-22→RQ-06
- S5 (RQ-07): PB-31, PB-33, PB-34, PB-35, PB-36, PB-37, PB-39
- S6: PB-23, PB-24, PB-25, PB-26, PB-27, PB-28→RQ-08; PB-18, PB-19, PB-20, PB-32→RQ-09

Kiểm đếm: 4 + 6 + 8 + 5 + 7 + 10 = 40 story. Task tổng ~90 giữ nguyên nội dung, chỉ gộp PB-30 (đang tách 2 chỗ) về 1 story và đánh số lại.

## 4. Thuật toán đánh số lại (trong bshoes_data.py)

1. Mỗi PB khai báo kèm `rq` (RQ-xx) và `sprint` (1..6) thay cho trường sprint rời rạc cũ. Story point vẫn tính từ 4 tiêu chí UP.
2. Sắp xếp PB theo khóa `(sprint, thứ tự RQ trong sprint, priority số, thứ tự khai báo)` rồi gán `PB-01..PB-40` theo thứ tự đó.
3. Task tham chiếu PB bằng khóa ổn định (id cũ hoặc key). Sau khi PB có số mới, sắp Task theo `(số PB mới, thứ tự khai báo trong PB)` rồi gán `T-01..T-nn`.
4. Sinh sẵn 3 map: `pb_by_rq`, `pb_sprint`, `task_story/backlog/sprint` để generator dùng lại, tránh lệch số giữa docx và xlsx.

Bất biến phải verify: mọi Task có Story ID trỏ tới PB tồn tại; mọi PB có RQ tồn tại; số PB và Task liên tục, không trùng, chạy đúng thứ tự Sprint.

## 5. Sản phẩm bàn giao

### Workshop 1 - Kế hoạch dự án (docx)
Chương: (1) Tổng quan + nhóm; (2) Danh sách chức năng (6 lõi + 3 mở rộng); (3) **Product Backlog cấp Request** (bảng RQ As a/I want/So that + Priority + Business Value + Acceptance Criteria + State); (4) Quy trình Scrum + bảng phân rã RQ→PB→Task; (5) **Release Backlog + chia Sprint** (map PB vào Sprint#); (6) Sơ đồ chức năng + sơ đồ use case (mô tả cây); Kết luận.

### Workshop 2 - Ước lượng (docx + xlsx)
docx: mục tiêu PB/Sản phẩm/Sprint; công thức UP/AP/PPS; ED 18 yếu tố; bảng ước lượng từng story; per-sprint sprint goal + story points.
xlsx 7 sheet: 0.Tổng quan · 1.Product Backlog (RQ) · 2.Release Backlog (PB, có Sprint#) · 3.Sprint Planning · 4.Sprint Backlog (tách tiêu đề từng Sprint 1..6) · 5.ED · 6.Ước lượng Story. Công thức Excel tự tính (C, ED, PPS, VLOOKUP story point) như bản cũ.

### Workshop 3 - Kiến trúc + Trello (docx mới)
Chương: (1) Tầm nhìn kiến trúc (khái niệm, nội dung, đặc điểm, vai trò, ví dụ kiến trúc BShoes: Vue 3 SPA + Spring Boot/JPA + SQL Server, lợi ích); (2) Báo cáo dự án (khối lượng, tiến độ, ED); (3) Mô tả bảng Trello: cột Thành viên (tên, vai trò, việc), cột Product Backlog, 6 cột Sprint 1..6 với thẻ = các PB của sprint đó. Xuất kèm bảng mô phỏng board để dán/tham chiếu.

## 6. Thực hiện qua generator (giữ convention đã chốt)

- Sửa `bshoes_data.py`: thêm cấu trúc RQ (role/goal/reason/priority/business_value), thêm `rq`+`sprint` cho từng PB, viết hàm renumber mục 4, thêm `SPRINTS` mục tiêu mới.
- Sửa `gen_docx.py`: WS1 dùng Product Backlog RQ + Release Backlog; WS2 thêm per-sprint.
- Sửa `gen_ws2_xlsx.py`: sheet 1 = Product Backlog (RQ), sheet 2 = Release Backlog (PB), sheet 4 = Sprint Backlog tách từng sprint.
- Thêm `gen_ws3.py`: sinh WS3 docx.
- Giữ `docx_format.py` (TNR 13, số Ả Rập, caption H5/H6, cấm gạch ngang dài), chạy `verify_docx.py` đọc ngược. Không dùng em dash/en dash.

## 7. Kiểm thử / nghiệm thu

- `python gen_docx.py ..`, `python gen_ws2_xlsx.py ../WORKSHOP_2_BShoes.xlsx`, `python gen_ws3.py ..` chạy không lỗi.
- `verify_docx.py` pass (không có gạch ngang dài, đúng heading/caption).
- Script kiểm bất biến ID (mục 4) pass: RQ/PB/Task liên tục, truy vết đủ, đúng thứ tự sprint.
- Đối chiếu tay: mỗi cột 3 artifact khớp cột file mẫu doc-school-request.
