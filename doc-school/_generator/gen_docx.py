# -*- coding: utf-8 -*-
"""
Sinh 2 file Word workshop theo format chốt ngày 2026-07-17.
Định dạng nằm ở docx_format.py; file này chỉ lo nội dung.

Mô hình dữ liệu 3 cấp: RQ (request) -> PB (user story) -> Task, đánh số theo Sprint.

Chạy:  python gen_docx.py <thư mục xuất>
"""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from bshoes_data import *
from docx import Document
from docx_format import setup, title, Chuong, ket_luan

OUTDIR = sys.argv[1]

SPRINT_DATES = {
    1: "08/06/2026 đến 21/06/2026",
    2: "22/06/2026 đến 05/07/2026",
    3: "06/07/2026 đến 19/07/2026",
    4: "20/07/2026 đến 02/08/2026",
    5: "03/08/2026 đến 16/08/2026",
    6: "17/08/2026 đến 30/08/2026",
}
SPRINT_OWNER = {1: "DEV1 + DEV2", 2: "DEV1 + DEV2", 3: "DEV1 + DEV2 + DEV3",
                4: "DEV1 + DEV2 + DEV3", 5: "DEV2 + DEV3", 6: "DEV1 + DEV2"}

TONG_GIO = sum(t["est"] for t in TASKS)
TONG_SP = sum(fib((p["tuong_tac"] + p["quy_tac"] + p["thuc_the"] + p["thao_tac"]) * C_DEFAULT * ED_TOTAL / 36) for p in PBS)


def pbs_of(rq_id):
    return [p for p in PBS if p["rq"] == rq_id]


def tasks_of(pb_id):
    return [t for t in TASKS if t["story"] == pb_id]


# =====================================================================
# WORKSHOP 1: Kế hoạch dự án
# =====================================================================
doc = setup(Document())
title(doc, "Kế hoạch dự án " + PROJECT, "WORKSHOP 1, phiên bản %s" % VERSION)

# ---------------------------------------------------------- Chương 1
c1 = Chuong(doc, 1, "Tổng quan dự án")
c1.para("Chương này giới thiệu bối cảnh dự án BShoes, công nghệ sử dụng và cách nhóm "
        "phân vai. Đây là căn cứ để hiểu vì sao kế hoạch ở các chương sau được chia nhỏ "
        "theo ba cấp yêu cầu và ưu tiên kiểm thử.")

c1.h2(1, "Thông tin chung")
c1.para("BShoes là hệ thống quản lý và bán hàng cho cửa hàng giày, được chuyển từ ứng dụng "
        "desktop NetBeans sang nền web. Nhóm làm theo Scrum với 6 Sprint, mỗi Sprint 2 tuần, "
        "hạn chót %s. Bảng 1.1 tóm tắt các thông tin cốt lõi của dự án." % DEADLINE)
c1.bang("Thông tin chung của dự án", ["Mục", "Nội dung"], [
    ["Tên dự án", PROJECT],
    ["Phiên bản", VERSION],
    ["Khung làm việc", "Scrum, 6 Sprint, mỗi Sprint 2 tuần"],
    ["Công nghệ", "Spring Boot (JPA) + SQL Server; Vue 3 SPA; Chart.js"],
    ["Cấu trúc backlog", "3 cấp: %d Request (RQ), %d Product Backlog (PB), %d Task"
                         % (len(RQS), len(PBS), len(TASKS))],
    ["Tổng khối lượng", "%d giờ công" % TONG_GIO],
    ["Thời hạn hoàn thành", DEADLINE],
], [4, 12])

c1.h2(2, "Nhóm thực hiện và phân vai")
c1.para("Nhóm đã từng hợp tác ở dự án trước và tinh thần hợp tác rất tích cực, tuy nhiên "
        "trình độ IT của các thành viên còn hạn chế. Vì vậy kế hoạch ưu tiên chia task nhỏ, "
        "có người kèm cặp và dành thời gian kiểm thử kỹ hơn mức thông thường. Bảng 1.2 liệt "
        "kê vai trò và trách nhiệm của từng thành viên.")
