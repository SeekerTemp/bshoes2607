# -*- coding: utf-8 -*-
"""
gen_ws5.py — mở rộng BShoes_TestCase.xlsx từ 3 sprint lên đủ 6 sprint.

Nguồn chuẩn (bắt buộc bám sát):
    doc-school-request/2607lts_nhóm 1_WebBshoes_temp.xlsx
      - ws1.3.1. Product Backlog  → RQ-1 … RQ-40
      - ws1.3.2. Release Backlog  → PB-1 … PB-13
      - ws1.3.3 Sprint Backlog    → cột Sprint# ánh xạ PB → Sprint
      - ws4.1 sprints-fix         → tên & mốc thời gian từng sprint

Ánh xạ Sprint → Product Backlog → User Story (lấy nguyên từ file nguồn):
    Sprint 1 - PLANNING  (01-07/07/2026)  pb-1..PB-4   RQ-1  → RQ-10
    Sprint 2 - PROTOTYPE (08-14/07/2026)  PB-5, PB-6   RQ-11 → RQ-21
    Sprint 3 - TEST      (15-21/07/2026)  PB-7, PB-8   RQ-22 → RQ-24
    Sprint 4 - POLISH    (22-28/07/2026)  PB-9, PB-10  RQ-25 → RQ-33
    Sprint 5 - LAUNCH    (29/07-04/08/26) PB-11..PB-13 RQ-34 → RQ-40
    Sprint 6 - LAUNCH    (05-11/08/2026)  —            hồi quy toàn hệ thống

Script CHỈ thêm 6 sheet module mới (Sprint 4/5/6) và dựng lại 3 sheet tổng hợp
(Cover / Test Case List / Test Report). 10 sheet module của Sprint 1-3 giữ
nguyên không đụng tới. Chạy lại nhiều lần cho kết quả giống hệt (idempotent).

    python doc-school/_generator/gen_ws5.py
"""

import os
import sys

from openpyxl import load_workbook
from openpyxl.styles import Alignment, Border, Font, PatternFill, Side

# --------------------------------------------------------------------------
# Đường dẫn
# --------------------------------------------------------------------------
HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.dirname(os.path.dirname(HERE))            # …/bshoes2607
WORKSPACE = os.path.dirname(REPO)                        # …/temp_AGILE

BASE = os.path.join(REPO, "doc-school", "ws5", "BShoes_TestCase.xlsx")
TARGETS = [
    BASE,
    os.path.join(WORKSPACE, "doc-school-request", "BShoes_TestCase.xlsx"),
]

# --------------------------------------------------------------------------
# Bảng màu / style — sao đúng theo 10 sheet module đã có
# --------------------------------------------------------------------------
NAVY = "1F3864"
BLUE = "2E75B6"
LIGHT = "DDEBF7"
BAND = "BDD7EE"
CREAM = "FFF2CC"
OK_BG, OK_FG = "C6EFCE", "006100"
NG_BG, NG_FG = "FFC7CE", "9C0006"

_thin = Side(style="thin", color="B4C6E7")
BORDER = Border(left=_thin, right=_thin, top=_thin, bottom=_thin)

F_TITLE = Font(bold=True, size=14, color="FFFFFF")
F_HDR = Font(bold=True, size=10, color="FFFFFF")
F_HDR11 = Font(bold=True, color="FFFFFF")
F_BAND = Font(bold=True, color=NAVY)
F_BOLD = Font(bold=True)
F_BODY = Font(size=10)
F_11 = Font(size=11)
F_OK = Font(bold=True, color=OK_FG)
F_NG = Font(bold=True, color=NG_FG)
F_MUTED = Font(color="808080")

FILL_TITLE = PatternFill("solid", start_color=NAVY)
FILL_SECT = PatternFill("solid", start_color=BLUE)
FILL_LIGHT = PatternFill("solid", start_color=LIGHT)
FILL_BAND = PatternFill("solid", start_color=BAND)
FILL_CREAM = PatternFill("solid", start_color=CREAM)
FILL_OK = PatternFill("solid", start_color=OK_BG)
FILL_NG = PatternFill("solid", start_color=NG_BG)

A_CTR = Alignment(horizontal="center", vertical="center")
A_CTR_WRAP = Alignment(horizontal="center", vertical="center", wrap_text=True)
A_MID_WRAP = Alignment(vertical="center", wrap_text=True)
A_TOP_L = Alignment(horizontal="left", vertical="top", wrap_text=True)
A_TOP_C = Alignment(horizontal="center", vertical="top", wrap_text=True)
A_LEFT_MID = Alignment(horizontal="left", vertical="center", wrap_text=True)

MODULE_HEADERS = [
    "ID (Mã)", "Test Title", "Test Case Description (Mô tả)",
    "Test Case Procedure (Thủ tục thực hiện)", "Test Data (Dữ liệu vào)",
    "Expected Output (Kết quả mong muốn)", "Actual Result (Kết quả thực tế)",
    "Video minh chứng", "Inter-test case Dependence",
    "Result 1", "Test date 1", "Result 2", "Test date 2", "Video",
    "Người thực hiện",
]
MODULE_WIDTHS = {"A": 11, "B": 26, "C": 34, "E": 26, "F": 34, "G": 30,
                 "H": 12, "I": 20, "J": 10, "K": 13, "L": 10, "M": 13,
                 "N": 10, "O": 20}

SPRINT4 = "Sprint 4 - POLISH (22-28/07/2026)"
SPRINT5 = "Sprint 5 - LAUNCH (29/07-04/08/2026)"
SPRINT6 = "Sprint 6 - LAUNCH (05-11/08/2026)"

ISSUE_DATE = "08/08/2026"

# --------------------------------------------------------------------------
# Nội dung 6 module mới
#   mỗi test case = (id, title, description, procedure, data, expected,
#                    actual, result, date)
#   dòng nhóm = ("#", "TÊN NHÓM")
# --------------------------------------------------------------------------

