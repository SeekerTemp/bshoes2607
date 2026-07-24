# -*- coding: utf-8 -*-
"""
gen_asm1.py
===========
Sinh MỘT file Word duy nhất, gộp Workshop 1 và Workshop 2 của dự án BShoes,
theo đúng khuôn định dạng ở docx_format.py, dữ liệu lấy từ ws_source.py
(nguồn sự thật duy nhất, không bịa số liệu).

Cấu trúc:
    Trang bìa (cover_page) + Mục lục (muc_luc)
    PHẦN A - WORKSHOP 1 (Nền tảng Agile/Scrum, Product Backlog, User Story,
             sơ đồ chức năng, Demo, kế hoạch Sprint, biên bản họp, nhật ký)
             : Chương 1..10
    PHẦN B - WORKSHOP 2 (Thành viên, Product Goal, Product Backlog Goal,
             thời lượng Sprint, mục tiêu từng Sprint, ước lượng, Sprint
             Backlog, biên bản họp Sprint Planning) : Chương 1..9 (nối tiếp)
    KẾT LUẬN

build_ws1() và build_ws2() viết nội dung từng Workshop, được đánh số chương
theo tham số base (base=0 -> 1..N). Hai hàm này được tái sử dụng nguyên vẹn
bởi gen_ws12.py để sinh hai file độc lập WORKSHOP_1_BShoes.docx và
WORKSHOP_2_MucTieu_BShoes.docx.

Chạy:
    cd doc-school/_generator
    PYTHONIOENCODING=utf-8 python gen_asm1.py ..
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from docx import Document
from docx.shared import Pt, RGBColor, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_LINE_SPACING
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn

from docx_format import setup, title, Chuong, ket_luan, kiem_tra_gach_ngang
from ws_source import (
    TEAM, PROJECT, WORK_ASSIGN, USECASES, RQS, RQ_BY_ID, PBS, STORY_SPRINT,
    SPRINTS, DEFENSE, TOTALS, TASKS, ED_TOTAL, ED_MAX, C_DEFAULT, ENV_FACTOR,
)

# ---------------------------------------------------------------------------
# Số liệu tổng hợp dùng nhiều nơi (an toàn khi import, không có side effect)
# ---------------------------------------------------------------------------

N_RQ = len(RQS)
N_NEW = sum(1 for r in RQS if r["state"] == "New")
N_REMOVED = sum(1 for r in RQS if r["state"] == "Removed")
N_PB = len(PBS)
N_SPRINT = len(SPRINTS)
N_TASK = len(TASKS)
TONG_GIO = sum(t["est"] for t in TASKS)
TONG_SP_NEW = sum(r["sp"] for r in RQS if r["state"] == "New")
DU_AN_BAT_DAU = SPRINTS[0]["start"]
DU_AN_KET_THUC = SPRINTS[-1]["end"]

DOI_TUONG_HOAN = "báo cáo, bảo hành, cửa hàng online, đặt trước"


def pb_stories_str(pb):
    return ", ".join(pb["stories"]) if pb["stories"] else ""


def sprint_label(s):
    return "Sprint %d" % s["num"]


def tasks_of_sprint(n):
    return [t for t in TASKS if t["sprint"] == n]


# ---------------------------------------------------------------------------
# Mức độ cấp thiết (map priority số -> nhãn chữ, dùng cho Product Backlog)
# ---------------------------------------------------------------------------

PRIORITY_LABEL = {1: "Cao", 2: "Trung bình", 3: "Thấp"}


def priority_label(p):
    return PRIORITY_LABEL.get(p, str(p))


def _decap(s):
    """Hạ chữ cái đầu (nếu có) để nối câu tự nhiên, giữ nguyên phần còn lại."""
    if not s:
        return s
    return s[:1].lower() + s[1:]


def user_story_text(r):
    """
    'Là <role>, tôi muốn <goal>, <so_that>.'
    role đã có sẵn tiền tố 'Là', goal đã có sẵn tiền tố 'Tôi muốn', so_that đã
    có sẵn tiền tố 'Để' trong dữ liệu gốc (ws1.3.1. Product Backlog), nên chỉ
    cần nối lại và hạ chữ đầu goal/so_that cho câu đọc tự nhiên.
    """
    return "%s, %s, %s." % (r["role"], _decap(r["goal"]), _decap(r["so_that"]))


# ---------------------------------------------------------------------------
# Tiêu chí nghiệm thu (Acceptance Criteria) ngắn gọn cho từng User Story,
# bám sát goal/so_that gốc của mỗi Request (không bỏ trống).
# ---------------------------------------------------------------------------

AC_BY_ID = {
    "RQ-1": "Nhập đúng tài khoản và mật khẩu hợp lệ thì đăng nhập thành công, vào đúng màn hình theo vai trò; sai thông tin thì báo lỗi và không cho vào hệ thống.",
    "RQ-2": "Thêm, sửa, xóa, tìm kiếm nhân viên hoạt động đúng; mỗi nhân viên được gán đúng một vai trò và danh sách cập nhật ngay sau thao tác.",
    "RQ-3": "Mỗi vai trò có một bộ quyền mặc định lưu được; khi tạo mới hoặc đổi vai trò cho nhân viên, hệ thống tự áp đúng bộ quyền mặc định của vai trò đó.",
    "RQ-4": "Có thể bật tắt quyền riêng cho từng nhân viên ngoài quyền mặc định của vai trò; quyền tùy chỉnh có hiệu lực ngay ở lần đăng nhập tiếp theo.",
    "RQ-5": "Thêm, sửa sản phẩm lưu đúng dữ liệu; xóa mềm chuyển sản phẩm vào thùng rác thay vì xóa hẳn; khôi phục từ thùng rác trả sản phẩm về danh sách hoạt động.",
    "RQ-6": "Mỗi biến thể xác định đúng theo màu và size, có giá bán, giá nhập và trạng thái bán riêng; cập nhật một biến thể không ảnh hưởng biến thể khác.",
    "RQ-7": "Thêm, sửa, xóa danh mục hoạt động đúng; một sản phẩm gán được vào danh mục và hiển thị đúng khi lọc theo danh mục.",
    "RQ-8": "Thêm, sửa, xóa nhóm thuộc tính hoạt động đúng; lọc sản phẩm theo từng loại thuộc tính trả về đúng kết quả.",
    "RQ-9": "Trên cùng một màn hình có thể thao tác sản phẩm, danh mục và thuộc tính mà không cần chuyển trang; thay đổi ở phần nào phản ánh ngay ở phần liên quan.",
    "RQ-10": "Chọn đúng biến thể sản phẩm và nhập số lượng thì tồn kho tăng đúng bằng số lượng nhập; lịch sử nhập kho được ghi lại.",
    "RQ-11": "Tạo được hóa đơn chờ để lưu tạm giao dịch đang bán; hóa đơn chờ có thể mở lại để tiếp tục thao tác trước khi thanh toán.",
    "RQ-12": "Thêm sản phẩm vào giỏ thì tồn kho biến thể tương ứng bị trừ tạm ngay và tổng tiền hóa đơn được tính lại chính xác.",
    "RQ-13": "Nhập đúng mã và đủ điều kiện thì giá trị giảm được tính đúng theo điều kiện của phiếu; không đủ điều kiện thì báo lỗi và không áp dụng.",
    "RQ-14": "Chọn tiền mặt, chuyển khoản hoặc kết hợp đều hoàn tất được giao dịch khi đủ số tiền; lịch sử thanh toán lưu đúng hình thức và số tiền đã dùng.",
    "RQ-15": "In được hóa đơn hoặc phiếu tạm tính với đầy đủ sản phẩm, số lượng, đơn giá và tổng tiền khớp với hóa đơn trên hệ thống.",
    "RQ-16": "Hủy hóa đơn thì tồn kho các sản phẩm trong hóa đơn được hoàn trả đúng số lượng; lịch sử hủy hóa đơn ghi lại thời điểm và người thực hiện.",
    "RQ-17": "Quét đúng mã QR hoặc mã vạch thì sản phẩm tương ứng được thêm ngay vào hóa đơn mà không cần tìm kiếm thủ công.",
    "RQ-18": "Nhập nhanh tên và số điện thoại thì khách hàng mới được tạo và gắn ngay vào hóa đơn đang bán mà không phải rời màn hình bán hàng.",
    "RQ-19": "Tạo đơn giao hàng thì hóa đơn được cộng thêm phí vận chuyển và chuyển đúng sang trạng thái chờ giao.",
    "RQ-20": "Cập nhật trạng thái đã giao thì đơn chuyển đúng sang đã giao thành công và không cho cập nhật lặp lại trên đơn đã hoàn tất.",
    "RQ-21": "Thêm, sửa, xóa phiếu giảm giá hoạt động đúng; điều kiện áp dụng (thời gian, giá trị) được kiểm tra đúng khi phiếu được sử dụng.",
    "RQ-22": "Trả hàng thì tồn kho sản phẩm trả được hoàn lại đúng số lượng; lịch sử trả hàng ghi rõ hóa đơn gốc và lý do trả.",
    "RQ-23": "Thêm, sửa, xóa, tìm kiếm khách hàng hoạt động đúng; tìm kiếm theo tên hoặc số điện thoại trả về đúng khách hàng cần tìm.",
    "RQ-24": "Một khách hàng lưu được nhiều địa chỉ giao hàng; chọn đúng địa chỉ khi tạo đơn giao hàng cho khách đó.",
    "RQ-25": "Màn hình KPI hiển thị đúng doanh thu, lợi nhuận và số đơn hàng theo khoảng thời gian được chọn.",
    "RQ-26": "Biểu đồ hiển thị đúng doanh thu, giá vốn và lợi nhuận theo từng tháng trong năm được chọn.",
    "RQ-27": "Danh sách ROI hiển thị đúng theo từng sản phẩm, sắp xếp được theo hiệu quả kinh doanh cao thấp.",
    "RQ-28": "Chỉ số hiển thị đúng tỷ lệ khách hàng mới và khách hàng quay lại theo khoảng thời gian được chọn.",
    "RQ-29": "Danh sách sản phẩm bán chạy, bán chậm và xu hướng tiêu thụ được xếp hạng đúng theo số liệu bán hàng thực tế.",
    "RQ-30": "Xuất báo cáo ra file Excel đúng định dạng, dữ liệu trong file khớp với dữ liệu đang hiển thị trên màn hình báo cáo.",
    "RQ-31": "Tạo phiếu bảo hành gắn đúng khách hàng, sản phẩm và hóa đơn gốc; phiếu bảo hành mới ở đúng trạng thái tiếp nhận.",
    "RQ-32": "Cập nhật trạng thái bảo hành theo đúng quy trình xử lý; trạng thái mới nhất hiển thị đúng khi tra cứu phiếu bảo hành.",
    "RQ-33": "In được phiếu bảo hành với đầy đủ thông tin khách hàng, sản phẩm và thời hạn bảo hành khớp với phiếu trên hệ thống.",
    "RQ-34": "Trang chi tiết hiển thị đúng các lựa chọn màu, kích cỡ; hết hàng thì cho đặt trước thay vì cho mua ngay.",
    "RQ-35": "Trang chủ hiển thị sản phẩm nổi bật và ô tìm kiếm hoạt động đúng, trả về đúng sản phẩm phù hợp với từ khóa.",
    "RQ-36": "Thêm sản phẩm vào giỏ hàng online thì số lượng và tổng tiền tạm tính cập nhật đúng, chỉnh sửa số lượng trong giỏ được ngay.",
    "RQ-37": "Đặt hàng thành công tạo đúng đơn hàng gắn với địa chỉ khách chọn; khách nhận được xác nhận đơn đã đặt.",
    "RQ-38": "Khách hàng xem được đúng trạng thái đơn hàng của mình theo thời gian thực và xác nhận đã nhận hàng khi đơn giao tới.",
    "RQ-39": "Sản phẩm hết hàng cho đặt trước thành công; khi có hàng trở lại, khách đặt trước được ưu tiên thông báo.",
    "RQ-40": "Danh sách phiếu đặt trước hiển thị đúng; khi có hàng, chuyển phiếu đặt trước thành hóa đơn và thông báo cho khách đúng thời điểm.",
}


def ac_for(r):
    return AC_BY_ID.get(r["id"], "Thao tác thực hiện đúng %s và dữ liệu được lưu chính xác." % _decap(r["goal"]))


# ---------------------------------------------------------------------------
# Chức năng của mỗi vai trò (dùng cho bảng "Phân vai nhóm", chương 2)
# ---------------------------------------------------------------------------

ROLE_FUNC = {
    "PO": "Thu thập và tổng hợp yêu cầu nghiệp vụ, quản lý Product Backlog, xác định độ ưu tiên và Business Value, làm đầu mối trao đổi với giảng viên.",
    "SM": "Tổ chức Sprint Planning, Daily Scrum, Sprint Review, Sprint Retrospective; theo dõi tiến độ và gỡ vướng mắc cho nhóm trong từng Sprint.",
    "DEV": "Phân tích, thiết kế, lập trình và kiểm thử các chức năng được phân công trong từng Sprint, báo cáo tiến độ cho SM.",
    "Mentor": "Giám sát chung, định hướng nghiệp vụ và góp ý kỹ thuật xuyên suốt dự án, không trực tiếp thực hiện Task.",
}


def func_for(role):
    return ROLE_FUNC.get(role, "Tham gia thực hiện các đầu việc được nhóm phân công.")


ROLE_DISPLAY = {"PO": "Product Owner (PO)", "SM": "Scrum Master (SM)", "DEV": "Developer (DEV)"}
_ROLE_ORDER = {"PO": 0, "SM": 1, "DEV": 2}
CORE_TEAM = sorted(
    [m for m in TEAM if m["role"] in ("PO", "SM", "DEV")],
    key=lambda m: (_ROLE_ORDER.get(m["role"], 9), m["name"]),
)


def member_label(m):
    return "%s (%s)" % (m["name"], m["role"])


# ---------------------------------------------------------------------------
# Biên bản 3 buổi họp (chương "Biên bản nhận xét các thành viên buổi họp")
# ---------------------------------------------------------------------------

MEETINGS = [
    {
        "date": "01/07/2026",
        "content": (
            "Họp khởi động dự án, thống nhất đề tài BShoes, thảo luận mục tiêu sản phẩm "
            "và phân vai nhóm giữa PO, SM và hai DEV."
        ),
        "rows": [
            ["Nguyễn Đình Dũng (PO)", "Tốt", "Đề xuất đề tài, phác thảo phạm vi sản phẩm", "Chủ động, đề xuất hướng đi rõ ràng", "Chuẩn bị kỹ, dẫn dắt buổi họp tốt", "9"],
            ["Hoàng Lê Bảo Ngọc (SM)", "Tốt", "Chuẩn bị lịch họp và checklist quy trình Scrum", "Đóng góp về cách tổ chức các sự kiện Scrum", "Nắm rõ vai trò Scrum Master", "9"],
            ["Lưu Đình Bắc (DEV)", "Khá", "Tìm hiểu Java Spring Boot và Vue SPA", "Góp ý về khả năng đáp ứng kỹ thuật", "Tích cực, cần chuẩn bị kỹ hơn về kiến trúc hệ thống", "8"],
            ["Đỗ Anh Vũ (DEV)", "Khá", "Tìm hiểu SQL Server và mô hình dữ liệu bán hàng", "Góp ý về cấu trúc dữ liệu sản phẩm, biến thể", "Tích cực tham gia thảo luận", "8"],
        ],
    },
    {
        "date": "08/07/2026",
        "content": (
            "Rà soát và chốt Product Backlog gồm 40 Request thu thập từ các actor, phân "
            "nhóm thành 13 Product Backlog theo chức năng, xác định mức ưu tiên và "
            "Business Value cho từng Request."
        ),
        "rows": [
            ["Nguyễn Đình Dũng (PO)", "Tốt", "Tổng hợp đầy đủ 40 Request kèm ưu tiên", "Đề xuất cách nhóm Request thành Product Backlog", "Nắm chắc nghiệp vụ bán hàng tại quầy", "9"],
            ["Hoàng Lê Bảo Ngọc (SM)", "Tốt", "Soát lại tính nhất quán của bảng ưu tiên", "Đề xuất tách các Request rủi ro cao ra khỏi phạm vi 6 Sprint", "Phản biện tốt, giúp thu hẹp phạm vi hợp lý", "9"],
            ["Lưu Đình Bắc (DEV)", "Khá", "Ước lượng sơ bộ độ khó kỹ thuật từng nhóm chức năng", "Góp ý về nhóm Bán hàng cần ưu tiên cao nhất", "Đóng góp thực tế, đúng trọng tâm", "8"],
            ["Đỗ Anh Vũ (DEV)", "Tốt", "Rà soát nhóm chức năng Sản phẩm, Danh mục, Sau bán hàng", "Đề xuất gộp thao tác quản lý trên cùng một màn hình", "Chuẩn bị chu đáo, đề xuất khả thi", "9"],
        ],
    },
    {
        "date": "15/07/2026",
        "content": (
            "Sprint Planning: chốt kế hoạch 6 Sprint, phân bổ 24 Request trạng thái New "
            "vào từng Sprint, thống nhất chuyển 16 Request còn lại sang trạng thái "
            "Removed và thống nhất Sprint Backlog chi tiết."
        ),
        "rows": [
            ["Nguyễn Đình Dũng (PO)", "Tốt", "Chuẩn bị bảng phân bổ Request theo Sprint", "Bảo vệ thứ tự ưu tiên đã chốt ở buổi họp trước", "Điều phối buổi họp hiệu quả", "9"],
            ["Hoàng Lê Bảo Ngọc (SM)", "Tốt", "Chuẩn bị mẫu Sprint Backlog và bảng ước lượng ED/C", "Đề xuất chốt ED = 18/36 và C = 1 cho toàn dự án", "Am hiểu công thức ước lượng, trình bày rõ ràng", "9"],
            ["Lưu Đình Bắc (DEV)", "Tốt", "Phân rã Task kỹ thuật cho nhóm Bán hàng, Tài khoản", "Góp ý thứ tự làm Task trong từng Sprint", "Chuẩn bị đầy đủ, bám sát Product Backlog", "9"],
            ["Đỗ Anh Vũ (DEV)", "Khá", "Phân rã Task kỹ thuật cho nhóm Sản phẩm, Báo cáo", "Góp ý về khối lượng Task ở Sprint 5, 6", "Tích cực, cần rõ hơn về ước lượng giờ công", "8"],
        ],
    },
]

# ---------------------------------------------------------------------------
# Nhật ký cập nhật (chương cuối Phần A)
# ---------------------------------------------------------------------------

UPDATE_LOG = [
    (
        "ID Request và Product Backlog chưa thống nhất giữa các bảng trong tài liệu.",
        "Chuẩn hóa lại theo đúng file Excel nguồn: mã RQ-x là ID cấp Product Backlog "
        "(Request gốc), mã PB-x là ID cấp Release Backlog (nhóm chức năng); dùng thống "
        "nhất xuyên suốt toàn bộ chương và bảng biểu.",
    ),
    (
        "Bảng User Story chưa có tiêu chí nghiệm thu, khó xác định khi nào một Story "
        "được xem là hoàn thành.",
        "Bổ sung cột Tiêu chí nghiệm thu (Acceptance Criteria) cho toàn bộ 40 User Story, "
        "viết ngắn gọn, bám theo Nhu cầu chức năng và Lý do của từng Request.",
    ),
    (
        "Hệ số môi trường ED và hệ số nhân C dùng để tính Story Point chưa được chốt "
        "rõ ràng.",
        "Thống nhất ED = %d/%d (bằng %.1f) và C = %d, áp dụng cố định cho công thức UP, "
        "AP, PPS trên toàn bộ Product Backlog." % (ED_TOTAL, ED_MAX, ENV_FACTOR, C_DEFAULT),
    ),
    (
        "Phạm vi 40 Request quá lớn so với năng lực thực tế của 4 thành viên trong 6 "
        "Sprint.",
        "Tách 16 Request thuộc nhóm báo cáo, bảo hành, cửa hàng online và đặt trước sang "
        "trạng thái Removed, chỉ giữ lại 24 Request New tập trung cho phần lõi bán hàng "
        "tại quầy.",
    ),
    (
        "Tài liệu thiếu phần giới thiệu chương trình Demo và biên bản làm việc nhóm.",
        "Bổ sung chương Giới thiệu vắn tắt về chương trình Demo và chương Biên bản nhận "
        "xét các thành viên buổi họp với 3 buổi họp trong tháng 7/2026.",
    ),
]


# ===========================================================================
# Trang bìa và Mục lục (dùng python-docx trực tiếp, canh giữa)
# ===========================================================================

_COVER_FONT = "Times New Roman"
_COVER_GREEN = RGBColor(0x0B, 0x89, 0x5A)
_COVER_SPACING = 1.3


def _cover_run(run, size=Pt(13), bold=False, italic=False, color=None):
    run.font.name = _COVER_FONT
    run.font.size = size
    run._element.rPr.rFonts.set(qn("w:eastAsia"), _COVER_FONT)
    run.font.bold = bold
    run.font.italic = italic
    if color is not None:
        run.font.color.rgb = color
    return run


def _cover_para(doc, text, size=Pt(13), bold=False, italic=False, color=None, space_after=6):
    kiem_tra_gach_ngang(text)
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    p.paragraph_format.line_spacing = _COVER_SPACING
    p.paragraph_format.space_after = Pt(space_after)
    _cover_run(p.add_run(text), size=size, bold=bold, italic=italic, color=color)
    return p


def cover_page(doc, dong_bao_cao, dong_workshop):
    """
    Trang bìa: trường/khoa, dòng báo cáo + tên workshop, môn học, dự án, bảng
    thành viên nhóm, tên nhóm, tháng năm. Trang mới bắt đầu bằng page break
    ở cuối để nội dung chương 1 luôn nằm ở trang riêng.
    """
    _cover_para(doc, "TRƯỜNG ĐẠI HỌC", size=Pt(15), bold=True, color=_COVER_GREEN)
    _cover_para(doc, "KHOA CÔNG NGHỆ THÔNG TIN", size=Pt(14), bold=True, color=_COVER_GREEN)
    doc.add_paragraph()
    doc.add_paragraph()

    _cover_para(doc, dong_bao_cao.upper(), size=Pt(20), bold=True, color=_COVER_GREEN)
    _cover_para(doc, dong_workshop.upper(), size=Pt(17), bold=True, color=_COVER_GREEN)
    _cover_para(doc, "MÔN HỌC: AGILE & SCRUM", size=Pt(14), bold=True)
    doc.add_paragraph()

    _cover_para(doc, "DỰ ÁN", size=Pt(14), bold=True)
    _cover_para(doc, PROJECT["name"], size=Pt(14), bold=True, italic=True)
    doc.add_paragraph()
    doc.add_paragraph()

    headers = ["STT", "Họ và tên", "Vai trò"]
    t = doc.add_table(rows=1, cols=len(headers))
    t.style = "Table Grid"
    t.alignment = WD_TABLE_ALIGNMENT.CENTER
    for i, h in enumerate(headers):
        c = t.rows[0].cells[i]
        c.text = ""
        c.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.CENTER
        _cover_run(c.paragraphs[0].add_run(h), bold=True, color=_COVER_GREEN)
    for i, m in enumerate(CORE_TEAM, start=1):
        kiem_tra_gach_ngang(m["name"])
        cells = t.add_row().cells
        for j, v in enumerate([str(i), m["name"], ROLE_DISPLAY.get(m["role"], m["role"])]):
            cells[j].text = ""
            cells[j].paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.CENTER
            _cover_run(cells[j].paragraphs[0].add_run(v))
    doc.add_paragraph()
    doc.add_paragraph()

    _cover_para(doc, "NHÓM 1", size=Pt(14), bold=True)
    _cover_para(doc, "Tháng 7 / 2026", size=Pt(13), italic=True)
    doc.add_page_break()


def muc_luc(doc, sections):
    """sections: list các cặp (nhãn phần, [mục con,...])."""
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    _cover_run(p.add_run("MỤC LỤC"), size=Pt(16), bold=True, color=_COVER_GREEN)
    doc.add_paragraph()
    for label, bullets in sections:
        kiem_tra_gach_ngang(label)
        lp = doc.add_paragraph()
        lp.paragraph_format.space_after = Pt(4)
        _cover_run(lp.add_run(label), size=Pt(13), bold=True, color=_COVER_GREEN)
        for b in bullets:
            kiem_tra_gach_ngang(b)
            bp = doc.add_paragraph()
            bp.paragraph_format.left_indent = Cm(0.8)
            bp.paragraph_format.space_after = Pt(2)
            _cover_run(bp.add_run("- " + b))
    doc.add_page_break()


# ===========================================================================
# WORKSHOP 1 - NỀN TẢNG AGILE/SCRUM, PRODUCT BACKLOG (Chương base+1..base+10)
# ===========================================================================

def build_ws1(doc, base=0, partb_ref="Workshop 2"):
    """
    Viết Chương base+1..base+10 (nội dung Workshop 1) vào doc.

    partb_ref: cụm từ dùng để tham chiếu tới nơi trình bày Sprint Backlog
    chi tiết (số chương cụ thể khi gộp trong asm1.docx, hoặc "Workshop 2"
    khi đứng một mình).
    """
    ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10 = (base + i for i in range(1, 11))

    # --------------------------------------------------------- Chương 1
    c1 = Chuong(doc, ch1, "Giới thiệu dự án")
    c1.para(
        "Chương này giới thiệu nền tảng lý thuyết Agile và Scrum mà nhóm áp dụng, sau "
        "đó giới thiệu tổng quan dự án BShoes: sản phẩm hướng tới, công nghệ sử dụng, "
        "mô hình phát triển và khung thời gian thực hiện. Đây là nền tảng để hiểu vì "
        "sao các chương sau chia công việc theo Request, User Story và Sprint như đã "
        "trình bày."
    )

    c1.h2(1, "Agile là gì?")
    c1.para(
        "Agile là cách tiếp cận phát triển phần mềm theo hướng linh hoạt: chia dự án "
        "lớn thành nhiều phần việc nhỏ, phát triển và đánh giá lặp lại theo từng chu kỳ "
        "ngắn gọi là Sprint, nhờ đó nhóm dễ thích ứng khi yêu cầu thay đổi thay vì phải "
        "lập kế hoạch chi tiết cho toàn bộ dự án ngay từ đầu."
    )
    c1.h3(1, 1, "Đặc điểm của Agile")
    c1.bullet_item("Triển khai theo từng giai đoạn ngắn, gọi là Sprint hoặc Iteration.")
    c1.bullet_item("Trao đổi thường xuyên với khách hàng hoặc giảng viên để nhận phản hồi sớm.")
    c1.bullet_item("Chấp nhận và thích ứng với thay đổi yêu cầu trong suốt quá trình phát triển.")
    c1.bullet_item("Ưu tiên tạo ra sản phẩm dùng được ngay sau mỗi chu kỳ, thay vì chờ hoàn thiện toàn bộ.")
    c1.bullet_item("Đề cao sự hợp tác chặt chẽ giữa các thành viên trong nhóm.")
    c1.h3(1, 2, "4 giá trị cốt lõi của Agile Manifesto (2001)")
    c1.so_thu_tu("Con người và sự tương tác được coi trọng hơn quy trình và công cụ.", 1)
    c1.so_thu_tu("Sản phẩm chạy tốt được coi trọng hơn tài liệu trình bày đầy đủ.", 2)
    c1.so_thu_tu("Hợp tác với khách hàng được coi trọng hơn đàm phán hợp đồng.", 3)
    c1.so_thu_tu("Phản hồi và thích ứng với thay đổi được coi trọng hơn bám sát kế hoạch ban đầu.", 4)

    c1.h2(2, "Scrum là gì?")
    c1.para(
        "Scrum là khung làm việc (framework) phổ biến nhất để áp dụng Agile vào thực "
        "tế, quy định rõ các vai trò, sự kiện và quy tắc cụ thể giúp nhóm phát triển "
        "vận hành hiệu quả. Nói cách khác, Agile là tư duy chung, còn Scrum là cách cụ "
        "thể hóa tư duy đó thành quy trình làm việc hằng ngày."
    )
    c1.h3(2, 1, "Các vai trò trong Scrum")
    c1.bullet_item("Product Owner (PO): đại diện phía khách hàng và giảng viên, quản lý Product Backlog, quyết định thứ tự ưu tiên và tính năng cần làm.")
    c1.bullet_item("Scrum Master (SM): dẫn dắt nhóm tuân thủ đúng quy trình Scrum, gỡ bỏ vướng mắc và tổ chức các sự kiện Scrum.")
    c1.bullet_item("Development Team (Dev): trực tiếp phân tích, thiết kế, lập trình và kiểm thử để hoàn thành mục tiêu từng Sprint.")
    c1.h3(2, 2, "Các sự kiện trong Scrum")
    c1.so_thu_tu("Sprint: khoảng thời gian cố định, thường từ 1 đến 4 tuần, để phát triển một phần sản phẩm có thể sử dụng được.", 1)
    c1.so_thu_tu("Sprint Planning: chọn công việc từ Product Backlog, xác định Sprint Goal và lập Sprint Backlog cho Sprint sắp tới.", 2)
    c1.so_thu_tu("Daily Scrum: họp ngắn khoảng 15 phút mỗi ngày để cập nhật việc đã làm, việc sẽ làm và khó khăn gặp phải.", 3)
    c1.so_thu_tu("Sprint Review: trình diễn sản phẩm đã hoàn thành trong Sprint, thu thập phản hồi để điều chỉnh Product Backlog.", 4)
    c1.so_thu_tu("Sprint Retrospective: nhìn lại Sprint vừa qua để rút kinh nghiệm, cải thiện cách làm việc cho Sprint tiếp theo.", 5)

    c1.h2(3, "Giới thiệu dự án BShoes")
    c1.para(
        "BShoes là phần mềm quản lý cửa hàng giày, xây dựng theo Scrum với công nghệ %s. "
        "Nhóm dùng chu kỳ %s, thực hiện trong khoảng thời gian %s, với %s tham gia trực "
        "tiếp. Bảng 1.1 tổng hợp các thông tin cốt lõi này để tiện tra cứu."
        % (PROJECT["tech"], PROJECT["sprint_duration"], PROJECT["time"], PROJECT["team_size"])
    )
    c1.bang(
        "Thông tin chung của dự án",
        ["Mục", "Nội dung"],
        [
            ["Tên dự án", PROJECT["name"]],
            ["Framework", PROJECT["framework"]],
            ["Sprint Duration", PROJECT["sprint_duration"]],
            ["Thời gian dự án", PROJECT["time"]],
            ["Cơ sở dữ liệu", PROJECT["database"]],
            ["Công nghệ", PROJECT["tech"]],
            ["Quy mô nhóm", PROJECT["team_size"]],
        ],
        [4.5, 11.5],
    )
    c1.para(
        "Như vậy, BShoes là một dự án quy mô nhỏ, dùng Java Spring Boot và Vue SPA trên "
        "nền SQL Server, triển khai theo Scrum với chu kỳ Sprint ngắn một tuần để nhóm kịp "
        "thích ứng và điều chỉnh sau mỗi vòng lặp. Đây là căn cứ đầu vào cho việc phân vai "
        "và lập kế hoạch ở các chương tiếp theo."
    )

    # --------------------------------------------------------- Chương 2
    c2 = Chuong(doc, ch2, "Phân vai nhóm")
    c2.para(
        "Chương này trình bày cơ cấu nhân sự của nhóm thực hiện dự án BShoes, gồm vai "
        "trò, người phụ trách, thông tin liên hệ và chức năng cụ thể của từng người. "
        "Việc phân vai rõ ràng giúp trách nhiệm không chồng chéo và là cơ sở cho bảng "
        "phân công công việc ở chương %d." % ch3
    )
    c2.para(
        "Nhóm gồm %s: một Product Owner (PO) phụ trách nghiệp vụ và yêu cầu, một Scrum "
        "Master (SM) theo dõi tiến độ và quy trình, hai lập trình viên (DEV) trực tiếp "
        "xây dựng sản phẩm. Bảng 2.1 liệt kê đầy đủ vai trò, họ tên, handle, số điện "
        "thoại liên hệ và chức năng của từng thành viên." % PROJECT["team_size"]
    )
    c2.bang(
        "Danh sách thành viên nhóm",
        ["Vai trò", "Họ tên", "Handle", "SĐT", "Chức năng của mỗi người"],
        [[m["role"], m["name"], m["handle"], m["phone"], func_for(m["role"])] for m in TEAM],
        [1.6, 3.0, 2.2, 2.2, 7.5],
    )
    c2.para(
        "Ngoài bốn thành viên trực tiếp làm sản phẩm, dự án còn có một Mentor giám sát "
        "chung, định hướng nghiệp vụ và góp ý kỹ thuật xuyên suốt các Sprint mà không "
        "trực tiếp thực hiện task. Cơ cấu này gọn nhẹ, phù hợp với quy mô nhóm bốn người "
        "và tốc độ Sprint một tuần."
    )

    # --------------------------------------------------------- Chương 3
    c3 = Chuong(doc, ch3, "Bảng phân công công việc xuyên suốt dự án")
    c3.para(
        "Chương này mô tả việc phân công các đầu việc quản trị dự án cho từng vai trò "
        "PO, SM, Team và PM, từ lúc tập hợp yêu cầu đến lúc bàn giao và bảo hành. Đây là "
        "khung trách nhiệm chung áp dụng cho toàn bộ vòng đời dự án, khác với phân công "
        "task kỹ thuật theo Sprint ở %s." % partb_ref
    )
    c3.para(
        "Nhóm liệt kê %d đầu việc quản trị dự án và đánh dấu x vào vai trò phụ trách "
        "chính. PO đảm nhiệm phần lớn công việc liên quan tới yêu cầu và kế hoạch, SM "
        "phụ trách báo cáo và bàn giao, còn Team lo phần kiểm thử, ghi nhật ký và bảo "
        "hành. Bảng 3.1 trình bày chi tiết." % len(WORK_ASSIGN)
    )
    rows3 = [
        [
            w["stt"],
            w["task"],
            "x" if w["po"] else "",
            "x" if w["sm"] else "",
            "x" if w["team"] else "",
            "x" if w["pm"] else "",
        ]
        for w in WORK_ASSIGN
    ]
    c3.bang(
        "Phân công công việc quản trị dự án",
        ["STT", "Công việc", "PO", "SM", "Team", "PM"],
        rows3,
        [1.0, 8.5, 1.5, 1.5, 1.5, 1.5],
    )
    c3.para(
        "Bảng phân công cho thấy PO là đầu mối chính từ giai đoạn thu thập yêu cầu tới "
        "triển khai, trong khi SM giữ vai trò tổng kết và bàn giao. Vai trò PM không xuất "
        "hiện riêng vì nhóm quy mô nhỏ, các việc mang tính quản lý dự án được PO và SM "
        "chia nhau đảm nhiệm."
    )

    # --------------------------------------------------------- Chương 4
    c4 = Chuong(doc, ch4, "Product Backlog")
    c4.para(
        "Chương này trình bày Product Backlog của BShoes ở hai cấp: cấp Request, tức "
        "yêu cầu gốc thu thập từ các vai actor khác nhau viết theo mẫu As a, I want, So "
        "that; và cấp Release Backlog, tức các Request đã được PO gom thành nhóm chức "
        "năng để tiện lập kế hoạch. Đây là danh sách đầy đủ nhất, làm nền cho User Story "
        "và Sprint Backlog ở các chương tiếp theo."
    )

    c4.h2(1, "Product Backlog cấp Request")
    c4.para(
        "Product Backlog gồm %d Request, được PO thu thập từ các vai người dùng, quản "
        "trị, quản lý và khách hàng, mỗi Request kèm mức độ cấp thiết (Cao, Trung bình, "
        "Thấp) được quy đổi từ mức ưu tiên 1, 2, 3 trong file nguồn. Bảng 4.1 liệt kê "
        "đầy đủ %d Request theo đúng thứ tự trong Product Backlog gốc." % (N_RQ, N_RQ)
    )
    rows4 = [[r["id"], r["role"], r["goal"], r["so_that"], priority_label(r["priority"])] for r in RQS]
    c4.bang(
        "Product Backlog cấp Request (RQ)",
        ["ID", "Vai trò", "Nhu cầu chức năng", "Lý do", "Mức độ cấp thiết"],
        rows4,
        [1.3, 2.3, 4.4, 4.4, 2.6],
    )
    c4.para(
        "Trong %d Request, %d Request ở trạng thái New được đưa vào phạm vi triển khai "
        "của đợt này, %d Request ở trạng thái Removed và được nhóm chủ động hoãn lại, "
        "chủ yếu thuộc các nhóm %s do vượt quá thời gian và nguồn lực hiện có. Việc thu "
        "hẹp phạm vi này giúp nhóm bốn người tập trung hoàn thành tốt phần lõi bán hàng "
        "tại quầy trong thời gian dự án cho phép."
        % (N_RQ, N_NEW, N_REMOVED, DOI_TUONG_HOAN)
    )

    c4.h2(2, "Release Backlog theo nhóm chức năng")
    c4.para(
        "Sau khi có đầy đủ Request, nhóm gom chúng thành từng Product Backlog (PB) theo "
        "nhóm chức năng, mỗi PB có thể gồm nhiều user story và được gán vào một hoặc "
        "nhiều Sprint. Đây là bước PO chuyển từ danh sách yêu cầu rời rạc sang Release "
        "Backlog có thể lập kế hoạch."
    )
    c4.para(
        "Nhóm xác định %d Product Backlog, đặt tên theo nhóm chức năng nghiệp vụ như "
        "Đăng nhập, Quản lý nhân viên, Bán hàng, Báo cáo doanh thu... Mỗi PB liệt kê số "
        "user story trực thuộc, Sprint được xếp và các mã Request cấu thành. Bảng 4.2 "
        "trình bày đầy đủ %d Product Backlog." % (N_PB, N_PB)
    )
    rows_pb = [
        [
            pb["id"],
            pb["name"],
            len(pb["stories"]),
            ", ".join("Sprint %d" % s for s in pb["sprints"]) if pb["sprints"] else "",
            pb_stories_str(pb),
        ]
        for pb in PBS
    ]
    c4.bang(
        "Release Backlog theo nhóm chức năng",
        ["PB ID", "Nhóm chức năng", "Số US", "Sprint", "Các RQ"],
        rows_pb,
        [1.3, 4.2, 1.3, 1.7, 6.0],
    )
    c4.para(
        "Bảng cho thấy Bán hàng (PB-5) là nhóm nặng nhất với 10 user story, trải trên "
        "hai Sprint liên tiếp, đúng với vai trò là chức năng lõi mang lại giá trị chính "
        "cho cửa hàng. Các nhóm phục vụ khách hàng phía online như PB-11, PB-12, PB-13 "
        "được xếp ở Sprint 5, còn báo cáo và bảo hành dồn vào Sprint 6 cuối cùng, phù "
        "hợp định hướng làm nền tảng bán hàng trước, mở rộng sau."
    )

    # --------------------------------------------------------- Chương 5
    c5 = Chuong(doc, ch5, "User Story")
    c5.para(
        "Chương này viết lại toàn bộ %d Request ở chương %d thành User Story hoàn "
        "chỉnh theo mẫu Là <vai trò>, tôi muốn <nhu cầu>, <lý do>, kèm Story Point và "
        "tiêu chí nghiệm thu (Acceptance Criteria) cho từng Story. Đây là mức mô tả mà "
        "PO và Dev dùng trực tiếp khi trao đổi và kiểm thử tính năng." % (N_RQ, ch4)
    )
    rows5 = [
        [
            r["id"], r["role"], r["goal"], user_story_text(r),
            priority_label(r["priority"]), r["sp"], ac_for(r), r["state"],
        ]
        for r in RQS
    ]
    c5.bang(
        "User Story và tiêu chí nghiệm thu",
        ["ID", "Vai trò", "Nhu cầu", "Mô tả User Story", "Độ ưu tiên", "SP", "Tiêu chí nghiệm thu", "State"],
        rows5,
        [1.0, 2.3, 3.0, 4.4, 1.3, 0.8, 3.5, 1.2],
    )
    c5.para(
        "Mỗi User Story đều có một tiêu chí nghiệm thu ngắn gọn, bám sát nhu cầu chức "
        "năng và lý do của Request gốc, giúp Dev biết chính xác khi nào một Story được "
        "xem là hoàn thành và giúp SM có căn cứ kiểm tra ở Sprint Review. Cột State giữ "
        "nguyên New hoặc Removed như đã xác định ở chương %d, đảm bảo hai chương không "
        "lệch dữ liệu." % ch4
    )

    # --------------------------------------------------------- Chương 6
    c6 = Chuong(doc, ch6, "Sơ đồ phân rã chức năng & Bản đồ tư duy")
    c6.para(
        "Chương này bổ sung một góc nhìn trực quan cho Product Backlog và User Story đã "
        "trình bày ở chương %d và %d: phân rã toàn bộ hệ thống BShoes thành các nhóm "
        "chức năng (use case) cùng các bảng dữ liệu liên quan, giúp nhóm hình dung phạm "
        "vi kỹ thuật trước khi bước sang phần giới thiệu chương trình Demo." % (ch4, ch5)
    )
    c6.para(
        "Hệ thống được chia thành %d nhóm chức năng, từ tài khoản, danh mục, sản phẩm, "
        "khách hàng, bán hàng cho tới sau bán hàng và báo cáo. Mỗi nhóm có người phụ "
        "trách phân tích riêng và gắn với một số bảng dữ liệu cụ thể trong cơ sở dữ liệu "
        "%s." % (len(USECASES), PROJECT["database"])
    )
    for u in USECASES:
        c6.bullet_head("%s (phụ trách: %s)" % (u["group"], u["pic"]))
        for use in u["uses"]:
            c6.bullet_item(use)
        if u["tables"]:
            c6.bullet_item("Bảng dữ liệu liên quan: %s" % ", ".join(u["tables"]))
    c6.para(
        "Nhìn tổng thể, ba nhóm A, B, C (tài khoản, danh mục, sản phẩm) tạo nền dữ liệu "
        "gốc; nhóm E (bán hàng) là chức năng lõi mang lại giá trị chính; hai nhóm D và F "
        "(khách hàng, sau bán hàng) mở rộng trải nghiệm; nhóm G (báo cáo) tổng hợp số "
        "liệu để hỗ trợ ra quyết định. Cách chia này là cơ sở trực tiếp cho phần giới "
        "thiệu chương trình Demo ở chương %d." % ch7
    )

    # --------------------------------------------------------- Chương 7
    c7 = Chuong(doc, ch7, "Giới thiệu vắn tắt về chương trình Demo")
    c7.para(
        "Chương này giới thiệu ngắn gọn phạm vi chương trình Demo mà nhóm dự kiến trình "
        "chiếu ở các buổi Sprint Review, làm cầu nối giữa Product Backlog, User Story "
        "và Sơ đồ phân rã chức năng đã trình bày ở chương %d, %d và %d với sản phẩm "
        "thực tế." % (ch4, ch5, ch6)
    )
    c7.para(
        "BShoes vốn là phần mềm quản lý cửa hàng giày chạy trên desktop bằng Java "
        "NetBeans, nay được nhóm chuyển đổi sang mô hình web với Java Spring Boot ở "
        "backend và Vue SPA ở frontend, dùng chung cơ sở dữ liệu SQL Server. Chương "
        "trình Demo tập trung vào ba nhóm màn hình chính: màn hình bán hàng tại quầy "
        "(POS) cho nhân viên lập hóa đơn nhanh, giỏ hàng tự động trừ kho khi thanh toán "
        "và tự động hoàn kho khi hủy hoặc trả hàng; màn hình quản trị (Admin) để quản "
        "lý sản phẩm, biến thể, danh mục, khách hàng, phiếu giảm giá và phân quyền chi "
        "tiết theo từng nhân viên, kèm dashboard tổng quan doanh thu và đơn hàng; và "
        "giao diện cửa hàng trực tuyến (storefront) cho khách xem sản phẩm, hiện thuộc "
        "nhóm 16 Request đã hoãn lại nên chỉ có bản dựng sơ khai trong lần Demo này."
    )
    c7.para(
        "Việc chuyển từ desktop sang web giúp BShoes vận hành được trên nhiều thiết bị, "
        "dễ triển khai và bảo trì hơn, đồng thời vẫn giữ nguyên nghiệp vụ cốt lõi là bán "
        "hàng tại quầy chính xác, không sai lệch tồn kho. Đây là nội dung nhóm sẽ trình "
        "diễn trực tiếp trong các buổi bảo vệ, trước khi đi vào kế hoạch Sprint chi tiết "
        "ở chương %d." % ch8
    )

    # --------------------------------------------------------- Chương 8
    c8 = Chuong(doc, ch8, "Kế hoạch dự án - thời lượng và số Sprint")
    c8.para(
        "Chương này trình bày lịch trình tổng thể của dự án: dự án chia thành bao "
        "nhiêu Sprint, mỗi Sprint kéo dài bao lâu, mục tiêu là gì và khối lượng công "
        "việc quy đổi ra Story Point. Đây là bức tranh kế hoạch mà nhóm cam kết thực "
        "hiện trong Workshop 1."
    )
    c8.para(
        "Dự án BShoes chia thành %d Sprint, mỗi Sprint kéo dài %s, bắt đầu từ %s và "
        "Sprint cuối kết thúc vào %s. Sau %d Sprint, dự án bước vào giai đoạn %s để "
        "hoàn thiện tài liệu và chuẩn bị báo cáo. Bảng 8.1 liệt kê mục tiêu và khối "
        "lượng của từng Sprint."
        % (
            N_SPRINT,
            PROJECT["sprint_duration"].split(" (")[0],
            DU_AN_BAT_DAU,
            DU_AN_KET_THUC,
            N_SPRINT,
            DEFENSE["label"],
        )
    )
    rows8 = [
        [
            sprint_label(s),
            "%s - %s" % (s["start"], s["end"]),
            s["goal"],
            s["us"],
            s["pps"],
        ]
        for s in SPRINTS
    ]
    rows8.append([DEFENSE["label"], "", DEFENSE["goal"], TOTALS["us"], TOTALS["pps"]])
    c8.bang(
        "Kế hoạch Sprint và Story Points",
        ["Sprint", "Thời gian", "Mục tiêu", "Số US", "Story Points"],
        rows8,
        [2.0, 3.2, 6.8, 1.5, 2.0],
    )
    c8.para(
        "Tổng cộng dự án có %d user story với %.2f Story Points cộng dồn qua các "
        "Sprint, không tính giai đoạn Bảo vệ. Sprint 1 tập trung khảo sát và lập kế "
        "hoạch, các Sprint giữa dồn vào dựng tính năng và kiểm thử, Sprint cuối khép "
        "lại bằng việc ghép luồng hoàn chỉnh trước khi vào giai đoạn bảo vệ đồ án. "
        "Nhịp độ này cho thấy khối lượng được rải tương đối đều, không dồn cục vào một "
        "Sprint duy nhất." % (TOTALS["us"], TOTALS["pps"])
    )

    # --------------------------------------------------------- Chương 9
    c9 = Chuong(doc, ch9, "Biên bản nhận xét các thành viên buổi họp")
    c9.para(
        "Chương này ghi lại 3 buổi họp nhóm quan trọng trong quá trình chuẩn bị và lập "
        "kế hoạch dự án BShoes, gắn với các cột mốc đã trình bày ở chương %d: khởi động "
        "dự án, chốt Product Backlog và Sprint Planning. Mỗi buổi họp kèm bảng nhận xét "
        "ngắn cho từng thành viên để nhóm tự đánh giá mức độ chuẩn bị và đóng góp." % ch8
    )
    for i, mt in enumerate(MEETINGS, start=1):
        c9.h2(i, "Buổi họp ngày %s" % mt["date"])
        c9.para(mt["content"])
        c9.bang(
            "Nhận xét thành viên buổi họp ngày %s" % mt["date"],
            ["Tên thành viên", "Chuẩn bị (chất lượng)", "Chuẩn bị (nội dung)", "Đóng góp ý kiến", "Nhận xét", "Tổng cộng"],
            mt["rows"],
            [2.8, 1.8, 3.4, 3.4, 3.4, 1.7],
        )
    c9.para(
        "Xuyên suốt 3 buổi họp, cả bốn thành viên đều duy trì mức chuẩn bị Khá trở lên "
        "và có đóng góp ý kiến cụ thể, phản ánh đúng tinh thần hợp tác mà Scrum đề cao. "
        "Đây cũng là cơ sở thực tế để nhóm tự tin bước vào giai đoạn lập kế hoạch chi "
        "tiết ở Workshop 2."
    )

    # --------------------------------------------------------- Chương 10
    c10 = Chuong(doc, ch10, "Nhật ký cập nhật")
    c10.para(
        "Chương này ghi lại các vấn đề nhóm phát hiện trong quá trình rà soát tài liệu "
        "và cách xử lý tương ứng ở bản cập nhật này, giúp người đọc thấy rõ tài liệu đã "
        "thay đổi như thế nào so với bản nháp ban đầu."
    )
    c10.bang(
        "Nhật ký cập nhật tài liệu",
        ["STT", "Vấn đề", "Cách xử lý"],
        [[i + 1, van_de, cach_xu_ly] for i, (van_de, cach_xu_ly) in enumerate(UPDATE_LOG)],
        [1.0, 6.5, 8.5],
    )
    c10.para(
        "Các cập nhật trên đều xuất phát từ việc rà soát lại đúng file Excel nguồn và "
        "chuẩn hóa cách trình bày, không làm thay đổi bản chất số liệu đã thu thập. Đây "
        "là chương khép lại Workshop 1, trước khi Workshop 2 đi sâu vào mục tiêu sản "
        "phẩm và kế hoạch từng Sprint."
    )


# ===========================================================================
# WORKSHOP 2 - MỤC TIÊU, ƯỚC LƯỢNG VÀ SPRINT BACKLOG (Chương base+1..base+9)
# ===========================================================================

def build_ws2(doc, base=0, parta_ref_pb="Workshop 1", parta_ref_plan="Workshop 1"):
    """
    Viết Chương base+1..base+9 (nội dung Workshop 2) vào doc.

    parta_ref_pb: cụm từ tham chiếu ngược tới chương trình bày Release
    Backlog theo nhóm chức năng ở Workshop 1 (chương 4, mục 4.2 khi gộp
    trong asm1.docx, hoặc "Workshop 1" khi đứng một mình).
    parta_ref_plan: cụm từ tham chiếu ngược tới chương Kế hoạch Sprint ở
    Workshop 1 (chương 8 khi gộp, hoặc "Workshop 1" khi đứng một mình).
    """
    b1, b2, b3, b4, b5, b6, b7, b8, b9 = (base + i for i in range(1, 10))

    # --------------------------------------------------------- Chương 1
    c_b1 = Chuong(doc, b1, "Thành viên nhóm & Vai trò")
    c_b1.para(
        "Chương này nhắc lại thành viên trực tiếp thực hiện dự án BShoes trong các "
        "Sprint, làm rõ ai chịu trách nhiệm chính cho Product Goal, Sprint Goal và "
        "Sprint Backlog sẽ trình bày ở các chương tiếp theo."
    )
    for m in CORE_TEAM:
        c_b1.para("%s, vai trò %s." % (m["name"], ROLE_DISPLAY.get(m["role"], m["role"])))
    c_b1.para(
        "Bốn thành viên này cùng chịu trách nhiệm trước Product Goal của dự án: PO chốt "
        "mục tiêu và độ ưu tiên, SM đảm bảo quy trình Scrum được tuân thủ, hai Dev hiện "
        "thực hóa mục tiêu đó qua từng Sprint."
    )

    # --------------------------------------------------------- Chương 2
    c_b2 = Chuong(doc, b2, "Mục tiêu sản phẩm (Product Goal)")
    c_b2.para(
        "Chương này trả lời câu hỏi nền tảng của Workshop 2: sản phẩm BShoes cuối cùng "
        "hướng tới điều gì. Đây là kim chỉ nam để nhóm chấm ưu tiên và ước lượng ở các "
        "chương sau."
    )
    c_b2.para(
        "BShoes hướng tới xây dựng một hệ thống bán hàng tại quầy chính xác: nhân viên "
        "lập hóa đơn nhanh, hàng trong giỏ tự động trừ kho và tự động hoàn kho khi hủy "
        "hoặc trả hàng, tránh sai lệch tồn kho vốn là rủi ro lớn nhất khi bán hàng thủ "
        "công. Song song đó, hệ thống quản lý sản phẩm, biến thể theo size và màu, "
        "khách hàng, bảo hành và đơn giao hàng, đồng thời phân quyền chi tiết tới từng "
        "nhân viên theo vai trò. Ở giai đoạn xa hơn (Sprint 5, 6), sản phẩm mở rộng "
        "thêm kênh online cho khách xem và đặt hàng, cùng báo cáo doanh thu để chủ cửa "
        "hàng theo dõi tình hình kinh doanh."
    )
    c_b2.para(
        "Tóm lại, mục tiêu sản phẩm lấy độ chính xác của nghiệp vụ bán hàng tại quầy "
        "làm trọng tâm, các phần còn lại là lớp mở rộng xây dựng trên nền đó, đúng thứ "
        "tự ưu tiên mà nhóm đã xếp trong Release Backlog ở %s." % parta_ref_pb
    )

    # --------------------------------------------------------- Chương 3
    c_b3 = Chuong(doc, b3, "Mục tiêu của Product Backlog")
    c_b3.para(
        "Ngoài mục tiêu sản phẩm, nhóm cũng cần xác định rõ Product Backlog dùng để "
        "làm gì trong quá trình vận hành Scrum của dự án BShoes."
    )
    c_b3.para(
        "Product Backlog của BShoes là danh sách sống, tổng hợp toàn bộ %d Request thu "
        "thập từ actor, bóc tách thành %d Product Backlog và sắp theo Business Value "
        "cùng mức ưu tiên. Mục tiêu là giúp PO và cả nhóm luôn nhìn thấy việc cần làm "
        "trước, việc có thể hoãn, từ đó nhóm bốn thành viên với trình độ IT còn hạn chế "
        "biết tập trung đúng chỗ thay vì dàn trải. Với %d Request bị đưa về trạng thái "
        "Removed, Product Backlog còn đóng vai trò ghi nhận minh bạch phạm vi đã bị cắt "
        "giảm để không quên mất khi lập kế hoạch cho giai đoạn sau." % (N_RQ, N_PB, N_REMOVED)
    )
    c_b3.para(
        "Như vậy Product Backlog vừa là danh sách việc cần làm, vừa là công cụ quản lý "
        "phạm vi, giúp nhóm duy trì một nguồn thông tin duy nhất trong suốt %d Sprint."
        % N_SPRINT
    )

    # --------------------------------------------------------- Chương 4
    c_b4 = Chuong(doc, b4, "Dự định thời lượng và số lượng Sprint")
    c_b4.para(
        "Chương này nhắc lại cam kết về thời lượng dự án làm căn cứ xác định Sprint "
        "Goal cho từng Sprint ở chương tiếp theo, đồng thời liên hệ ngược tới kế hoạch "
        "chi tiết đã trình bày ở %s." % parta_ref_plan
    )
    c_b4.para(
        "Dự án BShoes dự kiến hoàn thành trong %d Sprint, mỗi Sprint kéo dài %s, bắt "
        "đầu từ %s và kết thúc vào %s, cộng thêm giai đoạn %s để hoàn thiện tài liệu và "
        "chuẩn bị bảo vệ. Bảng chi tiết từng Sprint cùng Story Points cộng dồn đã trình "
        "bày đầy đủ ở %s; chương này chỉ nhắc lại khung thời gian để nhóm bám theo khi "
        "lập Sprint Goal."
        % (
            N_SPRINT,
            PROJECT["sprint_duration"].split(" (")[0],
            DU_AN_BAT_DAU,
            DU_AN_KET_THUC,
            DEFENSE["label"],
            parta_ref_plan,
        )
    )
    c_b4.para(
        "Trong %d Sprint này, nhóm chỉ đưa vào %d Request ở trạng thái New, 16 Request "
        "còn lại đã được hoãn sang giai đoạn sau, nên khối lượng mỗi Sprint được giữ ở "
        "mức vừa phải với năng lực bốn thành viên." % (N_SPRINT, N_NEW)
    )

    # --------------------------------------------------------- Chương 5
    c_b5 = Chuong(doc, b5, "Mục tiêu cụ thể cho từng Sprint (Sprint Goals)")
    c_b5.para(
        "Chương này trình bày mục tiêu cụ thể của từng Sprint trong số %d Sprint của "
        "dự án, kèm khung thời gian và khối lượng quy đổi ra số user story và Story "
        "Points. Đây là cách nhóm chia nhỏ mục tiêu sản phẩm ở chương %d thành các mốc "
        "có thể kiểm chứng theo tuần." % (N_SPRINT, b2)
    )
    for s in SPRINTS:
        theme_line = s["theme"].split("\n")[0]
        c_b5.h2(s["num"], "Sprint %d: %s" % (s["num"], theme_line))
        c_b5.para(
            "Sprint %d diễn ra từ %s đến %s, do %s phụ trách chính, với chủ đề %s."
            % (s["num"], s["start"], s["end"], s["pic"], theme_line)
        )
        c_b5.para(
            "Mục tiêu: %s. Sprint này gồm %d user story, tương ứng %.2f Story Points."
            % (s["goal"], s["us"], s["pps"])
        )
    c_b5.para(
        "Nhìn xuyên suốt 6 Sprint, nhóm đi theo trình tự lập kế hoạch (Sprint 1), dựng "
        "bản build đầu tiên (Sprint 2), kiểm thử dữ liệu (Sprint 3), hoàn thiện và mở "
        "rộng tính năng nâng cao (Sprint 4), ghép luồng và ra mắt (Sprint 5, 6). Trình "
        "tự này đảm bảo phần lõi bán hàng luôn được dựng và kiểm thử trước khi mở rộng "
        "sang các tính năng phụ."
    )

    # --------------------------------------------------------- Chương 6
    c_b6 = Chuong(doc, b6, "Phương pháp ước lượng")
    c_b6.para(
        "Chương này giải thích công thức ước lượng Story Point mà nhóm áp dụng cho "
        "toàn bộ Product Backlog, theo đúng mẫu ước lượng của môn học, đồng thời nêu "
        "rõ các tham số nhóm đã chọn để tính toán."
    )
    c_b6.h2(1, "Công thức UP, AP, PPS")
    c_b6.para(
        "Công thức ước lượng gồm ba bước liên tiếp: tính điểm chưa hiệu chỉnh, hiệu "
        "chỉnh theo hệ số nhân, rồi quy đổi theo hệ số môi trường của nhóm."
    )
    c_b6.bullet_head("UP, điểm chưa hiệu chỉnh")
    c_b6.bullet_item("UP = tổng 4 tiêu chí: Loại tương tác, Quy tắc nghiệp vụ, Số thực thể, Thao tác dữ liệu")
    c_b6.bullet_item("Mỗi tiêu chí chấm từ 1 đến 3 điểm tùy độ phức tạp")
    c_b6.bullet_head("AP, điểm đã hiệu chỉnh")
    c_b6.bullet_item("AP = UP nhân C, trong đó C là hệ số nhân do nhóm chọn, C = %d" % C_DEFAULT)
    c_b6.bullet_head("PPS, điểm cho mỗi Story")
    c_b6.bullet_item("PPS = (AP nhân ED) chia 36, với 36 là điểm ED tối đa có thể đạt")
    c_b6.bullet_head("ED, điểm môi trường")
    c_b6.bullet_item("Tổng %d yếu tố môi trường, mỗi yếu tố chấm theo thang quy định, tối đa %d điểm" % (ED_TOTAL, ED_MAX))
    c_b6.para(
        "Ba công thức trên nối tiếp nhau: UP phản ánh độ phức tạp thuần của user "
        "story, AP hiệu chỉnh theo hệ số nhân C, còn PPS đưa thêm yếu tố môi trường ED "
        "để ra Story Points cuối cùng, dùng thống nhất cho mọi bảng ước lượng ở chương "
        "%d." % b7
    )

    c_b6.h2(2, "Hệ số môi trường ED và hệ số nhân C của nhóm")
    c_b6.para(
        "Sau khi có công thức, nhóm cần chốt giá trị cụ thể cho ED và C dựa trên đặc "
        "điểm thật của nhóm bốn người thực hiện dự án BShoes."
    )
    c_b6.para(
        "Nhóm tự chấm điểm môi trường ED = %d trên tối đa %d, tức hệ số môi trường ED "
        "= %d/%d = %.1f. Điểm này phản ánh đúng thực tế: nhóm mạnh về con người, tinh "
        "thần hợp tác tốt và đã cùng làm việc trước đó, nhưng còn yếu về công nghệ và "
        "hạ tầng, chưa có quy trình kiểm thử tự động hay CI/CD hoàn chỉnh. Về hệ số "
        "nhân, do tài liệu gốc không quy định giá trị bắt buộc, nhóm chọn C = %d, "
        "nghĩa là trung tính, không khuếch đại cũng không thu nhỏ điểm AP so với UP."
        % (ED_TOTAL, ED_MAX, ED_TOTAL, ED_MAX, ENV_FACTOR, C_DEFAULT)
    )
    c_b6.para(
        "Với ED = %.1f và C = %d, mọi Story Point trong dự án đều bị giảm còn một nửa "
        "so với năng suất lý tưởng, đúng như tinh thần ước lượng cẩn trọng cho một "
        "nhóm còn hạn chế về công nghệ và hạ tầng." % (ENV_FACTOR, C_DEFAULT)
    )

    # --------------------------------------------------------- Chương 7
    doc.add_page_break()
    c_b7 = Chuong(doc, b7, "Bảng ước lượng User Story")
    c_b7.para(
        "Chương này áp dụng công thức và các tham số ở chương %d để trình bày Story "
        "Points của toàn bộ %d Request, cùng mức ưu tiên và Business Value tương ứng, "
        "làm căn cứ xếp Sprint đã trình bày ở %s và chương %d."
        % (b6, N_RQ, parta_ref_plan, b5)
    )
    rows_b7 = [[r["id"], r["goal"], r["priority"], r["bv"], r["sp"], r["state"]] for r in RQS]
    c_b7.bang(
        "Ước lượng Story Point cho từng User Story",
        ["ID", "User Story", "Ưu tiên", "BV", "SP", "State"],
        rows_b7,
        [1.1, 6.5, 1.2, 1.6, 1.0, 1.6],
    )
    c_b7.para(
        "Tổng Story Points của %d Request là %.1f điểm, trong đó riêng %d Request "
        "trạng thái New đang triển khai chiếm %.1f điểm. Các Request có Business Value "
        "High tập trung ở nhóm đăng nhập, phân quyền và bán hàng, đúng với thứ tự ưu "
        "tiên 1 và 2, cho thấy bảng ước lượng nhất quán với cách nhóm xếp Release "
        "Backlog ở %s."
        % (N_RQ, sum(r["sp"] for r in RQS), N_NEW, TONG_SP_NEW, parta_ref_pb)
    )

    # --------------------------------------------------------- Chương 8
    doc.add_page_break()
    c_b8 = Chuong(doc, b8, "Sprint Backlog theo từng Sprint")
    c_b8.para(
        "Chương này chia Product Backlog thành Sprint Backlog cụ thể cho từng Sprint, "
        "mỗi Task gắn với một Product Backlog gốc, có thể kèm mã Request, số giờ ước "
        "tính và người phụ trách. Đây là mức chi tiết nhất mà Dev sử dụng hàng ngày để "
        "thực hiện công việc."
    )
    c_b8.para(
        "Tổng cộng %d Task được tách ra từ %d Product Backlog, với tổng khối lượng %d "
        "giờ công, trải đều trên %d Sprint. Các bảng dưới đây liệt kê Task theo từng "
        "Sprint, đúng theo Sprint đã xếp ở %s và chương %d."
        % (N_TASK, N_PB, TONG_GIO, N_SPRINT, parta_ref_plan, b5)
    )
    for s in SPRINTS:
        n = s["num"]
        c_b8.h3(1, n, "Sprint %d" % n)
        tasks_n = tasks_of_sprint(n)
        hrs = sum(t["est"] for t in tasks_n)
        c_b8.para(
            "Sprint %d có %d Task, tổng cộng %d giờ công, thực hiện từ %s đến %s."
            % (n, len(tasks_n), hrs, s["start"], s["end"])
        )
        rows_b8 = [
            [t["id"], t["backlog"], t["story"] if t["story"] else "", t["task"], t["est"], t["who"]]
            for t in tasks_n
        ]
        c_b8.bang(
            "Sprint Backlog Sprint %d" % n,
            ["Task ID", "Backlog (PB)", "Story (RQ)", "Nội dung task", "Giờ", "Phụ trách"],
            rows_b8,
            [1.2, 1.5, 1.5, 6.8, 1.0, 1.5],
        )
    c_b8.para(
        "Sprint Backlog cho thấy khối lượng Task lớn nhất rơi vào Sprint 5 và Sprint "
        "6, đúng thời điểm nhóm mở rộng kênh online cho khách và dựng báo cáo doanh "
        "thu, trong khi Sprint 1 nhẹ hơn vì chủ yếu là khảo sát và lập kế hoạch. Việc "
        "mỗi Task đều trỏ về đúng Product Backlog và Request gốc giúp truy vết công "
        "việc xuyên suốt ba cấp RQ, PB, Task mà không bị lệch."
    )

    # --------------------------------------------------------- Chương 9
    c_b9 = Chuong(doc, b9, "Biên Bản Cuộc Họp (Sprint Planning)")
    c_b9.para(
        "Thời gian: 20:00 - 21:30, ngày 15/07/2026."
    )
    c_b9.para(
        "Địa điểm: Google Meet."
    )
    c_b9.h2(1, "Thành viên tham gia")
    for m in CORE_TEAM:
        c_b9.para(member_label(m))
    c_b9.h2(2, "Nội dung cuộc họp")
    c_b9.bullet_item("Rà soát và thống nhất Product Backlog đã xây dựng trong Workshop 1.")
    c_b9.bullet_item("Thống nhất Product Goal và Product Backlog Goal của dự án BShoes.")
    c_b9.bullet_item("Xác định Sprint Goal cho %d Sprint dựa trên lộ trình phát triển của dự án." % N_SPRINT)
    c_b9.bullet_item("Phân bổ %d Request trạng thái New từ Product Backlog vào %d Sprint theo mẫu Sprint Planning chuẩn." % (N_NEW, N_SPRINT))
    c_b9.bullet_item("Thống nhất chuyển %d Request còn lại sang trạng thái Removed cho giai đoạn sau." % N_REMOVED)
    c_b9.bullet_item("Phân rã các User Story thành các Task kỹ thuật để hình thành Sprint Backlog chi tiết.")
    c_b9.bullet_item("Phân công công việc giữa các thành viên, bao gồm các Task đặc trưng của Product Owner.")
    c_b9.h2(3, "Kết quả cuộc họp")
    c_b9.bullet_item("Thống nhất Product Goal, Product Backlog Goal và Sprint Goal cho %d Sprint." % N_SPRINT)
    c_b9.bullet_item("Hoàn thành Sprint Planning theo đúng mẫu Sprint Backlog do giảng viên cung cấp.")
    c_b9.bullet_item("Hoàn thành Sprint Backlog chi tiết với Task kỹ thuật, thời gian ước lượng và người thực hiện.")
    c_b9.bullet_item("Thống nhất tiếp tục triển khai dự án theo kế hoạch trong các Workshop tiếp theo.")


# ===========================================================================
# CHẠY TRỰC TIẾP: sinh asm1.docx = bìa + mục lục + PART A (1..10) + PART B (1..9)
# ===========================================================================

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python gen_asm1.py <outdir>")
        sys.exit(1)

    OUTDIR = sys.argv[1]

    doc = setup(Document())

    cover_page(doc, "Báo cáo tổng hợp", "Workshop 1 - 2")

    muc_luc(
        doc,
        [
            (
                "PHẦN A: WORKSHOP 1 - Nền tảng Agile/Scrum và Product Backlog",
                [
                    "Giới thiệu Agile, Scrum và dự án BShoes",
                    "Phân vai nhóm và bảng phân công công việc xuyên suốt dự án",
                    "Product Backlog (40 Request) và User Story kèm tiêu chí nghiệm thu",
                    "Sơ đồ phân rã chức năng, giới thiệu Demo, kế hoạch 6 Sprint",
                    "Biên bản nhận xét các buổi họp và nhật ký cập nhật",
                ],
            ),
            (
                "PHẦN B: WORKSHOP 2 - Mục tiêu, ước lượng và Sprint Backlog",
                [
                    "Thành viên nhóm, Product Goal và Product Backlog Goal",
                    "Dự định thời lượng và mục tiêu từng Sprint (Sprint Goals)",
                    "Phương pháp ước lượng Story Point và bảng ước lượng User Story",
                    "Sprint Backlog chi tiết theo từng Sprint",
                    "Biên bản cuộc họp Sprint Planning",
                ],
            ),
        ],
    )

    # PHẦN A - WORKSHOP 1
    title(doc, "Phần A: Workshop 1", "Nền tảng Agile/Scrum, Product Backlog và kế hoạch dự án")
    build_ws1(doc, base=0, partb_ref="chương 18")

    # PHẦN B - WORKSHOP 2
    doc.add_page_break()
    title(doc, "Phần B: Workshop 2", "Mục tiêu, ước lượng và Sprint Backlog")
    build_ws2(doc, base=10, parta_ref_pb="chương 4", parta_ref_plan="chương 8")

    # KẾT LUẬN
    ket_luan(
        doc,
        [
            "Dự án BShoes xuất phát từ %d Request thu thập từ các actor, trong đó %d "
            "Request được đưa vào phạm vi triển khai và %d Request bị hoãn lại (nhóm %s) "
            "để nhóm bốn thành viên tập trung hoàn thiện phần lõi bán hàng tại quầy."
            % (N_RQ, N_NEW, N_REMOVED, DOI_TUONG_HOAN),
            "Toàn bộ Request được bóc tách thành %d Product Backlog theo nhóm chức năng, "
            "viết lại thành User Story kèm tiêu chí nghiệm thu, xếp vào %d Sprint kéo dài "
            "từ %s đến %s, cộng thêm giai đoạn %s để hoàn thiện tài liệu và báo cáo."
            % (N_PB, N_SPRINT, DU_AN_BAT_DAU, DU_AN_KET_THUC, DEFENSE["label"]),
            "Story Points được tính theo công thức UP, AP, PPS với hệ số môi trường ED = "
            "%d/%d (bằng %.1f) và hệ số nhân C = %d, phản ánh đúng thực trạng nhóm mạnh "
            "về con người nhưng còn hạn chế về công nghệ và hạ tầng. Tổng khối lượng dự "
            "án đạt %.2f Story Points và %d giờ công, được chia thành %d Task cụ thể cho "
            "từng Sprint, xuyên suốt được ghi nhận qua 3 buổi họp nhóm và biên bản Sprint "
            "Planning."
            % (ED_TOTAL, ED_MAX, ENV_FACTOR, C_DEFAULT, TOTALS["pps"], TONG_GIO, N_TASK),
        ],
    )

    f = os.path.join(OUTDIR, "asm1.docx")
    doc.save(f)
    print("OK ->", f)