c1.bang("Phân vai trong nhóm", ["Vai trò", "Thành viên", "Trách nhiệm"],
        [[c, n, r] for c, n, r in TEAM], [2.5, 3.5, 10])

# ---------------------------------------------------------- Chương 2
c2 = Chuong(doc, 2, "Danh sách chức năng của hệ thống")
c2.para("Chương này liệt kê toàn bộ chức năng hệ thống cần có. BShoes gồm 6 chức năng lõi "
        "phục vụ bán hàng tại quầy và 3 chức năng mở rộng hướng dữ liệu và hướng khách hàng. "
        "Danh sách là đầu vào để bóc tách thành Product Backlog ở chương 3.")

c2.h2(1, "Sáu chức năng lõi")
for txt, items in [
    ("Đăng nhập", ["Đăng nhập bằng tài khoản nhân viên, lấy đúng quyền theo vai trò"]),
    ("Quản lý Nhân viên và Phân quyền Nhân viên", [
        "CRUD nhân viên, gán vai trò",
        "Vai trò là bộ quyền mặc định, phân quyền truy cập tới từng nhân viên"]),
    ("Quản lý sản phẩm và Quản lý danh mục sản phẩm", [
        "Sản phẩm, biến thể theo màu và size, nhập kho",
        "Danh mục và 8 nhóm thuộc tính, gộp chung trong màn quản lý sản phẩm"]),
    ("Bán hàng tại quầy (POS), Xuất hóa đơn, Xuất báo cáo nhân viên", [
        "Tạo hóa đơn chờ, thêm hàng vào giỏ và tự trừ kho, quét QR",
        "Áp phiếu giảm giá, thanh toán tiền mặt, chuyển khoản hoặc kết hợp",
        "In hóa đơn, hủy đơn và hoàn kho"]),
    ("Quản lý đơn đặt hàng, xem lịch sử đặt hàng", [
        "Tạo đơn giao kèm phí ship, cập nhật đã giao, trả hàng và hoàn kho",
        "Tra cứu và xem lịch sử đơn hàng"]),
    ("Quản lý khách hàng", [
        "CRUD khách hàng và địa chỉ giao hàng",
        "Xem lịch sử mua hàng của khách"]),
]:
    c2.bullet_head(txt)
    for it in items:
        c2.bullet_item(it)

c2.h2(2, "Ba chức năng mở rộng")
c2.para("Ba chức năng mở rộng nhằm truyền tải định hướng kinh doanh hướng người dùng và "
        "hướng dữ liệu, giúp chủ doanh nghiệp đánh giá tình hình kinh doanh thực tế.")
for txt, items in [
    ("Xem Dashboard báo cáo kinh doanh, Xuất báo cáo doanh thu", [
        "KPI, dòng tiền, ROI, giữ chân khách, sản phẩm bán chạy",
        "Xuất báo cáo ra Excel"]),
    ("Sau bán hàng: Dịch vụ hậu mãi và bảo hành, Quản lý khuyến mãi", [
        "Tiếp nhận và xử lý bảo hành theo trạng thái, in phiếu",
        "Quản lý phiếu giảm giá và khuyến mãi"]),
    ("Xem sản phẩm và Mua hàng (Client - Khách hàng), Preorder sản phẩm", [
        "Trang chủ, trang chi tiết, giỏ hàng, đặt hàng giao tận nhà (COD)",
        "Đặt trước mẫu đang hết hàng, tra cứu đơn theo số điện thoại"]),
]:
    c2.bullet_head(txt)
    for it in items:
        c2.bullet_item(it)

# ---------------------------------------------------------- Chương 3
c3 = Chuong(doc, 3, "Product Backlog cấp Request")
c3.para("Chương này liệt kê các yêu cầu gốc từ actor dưới dạng user story chuẩn As a, "
        "I want, So that. Mỗi Request (RQ) là một dòng của Product Backlog và là gốc để "
        "bóc tách thành các user story chi tiết ở chương 4.")