TK = dict(
    sheet="TK. Báo cáo doanh thu",
    title="MODULE: BÁO CÁO DOANH THU  (TK)",
    code="TK",
    sprint=SPRINT4,
    pb="PB-9",
    rq="RQ-25, RQ-26, RQ-27, RQ-28, RQ-29, RQ-30",
    desc="Kế toán xem KPI tổng quan, biểu đồ dòng tiền theo tháng, ROI theo sản "
         "phẩm, tỷ lệ giữ chân khách hàng, sản phẩm bán chạy/bán chậm/xu hướng "
         "và xuất báo cáo ra Excel.",
    tester="Nguyễn Đình Dũng",
    rows=[
        ("#", "KPI TỔNG QUAN (RQ-25)"),
        ("TK_01", "Hiển thị KPI tổng quan",
         "Mở màn hình Thống kê, kiểm tra 4 thẻ KPI doanh thu, lợi nhuận, số đơn, số khách.",
         "B1: Đăng nhập vai trò Quản trị\nB2: Vào menu Thống kê\nB3: Quan sát dãy thẻ KPI",
         "N/A",
         "Gọi GET /api/thong-ke/tong-quan trả 200; 4 thẻ hiện số liệu đúng định dạng tiền VNĐ.",
         "4 thẻ KPI hiển thị đúng số liệu, API trả 200.",
         "Pass", "2026-07-22"),
        ("TK_02", "KPI doanh thu hôm nay",
         "Kiểm tra số liệu doanh thu trong ngày hiện tại.",
         "B1: Vào Thống kê\nB2: Xem khối doanh thu hôm nay",
         "Ngày hệ thống: 22/07/2026",
         "GET /api/thong-ke/hom-nay trả đúng doanh thu các hóa đơn đã thanh toán trong ngày.",
         "Doanh thu hôm nay khớp với tổng hóa đơn đã thanh toán trong ngày.",
         "Pass", "2026-07-22"),
        ("TK_03", "Lọc KPI theo khoảng ngày",
         "Chọn khoảng ngày và kiểm tra số liệu được lọc lại.",
         "B1: Chọn Từ ngày và Đến ngày\nB2: Bấm Xem\nB3: Đối chiếu với dữ liệu hóa đơn",
         "tuNgay: 2026-07-01\ndenNgay: 2026-07-21",
         "GET /api/thong-ke/theo-ngay trả danh sách doanh thu trong khoảng; các thẻ KPI cập nhật lại.",
         "Số liệu lọc đúng theo khoảng ngày đã chọn.",
         "Pass", "2026-07-22"),
        ("TK_04", "Khoảng ngày không hợp lệ",
         "Nhập Đến ngày nhỏ hơn Từ ngày.",
         "B1: Chọn Từ ngày 20/07/2026\nB2: Chọn Đến ngày 01/07/2026\nB3: Bấm Xem",
         "tuNgay: 2026-07-20\ndenNgay: 2026-07-01",
         'Cảnh báo "Đến ngày phải lớn hơn hoặc bằng Từ ngày", không gọi API.',
         "Vẫn gọi API và trả về bảng rỗng, không có cảnh báo cho người dùng.",
         "Fail", "2026-07-22"),
        ("#", "BIỂU ĐỒ DÒNG TIỀN THEO THÁNG (RQ-26)"),
        ("TK_05", "Biểu đồ dòng tiền 12 tháng",
         "Kiểm tra biểu đồ đường doanh thu - giá vốn - lợi nhuận theo 12 tháng.",
         "B1: Vào Thống kê\nB2: Xem khối Dòng tiền theo tháng",
         "nam: 2026",
         "GET /api/thong-ke/dong-tien?nam=2026 trả 12 điểm; biểu đồ vẽ 3 đường có chú giải và tooltip định dạng VNĐ.",
         "Biểu đồ vẽ đủ 3 đường, tooltip hiển thị đúng định dạng tiền.",
         "Pass", "2026-07-23"),
        ("TK_06", "Đổi năm thống kê",
         "Đổi năm trong ô chọn và kiểm tra biểu đồ vẽ lại.",
         "B1: Mở ô chọn Năm\nB2: Chọn năm khác\nB3: Quan sát biểu đồ",
         "nam: 2025",
         "Danh sách năm lấy từ GET /api/thong-ke/nam; biểu đồ và bảng vẽ lại theo năm đã chọn.",
         "Danh sách năm đúng dữ liệu thật, biểu đồ vẽ lại theo năm chọn.",
         "Pass", "2026-07-23"),
        ("TK_07", "Năm chưa có dữ liệu",
         "Chọn năm không phát sinh hóa đơn nào.",
         "B1: Chọn năm không có dữ liệu\nB2: Quan sát biểu đồ",
         "nam: 2024",
         "Biểu đồ hiển thị trống với trục đầy đủ, không lỗi JavaScript trên console.",
         "Biểu đồ trống, console sạch, không lỗi.",
         "Pass", "2026-07-23"),
        ("#", "ROI THEO SẢN PHẨM (RQ-27)"),
        ("TK_08", "Biểu đồ ROI theo sản phẩm",
         "Kiểm tra biểu đồ cột ngang ROI = lợi nhuận / giá vốn.",
         "B1: Vào Thống kê\nB2: Xem khối ROI theo sản phẩm\nB3: Rê chuột lên một cột",
         "N/A",
         'GET /api/thong-ke/roi trả 200; tooltip hiển thị "ROI xx%"; sắp xếp giảm dần.',
         'Biểu đồ ROI hiển thị đúng, tooltip dạng "ROI xx%".',
         "Pass", "2026-07-24"),
        ("TK_09", "ROI khi giá vốn bằng 0",
         "Sản phẩm chưa có giá nhập, kiểm tra không chia cho 0.",
         "B1: Tạo sản phẩm có giá nhập = 0\nB2: Bán 1 đơn\nB3: Mở lại biểu đồ ROI",
         "gia_nhap: 0\ngia_ban: 500000",
         "Sản phẩm được bỏ qua hoặc hiển thị ROI = 0, không trả Infinity/NaN.",
         "API trả giá trị Infinity, biểu đồ hiển thị cột vô hạn làm vỡ tỷ lệ trục.",
         "Fail", "2026-07-24"),
        ("#", "TỶ LỆ GIỮ CHÂN KHÁCH HÀNG (RQ-28)"),
        ("TK_10", "Biểu đồ khách quay lại / khách mới",
         "Kiểm tra biểu đồ tròn tỷ lệ giữ chân khách hàng.",
         "B1: Vào Thống kê\nB2: Xem khối Tỷ lệ giữ chân khách hàng",
         "N/A",
         "GET /api/thong-ke/retention trả số khách quay lại và khách mới; biểu đồ tròn hiển thị đúng 2 phần.",
         "Biểu đồ tròn đúng số liệu, tooltip hiện số khách từng nhóm.",
         "Pass", "2026-07-24"),
        ("#", "SẢN PHẨM BÁN CHẠY - BÁN CHẬM - XU HƯỚNG (RQ-29)"),
        ("TK_11", "Bảng bán chạy và bán chậm",
         "Kiểm tra 2 bảng xếp hạng sản phẩm theo số lượng bán.",
         "B1: Vào Thống kê\nB2: Xem 2 bảng Bán chạy / Bán chậm",
         "N/A",
         "GET /api/thong-ke/san-pham trả danh sách; bảng bán chạy sắp giảm dần, bán chậm sắp tăng dần.",
         "Hai bảng xếp hạng đúng thứ tự và đúng số lượng đã bán.",
         "Pass", "2026-07-25"),
        ("TK_12", "Bảng xu hướng tăng trưởng",
         "Kiểm tra bảng so sánh tăng trưởng với năm liền trước.",
         "B1: Vào Thống kê\nB2: Xem bảng Xu hướng",
         "nam: 2026",
         "GET /api/thong-ke/trending?nam=2026 trả % tăng trưởng; tăng hiển thị màu xanh, giảm hiển thị màu đỏ.",
         "Bảng xu hướng hiển thị đúng % và đúng màu theo chiều tăng/giảm.",
         "Pass", "2026-07-25"),
        ("#", "XUẤT BÁO CÁO EXCEL (RQ-30)"),
        ("TK_13", "Xuất báo cáo ra Excel",
         'Bấm nút "Xuất Excel" và kiểm tra file tải về.',
         "B1: Vào Thống kê\nB2: Chọn năm 2026\nB3: Bấm Xuất Excel\nB4: Mở file tải về",
         "nam: 2026",
         "Tải về file .xlsx gồm KPI, dòng tiền theo tháng, ROI, bán chạy/bán chậm; số liệu khớp màn hình.",
         "File Excel tải về đủ các bảng, số liệu khớp với màn hình.",
         "Pass", "2026-07-27"),
        ("TK_14", "Xuất Excel khi không có dữ liệu",
         "Xuất báo cáo cho năm chưa phát sinh giao dịch.",
         "B1: Chọn năm không có dữ liệu\nB2: Bấm Xuất Excel",
         "nam: 2024",
         "File vẫn tải về với đầy đủ tiêu đề cột, các bảng rỗng, không báo lỗi.",
         "File tải về có tiêu đề cột, bảng rỗng, không lỗi.",
         "Pass", "2026-07-27"),
    ],
)

BH = dict(
    sheet="BH. Dịch vụ bảo hành",
    title="MODULE: DỊCH VỤ BẢO HÀNH  (BH)",
    code="BH",
    sprint=SPRINT4,
    pb="PB-10",
    rq="RQ-31, RQ-32, RQ-33",
    desc="Nhân viên bảo hành tiếp nhận đơn bảo hành gắn với khách hàng và sản "
         "phẩm, cập nhật trạng thái theo tiến trình xử lý và in phiếu bảo hành "
         "cho khách.",
    tester="Hoàng Lê Bảo Ngọc",
    rows=[
        ("#", "TIẾP NHẬN ĐƠN BẢO HÀNH (RQ-31)"),
        ("BH_01", "Hiển thị danh sách phiếu bảo hành",
         "Mở màn hình Bảo hành, kiểm tra lưới dữ liệu và các thẻ đếm theo trạng thái.",
         "B1: Đăng nhập\nB2: Vào menu Bảo hành\nB3: Quan sát lưới và các tab",
         "N/A",
         "GET /api/bao-hanh trả 200; lưới hiện mã phiếu, khách, sản phẩm, ngày tiếp nhận, trạng thái.",
         "Lưới hiển thị đủ cột, số đếm trên tab khớp với dữ liệu.",
         "Pass", "2026-07-25"),
        ("BH_02", "Tiếp nhận phiếu bảo hành mới",
         "Tạo phiếu bảo hành gắn khách hàng, sản phẩm và nhân viên tiếp nhận.",
         "B1: Bấm Tiếp nhận\nB2: Chọn khách hàng, sản phẩm, nhân viên\nB3: Nhập tình trạng\nB4: Lưu\nB5: Tải lại trang",
         "khach_hang: Trần Thu Hà\nsan_pham: Nike Air Zoom - Đen/42\ntinh_trang: Bong keo đế",
         "POST /api/bao-hanh trả 200, phiếu mới xuất hiện ở trạng thái Chưa xử lý và còn sau khi tải lại.",
         "Phiếu được lưu, còn nguyên sau khi tải lại trang.",
         "Pass", "2026-07-25"),
        ("BH_03", "Bỏ trống thông tin bắt buộc",
         "Lưu phiếu khi chưa chọn khách hàng hoặc sản phẩm.",
         "B1: Bấm Tiếp nhận\nB2: Để trống khách hàng và sản phẩm\nB3: Bấm Lưu",
         "khach_hang: (trống)\nsan_pham: (trống)",
         'Cảnh báo "Vui lòng chọn khách hàng và sản phẩm", không gọi API.',
         "Không có cảnh báo; API bị gọi và trả lỗi 500 hiển thị thô cho người dùng.",
         "Fail", "2026-07-25"),
        ("BH_04", "Tìm kiếm phiếu bảo hành",
         "Tìm theo mã phiếu, tên khách hoặc số điện thoại.",
         "B1: Nhập từ khóa vào ô tìm kiếm\nB2: Quan sát lưới",
         "tu_khoa: 0912",
         "Lưới chỉ còn các phiếu khớp từ khóa; xóa từ khóa thì hiện lại toàn bộ.",
         "Tìm kiếm lọc đúng theo mã, tên khách và số điện thoại.",
         "Pass", "2026-07-26"),
        ("BH_05", "Lọc theo tab trạng thái",
         "Chuyển giữa các tab trạng thái xử lý.",
         "B1: Bấm lần lượt các tab trạng thái\nB2: Đối chiếu số đếm với số dòng",
         "N/A",
         "Mỗi tab chỉ hiện phiếu đúng trạng thái, số trên tab bằng số dòng trong lưới.",
         "Lọc đúng, số đếm khớp số dòng.",
         "Pass", "2026-07-26"),
        ("#", "CẬP NHẬT TRẠNG THÁI BẢO HÀNH (RQ-32)"),
        ("BH_06", "Chuyển trạng thái xử lý",
         "Chuyển phiếu từ Chưa xử lý sang Đã chẩn đoán.",
         "B1: Chọn 1 phiếu\nB2: Đổi trạng thái sang Đã chẩn đoán\nB3: Lưu\nB4: Tải lại trang",
         "trang_thai: Đã chẩn đoán",
         "PUT /api/bao-hanh/{id}/trang-thai trả 200; thanh tiến trình nhảy sang bước Chẩn đoán và giữ sau khi tải lại.",
         "Trạng thái đổi đúng, thanh tiến trình nhảy bước, dữ liệu được lưu.",
         "Pass", "2026-07-26"),
        ("BH_07", "Hoàn tất và trả máy cho khách",
         "Chuyển phiếu sang trạng thái Đã trả.",
         "B1: Chọn phiếu ở trạng thái Đã xử lý\nB2: Đổi sang Đã trả\nB3: Lưu",
         "trang_thai: Đã trả",
         "Phiếu chuyển sang tab hoàn tất, thanh tiến trình đầy đủ 4 bước.",
         "Phiếu vào tab hoàn tất, tiến trình hiển thị đủ 4 bước.",
         "Pass", "2026-07-27"),
        ("BH_08", "Xóa mềm phiếu bảo hành",
         "Hủy một phiếu bảo hành đã tiếp nhận nhầm.",
         "B1: Chọn phiếu\nB2: Bấm Xóa\nB3: Xác nhận\nB4: Tải lại trang",
         "N/A",
         "DELETE /api/bao-hanh/{id} trả 200; phiếu biến khỏi lưới và không quay lại sau khi tải lại.",
         "Phiếu bị xóa mềm, không còn hiển thị sau khi tải lại.",
         "Pass", "2026-07-27"),
        ("#", "IN PHIẾU BẢO HÀNH (RQ-33)"),
        ("BH_09", "In phiếu bảo hành",
         "In phiếu bảo hành để giao cho khách hàng.",
         "B1: Chọn 1 phiếu\nB2: Bấm In phiếu bảo hành\nB3: Kiểm tra bản xem trước",
         "N/A",
         "Mở cửa sổ xem trước bản in với mã phiếu, khách hàng, sản phẩm, tình trạng, ngày tiếp nhận và ngày hẹn trả.",
         "Bản xem trước hiển thị đầy đủ và đúng thông tin phiếu.",
         "Pass", "2026-07-28"),
        ("BH_10", "In khi chưa chọn phiếu",
         "Bấm In khi chưa chọn dòng nào trong lưới.",
         "B1: Bỏ chọn tất cả\nB2: Bấm In phiếu bảo hành",
         "N/A",
         'Cảnh báo "Vui lòng chọn phiếu bảo hành cần in", không mở cửa sổ in.',
         "Hiện cảnh báo đúng, không mở cửa sổ in.",
         "Pass", "2026-07-28"),
    ],
)

