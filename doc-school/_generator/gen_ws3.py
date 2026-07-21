# -*- coding: utf-8 -*-
"""Sinh WORKSHOP_3_BShoes.docx: Tầm nhìn kiến trúc + Báo cáo + bảng Trello.

Theo 'Yêu cầu workshop 3.docx': (1) tầm nhìn kiến trúc (khái niệm, nội dung, đặc
điểm, vai trò, ví dụ, lợi ích); (2) báo cáo; (3) bảng Trello gồm cột Thành viên,
Product Backlog và 6 cột Sprint.

Chạy:  python gen_ws3.py <thư mục xuất>
"""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from bshoes_data import *
from docx import Document
from docx_format import setup, title, Chuong, ket_luan

OUTDIR = sys.argv[1]
TONG_GIO = sum(t["est"] for t in TASKS)

doc = setup(Document())
title(doc, "Tầm nhìn kiến trúc và bảng Trello " + PROJECT, "WORKSHOP 3, phiên bản %s" % VERSION)

# ---------------------------------------------------------- Chương 1
c1 = Chuong(doc, 1, "Tầm nhìn kiến trúc")
c1.para("Chương này trình bày khái niệm, đặc điểm, vai trò và lợi ích của tầm nhìn kiến "
        "trúc, kèm ví dụ kiến trúc thực tế của hệ thống BShoes. Đây là bức tranh chung để "
        "cả nhóm thống nhất cấu trúc trước khi lập trình chi tiết.")

c1.h2(1, "Khái niệm và nội dung")
c1.para("Tầm nhìn kiến trúc (Architecture Vision) là mô tả tổng thể ở mức cao về cách hệ "
        "thống được chia thành các thành phần, cách các thành phần giao tiếp và những ràng "
        "buộc công nghệ quan trọng. Nội dung của tầm nhìn kiến trúc gồm các tầng chính, công "
        "nghệ cho mỗi tầng, luồng dữ liệu giữa các tầng và các quyết định thiết kế cốt lõi "
        "như trừ kho nguyên tử hay phân quyền tập trung.")

c1.h2(2, "Đặc điểm và vai trò")
c1.bullet_head("Đặc điểm")
for it in ["Ở mức cao, không đi vào chi tiết cài đặt từng hàm",
           "Ổn định, ít thay đổi trong suốt dự án, làm điểm tựa cho mọi Sprint",
           "Dễ truyền đạt, một sơ đồ hoặc một bảng là cả nhóm hiểu chung"]:
    c1.bullet_item(it)
c1.bullet_head("Vai trò")
for it in ["Định hướng cho nhóm phát triển hiểu chung một cấu trúc",
           "Là căn cứ để chia task và phân công theo tầng",
           "Giảm rủi ro tích hợp vì đã thống nhất ranh giới giữa các thành phần"]:
    c1.bullet_item(it)

c1.h2(3, "Ví dụ: kiến trúc BShoes")
c1.para("BShoes theo kiến trúc phân tầng client, server và cơ sở dữ liệu. Giao diện là ứng "
        "dụng một trang (SPA) gọi API REST của server; server xử lý nghiệp vụ và truy cập cơ "
        "sở dữ liệu qua JPA. Bảng 1.1 mô tả trách nhiệm từng tầng.")
c1.bang("Các tầng kiến trúc của BShoes", ["Tầng", "Công nghệ", "Trách nhiệm"], [
    ["Giao diện (Client)", "Vue 3 SPA, Chart.js", "Màn hình POS, quản trị, cửa hàng online, biểu đồ thống kê"],
    ["Dịch vụ (Server)", "Spring Boot, REST API", "Nghiệp vụ, xác thực, phân quyền, trừ kho nguyên tử"],
    ["Truy cập dữ liệu", "Spring Data JPA", "Ánh xạ thực thể, truy vấn, quản lý giao dịch"],
    ["Cơ sở dữ liệu", "SQL Server", "Lưu trữ sản phẩm, hóa đơn, khách hàng, bảo hành, đặt trước"],
], [3.4, 4.2, 8.4])

c1.h2(4, "Lợi ích")
for it in ["Tách giao diện và nghiệp vụ nên hai phía phát triển song song",
           "Trừ kho nguyên tử ở tầng dịch vụ nên không bao giờ bán vượt tồn kho",
           "Phân quyền tập trung ở server nên kiểm soát truy cập tới từng nhân viên",
           "Đổi công nghệ một tầng ít ảnh hưởng tầng khác nhờ ranh giới rõ ràng"]:
    c1.bullet_item(it)