c3.h2(1, "Bảng Product Backlog")
c3.para("Product Backlog có %d Request, sắp theo giá trị nghiệp vụ. Business Value và Độ "
        "ưu tiên cho biết hạng mục nào cần làm trước. Bảng 3.1 trình bày đầy đủ các Request "
        "kèm lý do (So that)." % len(RQS))
rows = [[r["id"], "Là %s" % r["role"], r["goal"], r["so_that"], r["priority"], r["bv"], "New"] for r in RQS]
c3.bang("Product Backlog cấp Request (RQ)",
        ["ID", "As a/an (vai trò)", "I want to (mục tiêu)", "So that (lý do)", "Ưu tiên", "Business Value", "State"],
        rows, [1.3, 2.4, 4.8, 4.2, 1.2, 1.7, 1.4])

# ---------------------------------------------------------- Chương 4
c4 = Chuong(doc, 4, "Quy trình làm việc và phân rã yêu cầu")
c4.para("Chương này mô tả cách nhóm biến Request của actor thành công việc cụ thể cho lập "
        "trình viên. Toàn bộ backlog được tổ chức theo ba cấp RQ, PB và Task, trình bày ở mục 4.2.")

c4.h2(1, "Quy trình làm việc của nhóm")
for i, s in enumerate([
    "Xác nhận vị trí và vai trò cho từng thành viên trong nhóm",
    "Thu thập Request từ actor (RQ) dưới dạng As a, I want, So that",
    "PO bóc tách mỗi RQ thành các Product Backlog (user story) và sắp thứ tự ưu tiên",
    "PO cùng nhóm chia mỗi PB thành các Task kèm ước tính giờ và người phụ trách",
    "Dev nhận Task và hoàn thành đúng thời hạn của Sprint",
    "SM theo dõi tiến độ và review chất lượng, QC test, PO nghiệm thu Done",
], 1):
    c4.so_thu_tu(s, i)

c4.h2(2, "Bảng phân rã ba cấp")
c4.para("Cấp 1 là RQ, yêu cầu đến từ actor. Cấp 2 là PB, do PO bóc tách thành user story, "
        "mỗi RQ có nhiều PB. Cấp 3 là Task, mỗi PB có nhiều Task để Dev thực hiện. Các bảng "
        "dưới đây trình bày đầy đủ %d Request, %d Product Backlog và %d Task."
        % (len(RQS), len(PBS), len(TASKS)))

for idx, r in enumerate(RQS, 1):
    c4.h3(2, idx, "%s: %s (actor: %s)" % (r["id"], r["ten"], r["role"]))
    c4.para("Là %s, tôi muốn %s, %s." % (r["role"], r["goal"], r["so_that"]))
    rows = []
    for pb in pbs_of(r["id"]):
        tks = tasks_of(pb["id"])
        for i, t in enumerate(tks):
            rows.append([pb["id"] if i == 0 else "", pb["user_story"] if i == 0 else "",
                         t["id"], t["task"], t["est"], t["who"]])
    c4.bang("Phân rã %s thành user story và task" % r["id"],
            ["PB", "User Story", "Task", "Nội dung task", "Giờ", "Phụ trách"],
            rows, [1.4, 4.6, 1.2, 5.0, 1.0, 1.6])

# ---------------------------------------------------------- Chương 5
doc.add_page_break()
c5 = Chuong(doc, 5, "Release Backlog và phân chia Sprint")
c5.para("Chương này xếp các user story vào 6 Sprint theo Release Backlog. Nguyên tắc xếp là "
        "làm nền tảng và bán hàng tại quầy (POS) trước làm khung chuẩn, mở rộng cửa hàng "
        "online cho khách ở Sprint sau, phân tích số liệu sau cùng.")

c5.h2(1, "Bảng Release Backlog")
c5.para("Release Backlog gom user story theo Backlog (chính là Request) và gán mỗi story "
        "vào một Sprint kèm Business Value. Bảng 5.1 là toàn bộ %d user story." % len(PBS))