TKH = dict(
    sheet="TKH. Trang khách hàng",
    title="MODULE: TRANG KHÁCH HÀNG - QUẢNG BÁ SẢN PHẨM  (TKH)",
    code="TKH",
    sprint=SPRINT5,
    pb="PB-11",
    rq="RQ-34, RQ-35, RQ-36",
    desc="Khách hàng xem trang chủ cửa hàng, lọc và sắp xếp sản phẩm theo danh "
         "mục, xem chi tiết sản phẩm để chọn màu - kích cỡ và thêm sản phẩm vào "
         "giỏ hàng.",
    tester="Đỗ Anh Vũ",
    rows=[
        ("#", "TRANG CHỦ CỬA HÀNG (RQ-35)"),
        ("TKH_01", "Hiển thị trang chủ cửa hàng",
         "Mở trang chủ phía khách, kiểm tra lưới sản phẩm và thanh danh mục.",
         "B1: Mở /home\nB2: Quan sát lưới sản phẩm và thanh danh mục",
         "N/A",
         "Lưới sản phẩm nạp từ API biến thể phía cửa hàng; mỗi thẻ có ảnh, tên, giá, thương hiệu.",
         "Trang chủ hiển thị đầy đủ, dữ liệu lấy từ API thật.",
         "Pass", "2026-07-29"),
        ("TKH_02", "Danh mục lấy từ dữ liệu thật",
         "Kiểm tra thanh danh mục không phải danh sách cứng.",
         "B1: Mở /home\nB2: Đối chiếu danh mục với màn hình Danh mục bên quản trị",
         "N/A",
         "Danh mục nạp từ loai_san_pham, trùng khớp với danh sách bên quản trị, có mục Tất cả.",
         "Danh mục khớp hoàn toàn với dữ liệu quản trị.",
         "Pass", "2026-07-29"),
        ("TKH_03", "Lọc sản phẩm theo danh mục",
         "Bấm một danh mục và kiểm tra lưới được lọc lại.",
         "B1: Bấm danh mục Giày thể thao\nB2: Đếm sản phẩm\nB3: Bấm lại Tất cả",
         "danh_muc: Giày thể thao",
         "Lưới chỉ còn sản phẩm thuộc danh mục đã chọn; bấm Tất cả hiện lại toàn bộ.",
         "Lọc đúng theo danh mục, Tất cả hiện lại đủ sản phẩm.",
         "Pass", "2026-07-29"),
        ("TKH_04", "Sắp xếp trên nền bộ lọc",
         "Sắp xếp theo giá và theo bán chạy khi đang lọc danh mục.",
         "B1: Chọn 1 danh mục\nB2: Đổi sắp xếp sang Giá tăng dần\nB3: Đổi sang Bán chạy",
         "sap_xep: Giá tăng dần",
         "Thứ tự thay đổi đúng nhưng vẫn giữ bộ lọc danh mục đang chọn.",
         "Sắp xếp hoạt động đúng, bộ lọc danh mục không bị mất.",
         "Pass", "2026-07-30"),
        ("TKH_05", "Tìm kiếm sản phẩm theo từ khóa",
         "Nhập từ khóa vào ô tìm kiếm trên trang chủ.",
         "B1: Nhập từ khóa\nB2: Bấm Tìm\nB3: Quan sát lưới",
         "tu_khoa: Nike",
         "Chỉ hiện sản phẩm có tên chứa từ khóa; không có kết quả thì hiện thông báo thân thiện.",
         "Tìm kiếm lọc đúng, trường hợp rỗng có thông báo.",
         "Pass", "2026-07-30"),
        ("TKH_06", "Backend không sẵn sàng",
         "Tắt backend rồi mở trang chủ.",
         "B1: Dừng backend\nB2: Mở /home\nB3: Quan sát trang",
         "N/A",
         "Hiện banner dữ liệu demo, lưới vẫn có nội dung minh họa, không trắng trang và không lỗi chặn.",
         "Banner dữ liệu demo hiển thị, trang không bị trắng.",
         "Pass", "2026-07-30"),
        ("#", "CHI TIẾT SẢN PHẨM (RQ-34)"),
        ("TKH_07", "Mở chi tiết sản phẩm",
         "Bấm vào một sản phẩm để mở trang chi tiết.",
         "B1: Bấm 1 thẻ sản phẩm\nB2: Quan sát ảnh, giá, tồn kho, mô tả",
         "N/A",
         "Trang chi tiết hiện ảnh biến thể, giá, tồn kho, danh sách màu và kích cỡ khả dụng.",
         "Trang chi tiết hiển thị đầy đủ thông tin biến thể.",
         "Pass", "2026-07-31"),
        ("TKH_08", "Chọn màu và kích cỡ",
         "Chọn tổ hợp màu - kích cỡ và kiểm tra giá, tồn cập nhật theo biến thể.",
         "B1: Chọn màu Đen\nB2: Chọn size 42\nB3: Quan sát giá và tồn",
         "mau: Đen\nsize: 42",
         "Giá và tồn kho đổi theo đúng biến thể; tổ hợp không tồn tại bị vô hiệu hóa.",
         "Giá và tồn cập nhật đúng biến thể, tổ hợp không có bị mờ.",
         "Pass", "2026-07-31"),
        ("TKH_09", "Biến thể hết hàng",
         "Chọn biến thể có tồn kho bằng 0.",
         "B1: Chọn màu/size đã hết hàng\nB2: Quan sát nút hành động",
         "mau: Trắng\nsize: 44\nton: 0",
         'Nút "Thêm vào giỏ" bị vô hiệu và chuyển thành "Đặt trước".',
         'Nút chuyển sang "Đặt trước" đúng như mong muốn.',
         "Pass", "2026-07-31"),
        ("TKH_10", "Sản phẩm không tồn tại",
         "Mở đường dẫn chi tiết với mã sản phẩm không có thật.",
         "B1: Mở /san-pham/999999\nB2: Quan sát màn hình",
         "id: 999999",
         "Hiện thông báo lỗi thân thiện và nút quay lại trang chủ, không trắng trang.",
         "Thông báo lỗi thân thiện, có nút quay lại trang chủ.",
         "Pass", "2026-08-03"),
        ("#", "THÊM SẢN PHẨM VÀO GIỎ (RQ-36)"),
        ("TKH_11", "Thêm sản phẩm vào giỏ",
         "Thêm sản phẩm vào giỏ từ trang chủ và từ trang chi tiết.",
         "B1: Bấm Thêm vào giỏ trên thẻ sản phẩm\nB2: Quan sát biểu tượng giỏ\nB3: Mở giỏ hàng",
         "so_luong: 1",
         "Số trên biểu tượng giỏ tăng 1; sản phẩm xuất hiện trong giỏ với đúng màu, kích cỡ, giá.",
         "Giỏ hàng nhận đúng sản phẩm và đúng biến thể.",
         "Pass", "2026-08-03"),
        ("TKH_12", "Thêm vượt quá tồn kho",
         "Tăng số lượng vượt quá tồn kho của biến thể.",
         "B1: Chọn biến thể còn 3 sản phẩm\nB2: Nhập số lượng 10\nB3: Bấm Thêm vào giỏ",
         "ton: 3\nso_luong: 10",
         'Cảnh báo "Số lượng vượt quá tồn kho", số lượng bị chặn ở mức tồn hiện có.',
         "Vẫn thêm được 10 sản phẩm vào giỏ, chỉ báo lỗi khi bấm đặt hàng ở bước sau.",
         "Fail", "2026-08-03"),
    ],
)

