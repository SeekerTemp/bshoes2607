# -*- coding: utf-8 -*-
"""
Sinh 2 file Word workshop theo format người dùng chốt ngày 2026-07-17.
Định dạng nằm ở docx_format.py; file này chỉ lo nội dung.

Chạy:  python gen_docx.py <thư mục xuất>
"""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from bshoes_data import *
from docx import Document
from docx_format import setup, title, Chuong, ket_luan, kiem_tra_gach_ngang

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
                4: "DEV1 + DEV3", 5: "DEV2 + DEV3", 6: "DEV1 + DEV2"}

TONG_GIO = sum(t[4] for t in TASKS)
TONG_SP = sum(fib((p[7] + p[8] + p[9] + p[10]) * C_DEFAULT * ED_TOTAL / 36) for p in PBS)


# =====================================================================
# WORKSHOP 1: Kế hoạch dự án
# =====================================================================
doc = setup(Document())
title(doc, "Kế hoạch dự án " + PROJECT, "WORKSHOP 1, phiên bản %s" % VERSION)

# ---------------------------------------------------------- Chương 1
c1 = Chuong(doc, 1, "Tổng quan dự án")
c1.para("Chương này giới thiệu bối cảnh dự án BShoes, công nghệ sử dụng và cách nhóm "
        "phân vai. Đây là căn cứ để hiểu vì sao kế hoạch ở các chương sau được chia nhỏ "
        "và ưu tiên kiểm thử.")

c1.h2(1, "Thông tin chung")
c1.para("BShoes là hệ thống quản lý và bán hàng cho cửa hàng giày, được chuyển từ ứng dụng "
        "desktop NetBeans sang nền web. Nhóm làm theo Scrum với 6 Sprint, mỗi Sprint 2 tuần, "
        "hạn chót %s. Bảng 1.1 tóm tắt các thông tin cốt lõi của dự án." % DEADLINE)