rows = []
for r in RQS:
    for pb in pbs_of(r["id"]):
        rows.append([r["id"], r["ten"], pb["user_story"], pb["id"], pb["priority"], pb["bv"], "Sprint %d" % pb["sprint"]])
c5.bang("Release Backlog: user story theo Backlog và Sprint",
        ["Backlog ID", "Backlog", "User Story", "Story ID", "Ưu tiên", "Business Value", "Sprint"],
        rows, [1.4, 2.6, 5.2, 1.4, 1.2, 1.7, 1.5])

c5.h2(2, "Bảng phân chia Sprint")
rows = []
for sp, goal in SPRINTS:
    pbs = [p["id"] for p in PBS if p["sprint"] == sp]
    hrs = sum(t["est"] for t in TASKS if t["sprint"] == sp)
    rows.append(["Sprint %d" % sp, SPRINT_DATES[sp], goal, ", ".join(pbs), SPRINT_OWNER[sp], hrs])
c5.bang("Phân chia công việc theo Sprint",
        ["Sprint", "Thời gian", "Mục tiêu", "Product Backlog", "Phụ trách", "Giờ"],
        rows, [1.4, 3.2, 4.6, 3.6, 2.4, 1.0])

c5.h2(3, "Kiểm thử và dự phòng")
c5.bullet_head("Kiểm thử và nghiệm thu tổng thể")
c5.bullet_item("31/08/2026 đến 13/09/2026, do QC và PO thực hiện")
c5.bullet_head("Dự phòng sửa lỗi và hoàn thiện tài liệu")
c5.bullet_item("14/09/2026 đến 30/09/2026")

# ---------------------------------------------------------- Chương 6
c6 = Chuong(doc, 6, "Sơ đồ chức năng")
c6.para("Chương này trình bày cây chức năng của hệ thống theo dạng phân cấp, giúp nhìn nhanh "
        "phạm vi sản phẩm.")
c6.h2(1, "Cây chức năng phân cấp")
tree = [
    ("Đăng nhập và phân quyền theo từng nhân viên", [
        "Đăng nhập", "Lưới phân quyền nhân viên", "Vai trò làm bộ quyền mặc định"]),
    ("Bán hàng tại quầy (POS)", [
        "Tạo hóa đơn, giỏ hàng tự trừ kho, phiếu giảm giá",
        "Thanh toán tiền mặt, VietQR, kết hợp",
        "In hóa đơn, hủy đơn, quét QR"]),
    ("Quản lý sản phẩm", [
        "Sản phẩm, biến thể, nhập kho",
        "Danh mục và thuộc tính (tab trong màn sản phẩm)"]),
    ("Giao hàng", ["Tạo đơn giao kèm phí ship", "Đã giao", "Trả hàng và hoàn kho"]),
    ("Bảo hành", ["Tiếp nhận", "Xử lý theo trạng thái", "In phiếu"]),
    ("Đặt trước", ["Khách đăng ký khi hết hàng", "Nhân viên chuyển phiếu thành đơn"]),
    ("Cửa hàng online", ["Trang chủ", "Trang chi tiết sản phẩm", "Giỏ hàng và đặt hàng COD", "Tra cứu đơn"]),
    ("Khách hàng", ["Khách hàng", "Địa chỉ giao hàng"]),
    ("Nhân viên", ["Danh sách nhân viên", "Phân quyền", "Vai trò"]),
    ("Khuyến mãi", ["Phiếu giảm giá"]),
    ("Thống kê và phân tích", ["KPI", "Dòng tiền", "ROI", "Giữ chân khách", "Bán chạy và bán chậm", "Xuất Excel"]),
]
for nhanh, la in tree:
    c6.bullet_head(nhanh)
    for x in la:
        c6.bullet_item(x)

