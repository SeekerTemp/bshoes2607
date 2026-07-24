# -*- coding: utf-8 -*-
"""
gen_asm1.py
===========
Sinh MỘT file Word duy nhất, gộp Workshop 1 và Workshop 2 của dự án BShoes,
theo đúng khuôn định dạng ở docx_format.py, dữ liệu lấy từ ws_source.py
(nguồn sự thật duy nhất, không bịa số liệu).

Cấu trúc:
    Trang tiêu đề
    PHẦN A - WORKSHOP 1 (Kế hoạch & Product Backlog)  : Chương 1..7
    PHẦN B - WORKSHOP 2 (Mục tiêu & ước lượng & Sprint Backlog) : Chương 8..12
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

from docx_format import setup, title, Chuong, ket_luan
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


def rq_row(r):
    return [r["id"], r["role"], r["goal"], r["so_that"], r["priority"], r["bv"], r["sp"], r["state"]]


def pb_stories_str(pb):
    return ", ".join(pb["stories"]) if pb["stories"] else ""


def sprint_label(s):
    return "Sprint %d" % s["num"]


def tasks_of_sprint(n):
    return [t for t in TASKS if t["sprint"] == n]


# ===========================================================================
# WORKSHOP 1 - KẾ HOẠCH VÀ PRODUCT BACKLOG (Chương base+1 .. base+7)
# ===========================================================================

def build_ws1(doc, base=0, partb_ref="Workshop 2"):
    """
    Viết Chương base+1..base+7 (nội dung Workshop 1) vào doc.

    partb_ref: cụm từ dùng để tham chiếu tới nơi trình bày Sprint Backlog
    chi tiết (chương 12 khi gộp trong asm1.docx, hoặc "Workshop 2" khi đứng
    một mình).
    """
    ch1, ch2, ch3, ch4, ch5, ch6, ch7 = (base + i for i in range(1, 8))

    # --------------------------------------------------------- Chương 1
    c1 = Chuong(doc, ch1, "Giới thiệu dự án")
    c1.para(
        "Chương này giới thiệu tổng quan dự án BShoes: sản phẩm hướng tới, công nghệ sử "
        "dụng, mô hình phát triển và khung thời gian thực hiện. Đây là nền tảng để hiểu "
        "vì sao các chương sau chia công việc theo Request, Product Backlog và Sprint như "
        "đã trình bày."
    )
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
        "trò, người phụ trách và thông tin liên hệ. Việc phân vai rõ ràng giúp trách "
        "nhiệm không chồng chéo và là cơ sở cho bảng phân công công việc ở chương %d."
        % ch3
    )
    c2.para(
        "Nhóm gồm %s: một Product Owner (PO) phụ trách nghiệp vụ và yêu cầu, một Scrum "
        "Master (SM) theo dõi tiến độ và quy trình, hai lập trình viên (DEV) trực tiếp "
        "xây dựng sản phẩm. Bảng 2.1 liệt kê đầy đủ vai trò, họ tên, handle và số điện "
        "thoại liên hệ của từng thành viên." % PROJECT["team_size"]
    )
    c2.bang(
        "Danh sách thành viên nhóm",
        ["Vai trò", "Họ tên", "Handle", "SĐT"],
        [[m["role"], m["name"], m["handle"], m["phone"]] for m in TEAM],
        [2.2, 5.0, 3.5, 3.0],
    )
    c2.para(
        "Ngoài bốn thành viên trực tiếp làm sản phẩm, dự án còn có một Mentor giám sát "
        "chung, định hướng nghiệp vụ và góp ý kỹ thuật xuyên suốt các Sprint mà không "
        "trực tiếp thực hiện task. Cơ cấu này gọn nhẹ, phù hợp với quy mô nhóm bốn người "
        "và tốc độ Sprint một tuần."
    )

    # --------------------------------------------------------- Chương 3
    c3 = Chuong(doc, ch3, "Bảng phân công công việc")
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
    c4 = Chuong(doc, ch4, "Sơ đồ phân rã chức năng")
    c4.para(
        "Chương này phân rã hệ thống BShoes thành các nhóm chức năng (use case) cùng "
        "các bảng dữ liệu liên quan. Đây là bước trung gian giữa yêu cầu nghiệp vụ và "
        "Product Backlog, giúp nhóm hình dung phạm vi kỹ thuật trước khi viết Request "
        "chi tiết ở chương %d." % ch5
    )
    c4.para(
        "Hệ thống được chia thành %d nhóm chức năng, từ tài khoản, danh mục, sản phẩm, "
        "khách hàng, bán hàng cho tới sau bán hàng và báo cáo. Mỗi nhóm có người phụ "
        "trách phân tích riêng và gắn với một số bảng dữ liệu cụ thể trong cơ sở dữ liệu "
        "%s." % (len(USECASES), PROJECT["database"])
    )
    for u in USECASES:
        c4.bullet_head("%s (phụ trách: %s)" % (u["group"], u["pic"]))
        for use in u["uses"]:
            c4.bullet_item(use)
        if u["tables"]:
            c4.bullet_item("Bảng dữ liệu liên quan: %s" % ", ".join(u["tables"]))
    c4.para(
        "Nhìn tổng thể, ba nhóm A, B, C (tài khoản, danh mục, sản phẩm) tạo nền dữ liệu "
        "gốc; nhóm E (bán hàng) là chức năng lõi mang lại giá trị chính; hai nhóm D và F "
        "(khách hàng, sau bán hàng) mở rộng trải nghiệm; nhóm G (báo cáo) tổng hợp số "
        "liệu để hỗ trợ ra quyết định. Cách chia này là cơ sở trực tiếp để nhóm bóc tách "
        "Product Backlog ở chương sau."
    )

    # --------------------------------------------------------- Chương 5
    c5 = Chuong(doc, ch5, "Product Backlog (Request)")
    c5.para(
        "Chương này trình bày toàn bộ Product Backlog ở cấp Request, tức yêu cầu gốc "
        "thu thập từ các vai actor khác nhau, viết theo mẫu As a, I want, So that. Đây "
        "là danh sách đầy đủ nhất, làm nền cho việc xếp Release Backlog và Sprint ở các "
        "chương tiếp theo."
    )
    c5.para(
        "Product Backlog gồm %d Request, được PO thu thập từ các vai người dùng, quản "
        "trị, quản lý và khách hàng, mỗi Request kèm mức ưu tiên (1 là cao nhất), "
        "Business Value và điểm Story Point ước lượng. Bảng 5.1 liệt kê đầy đủ %d "
        "Request theo đúng thứ tự trong Product Backlog gốc." % (N_RQ, N_RQ)
    )
    rows5 = [rq_row(r) for r in RQS]
    c5.bang(
        "Product Backlog cấp Request (RQ)",
        ["ID", "Vai trò", "Nhu cầu", "Lý do", "Ưu tiên", "Business Value", "SP", "State"],
        rows5,
        [1.1, 2.0, 4.0, 4.0, 1.0, 1.6, 0.8, 1.2],
    )
    c5.para(
        "Trong %d Request, %d Request ở trạng thái New được đưa vào phạm vi triển khai "
        "của đợt này, %d Request ở trạng thái Removed và được nhóm chủ động hoãn lại, "
        "chủ yếu thuộc các nhóm %s do vượt quá thời gian và nguồn lực hiện có. Việc thu "
        "hẹp phạm vi này giúp nhóm bốn người tập trung hoàn thành tốt phần lõi bán hàng "
        "tại quầy trong thời gian dự án cho phép."
        % (N_RQ, N_NEW, N_REMOVED, DOI_TUONG_HOAN)
    )

    # --------------------------------------------------------- Chương 6
    c6 = Chuong(doc, ch6, "Release Backlog theo nhóm chức năng")
    c6.para(
        "Chương này gom các Request thành từng Product Backlog (PB) theo nhóm chức "
        "năng, mỗi PB có thể gồm nhiều user story và được gán vào một hoặc nhiều Sprint. "
        "Đây là bước PO chuyển từ danh sách yêu cầu rời rạc sang Release Backlog có thể "
        "lập kế hoạch."
    )
    c6.para(
        "Nhóm xác định %d Product Backlog, đặt tên theo nhóm chức năng nghiệp vụ như "
        "Đăng nhập, Quản lý nhân viên, Bán hàng, Báo cáo doanh thu... Mỗi PB liệt kê số "
        "user story trực thuộc, Sprint được xếp và các mã Request cấu thành. Bảng 6.1 "
        "trình bày đầy đủ %d Product Backlog." % (N_PB, N_PB)
    )
    rows6 = [
        [
            pb["id"],
            pb["name"],
            len(pb["stories"]),
            ", ".join("Sprint %d" % s for s in pb["sprints"]) if pb["sprints"] else "",
            pb_stories_str(pb),
        ]
        for pb in PBS
    ]
    c6.bang(
        "Release Backlog theo nhóm chức năng",
        ["PB ID", "Nhóm chức năng", "Số US", "Sprint", "Các RQ"],
        rows6,
        [1.3, 4.2, 1.3, 1.7, 6.0],
    )
    c6.para(
        "Bảng cho thấy Bán hàng (PB-5) là nhóm nặng nhất với 10 user story, trải trên "
        "hai Sprint liên tiếp, đúng với vai trò là chức năng lõi mang lại giá trị chính "
        "cho cửa hàng. Các nhóm phục vụ khách hàng phía online như PB-11, PB-12, PB-13 "
        "được xếp ở Sprint 5, còn báo cáo và bảo hành dồn vào Sprint 6 cuối cùng, phù "
        "hợp định hướng làm nền tảng bán hàng trước, mở rộng sau."
    )

    # --------------------------------------------------------- Chương 7
    c7 = Chuong(doc, ch7, "Kế hoạch dự án - thời lượng và số Sprint")
    c7.para(
        "Chương này trình bày lịch trình tổng thể của dự án: dự án chia thành bao "
        "nhiêu Sprint, mỗi Sprint kéo dài bao lâu, mục tiêu là gì và khối lượng công "
        "việc quy đổi ra Story Point. Đây là bức tranh kế hoạch mà nhóm cam kết thực "
        "hiện trong Workshop 1."
    )
    c7.para(
        "Dự án BShoes chia thành %d Sprint, mỗi Sprint kéo dài %s, bắt đầu từ %s và "
        "Sprint cuối kết thúc vào %s. Sau %d Sprint, dự án bước vào giai đoạn %s để "
        "hoàn thiện tài liệu và chuẩn bị báo cáo. Bảng 7.1 liệt kê mục tiêu và khối "
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
    rows7 = [
        [
            sprint_label(s),
            "%s - %s" % (s["start"], s["end"]),
            s["goal"],
            s["us"],
            s["pps"],
        ]
        for s in SPRINTS
    ]
    rows7.append([DEFENSE["label"], "", DEFENSE["goal"], TOTALS["us"], TOTALS["pps"]])
    c7.bang(
        "Kế hoạch Sprint và Story Points",
        ["Sprint", "Thời gian", "Mục tiêu", "Số US", "Story Points"],
        rows7,
        [2.0, 3.2, 6.8, 1.5, 2.0],
    )
    c7.para(
        "Tổng cộng dự án có %d user story với %.2f Story Points cộng dồn qua các "
        "Sprint, không tính giai đoạn Bảo vệ. Sprint 1 tập trung khảo sát và lập kế "
        "hoạch, các Sprint giữa dồn vào dựng tính năng và kiểm thử, Sprint cuối khép "
        "lại bằng việc ghép luồng hoàn chỉnh trước khi vào giai đoạn bảo vệ đồ án. "
        "Nhịp độ này cho thấy khối lượng được rải tương đối đều, không dồn cục vào một "
        "Sprint duy nhất." % (TOTALS["us"], TOTALS["pps"])
    )


# ===========================================================================
# WORKSHOP 2 - MỤC TIÊU, ƯỚC LƯỢNG VÀ SPRINT BACKLOG (Chương base+1 .. base+5)
# ===========================================================================

def build_ws2(doc, base=0, parta_ref_ch6="Workshop 1", parta_ref_ch7="Workshop 1"):
    """
    Viết Chương base+1..base+5 (nội dung Workshop 2) vào doc.

    parta_ref_ch6, parta_ref_ch7: cụm từ dùng để tham chiếu ngược tới nội
    dung Release Backlog (chương 6) và Kế hoạch Sprint (chương 7) của
    Workshop 1. Khi gộp trong asm1.docx, đây là "chương 6" / "chương 7"
    (vì Workshop 1 nằm cùng file, đứng trước). Khi đứng một mình, mặc định
    là "Workshop 1" vì các chương đó không tồn tại trong file độc lập.
    """
    ch8, ch9, ch10, ch11, ch12 = (base + i for i in range(1, 6))

    # --------------------------------------------------------- Chương 8
    c8 = Chuong(doc, ch8, "Mục tiêu sản phẩm và Product Backlog")
    c8.para(
        "Chương này trả lời hai câu hỏi nền tảng của Workshop 2: sản phẩm BShoes cuối "
        "cùng hướng tới điều gì, và Product Backlog tồn tại để phục vụ mục đích gì. "
        "Đây là kim chỉ nam để nhóm chấm ưu tiên và ước lượng ở các chương sau."
    )

    c8.h2(1, "Mục tiêu sản phẩm (Product Goal)")
    c8.para(
        "Mục tiêu sản phẩm cần được phát biểu ngắn gọn nhưng bao quát toàn bộ giá trị "
        "mà BShoes mang lại cho cửa hàng giày, làm định hướng chung cho mọi Sprint."
    )
    c8.para(
        "BShoes hướng tới xây dựng một hệ thống bán hàng tại quầy chính xác: nhân viên "
        "lập hóa đơn nhanh, hàng trong giỏ tự động trừ kho và tự động hoàn kho khi hủy "
        "hoặc trả hàng, tránh sai lệch tồn kho vốn là rủi ro lớn nhất khi bán hàng thủ "
        "công. Song song đó, hệ thống quản lý sản phẩm, biến thể theo size và màu, "
        "khách hàng, bảo hành và đơn giao hàng, đồng thời phân quyền chi tiết tới từng "
        "nhân viên theo vai trò. Ở giai đoạn xa hơn (Sprint 5, 6), sản phẩm mở rộng "
        "thêm kênh online cho khách xem và đặt hàng, cùng báo cáo doanh thu để chủ cửa "
        "hàng theo dõi tình hình kinh doanh."
    )
    c8.para(
        "Tóm lại, mục tiêu sản phẩm lấy độ chính xác của nghiệp vụ bán hàng tại quầy "
        "làm trọng tâm, các phần còn lại là lớp mở rộng xây dựng trên nền đó, đúng thứ "
        "tự ưu tiên mà nhóm đã xếp trong Release Backlog ở %s." % parta_ref_ch6
    )

    c8.h2(2, "Mục tiêu của Product Backlog")
    c8.para(
        "Ngoài mục tiêu sản phẩm, nhóm cũng cần xác định rõ Product Backlog dùng để "
        "làm gì trong quá trình vận hành Scrum của dự án BShoes."
    )
    c8.para(
        "Product Backlog của BShoes là danh sách sống, tổng hợp toàn bộ %d Request thu "
        "thập từ actor, bóc tách thành %d Product Backlog và sắp theo Business Value "
        "cùng mức ưu tiên. Mục tiêu là giúp PO và cả nhóm luôn nhìn thấy việc cần làm "
        "trước, việc có thể hoãn, từ đó nhóm bốn thành viên với trình độ IT còn hạn chế "
        "biết tập trung đúng chỗ thay vì dàn trải. Với %d Request bị đưa về trạng thái "
        "Removed, Product Backlog còn đóng vai trò ghi nhận minh bạch phạm vi đã bị cắt "
        "giảm để không quên mất khi lập kế hoạch cho giai đoạn sau." % (N_RQ, N_PB, N_REMOVED)
    )
    c8.para(
        "Như vậy Product Backlog vừa là danh sách việc cần làm, vừa là công cụ quản lý "
        "phạm vi, giúp nhóm duy trì một nguồn thông tin duy nhất trong suốt %d Sprint."
        % N_SPRINT
    )

    # --------------------------------------------------------- Chương 9
    c9 = Chuong(doc, ch9, "Mục tiêu từng Sprint")
    c9.para(
        "Chương này trình bày mục tiêu cụ thể của từng Sprint trong số %d Sprint của "
        "dự án, kèm khung thời gian và khối lượng quy đổi ra số user story và Story "
        "Points. Đây là cách nhóm chia nhỏ mục tiêu sản phẩm ở chương %d thành các mốc "
        "có thể kiểm chứng theo tuần." % (N_SPRINT, ch8)
    )
    for s in SPRINTS:
        theme_line = s["theme"].split("\n")[0]
        c9.h2(s["num"], "Sprint %d: %s" % (s["num"], theme_line))
        c9.para(
            "Sprint %d diễn ra từ %s đến %s, do %s phụ trách chính, với chủ đề %s."
            % (s["num"], s["start"], s["end"], s["pic"], theme_line)
        )
        c9.para(
            "Mục tiêu: %s. Sprint này gồm %d user story, tương ứng %.2f Story Points."
            % (s["goal"], s["us"], s["pps"])
        )
    c9.para(
        "Nhìn xuyên suốt 6 Sprint, nhóm đi theo trình tự lập kế hoạch (Sprint 1), dựng "
        "bản build đầu tiên (Sprint 2), kiểm thử dữ liệu (Sprint 3), hoàn thiện và mở "
        "rộng tính năng nâng cao (Sprint 4), ghép luồng và ra mắt (Sprint 5, 6). Trình "
        "tự này đảm bảo phần lõi bán hàng luôn được dựng và kiểm thử trước khi mở rộng "
        "sang các tính năng phụ."
    )

    # --------------------------------------------------------- Chương 10
    c10 = Chuong(doc, ch10, "Phương pháp ước lượng")
    c10.para(
        "Chương này giải thích công thức ước lượng Story Point mà nhóm áp dụng cho "
        "toàn bộ Product Backlog, theo đúng mẫu ước lượng của môn học, đồng thời nêu "
        "rõ các tham số nhóm đã chọn để tính toán."
    )
    c10.h2(1, "Công thức UP, AP, PPS")
    c10.para(
        "Công thức ước lượng gồm ba bước liên tiếp: tính điểm chưa hiệu chỉnh, hiệu "
        "chỉnh theo hệ số nhân, rồi quy đổi theo hệ số môi trường của nhóm."
    )
    c10.bullet_head("UP, điểm chưa hiệu chỉnh")
    c10.bullet_item("UP = tổng 4 tiêu chí: Loại tương tác, Quy tắc nghiệp vụ, Số thực thể, Thao tác dữ liệu")
    c10.bullet_item("Mỗi tiêu chí chấm từ 1 đến 3 điểm tùy độ phức tạp")
    c10.bullet_head("AP, điểm đã hiệu chỉnh")
    c10.bullet_item("AP = UP nhân C, trong đó C là hệ số nhân do nhóm chọn, C = %d" % C_DEFAULT)
    c10.bullet_head("PPS, điểm cho mỗi Story")
    c10.bullet_item("PPS = (AP nhân ED) chia 36, với 36 là điểm ED tối đa có thể đạt")
    c10.bullet_head("ED, điểm môi trường")
    c10.bullet_item("Tổng %d yếu tố môi trường, mỗi yếu tố chấm theo thang quy định, tối đa %d điểm" % (ED_TOTAL, ED_MAX))
    c10.para(
        "Ba công thức trên nối tiếp nhau: UP phản ánh độ phức tạp thuần của user "
        "story, AP hiệu chỉnh theo hệ số nhân C, còn PPS đưa thêm yếu tố môi trường ED "
        "để ra Story Points cuối cùng, dùng thống nhất cho mọi bảng ước lượng ở chương "
        "%d." % ch11
    )

    c10.h2(2, "Hệ số môi trường ED và hệ số nhân C của nhóm")
    c10.para(
        "Sau khi có công thức, nhóm cần chốt giá trị cụ thể cho ED và C dựa trên đặc "
        "điểm thật của nhóm bốn người thực hiện dự án BShoes."
    )
    c10.para(
        "Nhóm tự chấm điểm môi trường ED = %d trên tối đa %d, tức hệ số môi trường ED "
        "= %d/%d = %.1f. Điểm này phản ánh đúng thực tế: nhóm mạnh về con người, tinh "
        "thần hợp tác tốt và đã cùng làm việc trước đó, nhưng còn yếu về công nghệ và "
        "hạ tầng, chưa có quy trình kiểm thử tự động hay CI/CD hoàn chỉnh. Về hệ số "
        "nhân, do tài liệu gốc không quy định giá trị bắt buộc, nhóm chọn C = %d, "
        "nghĩa là trung tính, không khuếch đại cũng không thu nhỏ điểm AP so với UP."
        % (ED_TOTAL, ED_MAX, ED_TOTAL, ED_MAX, ENV_FACTOR, C_DEFAULT)
    )
    c10.para(
        "Với ED = %.1f và C = %d, mọi Story Point trong dự án đều bị giảm còn một nửa "
        "so với năng suất lý tưởng, đúng như tinh thần ước lượng cẩn trọng cho một "
        "nhóm còn hạn chế về công nghệ và hạ tầng." % (ENV_FACTOR, C_DEFAULT)
    )

    # --------------------------------------------------------- Chương 11
    doc.add_page_break()
    c11 = Chuong(doc, ch11, "Bảng ước lượng User Story")
    c11.para(
        "Chương này áp dụng công thức và các tham số ở chương %d để trình bày Story "
        "Points của toàn bộ %d Request, cùng mức ưu tiên và Business Value tương ứng, "
        "làm căn cứ xếp Sprint đã trình bày ở %s và chương %d."
        % (ch10, N_RQ, parta_ref_ch7, ch9)
    )
    rows11 = [[r["id"], r["goal"], r["priority"], r["bv"], r["sp"], r["state"]] for r in RQS]
    c11.bang(
        "Ước lượng Story Point cho từng User Story",
        ["ID", "User Story", "Ưu tiên", "BV", "SP", "State"],
        rows11,
        [1.1, 6.5, 1.2, 1.6, 1.0, 1.6],
    )
    c11.para(
        "Tổng Story Points của %d Request là %.1f điểm, trong đó riêng %d Request "
        "trạng thái New đang triển khai chiếm %.1f điểm. Các Request có Business Value "
        "High tập trung ở nhóm đăng nhập, phân quyền và bán hàng, đúng với thứ tự ưu "
        "tiên 1 và 2, cho thấy bảng ước lượng nhất quán với cách nhóm xếp Release "
        "Backlog ở %s."
        % (N_RQ, sum(r["sp"] for r in RQS), N_NEW, TONG_SP_NEW, parta_ref_ch6)
    )

    # --------------------------------------------------------- Chương 12
    doc.add_page_break()
    c12 = Chuong(doc, ch12, "Sprint Backlog theo từng Sprint")
    c12.para(
        "Chương này chia Product Backlog thành Sprint Backlog cụ thể cho từng Sprint, "
        "mỗi Task gắn với một Product Backlog gốc, có thể kèm mã Request, số giờ ước "
        "tính và người phụ trách. Đây là mức chi tiết nhất mà Dev sử dụng hàng ngày để "
        "thực hiện công việc."
    )
    c12.para(
        "Tổng cộng %d Task được tách ra từ %d Product Backlog, với tổng khối lượng %d "
        "giờ công, trải đều trên %d Sprint. Các bảng dưới đây liệt kê Task theo từng "
        "Sprint, đúng theo Sprint đã xếp ở %s và chương %d."
        % (N_TASK, N_PB, TONG_GIO, N_SPRINT, parta_ref_ch7, ch9)
    )
    for s in SPRINTS:
        n = s["num"]
        c12.h3(1, n, "Sprint %d" % n)
        tasks_n = tasks_of_sprint(n)
        hrs = sum(t["est"] for t in tasks_n)
        c12.para(
            "Sprint %d có %d Task, tổng cộng %d giờ công, thực hiện từ %s đến %s."
            % (n, len(tasks_n), hrs, s["start"], s["end"])
        )
        rows12 = [
            [t["id"], t["backlog"], t["story"] if t["story"] else "", t["task"], t["est"], t["who"]]
            for t in tasks_n
        ]
        c12.bang(
            "Sprint Backlog Sprint %d" % n,
            ["Task ID", "Backlog (PB)", "Story (RQ)", "Nội dung task", "Giờ", "Phụ trách"],
            rows12,
            [1.2, 1.5, 1.5, 6.8, 1.0, 1.5],
        )
    c12.para(
        "Sprint Backlog cho thấy khối lượng Task lớn nhất rơi vào Sprint 5 và Sprint "
        "6, đúng thời điểm nhóm mở rộng kênh online cho khách và dựng báo cáo doanh "
        "thu, trong khi Sprint 1 nhẹ hơn vì chủ yếu là khảo sát và lập kế hoạch. Việc "
        "mỗi Task đều trỏ về đúng Product Backlog và Request gốc giúp truy vết công "
        "việc xuyên suốt ba cấp RQ, PB, Task mà không bị lệch."
    )


# ===========================================================================
# CHẠY TRỰC TIẾP: sinh asm1.docx = title + PART A (1..7) + PART B (8..12)
# ===========================================================================

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python gen_asm1.py <outdir>")
        sys.exit(1)

    OUTDIR = sys.argv[1]

    doc = setup(Document())

    title(
        doc,
        "BÁO CÁO TỔNG HỢP WORKSHOP 1 - 2",
        "%s, Nhóm 1" % PROJECT["name"],
    )

    # PHẦN A - WORKSHOP 1
    title(doc, "Phần A: Workshop 1", "Kế hoạch dự án và Product Backlog")
    build_ws1(doc, base=0, partb_ref="chương 12")

    # PHẦN B - WORKSHOP 2
    doc.add_page_break()
    title(doc, "Phần B: Workshop 2", "Mục tiêu, ước lượng và Sprint Backlog")
    build_ws2(doc, base=7, parta_ref_ch6="chương 6", parta_ref_ch7="chương 7")

    # KẾT LUẬN
    ket_luan(
        doc,
        [
            "Dự án BShoes xuất phát từ %d Request thu thập từ các actor, trong đó %d "
            "Request được đưa vào phạm vi triển khai và %d Request bị hoãn lại (nhóm %s) "
            "để nhóm bốn thành viên tập trung hoàn thiện phần lõi bán hàng tại quầy."
            % (N_RQ, N_NEW, N_REMOVED, DOI_TUONG_HOAN),
            "Toàn bộ Request được bóc tách thành %d Product Backlog theo nhóm chức năng, "
            "xếp vào %d Sprint kéo dài từ %s đến %s, cộng thêm giai đoạn %s để hoàn "
            "thiện tài liệu và báo cáo."
            % (N_PB, N_SPRINT, DU_AN_BAT_DAU, DU_AN_KET_THUC, DEFENSE["label"]),
            "Story Points được tính theo công thức UP, AP, PPS với hệ số môi trường ED = "
            "%d/%d (bằng %.1f) và hệ số nhân C = %d, phản ánh đúng thực trạng nhóm mạnh "
            "về con người nhưng còn hạn chế về công nghệ và hạ tầng. Tổng khối lượng dự "
            "án đạt %.2f Story Points và %d giờ công, được chia thành %d Task cụ thể cho "
            "từng Sprint."
            % (ED_TOTAL, ED_MAX, ENV_FACTOR, C_DEFAULT, TOTALS["pps"], TONG_GIO, N_TASK),
        ],
    )

    f = os.path.join(OUTDIR, "asm1.docx")
    doc.save(f)
    print("OK ->", f)
