# -*- coding: utf-8 -*-
"""BShoes workshop data: RQ (request) -> PB (product backlog / user story) -> Task.

Mo hinh 3 cap chuan Scrum:
  RQ-xx : request tu actor, dang As a / I want / So that      -> Product Backlog
  PB-xx : user story cu the boc tu RQ, co Priority/Sprint/SP  -> Release Backlog
  T-xx  : task cho Dev                                        -> Sprint Backlog

PB va Task duoc DANH SO LAI theo thu tu Sprint (ham renumber ben duoi), nen
Sprint Backlog chay lien tuc va moi Task truy vet du Story ID + Backlog ID + Sprint#.
"""

PROJECT = "BShoes: Hệ thống quản lý & bán hàng cửa hàng giày"
VERSION = "v1.2 (chuẩn hóa Scrum: RQ -> PB -> Task, đánh số theo Sprint)"
DEADLINE = "30/09/2026"

TEAM = [
    ("PO",   "[Thành viên 1]", "Product Owner: thu thập yêu cầu, viết Product Backlog, sắp ưu tiên, nghiệm thu"),
    ("SM",   "[Thành viên 2]", "Scrum Master: điều phối sprint, gỡ vướng, theo dõi tiến độ"),
    ("DEV1", "[Thành viên 3]", "Dev Backend: Spring Boot / JPA / SQL Server"),
    ("DEV2", "[Thành viên 4]", "Dev Frontend: Vue 3 SPA"),
    ("DEV3", "[Thành viên 5]", "Dev Fullstack: tích hợp API, hỗ trợ 2 phía"),
    ("QC",   "[Thành viên 6]", "Kiểm thử: test chức năng, báo lỗi, xác nhận Done"),
]

ROLE_NAME = {"NV": "Nhân viên", "QL": "Quản lý", "NK": "Nhân viên kho", "BH": "NV bảo hành",
             "KT": "Kế toán", "ADMIN": "Quản trị", "KH": "Khách hàng"}

# ---- cap 1: RQ (request tu actor) -----------------------------------------
# id, ten, role, goal, so_that, priority(1..4), business_value, sprint
RQS = [
 {"id": "RQ-01", "ten": "Đăng nhập", "role": "nhân viên",
  "goal": "đăng nhập và được cấp đúng quyền theo vai trò",
  "so_that": "dùng hệ thống an toàn, đúng phạm vi", "priority": 1, "bv": "High", "sprint": 1},
 {"id": "RQ-02", "ten": "Nhân viên & Phân quyền", "role": "quản trị viên",
  "goal": "quản lý nhân viên và phân quyền truy cập theo vai trò và theo từng người",
  "so_that": "kiểm soát ai được dùng chức năng nào", "priority": 1, "bv": "High", "sprint": 1},
 {"id": "RQ-03", "ten": "Quản lý sản phẩm & danh mục", "role": "quản lý",
  "goal": "quản lý sản phẩm, biến thể màu/size, tồn kho, danh mục và thuộc tính",
  "so_that": "quản trị được toàn bộ hàng hóa", "priority": 2, "bv": "High", "sprint": 2},
 {"id": "RQ-04", "ten": "Bán hàng tại quầy (POS)", "role": "nhân viên bán hàng",
  "goal": "bán tại quầy, tự trừ kho, thanh toán và in hóa đơn",
  "so_that": "phục vụ khách nhanh và chính xác", "priority": 1, "bv": "High", "sprint": 3},
 {"id": "RQ-05", "ten": "Đơn đặt hàng & Giao hàng", "role": "nhân viên giao hàng",
  "goal": "tạo và theo dõi đơn giao, xử lý trả hàng và hoàn kho",
  "so_that": "giao hàng tận nơi có kiểm soát", "priority": 2, "bv": "Medium", "sprint": 4},
 {"id": "RQ-06", "ten": "Quản lý khách hàng", "role": "quản lý",
  "goal": "quản lý khách hàng và địa chỉ giao hàng",
  "so_that": "chăm sóc khách và giao đúng địa chỉ", "priority": 2, "bv": "Medium", "sprint": 4},
 {"id": "RQ-07", "ten": "Cửa hàng online & Preorder", "role": "khách hàng",
  "goal": "xem cửa hàng online, mua giao tận nhà và đặt trước mẫu hết hàng",
  "so_that": "mua sắm online thuận tiện", "priority": 3, "bv": "Medium", "sprint": 5},
 {"id": "RQ-08", "ten": "Dashboard & Báo cáo doanh thu", "role": "kế toán và chủ shop",
  "goal": "xem KPI, doanh thu, lợi nhuận, ROI và xuất báo cáo",
  "so_that": "ra quyết định kinh doanh dựa trên dữ liệu", "priority": 3, "bv": "High", "sprint": 6},
 {"id": "RQ-09", "ten": "Sau bán hàng: Bảo hành & Khuyến mãi", "role": "nhân viên bảo hành và quản lý",
  "goal": "tiếp nhận và xử lý bảo hành, quản lý phiếu giảm giá và khuyến mãi",
  "so_that": "chăm sóc khách sau bán và thúc đẩy doanh số", "priority": 3, "bv": "Medium", "sprint": 6},
]

