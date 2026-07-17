# -*- coding: utf-8 -*-
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from bshoes_data import *
from openpyxl import Workbook
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.utils import get_column_letter

OUT = sys.argv[1]
GREEN = "0B895A"; LIGHT = "E7F4EF"; GREY = "EEF1F4"
hdr_font = Font(bold=True, color="FFFFFF", size=11)
hdr_fill = PatternFill("solid", fgColor=GREEN)
title_font = Font(bold=True, size=14, color=GREEN)
bold = Font(bold=True)
thin = Side(style="thin", color="D5DBE2")
border = Border(left=thin, right=thin, top=thin, bottom=thin)
wrap = Alignment(wrap_text=True, vertical="top")
ctr = Alignment(horizontal="center", vertical="center")

wb = Workbook()

def style_header(ws, row, ncols):
    for c in range(1, ncols + 1):
        cell = ws.cell(row=row, column=c)
        cell.font = hdr_font; cell.fill = hdr_fill; cell.border = border
        cell.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
    ws.freeze_panes = ws.cell(row=row + 1, column=1)

def widths(ws, ws_widths):
    for i, w in enumerate(ws_widths, start=1):
        ws.column_dimensions[get_column_letter(i)].width = w

# ============ 0. Tổng quan ============
ws = wb.active; ws.title = "0. Tổng quan"
ws["A1"] = PROJECT; ws["A1"].font = title_font
ws["A2"] = "WORKSHOP 2: Product Backlog, Sprint Backlog & Ước lượng Story"; ws["A2"].font = bold
ws["A3"] = "Phiên bản: " + VERSION
ws["A4"] = "Hạn hoàn thành: " + DEADLINE
r = 6
ws.cell(row=r, column=1, value="Cấu trúc tài liệu (3 cấp)").font = bold; r += 1
for line in ["Cấp 1, REQ: yêu cầu từ actor (người dùng thực tế)",
             "Cấp 2, PB (Product Backlog): PO bóc tách yêu cầu thành use case / user story, mỗi REQ có nhiều PB",
             "Cấp 3, TASK: mỗi PB được chia thành nhiều task để Dev thực hiện"]:
    ws.cell(row=r, column=1, value=line); r += 1
r += 1
ws.cell(row=r, column=1, value="Công thức ước lượng (theo tài liệu 'Ước lượng số lượng Story')").font = bold; r += 1
for line in ["UP  (Điểm chưa hiệu chỉnh) = Loại tương tác + Quy tắc nghiệp vụ + Số thực thể + Thao tác dữ liệu   (mỗi mục 1..3)",
             "AP  (Điểm đã hiệu chỉnh)   = UP × C   (C = hệ số nhân)",
             "PPS (Điểm cho mỗi Story)   = (AP × ED) / 36",
             "ED  = tổng 18 yếu tố môi trường, mỗi yếu tố 0 hoặc 2  →  tối đa 36"]:
    ws.cell(row=r, column=1, value=line); r += 1
r += 1
ws.cell(row=r, column=1, value="Kết quả tính cho nhóm").font = bold; r += 1
ws.cell(row=r, column=1, value="ED của nhóm"); ws.cell(row=r, column=2, value="='5. ED (Môi trường)'!D23"); r += 1
ws.cell(row=r, column=1, value="Hệ số môi trường ED/36"); ws.cell(row=r, column=2, value="='5. ED (Môi trường)'!D24"); r += 1
ws.cell(row=r, column=1, value="C (hệ số nhân)"); ws.cell(row=r, column=2, value="='6. Ước lượng Story'!C2"); r += 2
ws.cell(row=r, column=1, value="Nhóm thực hiện").font = bold; r += 1
ws.cell(row=r, column=1, value="Vai trò").font = bold
ws.cell(row=r, column=2, value="Thành viên").font = bold
ws.cell(row=r, column=3, value="Trách nhiệm").font = bold; r += 1
for code, name, resp in TEAM:
    ws.cell(row=r, column=1, value=code); ws.cell(row=r, column=2, value=name)
    ws.cell(row=r, column=3, value=resp); r += 1
widths(ws, [22, 26, 90])