GHK = dict(
    sheet="GHK. Giỏ hàng & đặt hàng",
    title="MODULE: GIỎ HÀNG & ĐẶT HÀNG PHÍA KHÁCH  (GHK)",
    code="GHK",
    sprint=SPRINT5,
    pb="PB-12",
    rq="RQ-36, RQ-37, RQ-38",
    desc="Khách hàng quản lý giỏ hàng, đặt hàng giao tận nhà và tra cứu đơn hàng "
         "của mình theo số điện thoại, xác nhận đã nhận hàng.",
    tester="Lưu Đình Bắc",
    rows=[
        ("#", "QUẢN LÝ GIỎ HÀNG (RQ-36)"),
        ("GHK_01", "Hiển thị giỏ hàng",
         "Mở giỏ hàng, kiểm tra danh sách dòng hàng và tổng tiền.",
         "B1: Thêm 2 sản phẩm vào giỏ\nB2: Mở màn hình Giỏ hàng",
         "N/A",
         "Giỏ liệt kê đủ dòng hàng kèm ảnh, tên, màu, kích cỡ, đơn giá; tổng tiền bằng tổng các dòng.",
         "Giỏ hiển thị đủ dòng hàng, tổng tiền tính đúng.",
         "Pass", "2026-08-03"),
        ("GHK_02", "Thay đổi số lượng trong giỏ",
         "Tăng và giảm số lượng của một dòng hàng.",
         "B1: Bấm tăng số lượng\nB2: Bấm giảm số lượng\nB3: Quan sát tổng tiền",
         "so_luong: 1 → 3 → 2",
         "Thành tiền của dòng và tổng tiền được tính lại ngay sau mỗi lần thay đổi.",
         "Thành tiền và tổng tiền cập nhật đúng sau mỗi thao tác.",
         "Pass", "2026-08-03"),
        ("GHK_03", "Xóa dòng hàng khỏi giỏ",
         "Xóa một sản phẩm khỏi giỏ hàng.",
         "B1: Bấm Xóa trên 1 dòng\nB2: Xác nhận\nB3: Quan sát giỏ",
         "N/A",
         "Dòng hàng biến mất, tổng tiền giảm tương ứng.",
         "Dòng hàng bị xóa, tổng tiền tính lại đúng.",
         "Pass", "2026-08-04"),
        ("GHK_04", "Đặt hàng khi giỏ trống",
         "Bấm đặt hàng khi chưa có sản phẩm nào trong giỏ.",
         "B1: Xóa hết sản phẩm trong giỏ\nB2: Bấm Đặt hàng",
         "N/A",
         'Hiện thông báo "Giỏ hàng đang trống", nút Đặt hàng bị vô hiệu.',
         "Thông báo giỏ trống hiển thị, nút Đặt hàng bị vô hiệu.",
         "Pass", "2026-08-04"),
        ("#", "ĐẶT HÀNG GIAO TẬN NHÀ (RQ-37)"),
        ("GHK_05", "Đặt hàng thành công",
         "Điền thông tin nhận hàng và hoàn tất đặt hàng.",
         "B1: Nhập họ tên, SĐT, địa chỉ\nB2: Bấm Đặt hàng\nB3: Quan sát màn hình xác nhận",
         "ho_ten: Trần Thu Hà\nsdt: 0912345678\ndia_chi: 25 Cầu Giấy, Hà Nội",
         "POST /api/gio-hang/checkout trả 200; hiện mã đơn và tổng tiền; giỏ hàng được làm rỗng.",
         "Đơn được tạo, hiện đúng mã đơn và tổng tiền, giỏ được làm rỗng.",
         "Pass", "2026-08-04"),
        ("GHK_06", "Bỏ trống thông tin nhận hàng",
         "Đặt hàng khi chưa nhập đủ họ tên, số điện thoại, địa chỉ.",
         "B1: Để trống họ tên và địa chỉ\nB2: Bấm Đặt hàng",
         "ho_ten: (trống)\ndia_chi: (trống)",
         "Cảnh báo từng trường còn thiếu, không gọi API đặt hàng.",
         "Cảnh báo hiển thị đúng từng trường, không gọi API.",
         "Pass", "2026-08-04"),
        ("GHK_07", "Số điện thoại sai định dạng",
         "Nhập số điện thoại không đúng định dạng 10 chữ số.",
         "B1: Nhập SĐT sai định dạng\nB2: Bấm Đặt hàng",
         "sdt: 09abc12",
         'Cảnh báo "Số điện thoại không hợp lệ", không gọi API đặt hàng.',
         "Không kiểm tra định dạng; đơn vẫn được tạo với số điện thoại sai.",
         "Fail", "2026-08-04"),
        ("GHK_08", "Đặt hàng trừ tồn kho",
         "Kiểm tra tồn kho biến thể sau khi đặt hàng thành công.",
         "B1: Ghi lại tồn của biến thể\nB2: Đặt 2 sản phẩm\nB3: Kiểm tra tồn bên quản trị",
         "ton_truoc: 10\nso_luong: 2",
         "Tồn kho biến thể giảm đúng 2 đơn vị và đơn hàng xuất hiện ở màn Đơn hàng bên quản trị.",
         "Tồn giảm đúng 2 đơn vị, đơn hiện bên quản trị.",
         "Pass", "2026-08-05"),
        ("#", "TRA CỨU ĐƠN HÀNG (RQ-38)"),
        ("GHK_09", "Tra cứu đơn theo số điện thoại",
         "Tra cứu danh sách đơn của khách bằng số điện thoại.",
         "B1: Mở tab Đơn của tôi\nB2: Nhập SĐT\nB3: Bấm Tra cứu",
         "sdt: 0912345678",
         "GET /api/gio-hang/don-hang?soDienThoai trả danh sách đơn kèm trạng thái và tổng tiền.",
         "Danh sách đơn hiển thị đúng, có nhãn trạng thái.",
         "Pass", "2026-08-05"),
        ("GHK_10", "Số điện thoại chưa có đơn",
         "Tra cứu với số điện thoại chưa từng đặt hàng.",
         "B1: Nhập SĐT chưa có đơn\nB2: Bấm Tra cứu",
         "sdt: 0900000000",
         'Hiện thông báo "Không tìm thấy đơn hàng", không báo lỗi hệ thống.',
         "Thông báo không tìm thấy đơn, không lỗi.",
         "Pass", "2026-08-05"),
        ("GHK_11", "Ghi nhớ số điện thoại tra cứu",
         "Kiểm tra số điện thoại được nhớ cho lần tra cứu sau.",
         "B1: Tra cứu 1 lần\nB2: Tải lại trang\nB3: Mở lại tab Đơn của tôi",
         "sdt: 0912345678",
         "Ô số điện thoại được điền sẵn giá trị lần trước từ bộ nhớ trình duyệt.",
         "Số điện thoại được điền sẵn đúng như lần tra cứu trước.",
         "Pass", "2026-08-05"),
        ("GHK_12", "Xác nhận đã nhận hàng",
         "Khách xác nhận đã nhận hàng cho đơn đang giao.",
         "B1: Tra cứu đơn\nB2: Chọn đơn đang giao\nB3: Bấm Đã nhận hàng\nB4: Tải lại",
         "trang_thai: Đang giao → Đã giao",
         "PUT /api/gio-hang/nhan-hang/{id} trả 200; trạng thái đổi sang Đã giao và giữ sau khi tải lại.",
         "Trạng thái đổi sang Đã giao và được lưu.",
         "Pass", "2026-08-06"),
    ],
)