c1.bang("Thông tin chung của dự án", ["Mục", "Nội dung"], [
    ["Tên dự án", PROJECT],
    ["Phiên bản", VERSION],
    ["Khung làm việc", "Scrum, 6 Sprint, mỗi Sprint 2 tuần"],
    ["Công nghệ", "Spring Boot (JPA) + SQL Server; Vue 3 SPA; Chart.js"],
    ["Tổng khối lượng", "%d yêu cầu, %d product backlog, %d task, %d giờ"
                        % (len(REQS), len(PBS), len(TASKS), TONG_GIO)],
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
c2.para("Chương này liệt kê toàn bộ chức năng hệ thống cần có, nhóm theo nghiệp vụ. Danh "
        "sách là đầu vào để bóc tách thành product backlog ở chương 3.")

c2.h2(1, "Nhóm chức năng nghiệp vụ")
for txt, items in [
    ("Đăng nhập và phân quyền", [
        "Đăng nhập bằng tài khoản nhân viên",
        "Phân quyền truy cập màn hình cho từng nhân viên",
        "Vai trò đóng vai trò bộ quyền mặc định",
    ]),
    ("Bán hàng tại quầy (POS)", [
        "Tạo hóa đơn chờ, tối đa 3 hóa đơn cùng lúc",
        "Thêm hàng vào giỏ, hệ thống tự trừ kho",
        "Áp phiếu giảm giá, thanh toán tiền mặt, chuyển khoản hoặc kết hợp",
        "In hóa đơn, hủy đơn và hoàn kho, quét QR để thêm nhanh",
    ]),
    ("Quản lý sản phẩm", [
        "Sản phẩm, biến thể theo màu và size, nhập kho",
        "Danh mục và thuộc tính, gộp chung trong màn quản lý sản phẩm",
    ]),
    ("Giao hàng và bảo hành", [
        "Tạo đơn giao kèm phí ship, cập nhật đã giao",
        "Trả hàng và hoàn kho",
        "Tiếp nhận và xử lý bảo hành theo trạng thái, in phiếu",
    ]),
    ("Cửa hàng online cho khách", [
        "Trang chủ và trang chi tiết sản phẩm",
        "Giỏ hàng và đặt hàng giao tận nhà, thanh toán khi nhận",
        "Đặt trước mẫu đang hết hàng",
        "Tra cứu đơn theo số điện thoại và xác nhận đã nhận",
    ]),
    ("Quản trị và phân tích", [
        "Quản lý nhân viên, khách hàng và địa chỉ giao hàng",
        "Khuyến mãi bằng phiếu giảm giá",
        "Thống kê KPI, dòng tiền, ROI, giữ chân khách, xuất Excel",
    ]),
]:
    c2.bullet_head(txt)
    for it in items:
        c2.bullet_item(it)

# ---------------------------------------------------------- Chương 3
c3 = Chuong(doc, 3, "Quy trình làm việc và bảng phân rã yêu cầu")
c3.para("Chương này mô tả cách nhóm biến yêu cầu của actor thành công việc cụ thể cho lập "
        "trình viên. Toàn bộ backlog được tổ chức theo ba cấp, trình bày ở mục 3.2.")

c3.h2(1, "Quy trình làm việc của nhóm")
for i, s in enumerate([
    "Xác nhận vị trí và vai trò cho từng thành viên trong nhóm",
    "Thu thập yêu cầu từ actor (REQ) và liệt kê những công việc cần làm",
    "PO bóc tách mỗi REQ thành các product backlog (use case) và sắp thứ tự ưu tiên",
    "PO cùng nhóm chia mỗi product backlog thành các task kèm ước tính giờ và người phụ trách",
    "Dev nhận task và hoàn thành đúng thời hạn của Sprint",
    "SM theo dõi tiến độ và review chất lượng, QC test, PO nghiệm thu Done",
], 1):
    c3.so_thu_tu(s, i)

c3.h2(2, "Bảng phân rã ba cấp")
c3.para("Cấp 1 là REQ, tức yêu cầu đến từ actor. Cấp 2 là PB, do PO bóc tách thành use case, "
        "mỗi REQ có nhiều PB. Cấp 3 là TASK, mỗi PB có nhiều task để Dev thực hiện. Các bảng "
        "dưới đây trình bày đầy đủ %d yêu cầu, %d product backlog và %d task."
        % (len(REQS), len(PBS), len(TASKS)))

for idx, (rq, actor, desc) in enumerate(REQS, 1):
    c3.h3(2, idx, "%s. Actor: %s" % (rq, actor))
    c3.para(desc)
    rows = []
    for pb in [p for p in PBS if p[1] == rq]:
        tks = [t for t in TASKS if t[1] == pb[0]]
        for i, t in enumerate(tks):
            rows.append([pb[0] if i == 0 else "", pb[3] if i == 0 else "",
                         t[0], t[2], t[4], t[5]])
    c3.bang("Phân rã %s thành product backlog và task" % rq,
            ["PB", "User Story (use case)", "Task", "Nội dung task", "Giờ", "Phụ trách"],
            rows, [1.6, 5.2, 1.4, 5.2, 1.2, 1.8])

# ---------------------------------------------------------- Chương 4
doc.add_page_break()
c4 = Chuong(doc, 4, "Phân chia công việc theo Sprint")
c4.para("Chương này xếp các product backlog vào 6 Sprint, mỗi Sprint 2 tuần. Nguyên tắc xếp "
        "là làm nền tảng trước, nghiệp vụ bán hàng ở giữa, phân tích số liệu sau cùng, để "
        "tính năng sinh ra tiền được kiểm thử sớm nhất.")

c4.h2(1, "Bảng phân chia Sprint")
rows = []
for sp, goal in SPRINTS:
    pbs = [p[0] for p in PBS if p[6] == sp]
    hrs = sum(t[4] for t in TASKS if t[1] in pbs)
    rows.append(["Sprint %d" % sp, SPRINT_DATES[sp], goal, ", ".join(pbs), SPRINT_OWNER[sp], hrs])
c4.bang("Phân chia công việc theo Sprint",
        ["Sprint", "Thời gian", "Mục tiêu", "Product Backlog", "Phụ trách", "Giờ"],
        rows, [1.6, 3.4, 5.0, 3.6, 2.4, 1.0])

c4.h2(2, "Kiểm thử và dự phòng")
c4.bullet_head("Kiểm thử và nghiệm thu tổng thể")
c4.bullet_item("31/08/2026 đến 13/09/2026, do QC và PO thực hiện")
c4.bullet_head("Dự phòng sửa lỗi và hoàn thiện tài liệu")
c4.bullet_item("14/09/2026 đến 30/09/2026")

# ---------------------------------------------------------- Chương 5
c5 = Chuong(doc, 5, "Sơ đồ chức năng")
c5.para("Chương này trình bày cây chức năng của hệ thống theo dạng phân cấp, giúp nhìn nhanh "
        "phạm vi sản phẩm.")
c5.h2(1, "Cây chức năng phân cấp")
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
    c5.bullet_head(nhanh)
    for x in la:
        c5.bullet_item(x)

ket_luan(doc, [
    "Kế hoạch chia dự án BShoes thành %d yêu cầu từ actor, bóc tách tiếp thành %d product "
    "backlog và %d task, tổng cộng %d giờ, trải trên 6 Sprint và kết thúc trước hạn %s."
    % (len(REQS), len(PBS), len(TASKS), TONG_GIO, DEADLINE),
    "Cách chia này bám đúng điểm mạnh và điểm yếu của nhóm: các thành viên hợp tác tốt nhưng "
    "trình độ IT còn hạn chế, nên mỗi task được giữ ở mức nhỏ, có người phụ trách rõ ràng và "
    "luôn kèm task kiểm thử ở những phần dễ sai như trừ kho, thanh toán và phân quyền.",
    "Rủi ro lớn nhất là nhóm chưa có hạ tầng kiểm thử tự động và CI, thể hiện ở điểm môi "
    "trường ED chỉ đạt %d trên %d. Nhóm chấp nhận rủi ro này và bù lại bằng cách tăng thời "
    "gian kiểm thử thủ công cùng khoảng dự phòng hai tuần cuối." % (ED_TOTAL, ED_MAX),
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
        "thực hiện, được bóc tách từ %d yêu cầu của các actor thành %d hạng mục và sắp xếp "
        "theo giá trị nghiệp vụ. Mục tiêu là giúp nhóm luôn nhìn rõ việc cần làm và tập trung "
        "trước vào hạng mục mang lại giá trị cao nhất cho cửa hàng, cụ thể là bán được hàng và "
        "trừ kho chính xác. Với một nhóm còn hạn chế về IT, backlog còn đóng vai trò bảng phân "
        "công minh bạch, tránh việc hai người cùng sửa một chỗ." % (len(REQS), len(PBS)))

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
    pbs = [p[0] for p in PBS if p[6] == sp]
    pts = sum(fib((p[7] + p[8] + p[9] + p[10]) * C_DEFAULT * ED_TOTAL / 36) for p in PBS if p[6] == sp)
    hrs = sum(t[4] for t in TASKS if t[1] in pbs)
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
    up = pb[7] + pb[8] + pb[9] + pb[10]
    ap = up * C_DEFAULT
    pps = ap * ED_TOTAL / 36
    rows.append([pb[0], pb[3][:60], pb[7], pb[8], pb[9], pb[10], up, "%.1f" % ap, "%.2f" % pps, fib(pps)])
c5.bang("Ước lượng UP, AP, PPS và story point cho từng user story",
        ["ID", "User Story", "Tương tác", "Quy tắc", "Thực thể", "Thao tác", "UP", "AP", "PPS", "SP"],
        rows, [1.2, 5.4, 1.3, 1.2, 1.3, 1.3, 0.9, 0.9, 1.0, 0.8])

c5.h2(2, "Tổng hợp")
c5.bang("Tổng hợp khối lượng dự án", ["Chỉ số", "Giá trị"], [
    ["Tổng UP", sum(p[7] + p[8] + p[9] + p[10] for p in PBS)],
    ["Tổng Story Point", TONG_SP],
    ["Tổng giờ task", "%d giờ" % TONG_GIO],
    ["Số user story", len(PBS)],
    ["Số task", len(TASKS)],
], [7.0, 5.0])

ket_luan(doc, [
    "Product backlog của BShoes gồm %d user story, quy đổi được %d story point và %d giờ công. "
    "Hệ số môi trường ED bằng %.2f đã được nhân vào toàn bộ ước lượng, nên các con số này phản "
    "ánh năng suất thật của nhóm chứ không phải năng suất lý thuyết."
    % (len(PBS), TONG_SP, TONG_GIO, ED_TOTAL / ED_MAX),
    "Những story điểm cao nhất đều rơi vào nhóm trừ kho, thanh toán và phân quyền. Đây cũng là "
    "nơi nhóm bố trí task kiểm thử riêng, vì sai ở đây gây hậu quả trực tiếp lên tiền và hàng "
    "của cửa hàng.",
    "Nhóm giữ hệ số C bằng 1 và để ngỏ ở file Excel, nên khi giảng viên yêu cầu đổi C thì toàn "
    "bộ bảng ước lượng tự tính lại mà không phải sửa tay.",
])

f2 = os.path.join(OUTDIR, "WORKSHOP_2_MucTieu_BShoes.docx")
doc.save(f2)
print("OK ->", f2)