ket_luan(doc, [
    "Kế hoạch chia dự án BShoes thành %d Request từ actor, bóc tách tiếp thành %d Product "
    "Backlog và %d Task, tổng cộng %d giờ, trải trên 6 Sprint và kết thúc trước hạn %s."
    % (len(RQS), len(PBS), len(TASKS), TONG_GIO, DEADLINE),
    "Khung chuẩn là bán hàng tại quầy: Sprint 1 đến Sprint 3 dựng nền tảng, quản lý sản phẩm "
    "và POS. Cửa hàng online cho khách xem và mua hàng là phần mở rộng ở Sprint 5, phân tích "
    "số liệu ở Sprint 6.",
    "ID được chuẩn hóa ba cấp RQ, PB, Task và đánh số lại theo thứ tự Sprint, nên mỗi Task "
    "truy vết đủ Story ID, Backlog ID và Sprint, không còn lệch giữa task và story.",
])

f1 = os.path.join(OUTDIR, "WORKSHOP_1_BShoes.docx")
doc.save(f1)
print("OK ->", f1)


# =====================================================================
# WORKSHOP 2: Mô tả mục tiêu và ước lượng story
# =====================================================================
doc = setup(Document())
title(doc, "Workshop 2: Mô tả mục tiêu và ước lượng story", PROJECT + ", " + VERSION)

# ---------------------------------------------------------- Chương 1
c1 = Chuong(doc, 1, "Mục tiêu của Product Backlog và Sản phẩm")
c1.para("Chương này trả lời hai câu hỏi của workshop: product backlog để làm gì và sản phẩm "
        "cuối cùng hướng tới điều gì. Đây là căn cứ để chấm điểm ưu tiên ở các chương sau.")

c1.h2(1, "Mục tiêu của Product Backlog")
c1.para("Product backlog của BShoes là danh sách toàn bộ yêu cầu, tính năng và cải tiến cần "
        "thực hiện, được bóc tách từ %d Request của các actor thành %d user story và sắp xếp "
        "theo giá trị nghiệp vụ. Mục tiêu là giúp nhóm luôn nhìn rõ việc cần làm và tập trung "
        "trước vào hạng mục mang lại giá trị cao nhất cho cửa hàng, cụ thể là bán được hàng và "
        "trừ kho chính xác. Với một nhóm còn hạn chế về IT, backlog còn đóng vai trò bảng phân "
        "công minh bạch, tránh việc hai người cùng sửa một chỗ." % (len(RQS), len(PBS)))

c1.h2(2, "Mục tiêu của Sản phẩm")
c1.para("Xây dựng hệ thống quản lý và bán hàng cho cửa hàng giày BShoes, cho phép nhân viên "
        "bán tại quầy nhanh và chính xác, trong đó tồn kho tự trừ khi thêm hàng vào giỏ và tự "
        "hoàn khi hủy hoặc trả hàng. Hệ thống quản lý được sản phẩm, biến thể, tồn kho, khách "
        "hàng, bảo hành và giao hàng; phân quyền truy cập tới từng nhân viên; đồng thời cung "
        "cấp cho chủ cửa hàng bức tranh kinh doanh theo thời gian thực gồm doanh thu, lợi "
        "nhuận, ROI và tỷ lệ giữ chân khách hàng.")

# ---------------------------------------------------------- Chương 2
c2 = Chuong(doc, 2, "Mục tiêu của các Sprint")
c2.para("Chương này nêu mục tiêu từng Sprint kèm khối lượng quy đổi ra story point và giờ "
        "công, cho thấy công việc được rải đều hay dồn cục.")
c2.h2(1, "Bảng mục tiêu Sprint")
rows = []
for sp, goal in SPRINTS:
    pbs = [p for p in PBS if p["sprint"] == sp]
    pts = sum(fib((p["tuong_tac"] + p["quy_tac"] + p["thuc_the"] + p["thao_tac"]) * C_DEFAULT * ED_TOTAL / 36) for p in pbs)
    hrs = sum(t["est"] for t in TASKS if t["sprint"] == sp)
    rows.append(["Sprint %d" % sp, SPRINT_DATES[sp], goal, len(pbs), pts, hrs])