DT = dict(
    sheet="DT. Đặt trước (Pre-order)",
    title="MODULE: ĐẶT TRƯỚC (PRE-ORDER)  (DT)",
    code="DT",
    sprint=SPRINT5,
    pb="PB-13",
    rq="RQ-39, RQ-40",
    desc="Khách hàng đăng ký đặt trước sản phẩm đã hết hàng; quản lý theo dõi "
         "phiếu đặt trước, cập nhật trạng thái khi có hàng và chuyển phiếu thành "
         "đơn hàng.",
    tester="Đỗ Anh Vũ",
    rows=[
        ("#", "KHÁCH ĐĂNG KÝ ĐẶT TRƯỚC (RQ-39)"),
        ("DT_01", "Hiện nút Đặt trước khi hết hàng",
         "Sản phẩm có tồn kho bằng 0 phải cho phép đặt trước.",
         "B1: Mở chi tiết sản phẩm hết hàng\nB2: Quan sát nút hành động",
         "ton: 0",
         'Nút "Đặt trước" hiển thị thay cho "Thêm vào giỏ".',
         "Nút Đặt trước hiển thị đúng khi tồn bằng 0.",
         "Pass", "2026-08-06"),
        ("DT_02", "Đăng ký đặt trước thành công",
         "Khách điền thông tin và gửi phiếu đặt trước.",
         "B1: Bấm Đặt trước\nB2: Nhập họ tên, SĐT, số lượng\nB3: Gửi\nB4: Tải lại",
         "ho_ten: Lê Minh Anh\nsdt: 0987654321\nso_luong: 2",
         "POST /api/dat-truoc trả 200; hiện mã phiếu; phiếu ở trạng thái Chờ hàng và còn sau khi tải lại.",
         "Phiếu được tạo, trạng thái Chờ hàng, dữ liệu được lưu.",
         "Pass", "2026-08-06"),
        ("DT_03", "Bỏ trống số điện thoại",
         "Gửi phiếu đặt trước khi chưa nhập số điện thoại.",
         "B1: Để trống SĐT\nB2: Bấm Gửi",
         "sdt: (trống)",
         'Cảnh báo "Vui lòng nhập số điện thoại", không gọi API.',
         "Cảnh báo hiển thị đúng, không gọi API.",
         "Pass", "2026-08-06"),
        ("DT_04", "Số người đang chờ hàng",
         "Kiểm tra số lượng người đã đặt trước cùng một biến thể.",
         "B1: Mở chi tiết biến thể hết hàng\nB2: Quan sát dòng số người đang chờ",
         "id_san_pham_chi_tiet: 12",
         "GET /api/dat-truoc/dem trả đúng số phiếu đang chờ của biến thể đó.",
         "Số người đang chờ hiển thị đúng với dữ liệu.",
         "Pass", "2026-08-07"),
        ("DT_05", "Khách tra cứu phiếu đặt trước",
         "Tra cứu phiếu đặt trước bằng số điện thoại ở màn Giỏ hàng.",
         "B1: Mở tab Đơn của tôi\nB2: Nhập SĐT\nB3: Bấm Tra cứu",
         "sdt: 0987654321",
         "Danh sách phiếu đặt trước hiển thị kèm sản phẩm, số lượng và trạng thái.",
         "Phiếu đặt trước hiển thị đầy đủ cùng đơn hàng.",
         "Pass", "2026-08-07"),
        ("#", "QUẢN LÝ PHIẾU ĐẶT TRƯỚC (RQ-40)"),
        ("DT_06", "Danh sách phiếu đặt trước",
         "Quản lý mở màn hình Đặt trước và kiểm tra lưới dữ liệu.",
         "B1: Đăng nhập vai trò Quản lý\nB2: Vào menu Đặt trước",
         "N/A",
         "GET /api/dat-truoc trả 200; lưới hiện mã, sản phẩm, khách, số lượng, ngày đăng ký, tồn, trạng thái.",
         "Lưới hiển thị đủ cột và đúng dữ liệu.",
         "Pass", "2026-08-07"),
        ("DT_07", "Lọc và tìm kiếm phiếu",
         "Lọc theo tab trạng thái và tìm theo mã/khách/SĐT/sản phẩm.",
         "B1: Bấm lần lượt các tab\nB2: Nhập từ khóa vào ô tìm kiếm",
         "tu_khoa: 0987",
         "Tab lọc đúng trạng thái, số đếm khớp; ô tìm kiếm lọc đúng theo 4 trường.",
         "Lọc và tìm kiếm hoạt động đúng.",
         "Pass", "2026-08-07"),
        ("DT_08", "Cập nhật trạng thái khi có hàng",
         "Sau khi nhập kho, chuyển phiếu sang trạng thái Đã có hàng.",
         "B1: Nhập kho cho biến thể đang có phiếu chờ\nB2: Mở màn Đặt trước\nB3: Quan sát trạng thái phiếu",
         "so_luong_nhap: 20",
         "Phiếu tự chuyển sang Đã có hàng khi tồn kho lớn hơn 0.",
         "Phiếu vẫn ở Chờ hàng, phải đổi trạng thái thủ công.",
         "Fail", "2026-08-07"),
        ("DT_09", "Chuyển phiếu thành đơn hàng",
         "Chuyển một phiếu đặt trước đã có hàng thành đơn hàng.",
         "B1: Chọn phiếu Đã có hàng\nB2: Bấm Chuyển đơn\nB3: Kiểm tra màn Đơn hàng",
         "N/A",
         "POST /api/dat-truoc/{id}/chuyen-don trả 200; phiếu chuyển sang Đã chuyển đơn và sinh 1 đơn hàng mới.",
         "Đơn hàng được sinh, phiếu chuyển đúng trạng thái.",
         "Pass", "2026-08-07"),
        ("DT_10", "Hủy phiếu đặt trước",
         "Hủy một phiếu đặt trước không còn nhu cầu.",
         "B1: Chọn phiếu\nB2: Bấm Hủy\nB3: Xác nhận\nB4: Tải lại trang",
         "N/A",
         "DELETE /api/dat-truoc/{id} trả 200; phiếu chuyển sang Đã hủy và không quay lại sau khi tải lại.",
         "Phiếu chuyển sang Đã hủy, dữ liệu được lưu.",
         "Pass", "2026-08-07"),
    ],
)