# ---- cap 2: PB (user story boc tu RQ) -------------------------------------
# Khai bao bang KEY cu de tai dung _T. Thu tu khai bao da nhom theo RQ-01..RQ-09
# nen sprint khong giam; renumber se gan PB-01.. theo dung thu tu nay.
# (key_cu, rq, role_code, user_story, mo_ta, priority, bv, (tuong_tac, quy_tac, thuc_the, thao_tac))
_PB = [
 ("PB-01", "RQ-01", "NV", "Là nhân viên, tôi muốn đăng nhập để dùng hệ thống theo đúng quyền", "Xác thực tài khoản, lấy vai trò & quyền truy cập màn hình", 1, "High", (2, 2, 2, 1)),
 ("PB-29", "RQ-02", "ADMIN", "Là quản trị, tôi muốn CRUD nhân viên", "Thêm/sửa/xóa/tìm nhân viên, gán vai trò", 1, "High", (2, 2, 2, 3)),
 ("PB-30", "RQ-02", "ADMIN", "Là quản trị, tôi muốn đặt bộ quyền mặc định cho từng vai trò", "Vai trò là template: bộ quyền mặc định chép cho nhân viên khi tạo mới hoặc đổi vai trò", 1, "High", (2, 3, 2, 2)),
 ("PB-38", "RQ-02", "ADMIN", "Là quản trị, tôi muốn phân quyền truy cập cho từng nhân viên", "Lưới nhân viên x màn hình; vai trò chỉ là template, mỗi người mở rộng thêm được", 1, "High", (3, 3, 3, 3)),
 ("PB-10", "RQ-03", "QL", "Là quản lý, tôi muốn CRUD sản phẩm để quản trị danh mục hàng", "Thêm/sửa/xóa mềm/khôi phục/thùng rác sản phẩm", 2, "High", (2, 3, 3, 3)),
 ("PB-11", "RQ-03", "QL", "Là quản lý, tôi muốn quản lý biến thể (màu/size/giá/giá nhập)", "CRUD biến thể, sửa giá bán & giá vốn, trạng thái bán", 2, "High", (2, 3, 3, 3)),
 ("PB-12", "RQ-03", "NK", "Là nhân viên kho, tôi muốn nhập kho để tăng tồn", "Cộng số lượng tồn cho biến thể", 2, "Medium", (1, 1, 1, 1)),
 ("PB-13", "RQ-03", "QL", "Là quản lý, tôi muốn quản lý danh mục và xếp sản phẩm vào danh mục", "CRUD danh mục, gán/bỏ sản phẩm khỏi danh mục", 2, "Medium", (2, 2, 2, 2)),
 ("PB-14", "RQ-03", "QL", "Là quản lý, tôi muốn quản lý thuộc tính sản phẩm", "CRUD 8 nhóm thuộc tính, lọc theo đúng loại", 2, "Medium", (2, 2, 3, 3)),
 ("PB-40", "RQ-03", "QL", "Là quản lý, tôi muốn thao tác sản phẩm, danh mục, thuộc tính trong cùng một màn", "Gộp danh mục và thuộc tính thành tab của Quản lý sản phẩm, bớt nhảy màn", 3, "Low", (2, 1, 1, 1)),
 ("PB-02", "RQ-04", "NV", "Là nhân viên, tôi muốn tạo hóa đơn chờ để bắt đầu một lượt bán", "Sinh mã HĐ, trạng thái Chờ, tối đa 3 hóa đơn chờ", 1, "High", (2, 2, 2, 2)),
 ("PB-03", "RQ-04", "NV", "Là nhân viên, tôi muốn thêm hàng vào giỏ và hệ thống tự trừ kho", "Thêm dòng hóa đơn, trừ tồn nguyên tử, tính lại tổng tiền", 1, "High", (3, 3, 3, 3)),
 ("PB-04", "RQ-04", "NV", "Là nhân viên, tôi muốn áp phiếu giảm giá cho hóa đơn", "Kiểm tra đơn tối thiểu, loại %/tiền, giảm tối đa", 2, "Medium", (3, 3, 2, 2)),
 ("PB-05", "RQ-04", "NV", "Là nhân viên, tôi muốn thanh toán tiền mặt / chuyển khoản / kết hợp", "Kiểm tiền khách đưa, VietQR, chốt trạng thái, ghi lịch sử", 1, "High", (3, 3, 3, 3)),
 ("PB-06", "RQ-04", "NV", "Là nhân viên, tôi muốn in hóa đơn / phiếu tạm tính cho khách", "Kết xuất phiếu và gửi lệnh in", 1, "High", (3, 1, 2, 1)),
 ("PB-07", "RQ-04", "NV", "Là nhân viên, tôi muốn hủy hóa đơn và hoàn lại kho", "Đổi trạng thái Hủy, cộng trả tồn kho, ghi lịch sử", 2, "Medium", (3, 2, 3, 3)),
 ("PB-08", "RQ-04", "NV", "Là nhân viên, tôi muốn quét QR/mã vạch để thêm sản phẩm nhanh", "Bật camera, giải mã, tra cứu biến thể theo mã", 2, "Medium", (3, 2, 2, 2)),
 ("PB-09", "RQ-04", "NV", "Là nhân viên, tôi muốn thêm nhanh khách hàng ngay tại quầy", "Popup thêm khách (chỉ thêm, không xem danh sách)", 2, "Medium", (2, 1, 1, 1)),
 ("PB-15", "RQ-05", "NV", "Là nhân viên, tôi muốn tạo đơn giao hàng kèm phí ship", "Thanh toán + phí ship, chuyển trạng thái Chờ giao", 2, "High", (2, 3, 3, 3)),
 ("PB-16", "RQ-05", "NV", "Là nhân viên, tôi muốn cập nhật trạng thái Đã giao", "Chuyển Chờ giao -> Đã giao, ghi lịch sử", 2, "Medium", (1, 2, 2, 2)),
 ("PB-17", "RQ-05", "QL", "Là quản lý, tôi muốn xử lý trả hàng và hoàn kho", "Trạng thái Trả hàng, cộng trả tồn, ghi lịch sử", 2, "Medium", (2, 3, 3, 3)),
 ("PB-21", "RQ-06", "QL", "Là quản lý, tôi muốn CRUD khách hàng", "Thêm/sửa/xóa/tìm kiếm khách hàng", 2, "Medium", (2, 2, 1, 3)),
 ("PB-22", "RQ-06", "QL", "Là quản lý, tôi muốn quản lý địa chỉ giao hàng của khách", "CRUD nhiều địa chỉ cho mỗi khách", 2, "Medium", (2, 2, 2, 3)),
 ("PB-31", "RQ-07", "KH", "Là khách hàng, tôi muốn xem trang chủ cửa hàng", "Danh mục giày, sản phẩm, sắp xếp giá/bán chạy", 3, "Medium", (2, 2, 3, 1)),
 ("PB-33", "RQ-07", "KH", "Là khách hàng, tôi muốn thêm giày vào giỏ hàng", "Giỏ lưu ở trình duyệt, sửa số lượng, chặn vượt tồn kho", 3, "Medium", (2, 2, 1, 3)),
 ("PB-34", "RQ-07", "KH", "Là khách hàng, tôi muốn đặt hàng giao tận nhà (COD)", "Nhập người nhận/SĐT/địa chỉ, tạo đơn Chờ giao, trừ kho", 2, "High", (3, 3, 3, 2)),
 ("PB-35", "RQ-07", "KH", "Là khách hàng, tôi muốn tra cứu đơn của tôi và xác nhận đã nhận", "Tra theo SĐT, xem trạng thái, bấm Đã nhận hàng", 3, "Medium", (2, 2, 2, 2)),
 ("PB-36", "RQ-07", "KH", "Là khách hàng, tôi muốn đặt trước mẫu đang hết hàng", "Chỉ cho đặt khi tồn = 0, để lại SĐT, xem số người đang chờ", 3, "Medium", (2, 3, 2, 2)),
 ("PB-37", "RQ-07", "NV", "Là nhân viên, tôi muốn quản lý phiếu đặt trước và chuyển thành đơn khi hàng về", "Tab trạng thái, báo khách có hàng, chuyển phiếu thành hóa đơn giữ hàng", 3, "Medium", (2, 3, 3, 3)),
 ("PB-39", "RQ-07", "KH", "Là khách hàng, tôi muốn xem trang chi tiết sản phẩm", "Chọn màu/size; còn hàng thì mua, hết hàng thì đặt trước", 2, "High", (3, 2, 3, 1)),
 ("PB-23", "RQ-08", "KT", "Là kế toán, tôi muốn xem KPI tổng quan", "Doanh thu, lợi nhuận, số đơn theo trạng thái, đơn TB", 1, "High", (1, 3, 3, 1)),
 ("PB-24", "RQ-08", "KT", "Là kế toán, tôi muốn xem biểu đồ dòng tiền theo tháng", "Doanh thu / giá vốn / lợi nhuận 12 tháng, lọc theo năm", 2, "High", (2, 3, 3, 1)),
 ("PB-25", "RQ-08", "KT", "Là kế toán, tôi muốn xem ROI theo sản phẩm", "(Doanh thu - giá vốn)/giá vốn, xếp hạng", 2, "Medium", (2, 3, 3, 1)),
 ("PB-26", "RQ-08", "KT", "Là kế toán, tôi muốn xem tỷ lệ giữ chân khách hàng", "Khách quay lại vs khách mới", 2, "Medium", (1, 3, 2, 1)),
 ("PB-27", "RQ-08", "KT", "Là kế toán, tôi muốn xem SP bán chạy / bán chậm / xu hướng", "Xếp hạng theo số bán, tăng trưởng so kỳ trước", 2, "Medium", (2, 3, 3, 1)),
 ("PB-28", "RQ-08", "KT", "Là kế toán, tôi muốn xuất báo cáo ra Excel", "Kết xuất KPI + sản phẩm bán chạy ra file", 3, "Low", (1, 1, 2, 1)),
 ("PB-18", "RQ-09", "BH", "Là NV bảo hành, tôi muốn tiếp nhận đơn bảo hành", "Tạo phiếu BH gắn khách + sản phẩm + hóa đơn + serial", 1, "High", (2, 2, 3, 2)),
 ("PB-19", "RQ-09", "BH", "Là NV bảo hành, tôi muốn cập nhật trạng thái xử lý bảo hành", "6 trạng thái: chưa xử lý -> đã trả, lọc theo tab", 1, "High", (2, 3, 2, 2)),
 ("PB-20", "RQ-09", "BH", "Là NV bảo hành, tôi muốn in phiếu bảo hành", "Kết xuất phiếu bảo hành", 3, "Low", (1, 1, 2, 1)),
 ("PB-32", "RQ-09", "QL", "Là quản lý, tôi muốn quản lý phiếu giảm giá", "CRUD phiếu, thời hạn, điều kiện, trạng thái", 2, "Medium", (2, 3, 1, 3)),
]