c2.bang("Mục tiêu và khối lượng từng Sprint",
        ["Sprint", "Thời gian", "Mục tiêu Sprint", "Số User Story", "Story Points", "Giờ"],
        rows, [1.5, 3.2, 5.6, 1.8, 1.8, 1.0])

# ---------------------------------------------------------- Chương 3
c3 = Chuong(doc, 3, "Phương pháp ước lượng")
c3.para("Chương này trình bày công thức ước lượng số lượng story mà nhóm áp dụng, đúng theo "
        "mẫu trong tài liệu môn học.")
c3.h2(1, "Công thức")
c3.bullet_head("UP, điểm chưa hiệu chỉnh")
c3.bullet_item("UP = Loại tương tác + Quy tắc nghiệp vụ + Số thực thể + Thao tác dữ liệu")
c3.bullet_item("Mỗi tiêu chí chấm từ 1 đến 3")
c3.bullet_head("AP, điểm đã hiệu chỉnh")
c3.bullet_item("AP = UP nhân C, trong đó C là hệ số nhân, nhóm dùng C = 1")
c3.bullet_head("PPS, điểm cho mỗi story")
c3.bullet_item("PPS = (AP nhân ED) chia 36")
c3.bullet_head("ED, điểm môi trường")
c3.bullet_item("Tổng 18 yếu tố, mỗi yếu tố chấm 0 hoặc 2, tối đa 36")

c3.h2(2, "Ghi chú về hệ số C")
c3.para("Tài liệu gốc không quy định giá trị cụ thể cho hệ số nhân C. Nhóm chọn C = 1, tức "
        "trung tính, và để C là một ô tham số trong file Excel để có thể điều chỉnh lại khi "
        "giảng viên yêu cầu mà không phải tính tay toàn bộ bảng.")

# ---------------------------------------------------------- Chương 4
c4 = Chuong(doc, 4, "Đánh giá môi trường ED của nhóm")
c4.para("Chương này chấm 18 yếu tố môi trường của nhóm và rút ra hệ số dùng cho công thức "
        "PPS. Kết quả cho thấy rõ nhóm mạnh về con người nhưng yếu về công nghệ.")

c4.h2(1, "Bảng chấm điểm 18 yếu tố")
c4.para("Nhóm đã từng hợp tác thành công với nhau và hợp tác rất tích cực, nhưng trình độ IT "
        "còn hạn chế và chưa có hạ tầng kiểm thử tự động hay CI. Điều này thể hiện rõ trong "
        "điểm ED: khía cạnh Nhóm và Qui trình đạt điểm cao, trong khi khía cạnh Công nghệ "
        "bằng 0. Bảng 4.1 là chi tiết từng yếu tố kèm lý do chấm.")
c4.bang("Chấm điểm 18 yếu tố môi trường",
        ["Khía cạnh", "Yếu tố", "Giá trị (0/2)", "Lý do"],
        [[a, f, v, w] for a, f, v, w in ED], [3.0, 6.6, 1.6, 5.0])

c4.h2(2, "Tổng hợp theo khía cạnh")
seen, srows = [], []
for a, _, _, _ in ED:
    if a in seen:
        continue
    seen.append(a)
    srows.append([a, sum(e[2] for e in ED if e[0] == a), 6])
srows.append(["TỔNG ED", ED_TOTAL, ED_MAX])
c4.bang("Tổng điểm môi trường theo khía cạnh", ["Khía cạnh", "Điểm", "Tối đa"], srows, [7.0, 2.5, 2.5])
c4.para("ED đạt %d trên %d, tương ứng hệ số môi trường %.2f. Do đó PPS bằng AP nhân %.2f, "
        "nghĩa là năng suất quy đổi của nhóm chỉ còn một nửa so với nhóm có môi trường lý "
        "tưởng. Con số này phản ánh đúng thực tế và là lý do nhóm dành hẳn hai tuần cuối để "
        "dự phòng." % (ED_TOTAL, ED_MAX, ED_TOTAL / ED_MAX, ED_TOTAL / ED_MAX))