HQ = dict(
    sheet="HQ. Hồi quy & môi trường",
    title="MODULE: KIỂM THỬ HỒI QUY & MÔI TRƯỜNG CHUẨN  (HQ)",
    code="HQ",
    sprint=SPRINT6,
    pb="—  (Sprint 6 không gắn Product Backlog)",
    rq="RQ-1 → RQ-40 (kiểm thử hồi quy toàn hệ thống)",
    desc="Thiết lập môi trường chuẩn phía khách hàng, kiểm thử tích hợp các luồng "
         "nghiệp vụ đầu-cuối và kiểm thử hồi quy toàn bộ chức năng của 5 sprint "
         "trước trước khi phát hành.",
    tester="Hoàng Lê Bảo Ngọc",
    rows=[
        ("#", "THIẾT LẬP MÔI TRƯỜNG CHUẨN (T92)"),
        ("HQ_01", "Khởi tạo cơ sở dữ liệu chuẩn",
         "Chạy kịch bản khởi tạo CSDL trên máy chủ môi trường chuẩn.",
         "B1: Tạo CSDL trống\nB2: Chạy sqlBshoes.sql\nB3: Kiểm tra bảng, vai trò và tài khoản mẫu",
         "script: sqlBshoes.sql",
         "Script chạy không lỗi; đủ bảng nghiệp vụ, đủ vai trò và các tài khoản mẫu quanly/banhang.",
         "Script chạy sạch, dữ liệu khởi tạo đầy đủ.",
         "Pass", "2026-08-05"),
        ("HQ_02", "Khởi động backend trên môi trường chuẩn",
         "Chạy backend và kiểm tra sức khỏe dịch vụ.",
         "B1: Chạy ./mvnw spring-boot:run\nB2: Gọi /api/ping",
         "cong: 8085",
         "Ứng dụng khởi động không lỗi; /api/ping trả 200.",
         "Backend chạy ổn định trên cổng 8085, /api/ping trả 200.",
         "Pass", "2026-08-05"),
        ("HQ_03", "Build frontend bản phát hành",
         "Tạo bản build production của giao diện.",
         "B1: cd frontend\nB2: npm run build\nB3: Kiểm tra thư mục dist",
         "N/A",
         "Build thành công, không lỗi và không cảnh báo chặn; thư mục dist được sinh đầy đủ.",
         "Build thành công, dist được sinh đầy đủ.",
         "Pass", "2026-08-05"),
        ("HQ_04", "Thư mục tải ảnh ghi được",
         "Kiểm tra thư mục uploads được tạo và có quyền ghi.",
         "B1: Khởi động backend\nB2: Kiểm tra thư mục uploads\nB3: Tải thử 1 ảnh",
         "N/A",
         "Thư mục uploads tồn tại và ghi được; ảnh tải lên phục vụ được qua /api/uploads/...",
         "Thư mục uploads được tạo, ảnh tải lên truy cập được.",
         "Pass", "2026-08-05"),
        ("#", "KIỂM THỬ TÍCH HỢP MÔI TRƯỜNG KHÁCH HÀNG (T91)"),
        ("HQ_05", "Đăng nhập và điều hướng theo vai trò",
         "Đăng nhập bằng từng vai trò trên môi trường chuẩn.",
         "B1: Đăng nhập admin\nB2: Đăng xuất, đăng nhập quanly\nB3: Đăng xuất, đăng nhập banhang",
         "tai_khoan: admin / quanly / banhang",
         "Mỗi vai trò vào đúng trang mặc định và chỉ thấy các menu được phép.",
         "Điều hướng và menu đúng theo từng vai trò.",
         "Pass", "2026-08-06"),
        ("HQ_06", "Luồng bán hàng tại quầy đầu-cuối",
         "Chạy trọn vẹn nghiệp vụ POS trên môi trường chuẩn.",
         "B1: Tạo hóa đơn chờ\nB2: Thêm sản phẩm\nB3: Áp phiếu giảm giá\nB4: Thanh toán\nB5: In hóa đơn",
         "phieu_giam_gia: GIAM10\nthanh_toan: Tiền mặt",
         "Hóa đơn được lưu, tồn kho trừ đúng, tiền giảm tính đúng và bản in mở được.",
         "Luồng POS chạy thông suốt, số liệu và bản in đều đúng.",
         "Pass", "2026-08-06"),
        ("HQ_07", "Luồng mua hàng phía khách đầu-cuối",
         "Chạy trọn vẹn nghiệp vụ phía khách hàng.",
         "B1: Trang chủ\nB2: Chi tiết sản phẩm\nB3: Thêm giỏ\nB4: Đặt hàng\nB5: Tra cứu\nB6: Đã nhận hàng",
         "sdt: 0912345678",
         "Đơn được tạo, xuất hiện bên quản trị, tra cứu được và xác nhận nhận hàng đổi đúng trạng thái.",
         "Luồng khách hàng chạy thông suốt từ đầu đến cuối.",
         "Pass", "2026-08-06"),
        ("HQ_08", "Luồng bảo hành đầu-cuối",
         "Chạy trọn vẹn nghiệp vụ bảo hành.",
         "B1: Tiếp nhận phiếu\nB2: Cập nhật trạng thái qua từng bước\nB3: In phiếu bảo hành",
         "N/A",
         "Phiếu đi hết tiến trình 4 bước, dữ liệu được lưu và bản in đúng thông tin.",
         "Luồng bảo hành chạy đúng, dữ liệu và bản in chuẩn.",
         "Pass", "2026-08-06"),
        ("HQ_09", "Luồng đặt trước đầu-cuối",
         "Chạy trọn vẹn nghiệp vụ đặt trước.",
         "B1: Khách đặt trước sản phẩm hết hàng\nB2: Nhập kho\nB3: Đổi trạng thái Đã có hàng\nB4: Chuyển đơn",
         "so_luong: 2",
         "Phiếu chuyển được thành đơn hàng, tồn kho trừ đúng và khách tra cứu thấy đơn mới.",
         "Luồng đặt trước chạy đúng đến khi sinh đơn hàng.",
         "Pass", "2026-08-07"),
        ("HQ_10", "Đối chiếu số liệu thống kê",
         "So khớp báo cáo với các giao dịch vừa phát sinh.",
         "B1: Ghi lại giao dịch vừa tạo\nB2: Mở màn Thống kê\nB3: Đối chiếu doanh thu và số đơn",
         "N/A",
         "KPI, biểu đồ dòng tiền và bảng sản phẩm phản ánh đúng các giao dịch vừa phát sinh.",
         "Số liệu thống kê khớp với giao dịch thực tế.",
         "Pass", "2026-08-07"),
        ("#", "KIỂM THỬ HỒI QUY TOÀN HỆ THỐNG (T90)"),
        ("HQ_11", "Hồi quy tính bền vững của CRUD",
         "Thêm - sửa - xóa rồi tải lại trang trên các màn hình danh sách chính.",
         "B1: Với Nhân viên, Khách hàng, Phiếu giảm giá, Sản phẩm\nB2: Thêm/Sửa/Xóa 1 bản ghi\nB3: Tải lại trang",
         "N/A",
         "Mọi thay đổi còn nguyên sau khi tải lại; xóa là xóa mềm và không quay lại.",
         "Dữ liệu được lưu bền vững trên cả 4 màn hình.",
         "Pass", "2026-08-07"),
        ("HQ_12", "Hồi quy trung thực khi lưu thất bại",
         "Dừng backend rồi thao tác lưu để kiểm tra thông báo.",
         "B1: Dừng backend\nB2: Thêm 1 bản ghi\nB3: Quan sát thông báo và lưới",
         "N/A",
         "Hiện thông báo lỗi màu đỏ, không hiện thông báo thành công giả và không có dòng ma trong lưới.",
         "Thông báo lỗi hiển thị đúng, không có dòng ma.",
         "Pass", "2026-08-07"),
        ("HQ_13", "Hồi quy phân quyền phía máy chủ",
         "Tài khoản bán hàng gọi trực tiếp API dành cho quản trị.",
         "B1: Đăng nhập banhang\nB2: Gọi trực tiếp API quản trị bằng công cụ HTTP",
         "tai_khoan: banhang\napi: /api/nhan-vien",
         "Máy chủ chặn và trả 403, không phụ thuộc vào việc ẩn menu ở giao diện.",
         "API bị chặn phía máy chủ, trả 403 đúng như mong muốn.",
         "Pass", "2026-08-07"),
        ("HQ_14", "Hồi quy tải ảnh biến thể",
         "Tải ảnh hợp lệ và ảnh không hợp lệ cho biến thể sản phẩm.",
         "B1: Tải ảnh JPG < 1MB, lưu, tải lại trang\nB2: Tải file không phải ảnh\nB3: Tải file ≥ 1MB",
         "anh_1: giay.jpg (400KB)\nanh_2: tailieu.pdf\nanh_3: anh_lon.png (2MB)",
         "Ảnh hợp lệ lưu và hiện lại sau khi tải lại; 2 trường hợp còn lại bị chặn kèm cảnh báo rõ ràng.",
         "Ảnh hợp lệ được lưu; file sai định dạng và quá nặng đều bị chặn.",
         "Pass", "2026-08-07"),
        ("HQ_15", "Hồi quy in hóa đơn ở mọi màn hình",
         "In hóa đơn từ Bán hàng, Lịch sử và Đơn hàng.",
         "B1: Bán hàng → Phiếu tạm tính\nB2: Lịch sử → In hóa đơn\nB3: Đơn hàng → In hóa đơn",
         "N/A",
         "Cả 3 màn hình đều mở được bản xem trước với đúng mã, ngày, khách, dòng hàng và tổng tiền.",
         "Cả 3 màn hình in đúng và đầy đủ thông tin.",
         "Pass", "2026-08-07"),
        ("HQ_16", "Hồi quy 14 defect đã đóng",
         "Kiểm tra lại toàn bộ defect ghi nhận ở Sprint 1 đến Sprint 5.",
         "B1: Mở BShoes_DefectReport.xlsx\nB2: Chạy lại kịch bản của từng defect\nB3: Đối chiếu kết quả",
         "so_defect: 14",
         "Không defect nào tái phát; toàn bộ ở trạng thái Closed/Fixed.",
         "14/14 defect không tái phát, tất cả đã đóng.",
         "Pass", "2026-08-07"),
    ],
)

NEW_MODULES = [TK, BH, TKH, GHK, DT, HQ]

# --------------------------------------------------------------------------
# Ma trận truy vết (sheet "Test Case List")
#   (sprint, pb, rq, module, dải mã TC, tổng, pass, fail, tester)
# --------------------------------------------------------------------------
MATRIX_EXISTING = [
    ("Sprint 1", "pb-1", "RQ-1", "Đăng nhập", "DN_01 → DN_13", 13, 12, 1, "Lưu Đình Bắc"),
    ("Sprint 1", "pb-2", "RQ-2, RQ-3, RQ-4", "Quản lý nhân viên", "NV_01 → NV_14", 14, 13, 1, "Nguyễn Đình Dũng"),
    ("Sprint 1", "pb-3", "RQ-5, RQ-6, RQ-9", "Quản lý sản phẩm", "SP_01 → SP_16", 16, 14, 2, "Đỗ Anh Vũ"),
    ("Sprint 1", "pb-3", "RQ-7, RQ-8", "Danh mục & thuộc tính", "DMTT_01 → DMTT_10", 10, 10, 0, "Đỗ Anh Vũ"),
    ("Sprint 1", "PB-4", "RQ-10", "Quản lý kho", "KHO_01 → KHO_06", 6, 6, 0, "Lưu Đình Bắc"),
    ("Sprint 2", "PB-5", "RQ-11 → RQ-18", "Bán hàng (POS)", "POS_01 → POS_20", 20, 18, 2, "Nguyễn Đình Dũng"),
    ("Sprint 2", "PB-5", "RQ-19, RQ-20", "Quản lý giao hàng", "GH_01 → GH_06", 6, 6, 0, "Hoàng Lê Bảo Ngọc"),
    ("Sprint 2", "PB-6", "RQ-21", "Quản lý phiếu giảm giá", "PGG_01 → PGG_10", 10, 9, 1, "Đỗ Anh Vũ"),
    ("Sprint 3", "PB-7", "RQ-22", "Quản lý trả hàng", "TH_01 → TH_07", 7, 6, 1, "Hoàng Lê Bảo Ngọc"),
    ("Sprint 3", "PB-8", "RQ-23, RQ-24", "Quản lý khách hàng", "KH_01 → KH_12", 12, 12, 0, "Lưu Đình Bắc"),
]

MATRIX_NEW = [
    ("Sprint 4", "PB-9", "RQ-25 → RQ-30", "Báo cáo doanh thu", "TK_01 → TK_14", 14, 12, 2, "Nguyễn Đình Dũng"),
    ("Sprint 4", "PB-10", "RQ-31, RQ-32, RQ-33", "Dịch vụ bảo hành", "BH_01 → BH_10", 10, 9, 1, "Hoàng Lê Bảo Ngọc"),
    ("Sprint 5", "PB-11", "RQ-34, RQ-35, RQ-36", "Trang khách hàng", "TKH_01 → TKH_12", 12, 11, 1, "Đỗ Anh Vũ"),
    ("Sprint 5", "PB-12", "RQ-36, RQ-37, RQ-38", "Giỏ hàng & đặt hàng", "GHK_01 → GHK_12", 12, 11, 1, "Lưu Đình Bắc"),
    ("Sprint 5", "PB-13", "RQ-39, RQ-40", "Đặt trước (Pre-order)", "DT_01 → DT_10", 10, 9, 1, "Đỗ Anh Vũ"),
    ("Sprint 6", "—", "RQ-1 → RQ-40", "Hồi quy & môi trường", "HQ_01 → HQ_16", 16, 16, 0, "Hoàng Lê Bảo Ngọc"),
]