# ============ 1. Request -> Backlog -> Task ============
ws = wb.create_sheet("1. REQ-PB-TASK")
cols = ["REQ ID (cấp 1)", "Actor", "Yêu cầu từ actor", "PB ID (cấp 2)", "User Story (use case)",
        "Task ID (cấp 3)", "Task", "Ước tính (giờ)", "Phụ trách"]
ws.append(cols); style_header(ws, 1, len(cols))
row = 2
for rq, actor, desc in REQS:
    pbs = [p for p in PBS if p[1] == rq]
    first_req = True
    for pb in pbs:
        tks = [t for t in TASKS if t[1] == pb[0]]
        if not tks:
            tks = [("", pb[0], "(chưa chia task)", "", 0, "")]
        first_pb = True
        for t in tks:
            ws.cell(row=row, column=1, value=rq if first_req else "")
            ws.cell(row=row, column=2, value=actor if first_req else "")
            ws.cell(row=row, column=3, value=desc if first_req else "")
            ws.cell(row=row, column=4, value=pb[0] if first_pb else "")
            ws.cell(row=row, column=5, value=pb[3] if first_pb else "")
            ws.cell(row=row, column=6, value=t[0])
            ws.cell(row=row, column=7, value=t[2])
            ws.cell(row=row, column=8, value=t[4])
            ws.cell(row=row, column=9, value=t[5])
            for c in range(1, len(cols) + 1):
                ws.cell(row=row, column=c).border = border
                ws.cell(row=row, column=c).alignment = wrap
            first_req = False; first_pb = False
            row += 1
widths(ws, [12, 20, 42, 10, 46, 10, 40, 12, 11])

# ============ 2. Product Backlog ============
ws = wb.create_sheet("2. Product Backlog")
cols = ["REQ", "ID", "Vai trò", "User Story", "Mô tả chức năng", "Độ ưu tiên", "Sprint", "Story Points"]
ws.append(cols); style_header(ws, 1, len(cols))
for i, pb in enumerate(PBS, start=2):
    ws.cell(row=i, column=1, value=pb[1]); ws.cell(row=i, column=2, value=pb[0])
    ws.cell(row=i, column=3, value=pb[2]); ws.cell(row=i, column=4, value=pb[3])
    ws.cell(row=i, column=5, value=pb[4]); ws.cell(row=i, column=6, value=pb[5])
    ws.cell(row=i, column=7, value=pb[6])
    ws.cell(row=i, column=8, value="=VLOOKUP(B%d,'6. Ước lượng Story'!$A$5:$L$%d,12,FALSE)" % (i, 4 + len(PBS)))
    for c in range(1, len(cols) + 1):
        ws.cell(row=i, column=c).border = border; ws.cell(row=i, column=c).alignment = wrap
    ws.cell(row=i, column=8).alignment = ctr
tot = len(PBS) + 2
ws.cell(row=tot, column=7, value="TỔNG").font = bold
ws.cell(row=tot, column=8, value="=SUM(H2:H%d)" % (len(PBS) + 1)).font = bold
widths(ws, [9, 9, 10, 52, 46, 11, 8, 13])

# ============ 3. Sprint Planning ============
ws = wb.create_sheet("3. Sprint Planning")
ws["A1"] = "Sprint Duration: 2 tuần"; ws["A1"].font = bold
cols = ["Sprint", "Mục tiêu Sprint (Sprint Goal)", "Số User Story", "Tổng Story Points", "Tổng giờ task"]
ws.append([]); ws.append(cols); style_header(ws, 3, len(cols))
r = 4
for sp, goal in SPRINTS:
    ws.cell(row=r, column=1, value="Sprint %d" % sp)
    ws.cell(row=r, column=2, value=goal)
    ws.cell(row=r, column=3, value="=COUNTIF('2. Product Backlog'!$G$2:$G$%d,%d)" % (len(PBS) + 1, sp))
    ws.cell(row=r, column=4, value="=SUMIF('2. Product Backlog'!$G$2:$G$%d,%d,'2. Product Backlog'!$H$2:$H$%d)" % (len(PBS) + 1, sp, len(PBS) + 1))
    ws.cell(row=r, column=5, value="=SUMIF('4. Sprint Backlog'!$G$2:$G$%d,%d,'4. Sprint Backlog'!$E$2:$E$%d)" % (len(TASKS) + 1, sp, len(TASKS) + 1))
    for c in range(1, len(cols) + 1):
        ws.cell(row=r, column=c).border = border; ws.cell(row=r, column=c).alignment = wrap
    r += 1