# ---------------------------------------------------------- Chương 5
doc.add_page_break()
c5 = Chuong(doc, 5, "Bảng ước lượng từng User Story")
c5.para("Chương này áp công thức ở chương 3 với hệ số ED ở chương 4 cho toàn bộ %d user "
        "story, quy ra story point theo dãy Fibonacci." % len(PBS))
c5.h2(1, "Bảng ước lượng chi tiết")
rows = []
for pb in PBS:
    up = pb["tuong_tac"] + pb["quy_tac"] + pb["thuc_the"] + pb["thao_tac"]
    ap = up * C_DEFAULT
    pps = ap * ED_TOTAL / 36
    rows.append([pb["id"], pb["user_story"][:60], pb["tuong_tac"], pb["quy_tac"], pb["thuc_the"],
                 pb["thao_tac"], up, "%.1f" % ap, "%.2f" % pps, fib(pps)])
c5.bang("Ước lượng UP, AP, PPS và story point cho từng user story",
        ["ID", "User Story", "Tương tác", "Quy tắc", "Thực thể", "Thao tác", "UP", "AP", "PPS", "SP"],
        rows, [1.2, 5.4, 1.3, 1.2, 1.3, 1.3, 0.9, 0.9, 1.0, 0.8])

c5.h2(2, "Tổng hợp")
c5.bang("Tổng hợp khối lượng dự án", ["Chỉ số", "Giá trị"], [
    ["Tổng UP", sum(p["tuong_tac"] + p["quy_tac"] + p["thuc_the"] + p["thao_tac"] for p in PBS)],
    ["Tổng Story Point", TONG_SP],
    ["Tổng giờ task", "%d giờ" % TONG_GIO],
    ["Số user story", len(PBS)],
    ["Số task", len(TASKS)],
], [7.0, 5.0])

# ---------------------------------------------------------- Chương 6
doc.add_page_break()
c6 = Chuong(doc, 6, "Sprint Backlog theo từng Sprint")
c6.para("Chương này chia nhỏ Product Backlog thành Sprint Backlog cho từng Sprint. Mỗi bảng "
        "liệt kê Task của một Sprint kèm Product Backlog gốc, ước tính giờ và trạng thái, "
        "đúng mẫu Sprint Backlog của môn học.")
for sp, goal in SPRINTS:
    c6.h3(1, sp, "Sprint %d: %s" % (sp, goal))
    rows = [[t["id"], t["story"], t["task"], t["mo_ta"], t["est"], "To Do"]
            for t in TASKS if t["sprint"] == sp]
    c6.bang("Sprint Backlog Sprint %d" % sp,
            ["Task ID", "Product Backlog", "Task", "Mô tả công việc", "Estimate (giờ)", "Trạng thái"],
            rows, [1.2, 1.8, 3.6, 4.8, 1.6, 1.4])

ket_luan(doc, [
    "Product backlog của BShoes gồm %d user story, quy đổi được %d story point và %d giờ công. "
    "Hệ số môi trường ED bằng %.2f đã được nhân vào toàn bộ ước lượng, nên các con số này phản "
    "ánh năng suất thật của nhóm chứ không phải năng suất lý thuyết."
    % (len(PBS), TONG_SP, TONG_GIO, ED_TOTAL / ED_MAX),
    "Những story điểm cao nhất đều rơi vào nhóm trừ kho, thanh toán và phân quyền. Đây cũng là "
    "nơi nhóm bố trí task kiểm thử riêng, vì sai ở đây gây hậu quả trực tiếp lên tiền và hàng "
    "của cửa hàng.",
    "Sprint Backlog được tách theo từng Sprint và mỗi Task trỏ về đúng Product Backlog gốc, "
    "nên khối lượng mỗi Sprint minh bạch và không lệch với danh sách user story.",
])

f2 = os.path.join(OUTDIR, "WORKSHOP_2_MucTieu_BShoes.docx")
doc.save(f2)
print("OK ->", f2)