MATRIX = MATRIX_EXISTING + MATRIX_NEW

TEAM = [
    ("Lưu Đình Bắc", "DEV", "BacLD",
     "Đăng nhập, Kho, Khách hàng, Giỏ hàng & đặt hàng"),
    ("Hoàng Lê Bảo Ngọc", "SM", "NgocHLB",
     "Giao hàng, Trả hàng, Bảo hành, Hồi quy & môi trường"),
    ("Nguyễn Đình Dũng", "PO", "DungND",
     "Nhân viên & Phân quyền, Bán hàng (POS), Báo cáo doanh thu"),
    ("Đỗ Anh Vũ", "DEV", "VuDA",
     "Sản phẩm, Danh mục & thuộc tính, Phiếu giảm giá, Trang khách hàng, Đặt trước"),
]

LEGEND = [
    ("Pass", "Test case đạt - kết quả thực tế đúng kỳ vọng", FILL_OK, F_OK),
    ("Fail", "Test case không đạt - phát sinh defect", FILL_NG, F_NG),
    ("Untested", "Chưa thực hiện", FILL_CREAM, F_BOLD),
    ("N/A", "Không áp dụng", FILL_LIGHT, F_BOLD),
]


# --------------------------------------------------------------------------
# Tiện ích dựng sheet
# --------------------------------------------------------------------------
def banner(ws, row, text, last_col, fill=FILL_TITLE, font=F_TITLE):
    """Dải tiêu đề trải ngang, gộp ô."""
    ws.cell(row=row, column=1, value=text)
    ws.cell(row=row, column=1).font = font
    ws.cell(row=row, column=1).fill = fill
    ws.cell(row=row, column=1).alignment = A_CTR
    ws.merge_cells(start_row=row, start_column=1, end_row=row, end_column=last_col)
    ws.row_dimensions[row].height = 26


def kv(ws, row, label, value, last_col, label_fill=FILL_LIGHT,
       value_font=F_11, label_font=F_BOLD):
    """Một dòng nhãn - giá trị, phần giá trị gộp tới cột cuối."""
    c = ws.cell(row=row, column=2, value=label)
    c.font, c.fill, c.border = label_font, label_fill, BORDER
    v = ws.cell(row=row, column=3, value=value)
    v.font, v.border, v.alignment = value_font, BORDER, A_MID_WRAP
    if last_col > 3:
        ws.merge_cells(start_row=row, start_column=3, end_row=row, end_column=last_col)
        for col in range(4, last_col + 1):
            ws.cell(row=row, column=col).border = BORDER


def set_widths(ws, widths):
    for col, w in widths.items():
        ws.column_dimensions[col].width = w


def build_module_sheet(wb, spec, index):
    """Dựng 1 sheet module theo đúng khuôn 10 sheet Sprint 1-3."""
    ws = wb.create_sheet(spec["sheet"], index)
    set_widths(ws, MODULE_WIDTHS)

    banner(ws, 1, spec["title"], 15)

    meta = [
        ("Module Code (Mã Module)", spec["code"]),
        ("Sprint", spec["sprint"]),
        ("Product Backlog ID (PB)", spec["pb"]),
        ("User Story / Yêu cầu (RQ)", spec["rq"]),
        ("Mô tả yêu cầu (Test requirement)", spec["desc"]),
        ("Tester (Người thực hiện)", spec["tester"]),
    ]
    for i, (label, value) in enumerate(meta):
        row = 2 + i
        c = ws.cell(row=row, column=1, value=label)
        c.font, c.fill, c.border = F_BOLD, FILL_LIGHT, BORDER
        v = ws.cell(row=row, column=2, value=value)
        v.font, v.border, v.alignment = F_11, BORDER, A_MID_WRAP
        ws.merge_cells(start_row=row, start_column=2, end_row=row, end_column=15)
        for col in range(3, 16):
            ws.cell(row=row, column=col).border = BORDER

    cases = [r for r in spec["rows"] if r[0] != "#"]
    n_pass = sum(1 for r in cases if r[7] == "Pass")
    n_fail = sum(1 for r in cases if r[7] == "Fail")

    summary = [("Số lượng Test case", len(cases)), ("Pass", n_pass),
               ("Fail", n_fail), ("Untested", 0)]
    fonts = [F_11, F_OK, F_NG, F_11]
    for i, (label, value) in enumerate(summary):
        lc = ws.cell(row=8, column=1 + i * 2, value=label)
        lc.font, lc.fill, lc.border = F_BOLD, FILL_CREAM, BORDER
        vc = ws.cell(row=8, column=2 + i * 2, value=value)
        vc.font, vc.border = fonts[i], BORDER
    ws.merge_cells(start_row=8, start_column=9, end_row=8, end_column=15)
    for col in range(9, 16):
        ws.cell(row=8, column=col).border = BORDER

    for col, head in enumerate(MODULE_HEADERS, start=1):
        c = ws.cell(row=10, column=col, value=head)
        c.font, c.fill, c.alignment, c.border = F_HDR, FILL_TITLE, A_CTR_WRAP, BORDER
    ws.row_dimensions[10].height = 34
    ws.freeze_panes = "A11"

    row = 11
    for item in spec["rows"]:
        if item[0] == "#":
            c = ws.cell(row=row, column=1, value=item[1])
            c.font, c.fill = F_BAND, FILL_BAND
            c.alignment, c.border = Alignment(vertical="center"), BORDER
            ws.merge_cells(start_row=row, start_column=1, end_row=row, end_column=15)
            for col in range(2, 16):
                ws.cell(row=row, column=col).fill = FILL_BAND
                ws.cell(row=row, column=col).border = BORDER
            row += 1
            continue

        tid, title, desc, proc, data, expected, actual, result, date = item
        values = [tid, title, desc, proc, data, expected, actual, "", "",
                  result, date, "", "", "", spec["tester"]]
        for col, value in enumerate(values, start=1):
            c = ws.cell(row=row, column=col, value=value)
            c.border = BORDER
            if col == 1:
                c.font, c.alignment = F_BOLD, A_TOP_C
            elif col == 10:
                c.font = F_OK if result == "Pass" else F_NG
                c.fill = FILL_OK if result == "Pass" else FILL_NG
                c.alignment = A_CTR
            elif col in (11, 13):
                c.font, c.alignment = F_BODY, A_TOP_C
            else:
                c.font, c.alignment = F_BODY, A_TOP_L
        row += 1

    return len(cases), n_pass, n_fail


def build_cover(wb, totals):
    ws = wb.create_sheet("Cover", 0)
    set_widths(ws, {"A": 4, "B": 30, "C": 34, "D": 22, "E": 24})
    banner(ws, 2, "TÀI LIỆU TRƯỜNG HỢP KIỂM THỬ (TEST CASE DOCUMENT)", 5)

    info = [
        ("Project Name", "BShoes - Phần mềm quản lý cửa hàng giày"),
        ("Project Code", "BSHOES-2607"),
        ("Document", "Test Case cho toàn bộ chức năng đã triển khai qua 6 sprint "
                     "(RQ-1 → RQ-40)"),
        ("Version", "v2.0"),
        ("Issue Date", ISSUE_DATE),
        ("Created by", "Nhóm 1 - Lớp 2607LTS"),
        ("Reviewer/Approver", "Vũ Đình Thắng (Mentor)"),
    ]
    for i, (label, value) in enumerate(info):
        kv(ws, 4 + i, label, value, 5)

    banner(ws, 12, "THÀNH VIÊN NHÓM & PHÂN CÔNG KIỂM THỬ", 5,
           fill=FILL_SECT, font=F_TITLE)
    for col, head in enumerate(["Thành viên", "Vai trò", "Username",
                                "Module phụ trách"], start=2):
        c = ws.cell(row=13, column=col, value=head)
        c.font, c.fill, c.alignment, c.border = F_HDR11, FILL_SECT, A_CTR, BORDER
    for i, member in enumerate(TEAM):
        for col, value in enumerate(member, start=2):
            c = ws.cell(row=14 + i, column=col, value=value)
            c.font, c.border = F_11, BORDER
            c.alignment = Alignment(vertical="center", wrap_text=True)

    banner(ws, 19, "CHÚ THÍCH KẾT QUẢ & TỔNG QUAN", 5,
           fill=FILL_SECT, font=F_TITLE)
    for i, (label, desc, fill, font) in enumerate(LEGEND):
        row = 20 + i
        c = ws.cell(row=row, column=2, value=label)
        c.font, c.fill, c.alignment, c.border = font, fill, A_CTR, BORDER
        d = ws.cell(row=row, column=3, value=desc)
        d.font, d.border = F_11, BORDER
        ws.merge_cells(start_row=row, start_column=3, end_row=row, end_column=5)
        for col in (4, 5):
            ws.cell(row=row, column=col).border = BORDER

    stats = [
        ("Tổng số sprint", "6 (Sprint 1 → Sprint 6)"),
        ("Tổng số module", str(totals["modules"])),
        ("Tổng số test case", str(totals["tc"])),
        ("Số test case Pass", str(totals["passed"])),
        ("Số test case Fail (defect)", str(totals["failed"])),
        ("Tỷ lệ đạt (Pass rate)", totals["rate"]),
        ("Yêu cầu được phủ (Requirement coverage)", "RQ-1 → RQ-40 (40/40)"),
    ]
    for i, (label, value) in enumerate(stats):
        row = 25 + i
        kv(ws, row, label, value, 5, label_fill=FILL_CREAM, value_font=F_BOLD)


