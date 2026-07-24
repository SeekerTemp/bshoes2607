# -*- coding: utf-8 -*-
"""
gen_ws12.py
===========
Sinh HAI file Word độc lập cho Workshop 1 và Workshop 2 của dự án BShoes,
tái sử dụng nguyên vẹn build_ws1()/build_ws2() từ gen_asm1.py (cùng dữ liệu
xlsx qua ws_source.py, cùng khuôn định dạng docx_format.py).

    WORKSHOP_1_BShoes.docx           = title + Chương 1..7 (build_ws1)  + KẾT LUẬN
    WORKSHOP_2_MucTieu_BShoes.docx   = title + Chương 1..5 (build_ws2)  + KẾT LUẬN

Chạy:
    cd doc-school/_generator
    PYTHONIOENCODING=utf-8 python gen_ws12.py ..
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from docx import Document

from docx_format import setup, title, ket_luan
from ws_source import PROJECT, ED_TOTAL, ED_MAX, ENV_FACTOR, C_DEFAULT
from gen_asm1 import (
    build_ws1, build_ws2,
    N_RQ, N_NEW, N_REMOVED, N_PB, N_SPRINT, N_TASK, TONG_GIO,
    DOI_TUONG_HOAN, TOTALS,
)

if len(sys.argv) < 2:
    print("Usage: python gen_ws12.py <outdir>")
    sys.exit(1)

OUTDIR = sys.argv[1]

# ===========================================================================
# WORKSHOP 1 - Kế hoạch dự án và Product Backlog
# ===========================================================================

doc1 = setup(Document())

title(
    doc1,
    "WORKSHOP 1: Kế hoạch dự án và Product Backlog",
    "%s, Nhóm 1" % PROJECT["name"],
)

build_ws1(doc1, base=0)

ket_luan(
    doc1,
    [
        "Dự án BShoes xuất phát từ %d Request thu thập từ các actor, trong đó %d "
        "Request được đưa vào phạm vi triển khai và %d Request bị hoãn lại (nhóm %s) "
        "để nhóm bốn thành viên tập trung hoàn thiện phần lõi bán hàng tại quầy."
        % (N_RQ, N_NEW, N_REMOVED, DOI_TUONG_HOAN),
        "Toàn bộ %d Request được bóc tách thành %d nhóm chức năng (Product Backlog), "
        "xếp vào %d Sprint kèm giai đoạn bảo vệ đồ án, làm khung kế hoạch chung cho "
        "toàn bộ dự án."
        % (N_RQ, N_PB, N_SPRINT),
    ],
)

f1 = os.path.join(OUTDIR, "WORKSHOP_1_BShoes.docx")
doc1.save(f1)
print("OK ->", f1)

# ===========================================================================
# WORKSHOP 2 - Mục tiêu và ước lượng
# ===========================================================================

doc2 = setup(Document())

title(
    doc2,
    "WORKSHOP 2: Mục tiêu và ước lượng",
    "%s, Nhóm 1" % PROJECT["name"],
)

build_ws2(doc2, base=0)

ket_luan(
    doc2,
    [
        "Mục tiêu sản phẩm của BShoes lấy độ chính xác của nghiệp vụ bán hàng tại "
        "quầy làm trọng tâm, Product Backlog gồm %d Request được bóc tách thành %d "
        "nhóm chức năng để phục vụ mục tiêu đó qua %d Sprint." % (N_RQ, N_PB, N_SPRINT),
        "Story Points được tính theo công thức UP, AP, PPS với hệ số môi trường ED = "
        "%d/%d (bằng %.1f) và hệ số nhân C = %d, phản ánh đúng thực trạng nhóm mạnh "
        "về con người nhưng còn hạn chế về công nghệ và hạ tầng. Tổng khối lượng dự "
        "án đạt %.2f Story Points."
        % (ED_TOTAL, ED_MAX, ENV_FACTOR, C_DEFAULT, TOTALS["pps"]),
        "Sprint Backlog chi tiết hóa toàn bộ Product Backlog thành %d Task cụ thể, "
        "tổng cộng %d giờ công, mỗi Task trỏ về đúng Product Backlog và Request gốc "
        "để đảm bảo truy vết xuyên suốt ba cấp RQ, PB, Task." % (N_TASK, TONG_GIO),
    ],
)

f2 = os.path.join(OUTDIR, "WORKSHOP_2_MucTieu_BShoes.docx")
doc2.save(f2)
print("OK ->", f2)