ws.cell(row=r, column=2, value="TỔNG").font = bold
ws.cell(row=r, column=3, value="=SUM(C4:C%d)" % (r - 1)).font = bold
ws.cell(row=r, column=4, value="=SUM(D4:D%d)" % (r - 1)).font = bold
ws.cell(row=r, column=5, value="=SUM(E4:E%d)" % (r - 1)).font = bold
widths(ws, [11, 70, 14, 17, 14])

# ============ 4. Sprint Backlog ============
ws = wb.create_sheet("4. Sprint Backlog")
cols = ["Task ID", "Product Backlog", "Task", "Mô tả công việc", "Estimate (giờ)", "Phụ trách", "Sprint", "Trạng thái"]
ws.append(cols); style_header(ws, 1, len(cols))
pb_sprint = {p[0]: p[6] for p in PBS}
for i, t in enumerate(TASKS, start=2):
    ws.cell(row=i, column=1, value=t[0]); ws.cell(row=i, column=2, value=t[1])
    ws.cell(row=i, column=3, value=t[2]); ws.cell(row=i, column=4, value=t[3])
    ws.cell(row=i, column=5, value=t[4]); ws.cell(row=i, column=6, value=t[5])
    ws.cell(row=i, column=7, value=pb_sprint.get(t[1], ""))
    ws.cell(row=i, column=8, value="Done")
    for c in range(1, len(cols) + 1):
        ws.cell(row=i, column=c).border = border; ws.cell(row=i, column=c).alignment = wrap
tot = len(TASKS) + 2
ws.cell(row=tot, column=4, value="TỔNG GIỜ").font = bold
ws.cell(row=tot, column=5, value="=SUM(E2:E%d)" % (len(TASKS) + 1)).font = bold
widths(ws, [9, 15, 38, 46, 13, 11, 8, 11])

# ============ 5. ED (Môi trường) ============
ws = wb.create_sheet("5. ED (Môi trường)")
ws["A1"] = "ED: Đánh giá môi trường (Environment Degree)"; ws["A1"].font = title_font
ws["A2"] = "Mỗi yếu tố chấm 0 hoặc 2 (2 = thuận lợi cho nhóm). Tổng tối đa = 18 yếu tố × 2 = 36."
cols = ["Khía cạnh", "Yếu tố", "Khoảng giá trị", "Giá trị (0/2)", "Lý do chấm điểm của nhóm"]
ws.append([]); ws.append(cols); style_header(ws, 4, len(cols))
r = 5
for aspect, factor, val, why in ED:
    ws.cell(row=r, column=1, value=aspect); ws.cell(row=r, column=2, value=factor)
    ws.cell(row=r, column=3, value="0 / 2"); ws.cell(row=r, column=4, value=val)
    ws.cell(row=r, column=5, value=why)
    for c in range(1, len(cols) + 1):
        ws.cell(row=r, column=c).border = border; ws.cell(row=r, column=c).alignment = wrap
    ws.cell(row=r, column=3).alignment = ctr; ws.cell(row=r, column=4).alignment = ctr
    if val == 2:
        ws.cell(row=r, column=4).fill = PatternFill("solid", fgColor=LIGHT)
    r += 1