# ---- cap 3: TASK (giu nguyen noi dung, cot 0 = key PB cu) ------------------
# (pb_key_cu, task, mo_ta, est_hours, assignee)
_T = [
 ("PB-01", "Thiết kế bảng nhan_vien / vai_tro", "Chuẩn hóa CSDL, thêm cột quyen", 4, "DEV1"),
 ("PB-01", "API POST /api/auth/login", "Xác thực tài khoản, trả vai trò + quyền", 4, "DEV1"),
 ("PB-01", "Màn hình đăng nhập", "Form + báo lỗi sai tài khoản", 3, "DEV2"),
 ("PB-01", "Lưu phiên đăng nhập", "Store useAuth + localStorage, chặn route", 4, "DEV2"),
 ("PB-01", "Test đăng nhập", "Sai/đúng mật khẩu, hết phiên", 2, "QC"),
 ("PB-02", "API POST /hoa-don/create-empty", "Sinh mã HĐ, trạng thái 0, giới hạn 3 đơn chờ", 4, "DEV1"),
 ("PB-02", "Giao diện danh sách hóa đơn chờ", "Bảng hóa đơn + nút Tạo hóa đơn", 3, "DEV2"),
 ("PB-02", "Test tạo hóa đơn", "Tạo/chuyển/giới hạn 3 đơn", 2, "QC"),
 ("PB-03", "Query trừ kho nguyên tử", "UPDATE ... WHERE so_luong_ton >= n", 4, "DEV1"),
 ("PB-03", "API POST /hoa-don/{id}/items", "Thêm dòng + trừ kho + tính tổng", 5, "DEV1"),
 ("PB-03", "API sửa/xóa dòng giỏ", "PUT/DELETE + hoàn kho theo delta", 4, "DEV1"),
 ("PB-03", "Giỏ hàng nối server", "Thêm/tăng/giảm/xóa gọi API, đồng bộ tồn", 6, "DEV3"),
 ("PB-03", "Test trừ kho", "Thêm/bớt/xóa, kiểm tồn khớp, chặn quá tồn", 4, "QC"),
 ("PB-04", "API tính giảm giá", "Gọi hàm tinh_tien_giam_gia", 3, "DEV1"),
 ("PB-04", "Chọn phiếu giảm giá ở POS", "Dropdown + tính lại thành tiền", 3, "DEV2"),
 ("PB-05", "API POST /{id}/thanh-toan", "Chốt tổng tiền, trạng thái, phương thức", 5, "DEV1"),
 ("PB-05", "Ghi lịch sử hóa đơn", "Ghi lich_su_hoa_don khi thanh toán", 3, "DEV1"),
 ("PB-05", "Kiểm tra tiền khách đưa", "Chặn khi khách đưa < phải trả, tính tiền thừa", 3, "DEV2"),
 ("PB-05", "Popup VietQR + kết hợp", "QR số tiền, tách tiền mặt / chuyển khoản", 5, "DEV2"),
 ("PB-05", "Test thanh toán", "3 hình thức, kiểm DB + lịch sử + tồn", 4, "QC"),
 ("PB-06", "Dựng mẫu phiếu in", "HTML phiếu tạm tính / hóa đơn", 3, "DEV2"),
 ("PB-06", "In qua iframe ẩn", "Tránh bị chặn popup", 2, "DEV2"),
 ("PB-07", "API POST /{id}/huy", "Trạng thái Hủy + hoàn kho + lịch sử", 4, "DEV1"),
 ("PB-07", "Nút Hủy trên POS", "Xác nhận trước khi hủy", 2, "DEV2"),
 ("PB-08", "API GET /san-pham-chi-tiet/by-ma/{ma}", "Tra biến thể theo mã", 2, "DEV1"),
 ("PB-08", "Trình quét QR trên trình duyệt", "Camera + zxing, thêm vào giỏ", 5, "DEV3"),
 ("PB-09", "Component popup thêm khách", "Chỉ form, không xem danh sách", 3, "DEV2"),
 ("PB-09", "Gắn khách vào hóa đơn", "Lưu idKhachHang khi thanh toán", 2, "DEV3"),
 ("PB-10", "API /san-pham-ql CRUD", "Thêm/sửa/tìm/xóa mềm/khôi phục/thùng rác", 6, "DEV1"),
 ("PB-10", "Màn quản lý sản phẩm", "Bảng + form chi tiết + chọn ảnh", 6, "DEV2"),
 ("PB-10", "Test CRUD sản phẩm", "Thêm/sửa/ẩn/khôi phục", 3, "QC"),
 ("PB-11", "API biến thể CRUD", "Tạo/sửa/xóa mềm biến thể, gộp về 1 controller", 5, "DEV1"),
 ("PB-11", "Thêm cột gia_nhap", "CSDL + entity + DTO", 3, "DEV1"),
 ("PB-11", "Tab Sản phẩm chi tiết", "Sửa giá bán/giá vốn/tồn/màu/size", 5, "DEV2"),
 ("PB-12", "API POST /{id}/nhap-kho", "Cộng tồn, bật lại trạng thái bán", 2, "DEV1"),
 ("PB-12", "Ô nhập kho trên form biến thể", "Nhập số lượng + nút Nhập", 2, "DEV2"),
 ("PB-13", "API gán danh mục", "PUT /san-pham-ql/{id}/danh-muc", 3, "DEV1"),
 ("PB-13", "Màn danh mục + gán sản phẩm", "Danh sách + thêm/bỏ sản phẩm", 5, "DEV2"),
 ("PB-14", "API CRUD 8 nhóm thuộc tính", "Chất liệu, màu, size, kiểu...", 4, "DEV1"),
 ("PB-14", "Màn thuộc tính + lọc theo loại", "Chọn loại -> chỉ hiện đúng loại đó", 5, "DEV2"),
 ("PB-15", "Thêm cột phi_ship + API đặt hàng", "Tổng tiền gồm ship, trạng thái Chờ giao", 4, "DEV1"),
 ("PB-15", "Tab Đặt hàng ở POS", "Nhập phí ship, địa chỉ, nút Giao hàng", 4, "DEV2"),
 ("PB-16", "API POST /{id}/da-giao", "Chờ giao -> Đã giao + lịch sử", 2, "DEV1"),
 ("PB-16", "Màn theo dõi đơn giao", "Bảng + tab trạng thái + nút Đã giao", 5, "DEV2"),
 ("PB-17", "API POST /{id}/tra-hang", "Trạng thái Trả hàng + hoàn kho", 3, "DEV1"),
 ("PB-17", "Nút Trả hàng ở Giao hàng & Lịch sử", "Xác nhận + làm mới danh sách", 3, "DEV3"),
 ("PB-18", "Bảng bao_hanh + entity/repo", "CSDL mới cho bảo hành", 5, "DEV1"),
 ("PB-18", "API POST /api/bao-hanh", "Tạo phiếu bảo hành", 3, "DEV1"),
 ("PB-18", "Form tiếp nhận bảo hành", "Chọn khách/sản phẩm/serial/mô tả lỗi", 4, "DEV2"),
 ("PB-19", "API cập nhật trạng thái BH", "PUT /{id}/trang-thai", 2, "DEV1"),
 ("PB-19", "Màn bảo hành master-detail", "Tab trạng thái + stepper + panel chi tiết", 6, "DEV2"),
 ("PB-19", "Test luồng bảo hành", "Đủ 6 trạng thái", 3, "QC"),
 ("PB-20", "Mẫu in phiếu bảo hành", "Kết xuất phiếu", 2, "DEV2"),
 ("PB-21", "API khách hàng CRUD", "Thêm/sửa/xóa/tìm", 4, "DEV1"),
 ("PB-21", "Màn khách hàng", "Bảng + modal thêm/sửa", 4, "DEV2"),
 ("PB-22", "API địa chỉ CRUD", "GET theo khách, thêm/sửa/xóa", 4, "DEV1"),
 ("PB-22", "Tab Địa chỉ trong Khách hàng", "Chọn khách -> CRUD địa chỉ", 4, "DEV2"),
 ("PB-23", "Query tổng quan", "Doanh thu, lợi nhuận, đơn theo trạng thái", 4, "DEV1"),
 ("PB-23", "Thẻ KPI trên dashboard", "6 thẻ số liệu", 3, "DEV2"),
 ("PB-24", "Query dòng tiền theo tháng", "Doanh thu / giá vốn / lợi nhuận", 5, "DEV1"),
 ("PB-24", "Biểu đồ dòng tiền + lọc năm", "Chart.js, chọn năm vẽ lại", 5, "DEV2"),
 ("PB-25", "Query ROI theo sản phẩm", "Xếp hạng theo ROI", 4, "DEV1"),
 ("PB-25", "Biểu đồ ROI", "Thanh ngang", 3, "DEV2"),
 ("PB-26", "Query giữ chân khách", "Khách >1 đơn vs 1 đơn", 4, "DEV1"),
 ("PB-26", "Biểu đồ tròn giữ chân", "Doughnut + tỷ lệ", 2, "DEV2"),
 ("PB-27", "Query bán chạy/chậm/xu hướng", "Số bán + tăng trưởng so năm trước", 5, "DEV1"),
 ("PB-27", "3 bảng xếp hạng", "Bán chạy / bán chậm / xu hướng", 4, "DEV2"),
 ("PB-28", "Xuất Excel từ dashboard", "Kết xuất KPI + bán chạy", 3, "DEV3"),
 ("PB-29", "API nhân viên CRUD", "Thêm/sửa/xóa/tìm nhân viên", 4, "DEV1"),
 ("PB-29", "Màn nhân viên", "Bảng + modal", 4, "DEV2"),
 ("PB-30", "Cột vai_tro.quyen + API phân quyền", "GET/PUT quyền theo vai trò", 4, "DEV1"),
 ("PB-30", "Màn phân quyền", "Vai trò x màn hình, tick chọn, lưu", 5, "DEV2"),
 ("PB-30", "Ẩn menu & chặn route theo quyền", "Sidebar + router guard", 4, "DEV3"),
 ("PB-30", "Test phân quyền", "Đăng nhập từng vai trò, kiểm menu", 3, "QC"),
 ("PB-30", "Đổi vai trò thành template", "vai_tro.quyen hạ xuống thành bộ quyền mặc định, không còn là quyền hiệu lực", 2, "DEV1"),
 ("PB-30", "Tab Vai trò trong màn Nhân viên", "Sửa bộ quyền mặc định, ghi rõ không ảnh hưởng NV đang có", 3, "DEV2"),
 ("PB-31", "API sản phẩm cho trang chủ", "Danh sách biến thể còn hàng + ảnh", 3, "DEV1"),
 ("PB-31", "Trang chủ cửa hàng", "Hero, danh mục, lưới sản phẩm, sắp xếp", 6, "DEV2"),
 ("PB-32", "API phiếu giảm giá CRUD", "Thêm/sửa/xóa/tìm + view phiếu hiệu lực", 4, "DEV1"),
 ("PB-32", "Màn khuyến mãi", "Bảng + form điều kiện/thời hạn", 4, "DEV2"),
 ("PB-33", "API /san-pham-chi-tiet/store", "Catalogue giữ cả SP hết hàng cho trang chủ", 2, "DEV1"),
 ("PB-33", "Composable useCart", "Giỏ ở localStorage, cộng/trừ/xóa, chặn vượt tồn", 3, "DEV2"),
 ("PB-33", "Nút Thêm vào giỏ ở trang chủ", "Gắn giỏ thật, badge số lượng trên header", 2, "DEV2"),
 ("PB-34", "API POST /gio-hang/checkout", "Tạo đơn Chờ giao, COD, trừ kho nguyên tử từng dòng", 5, "DEV1"),
 ("PB-34", "Màn giỏ hàng + form nhận hàng", "Bảng dòng hàng, sửa SL, form tên/SĐT/địa chỉ", 5, "DEV2"),
 ("PB-34", "Test đặt hàng online", "Đủ hàng / thiếu hàng / thiếu thông tin", 3, "QC"),
 ("PB-35", "API GET /gio-hang/don-hang", "Tra đơn theo SĐT, bỏ hóa đơn nháp tại quầy", 2, "DEV1"),
 ("PB-35", "API PUT /gio-hang/nhan-hang/{id}", "Dùng lại daGiao: Chờ giao -> Đã giao + lịch sử", 1, "DEV1"),
 ("PB-35", "Tab Đơn của tôi", "Tra theo SĐT, danh sách + nút Đã nhận hàng", 4, "DEV2"),
 ("PB-36", "Bảng dat_truoc + entity/repo", "CSDL mới cho đặt trước", 4, "DEV1"),
 ("PB-36", "API POST /api/dat-truoc", "Chỉ cho đăng ký khi tồn = 0, sinh mã phiếu", 3, "DEV1"),
 ("PB-36", "Nút + modal Đặt trước ở trang chủ", "SP hết hàng: ảnh xám, form đăng ký, số người chờ", 4, "DEV2"),
 ("PB-37", "API trạng thái + chuyển đơn", "PUT trạng thái, POST chuyển phiếu thành hóa đơn giữ hàng", 4, "DEV1"),
 ("PB-37", "Màn quản lý đặt trước", "4 tab trạng thái + panel chi tiết + chuyển đơn", 5, "DEV2"),
 ("PB-37", "Test luồng đặt trước", "Đặt -> có hàng -> chuyển đơn -> trừ kho", 3, "QC"),
 ("PB-38", "Bảng nhan_vien_quyen + entity/repo", "Khóa kép (id_nhan_vien, man_hinh); rows là quyền hiệu lực", 4, "DEV1"),
 ("PB-38", "Service phân quyền + ngoại lệ ADMIN", "Lưới, lưu quyền, áp template; ADMIN toàn quyền tính động", 5, "DEV1"),
 ("PB-38", "Nối vào tạo NV và đổi vai trò", "Tạo NV thì chép template; đổi vai trò thì chép lại template mới", 3, "DEV1"),
 ("PB-38", "Login trả quyền hiệu lực", "Lấy quyền của chính nhân viên thay cho template vai trò", 2, "DEV1"),
 ("PB-38", "Lưới phân quyền (tab Nhân viên)", "Dòng NV x cột màn hình, đóng băng cột trái, lưu hàng loạt", 6, "DEV2"),
 ("PB-38", "Unit test logic phân quyền", "11 test: ngoại lệ ADMIN, parse template, khử trùng, chống N+1", 4, "QC"),
 ("PB-39", "Thêm idSanPham vào API cửa hàng", "Card ngoài trang chủ mới biết mở trang chi tiết nào", 1, "DEV1"),
 ("PB-39", "Trang chi tiết sản phẩm", "Ảnh, thông tin, chọn màu/size, mua hoặc đặt trước theo tồn của biến thể", 6, "DEV2"),
 ("PB-39", "Tách modal đặt trước dùng chung", "Dùng lại cho cả trang chủ và trang chi tiết", 2, "DEV2"),
 ("PB-40", "Tách panel danh mục / thuộc tính", "Bóc thân màn thành component để nhúng làm tab", 3, "DEV2"),
 ("PB-40", "Gộp tab vào màn sản phẩm", "4 tab, panel lazy chỉ gọi API khi mở", 2, "DEV2"),
]