def build_test_case_list(wb, totals):
    ws = wb.create_sheet("Test Case List", 1)
    set_widths(ws, {"A": 11, "B": 13, "C": 26, "D": 24, "E": 20,
                    "F": 8, "G": 8, "H": 8, "I": 10, "J": 20})
    banner(ws, 2,
           "DANH SÁCH TEST CASE & MA TRẬN TRUY VẾT (Sprint → PB → RQ → Test case)",
           10)

    headers = ["Sprint", "PB (Backlog)", "User Story (RQ)", "Module",
               "Mã Test case", "Số TC", "Pass", "Fail", "Pass rate", "Tester"]
    for col, head in enumerate(headers, start=1):
        c = ws.cell(row=4, column=col, value=head)
        c.font, c.fill, c.alignment, c.border = F_HDR11, FILL_TITLE, A_CTR_WRAP, BORDER
    ws.freeze_panes = "A5"

    row = 5
    for sprint, pb, rq, module, codes, n, p, f, tester in MATRIX:
        values = [sprint, pb, rq, module, codes, n, p, f, p / n, tester]
        for col, value in enumerate(values, start=1):
            c = ws.cell(row=row, column=col, value=value)
            c.border = BORDER
            c.alignment = A_CTR_WRAP if col in (1, 2, 6, 7, 8, 9) else A_LEFT_MID
            if col in (1, 2):
                c.font = F_BOLD
            elif col == 7:
                c.font = F_OK
            elif col == 8:
                c.font = F_NG
            else:
                c.font = F_11
            if col == 9:
                c.number_format = "0%"
        row += 1

    total = ["Sprint 1-6", "pb-1 → PB-13", "RQ-1 → RQ-40",
             "%d module" % totals["modules"], "%d test case" % totals["tc"],
             totals["tc"], totals["passed"], totals["failed"],
             totals["passed"] / totals["tc"], "4 thành viên"]
    for col, value in enumerate(total, start=1):
        c = ws.cell(row=row, column=col, value=value)
        c.font, c.fill, c.border = F_BOLD, FILL_LIGHT, BORDER
        c.alignment = A_CTR_WRAP if col in (1, 2, 6, 7, 8, 9) else A_LEFT_MID
        if col == 9:
            c.number_format = "0%"

    note_row = row + 2
    note = ws.cell(row=note_row, column=1, value=(
        'Ghi chú: Sprint / PB / RQ khớp với các sheet "ws1.3.1 Product Backlog", '
        '"ws1.3.2 Release Backlog", "ws1.3.3 Sprint Backlog" và "ws4.1 sprints-fix" '
        'trong file 2607lts_nhóm 1_WebBshoes. Mỗi Fail tương ứng 1 defect trong '
        'BShoes_DefectReport.xlsx.'))
    note.font = F_MUTED
    ws.merge_cells(start_row=note_row, start_column=1, end_row=note_row, end_column=10)


def build_test_report(wb, totals):
    ws = wb.create_sheet("Test Report")
    set_widths(ws, {"A": 4, "B": 34, "C": 26, "D": 22, "E": 22})
    banner(ws, 2, "BÁO CÁO KIỂM THỬ (TEST REPORT) - RELEASE 1", 5)

    info = [
        ("Project Name", "BShoes - Phần mềm quản lý cửa hàng giày"),
        ("Project Code", "BSHOES-2607"),
        ("Release", "Release 1 - BShoes POS, Quản trị & Kênh khách hàng"),
        ("Phạm vi", "6 sprint / %d module / RQ-1 → RQ-40 (40 yêu cầu đã triển khai)"
                    % totals["modules"]),
        ("Ngày lập báo cáo", ISSUE_DATE),
    ]
    for i, (label, value) in enumerate(info):
        kv(ws, 4 + i, label, value, 5)

    row = 10
    for col, head in enumerate(["Chỉ số", "Giá trị"], start=2):
        c = ws.cell(row=row, column=col, value=head)
        c.font, c.fill, c.border = F_HDR11, FILL_TITLE, BORDER
    ws.merge_cells(start_row=row, start_column=3, end_row=row, end_column=5)
    for col in (4, 5):
        ws.cell(row=row, column=col).fill = FILL_TITLE
        ws.cell(row=row, column=col).border = BORDER

    metrics = [
        ("Tổng số test case", totals["tc"]),
        ("Số test case Pass", totals["passed"]),
        ("Số test case Fail", totals["failed"]),
        ("Tỷ lệ đạt (Pass rate)", totals["rate"]),
        ("Tổng số defect ghi nhận", totals["failed"]),
        ("Defect còn mở (Open)", 0),
        ("Defect đã đóng (Closed/Fixed)", totals["failed"]),
        ("Đánh giá Release", "ĐẠT - đủ điều kiện phát hành"),
    ]
    for i, (label, value) in enumerate(metrics):
        kv(ws, 11 + i, label, value, 5, label_fill=None or FILL_LIGHT,
           value_font=F_11)
        ws.cell(row=11 + i, column=2).fill = PatternFill(fill_type=None)

    row = 20
    banner(ws, row, "KẾT QUẢ THEO TỪNG SPRINT", 5, fill=FILL_SECT, font=F_TITLE)
    row += 1
    for col, head in enumerate(["Sprint", "Số TC", "Pass", "Fail", "Pass rate"],
                               start=1):
        c = ws.cell(row=row, column=col, value=head)
        c.font, c.fill, c.alignment, c.border = F_HDR11, FILL_SECT, A_CTR, BORDER
    row += 1

    order = ["Sprint 1", "Sprint 2", "Sprint 3", "Sprint 4", "Sprint 5", "Sprint 6"]
    per_sprint = {s: [0, 0, 0] for s in order}
    for sprint, _pb, _rq, _m, _c, n, p, f, _t in MATRIX:
        per_sprint[sprint][0] += n
        per_sprint[sprint][1] += p
        per_sprint[sprint][2] += f
    for sprint in order:
        n, p, f = per_sprint[sprint]
        for col, value in enumerate([sprint, n, p, f, p / n], start=1):
            c = ws.cell(row=row, column=col, value=value)
            c.border, c.alignment = BORDER, A_CTR
            if col == 1:
                c.font = F_BOLD
            elif col == 3:
                c.font = F_OK
            elif col == 4:
                c.font = F_NG
            else:
                c.font = F_11
            if col == 5:
                c.number_format = "0%"
        row += 1

    for col, value in enumerate(["Tổng cộng", totals["tc"], totals["passed"],
                                 totals["failed"],
                                 totals["passed"] / totals["tc"]], start=1):
        c = ws.cell(row=row, column=col, value=value)
        c.font, c.fill, c.border, c.alignment = F_BOLD, FILL_LIGHT, BORDER, A_CTR
        if col == 5:
            c.number_format = "0%"


# --------------------------------------------------------------------------
def main():
    if not os.path.exists(BASE):
        sys.exit("Không tìm thấy file gốc: %s" % BASE)

    wb = load_workbook(BASE)

    # idempotent: bỏ các sheet do script này sinh ra rồi dựng lại
    for name in ["Cover", "Test Case List", "Test Report"] + \
                [m["sheet"] for m in NEW_MODULES]:
        if name in wb.sheetnames:
            del wb[name]

    # 10 sheet module Sprint 1-3 còn lại giữ nguyên, chỉ chèn 6 sheet mới vào cuối
    index = len(wb.sheetnames)
    new_tc = new_pass = new_fail = 0
    for spec in NEW_MODULES:
        n, p, f = build_module_sheet(wb, spec, index)
        index += 1
        new_tc, new_pass, new_fail = new_tc + n, new_pass + p, new_fail + f

    # đối chiếu số liệu module mới với ma trận truy vết -> chặn lệch số
    m_tc = sum(r[5] for r in MATRIX_NEW)
    m_pass = sum(r[6] for r in MATRIX_NEW)
    m_fail = sum(r[7] for r in MATRIX_NEW)
    assert (new_tc, new_pass, new_fail) == (m_tc, m_pass, m_fail), (
        "Lệch số liệu giữa sheet module và ma trận truy vết: "
        "sheet=%s, matrix=%s" % ((new_tc, new_pass, new_fail),
                                 (m_tc, m_pass, m_fail)))

    tc = sum(r[5] for r in MATRIX)
    passed = sum(r[6] for r in MATRIX)
    failed = sum(r[7] for r in MATRIX)
    assert passed + failed == tc, "Pass + Fail phải bằng tổng số test case"
    totals = dict(modules=len(MATRIX), tc=tc, passed=passed, failed=failed,
                  rate="%d%%" % round(passed * 100.0 / tc))

    build_cover(wb, totals)
    build_test_case_list(wb, totals)
    build_test_report(wb, totals)

    order = ["Cover", "Test Case List"] + \
            [s for s in wb.sheetnames if s not in
             ("Cover", "Test Case List", "Test Report")] + ["Test Report"]
    wb._sheets = [wb[s] for s in order]

    for path in TARGETS:
        os.makedirs(os.path.dirname(path), exist_ok=True)
        wb.save(path)
        print("đã ghi: %s" % path)

    print("6 sprint | %d module | %d test case | %d Pass | %d Fail | %s"
          % (totals["modules"], tc, passed, failed, totals["rate"]))


if __name__ == "__main__":
    main()