first, last = 5, r - 1
ws.cell(row=r, column=2, value="ED: TỔNG ĐIỂM MÔI TRƯỜNG").font = bold
ws.cell(row=r, column=4, value="=SUM(D%d:D%d)" % (first, last)).font = bold
ws.cell(row=r, column=4).fill = PatternFill("solid", fgColor=GREY); ws.cell(row=r, column=4).alignment = ctr
ws.cell(row=r, column=5, value="Tối đa 36")
r += 1
ws.cell(row=r, column=2, value="Hệ số môi trường = ED / 36").font = bold
ws.cell(row=r, column=4, value="=D%d/36" % (r - 1)).font = bold
ws.cell(row=r, column=4).number_format = "0.00"; ws.cell(row=r, column=4).alignment = ctr
r += 2
ws.cell(row=r, column=2, value="Tổng theo từng khía cạnh (tham khảo)").font = bold; r += 1
seen = []
for aspect, _, _, _ in ED:
    if aspect in seen: continue
    seen.append(aspect)
    rows = [i for i, e in enumerate(ED) if e[0] == aspect]
    ws.cell(row=r, column=2, value=aspect)
    ws.cell(row=r, column=4, value="=SUM(%s)" % ",".join("D%d" % (5 + i) for i in rows))
    ws.cell(row=r, column=4).alignment = ctr
    ws.cell(row=r, column=5, value="Tối đa 6")
    r += 1
widths(ws, [26, 74, 14, 13, 58])

# ============ 6. Ước lượng Story ============
ws = wb.create_sheet("6. Ước lượng Story")
ws["A1"] = "Ước lượng số lượng Story: UP / AP / PPS"; ws["A1"].font = title_font
ws["A2"] = "C (hệ số nhân) ="; ws["A2"].font = bold
ws["C2"] = C_DEFAULT; ws["C2"].fill = PatternFill("solid", fgColor=LIGHT); ws["C2"].font = bold
ws["D2"] = "ED ="; ws["D2"].font = bold
ws["E2"] = "='5. ED (Môi trường)'!D23"; ws["E2"].font = bold
ws["F2"] = "PPS = (AP × ED)/36 ;  AP = UP × C ;  UP = tổng 4 tiêu chí (mỗi tiêu chí 1..3)"
cols = ["ID", "User Story", "Loại tương tác\n(1-3)", "Quy tắc nghiệp vụ\n(1-3)", "Số thực thể\n(1-3)",
        "Thao tác dữ liệu\n(1-3)", "UP", "C", "AP", "ED", "PPS", "Story Point\n(Fibonacci)"]
ws.append([]); ws.append(cols); style_header(ws, 4, len(cols))
r = 5
for pb in PBS:
    pid, rq, role, story, mo_ta, uu, sp, tt, qt, te, td = pb
    ws.cell(row=r, column=1, value=pid); ws.cell(row=r, column=2, value=story)
    ws.cell(row=r, column=3, value=tt); ws.cell(row=r, column=4, value=qt)
    ws.cell(row=r, column=5, value=te); ws.cell(row=r, column=6, value=td)
    ws.cell(row=r, column=7, value="=SUM(C%d:F%d)" % (r, r))
    ws.cell(row=r, column=8, value="=$C$2")
    ws.cell(row=r, column=9, value="=G%d*H%d" % (r, r))
    ws.cell(row=r, column=10, value="=$E$2")
    ws.cell(row=r, column=11, value="=I%d*J%d/36" % (r, r))
    ws.cell(row=r, column=12, value=("=IF(K{r}<=1,1,IF(K{r}<=2,2,IF(K{r}<=3,3,IF(K{r}<=5,5,"
                                     "IF(K{r}<=8,8,IF(K{r}<=13,13,21))))))").format(r=r))
    for c in range(1, len(cols) + 1):
        ws.cell(row=r, column=c).border = border
        ws.cell(row=r, column=c).alignment = ctr if c != 2 else wrap
    ws.cell(row=r, column=11).number_format = "0.00"
    r += 1
ws.cell(row=r, column=2, value="TỔNG").font = bold
ws.cell(row=r, column=7, value="=SUM(G5:G%d)" % (r - 1)).font = bold
ws.cell(row=r, column=11, value="=SUM(K5:K%d)" % (r - 1)).font = bold
ws.cell(row=r, column=11).number_format = "0.00"
ws.cell(row=r, column=12, value="=SUM(L5:L%d)" % (r - 1)).font = bold
widths(ws, [8, 52, 12, 13, 11, 12, 7, 7, 8, 7, 9, 12])

wb.save(OUT)
print("OK ->", OUT)
print("ED =", ED_TOTAL, "/", ED_MAX, "=> he so", round(ED_TOTAL / ED_MAX, 2))
print("PBs:", len(PBS), " Tasks:", len(TASKS), " Tong gio:", sum(t[4] for t in TASKS))