# ---- renumber: gan PB-01.. va T-01.. theo thu tu Sprint -------------------
_rq_sprint = {r["id"]: r["sprint"] for r in RQS}

PBS = []
_oldkey_to_new = {}
for _i, (_oldkey, _rq, _role, _story, _mota, _prio, _bv, _up4) in enumerate(_PB, 1):
    _new_id = "PB-%02d" % _i
    _oldkey_to_new[_oldkey] = _new_id
    PBS.append({
        "id": _new_id, "old": _oldkey, "rq": _rq, "role": _role, "role_name": ROLE_NAME.get(_role, _role),
        "user_story": _story, "mo_ta": _mota, "priority": _prio, "bv": _bv, "sprint": _rq_sprint[_rq],
        "tuong_tac": _up4[0], "quy_tac": _up4[1], "thuc_the": _up4[2], "thao_tac": _up4[3],
    })

# gom task theo PB moi, giu thu tu khai bao trong _T
_task_by_new = {}
for _oldkey, _task, _mota, _est, _who in _T:
    _task_by_new.setdefault(_oldkey_to_new[_oldkey], []).append((_task, _mota, _est, _who))

TASKS = []
_n = 0
for _p in PBS:
    for _task, _mota, _est, _who in _task_by_new.get(_p["id"], []):
        _n += 1
        TASKS.append({
            "id": "T-%02d" % _n, "story": _p["id"], "backlog": _p["rq"], "sprint": _p["sprint"],
            "task": _task, "mo_ta": _mota, "est": _est, "who": _who,
        })