# ---------------------------------------------------------- Chương 2
c2 = Chuong(doc, 2, "Báo cáo dự án")
c2.para("Chương này tổng hợp khối lượng, tiến độ và đặc điểm nhóm của dự án theo Scrum, làm "
        "báo cáo kèm bảng Trello ở chương 3.")

c2.h2(1, "Khối lượng theo ba cấp")
c2.bang("Tổng hợp khối lượng dự án", ["Chỉ số", "Giá trị"], [
    ["Số Request (RQ)", len(RQS)],
    ["Số Product Backlog (PB)", len(PBS)],
    ["Số Task", len(TASKS)],
    ["Tổng giờ công", "%d giờ" % TONG_GIO],
    ["Số Sprint", len(SPRINTS)],
    ["Hệ số môi trường ED", "%d/%d = %.2f" % (ED_TOTAL, ED_MAX, ED_TOTAL / ED_MAX)],
], [6.0, 6.0])

c2.h2(2, "Tiến độ theo Sprint")
rows = []
for sp, goal in SPRINTS:
    npb = len([p for p in PBS if p["sprint"] == sp])
    hrs = sum(t["est"] for t in TASKS if t["sprint"] == sp)
    rows.append(["Sprint %d" % sp, goal, npb, hrs])
c2.bang("Khối lượng từng Sprint", ["Sprint", "Mục tiêu", "Số User Story", "Giờ"],
        rows, [1.6, 7.4, 1.8, 1.2])

# ---------------------------------------------------------- Chương 3
c3 = Chuong(doc, 3, "Bảng Trello dự án")
c3.para("Chương này mô tả bảng Trello của dự án gồm cột danh sách thành viên, cột Product "
        "Backlog và 6 cột Sprint từ Sprint 1 đến Sprint 6. Mỗi thẻ trên cột Sprint là một "
        "user story (PB) được xếp vào Sprint đó.")

c3.h2(1, "Cột danh sách thành viên")
c3.bang("Thành viên, vai trò và công việc", ["Vai trò", "Thành viên", "Công việc phụ trách"],
        [[a, n, r] for a, n, r in TEAM], [2.4, 3.4, 9.2])

c3.h2(2, "Cột Product Backlog")
c3.para("Cột Product Backlog liệt kê toàn bộ %d Request. Mỗi Request là một thẻ, khi kéo vào "
        "Sprint sẽ bung ra thành các user story con." % len(RQS))
c3.bang("Cột Product Backlog (theo Request)", ["Backlog ID", "Tên Request", "Số user story", "Sprint"],
        [[r["id"], r["ten"], len([p for p in PBS if p["rq"] == r["id"]]), "Sprint %d" % r["sprint"]] for r in RQS],
        [1.6, 6.0, 2.0, 2.4])

c3.h2(3, "Sáu cột Sprint")
c3.para("Mỗi cột Sprint chứa các thẻ user story sẽ hoàn thành trong Sprint đó. Bảng 3.3 liệt "
        "kê thẻ trên từng cột.")
rows = []
for sp, goal in SPRINTS:
    the = "; ".join("%s %s" % (p["id"], p["user_story"]) for p in PBS if p["sprint"] == sp)
    rows.append(["Sprint %d" % sp, goal, the])
c3.bang("Thẻ trên các cột Sprint", ["Cột", "Mục tiêu", "Các thẻ (Story ID + user story)"],
        rows, [1.6, 3.8, 10.6])

ket_luan(doc, [
    "Bảng Trello phản ánh đúng backlog đã chuẩn hóa: %d Request ở cột Product Backlog, bóc "
    "thành %d user story chia trên 6 cột Sprint, tổng %d Task và %d giờ công."
    % (len(RQS), len(PBS), len(TASKS), TONG_GIO),
    "Kiến trúc phân tầng client, server và cơ sở dữ liệu giúp nhóm phát triển song song, "
    "kiểm soát tồn kho bằng trừ kho nguyên tử và phân quyền tập trung tới từng nhân viên.",
])

f = os.path.join(OUTDIR, "WORKSHOP_3_BShoes.docx")
doc.save(f)
print("OK ->", f)