# ---- ED: Environment Degree (0/2 each, 18 factors, max 36) -----------------
ED = [
 ("Khía cạnh tổ chức", "Đã có những phòng ban khác nhau cùng làm việc thành công trong một dự án Scrum?", 0,
  "Nhóm môn học, chưa có nhiều phòng ban cùng làm Scrum"),
 ("Khía cạnh tổ chức", "Có sự chống đối mạnh mẽ với Scrum trong tổ chức?", 2,
  "Không có chống đối, cả nhóm hợp tác tích cực"),
 ("Khía cạnh tổ chức", "Có tồn tại sự hỗ trợ lớn về Scrum giữa những phòng ban khác nhau trong công ty?", 0,
  "Chưa có cơ chế hỗ trợ liên phòng ban"),
 ("Khía cạnh hạ tầng phát triển", "Kiểm thử tự động đã được áp dụng và trở thành kỹ thuật phổ biến?", 0,
  "Dự án chưa có bộ test tự động"),
 ("Khía cạnh hạ tầng phát triển", "Kiểm thử tích hợp liên tục (CI) đã phổ biến?", 0,
  "Chưa dựng pipeline CI"),
 ("Khía cạnh hạ tầng phát triển", "Môi trường build hàng ngày đã phổ biến?", 2,
  "Build mvn/npm chạy được hằng ngày, đã kiểm chứng"),
 ("Khía cạnh nhóm", "Scrum là hoàn toàn mới đối với nhóm?", 0,
  "Scrum còn khá mới với nhóm"),
 ("Khía cạnh nhóm", "Các thành viên trong nhóm đã từng làm việc thành công với nhau?", 2,
  "Nhóm đã từng hợp tác ở dự án trước"),
 ("Khía cạnh nhóm", "Các thành viên trong nhóm hiểu và tôn trọng lẫn nhau?", 2,
  "Tinh thần hợp tác tích cực"),
 ("Khía cạnh công nghệ", "Nhóm phát triển có nhiều kinh nghiệm với ngôn ngữ lập trình?", 0,
  "Trình độ IT của nhóm còn hạn chế"),
 ("Khía cạnh công nghệ", "Thành viên có nhiều kinh nghiệm với công nghệ được dùng (Spring Boot/Vue/SQL Server)?", 0,
  "Công nghệ mới với phần lớn thành viên"),
 ("Khía cạnh công nghệ", "Môi trường sản xuất với Scrum đã sẵn sàng chưa?", 0,
  "Chưa có môi trường production/CSDL ổn định"),
 ("Khía cạnh qui trình", "Scrum có phải là khung làm việc được chấp thuận trong công ty?", 2,
  "Scrum là khung bắt buộc của dự án môn học"),
 ("Khía cạnh qui trình", "Trong công ty có sự hỗ trợ tốt cho Scrum?", 2,
  "Giảng viên/PO hướng dẫn sát"),
 ("Khía cạnh qui trình", "Trong công ty có sự phản đối đáng kể nào đối với Scrum?", 2,
  "Không có phản đối"),
 ("Khía cạnh nghiệp vụ", "Có một Product Owner hoàn toàn sẵn sàng và gắn bó lâu dài với nhóm?", 2,
  "PO là thành viên nhóm, gắn bó suốt dự án"),
 ("Khía cạnh nghiệp vụ", "Product Owner đã quen thuộc với Scrum nhưng vẫn thiếu kinh nghiệm thực tế?", 2,
  "PO nắm lý thuyết, còn thiếu thực chiến"),
 ("Khía cạnh nghiệp vụ", "Product Owner đã từng thành công với Scrum trước đây chưa?", 0,
  "Chưa từng dẫn dắt dự án Scrum thành công"),
]
ED_TOTAL = sum(x[2] for x in ED)          # 18
ED_MAX = 36
C_DEFAULT = 1.0                            # hệ số nhân (tài liệu không quy định -> mặc định 1)

SPRINTS = [
 (1, "Nền tảng: đăng nhập, nhân viên và phân quyền"),
 (2, "Quản lý sản phẩm: sản phẩm, biến thể, nhập kho, danh mục, thuộc tính"),
 (3, "Bán hàng tại quầy: giỏ hàng trừ kho, thanh toán, in hóa đơn, quét QR"),
 (4, "Đơn đặt hàng, giao hàng, trả hàng và quản lý khách hàng"),
 (5, "Cửa hàng online: trang chủ, chi tiết, giỏ hàng, đặt hàng COD, đặt trước"),
 (6, "Dashboard doanh thu, bảo hành và khuyến mãi"),
]


def fib(x):
    for f in (1, 2, 3, 5, 8, 13, 21):
        if x <= f:
            return f
    return 21
