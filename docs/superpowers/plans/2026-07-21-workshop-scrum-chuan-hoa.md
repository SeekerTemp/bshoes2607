# Chuẩn hóa Workshop 1-2-3 theo Scrum - Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Sinh lại bộ tài liệu Workshop 1-2-3 của BShoes theo đúng chuẩn Scrum của môn học, với mô hình ID 3 cấp RQ→PB→Task đánh số theo Sprint và đủ 3 artifact Product/Release/Sprint Backlog.

**Architecture:** Toàn bộ tài liệu sinh từ `bshoes_data.py` + generator. Thêm cấu trúc RQ, gắn `rq`+`sprint` cho từng PB, viết hàm renumber tính PB/Task ID theo thứ tự sprint, rồi cập nhật các generator để in đúng cột theo file mẫu `doc-school-request`. Một script kiểm bất biến ID đóng vai trò "test".

**Tech Stack:** Python 3.10, python-docx, openpyxl. Chạy trong `doc-school/_generator/`.

---

## File Structure

- Modify `doc-school/_generator/bshoes_data.py`: thêm `RQS`, đổi cấu trúc `PBS` (thêm rq + sprint), hàm `build_ids()` renumber, `SPRINTS` mới, các map dẫn xuất.
- Create `doc-school/_generator/check_ids.py`: kiểm bất biến ID (test).
- Modify `doc-school/_generator/gen_docx.py`: WS1 dùng Product Backlog (RQ) + Release Backlog; WS2 thêm per-sprint.
- Modify `doc-school/_generator/gen_ws2_xlsx.py`: sheet Product Backlog (RQ), Release Backlog (PB), Sprint Backlog tách sprint.
- Create `doc-school/_generator/gen_ws3.py`: WS3 docx.
- Regenerate: `WORKSHOP_1_BShoes.docx`, `WORKSHOP_2_MucTieu_BShoes.docx`, `WORKSHOP_2_BShoes.xlsx`, `WORKSHOP_3_BShoes.docx`.

Convention giữ nguyên (từ `_generator/README.md`): TNR 13, số Ả Rập, caption H5/H6, chú thích bảng trên/hình dưới, mỗi chương mở bài 2-3 câu + tối thiểu 1 bảng/hình, kết luận cuối. **Cấm em dash và en dash**.

---

## Task 1: Cấu trúc dữ liệu RQ + renumber theo sprint

**Files:**
- Modify: `doc-school/_generator/bshoes_data.py`
- Test: `doc-school/_generator/check_ids.py`

- [ ] **Step 1: Viết script kiểm bất biến (test) `check_ids.py`**

```python
# -*- coding: utf-8 -*-
"""Kiem bat bien ID: RQ->PB->Task lien tuc, truy vet du, dung thu tu sprint."""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from bshoes_data import RQS, PBS, TASKS, SPRINTS

def fail(m):
    print("FAIL:", m); sys.exit(1)

rq_ids = {r["id"] for r in RQS}
pb_ids = [p["id"] for p in PBS]
sprints = {s[0] for s in SPRINTS}

# 1. PB lien tuc PB-01..PB-nn, moi PB co RQ ton tai + sprint hop le
for i, p in enumerate(PBS, 1):
    if p["id"] != "PB-%02d" % i: fail("PB khong lien tuc tai %s (mong PB-%02d)" % (p["id"], i))
    if p["rq"] not in rq_ids: fail("%s tro RQ khong ton tai: %s" % (p["id"], p["rq"]))
    if p["sprint"] not in sprints: fail("%s sprint la %s" % (p["id"], p["sprint"]))

# 2. PB sap dung thu tu sprint (khong giam)
sp_seq = [p["sprint"] for p in PBS]
if sp_seq != sorted(sp_seq): fail("PB khong chay theo thu tu sprint: %s" % sp_seq)

# 3. Task lien tuc T-01.., Story ID tro PB ton tai
for i, t in enumerate(TASKS, 1):
    if t["id"] != "T-%02d" % i: fail("Task khong lien tuc tai %s (mong T-%02d)" % (t["id"], i))
    if t["story"] not in pb_ids: fail("%s Story ID khong ton tai: %s" % (t["id"], t["story"]))

# 4. Task nhom lien tuc theo PB (khong dan xen)
seen = []
for t in TASKS:
    if not seen or seen[-1] != t["story"]:
        if t["story"] in seen: fail("Task cua %s bi tach lam nhieu cho" % t["story"])
        seen.append(t["story"])

# 5. Backlog ID cua task = RQ cua story
pb_rq = {p["id"]: p["rq"] for p in PBS}
for t in TASKS:
    if t["backlog"] != pb_rq[t["story"]]:
        fail("%s Backlog ID %s != RQ cua story (%s)" % (t["id"], t["backlog"], pb_rq[t["story"]]))

print("OK: %d RQ, %d PB, %d Task. Bat bien ID dat." % (len(RQS), len(PBS), len(TASKS)))
```

- [ ] **Step 2: Chạy test, xác nhận FAIL vì bshoes_data chưa có RQS/cấu trúc mới**

Run: `cd doc-school/_generator && PYTHONIOENCODING=utf-8 python check_ids.py`
Expected: FAIL (ImportError RQS hoặc PBS chưa phải dict).

- [ ] **Step 3: Viết lại `bshoes_data.py` phần RQ + PB + renumber**

Thay khối `REQS` và `PBS` cũ. Giữ nguyên `_T` (nội dung task) nhưng cột đầu vẫn là PB key CŨ để không phải gõ lại 90 dòng; renumber sẽ ánh xạ key cũ sang PB mới.

```python
# ---- RQ: request tu actor (As a/I want/So that) ----
# id, ten, role, goal, so_that, priority(1..4), business_value, sprint
RQS = [
 {"id":"RQ-01","ten":"Đăng nhập","role":"nhân viên","goal":"đăng nhập và được cấp đúng quyền theo vai trò","so_that":"dùng hệ thống an toàn, đúng phạm vi","priority":1,"bv":"High","sprint":1},
 {"id":"RQ-02","ten":"Nhân viên & Phân quyền","role":"quản trị viên","goal":"quản lý nhân viên và phân quyền truy cập theo vai trò và theo từng người","so_that":"kiểm soát ai được dùng chức năng nào","priority":1,"bv":"High","sprint":1},
 {"id":"RQ-03","ten":"Quản lý sản phẩm & danh mục","role":"quản lý","goal":"quản lý sản phẩm, biến thể màu/size, tồn kho, danh mục và thuộc tính","so_that":"quản trị được toàn bộ hàng hóa","priority":2,"bv":"High","sprint":2},
 {"id":"RQ-04","ten":"Bán hàng tại quầy (POS)","role":"nhân viên bán hàng","goal":"bán tại quầy, tự trừ kho, thanh toán và in hóa đơn","so_that":"phục vụ khách nhanh và chính xác","priority":1,"bv":"High","sprint":3},
 {"id":"RQ-05","ten":"Đơn đặt hàng & Giao hàng","role":"nhân viên giao hàng","goal":"tạo và theo dõi đơn giao, xử lý trả hàng và hoàn kho","so_that":"giao hàng tận nơi có kiểm soát","priority":2,"bv":"Medium","sprint":4},
 {"id":"RQ-06","ten":"Quản lý khách hàng","role":"quản lý","goal":"quản lý khách hàng và địa chỉ giao hàng","so_that":"chăm sóc khách và giao đúng địa chỉ","priority":2,"bv":"Medium","sprint":4},
 {"id":"RQ-07","ten":"Cửa hàng online & Preorder","role":"khách hàng","goal":"xem cửa hàng online, mua giao tận nhà và đặt trước mẫu hết hàng","so_that":"mua sắm online thuận tiện","priority":3,"bv":"Medium","sprint":5},
 {"id":"RQ-08","ten":"Dashboard & Báo cáo doanh thu","role":"kế toán và chủ shop","goal":"xem KPI, doanh thu, lợi nhuận, ROI và xuất báo cáo","so_that":"ra quyết định kinh doanh dựa trên dữ liệu","priority":3,"bv":"High","sprint":6},
 {"id":"RQ-09","ten":"Sau bán hàng: Bảo hành & Khuyến mãi","role":"nhân viên bảo hành và quản lý","goal":"tiếp nhận và xử lý bảo hành, quản lý phiếu giảm giá và khuyến mãi","so_that":"chăm sóc khách sau bán và thúc đẩy doanh số","priority":3,"bv":"Medium","sprint":6},
]

# ---- PB: user story bóc từ RQ. Khai báo bằng KEY cũ để tái dùng _T. ----
# key_cu, rq, role_code, user_story, mo_ta, priority, bv, (tuong_tac, quy_tac, thuc_the, thao_tac)
_PB = [
 ("PB-01","RQ-01","NV","Là nhân viên, tôi muốn đăng nhập để dùng hệ thống theo đúng quyền","Xác thực tài khoản, lấy vai trò & quyền truy cập màn hình",1,"High",(2,2,2,1)),
 ("PB-29","RQ-02","ADMIN","Là quản trị, tôi muốn CRUD nhân viên","Thêm/sửa/xóa/tìm nhân viên, gán vai trò",1,"High",(2,2,2,3)),
 ("PB-30","RQ-02","ADMIN","Là quản trị, tôi muốn đặt bộ quyền mặc định cho từng vai trò","Vai trò là template: bộ quyền mặc định chép cho nhân viên khi tạo mới hoặc đổi vai trò",1,"High",(2,3,2,2)),
 ("PB-38","RQ-02","ADMIN","Là quản trị, tôi muốn phân quyền truy cập cho từng nhân viên","Lưới nhân viên x màn hình; vai trò chỉ là template, mỗi người mở rộng thêm được",1,"High",(3,3,3,3)),
 ("PB-10","RQ-03","QL","Là quản lý, tôi muốn CRUD sản phẩm để quản trị danh mục hàng","Thêm/sửa/xóa mềm/khôi phục/thùng rác sản phẩm",2,"High",(2,3,3,3)),
 ("PB-11","RQ-03","QL","Là quản lý, tôi muốn quản lý biến thể (màu/size/giá/giá nhập)","CRUD biến thể, sửa giá bán & giá vốn, trạng thái bán",2,"High",(2,3,3,3)),
 ("PB-12","RQ-03","NK","Là nhân viên kho, tôi muốn nhập kho để tăng tồn","Cộng số lượng tồn cho biến thể",2,"Medium",(1,1,1,1)),
 ("PB-13","RQ-03","QL","Là quản lý, tôi muốn quản lý danh mục và xếp sản phẩm vào danh mục","CRUD danh mục, gán/bỏ sản phẩm khỏi danh mục",2,"Medium",(2,2,2,2)),
 ("PB-14","RQ-03","QL","Là quản lý, tôi muốn quản lý thuộc tính sản phẩm","CRUD 8 nhóm thuộc tính, lọc theo đúng loại",2,"Medium",(2,2,3,3)),
 ("PB-40","RQ-03","QL","Là quản lý, tôi muốn thao tác sản phẩm, danh mục, thuộc tính trong cùng một màn","Gộp danh mục và thuộc tính thành tab của Quản lý sản phẩm, bớt nhảy màn",3,"Low",(2,1,1,1)),
 ("PB-02","RQ-04","NV","Là nhân viên, tôi muốn tạo hóa đơn chờ để bắt đầu một lượt bán","Sinh mã HĐ, trạng thái Chờ, tối đa 3 hóa đơn chờ",1,"High",(2,2,2,2)),
 ("PB-03","RQ-04","NV","Là nhân viên, tôi muốn thêm hàng vào giỏ và hệ thống tự trừ kho","Thêm dòng hóa đơn, trừ tồn nguyên tử, tính lại tổng tiền",1,"High",(3,3,3,3)),
 ("PB-04","RQ-04","NV","Là nhân viên, tôi muốn áp phiếu giảm giá cho hóa đơn","Kiểm tra đơn tối thiểu, loại %/tiền, giảm tối đa",2,"Medium",(3,3,2,2)),
 ("PB-05","RQ-04","NV","Là nhân viên, tôi muốn thanh toán tiền mặt / chuyển khoản / kết hợp","Kiểm tiền khách đưa, VietQR, chốt trạng thái, ghi lịch sử",1,"High",(3,3,3,3)),
 ("PB-06","RQ-04","NV","Là nhân viên, tôi muốn in hóa đơn / phiếu tạm tính cho khách","Kết xuất phiếu và gửi lệnh in",1,"High",(3,1,2,1)),
 ("PB-07","RQ-04","NV","Là nhân viên, tôi muốn hủy hóa đơn và hoàn lại kho","Đổi trạng thái Hủy, cộng trả tồn kho, ghi lịch sử",2,"Medium",(3,2,3,3)),
 ("PB-08","RQ-04","NV","Là nhân viên, tôi muốn quét QR/mã vạch để thêm sản phẩm nhanh","Bật camera, giải mã, tra cứu biến thể theo mã",2,"Medium",(3,2,2,2)),
 ("PB-09","RQ-04","NV","Là nhân viên, tôi muốn thêm nhanh khách hàng ngay tại quầy","Popup thêm khách (chỉ thêm, không xem danh sách)",2,"Medium",(2,1,1,1)),
 ("PB-15","RQ-05","NV","Là nhân viên, tôi muốn tạo đơn giao hàng kèm phí ship","Thanh toán + phí ship, chuyển trạng thái Chờ giao",2,"High",(2,3,3,3)),
 ("PB-16","RQ-05","NV","Là nhân viên, tôi muốn cập nhật trạng thái Đã giao","Chuyển Chờ giao -> Đã giao, ghi lịch sử",2,"Medium",(1,2,2,2)),
 ("PB-17","RQ-05","QL","Là quản lý, tôi muốn xử lý trả hàng và hoàn kho","Trạng thái Trả hàng, cộng trả tồn, ghi lịch sử",2,"Medium",(2,3,3,3)),
 ("PB-21","RQ-06","QL","Là quản lý, tôi muốn CRUD khách hàng","Thêm/sửa/xóa/tìm kiếm khách hàng",2,"Medium",(2,2,1,3)),
 ("PB-22","RQ-06","QL","Là quản lý, tôi muốn quản lý địa chỉ giao hàng của khách","CRUD nhiều địa chỉ cho mỗi khách",2,"Medium",(2,2,2,3)),
 ("PB-31","RQ-07","KH","Là khách hàng, tôi muốn xem trang chủ cửa hàng","Danh mục giày, sản phẩm, sắp xếp giá/bán chạy",3,"Medium",(2,2,3,1)),
 ("PB-33","RQ-07","KH","Là khách hàng, tôi muốn thêm giày vào giỏ hàng","Giỏ lưu ở trình duyệt, sửa số lượng, chặn vượt tồn kho",3,"Medium",(2,2,1,3)),
 ("PB-34","RQ-07","KH","Là khách hàng, tôi muốn đặt hàng giao tận nhà (COD)","Nhập người nhận/SĐT/địa chỉ, tạo đơn Chờ giao, trừ kho",2,"High",(3,3,3,2)),
 ("PB-35","RQ-07","KH","Là khách hàng, tôi muốn tra cứu đơn của tôi và xác nhận đã nhận","Tra theo SĐT, xem trạng thái, bấm Đã nhận hàng",3,"Medium",(2,2,2,2)),
 ("PB-36","RQ-07","KH","Là khách hàng, tôi muốn đặt trước mẫu đang hết hàng","Chỉ cho đặt khi tồn = 0, để lại SĐT, xem số người đang chờ",3,"Medium",(2,3,2,2)),
 ("PB-37","RQ-07","NV","Là nhân viên, tôi muốn quản lý phiếu đặt trước và chuyển thành đơn khi hàng về","Tab trạng thái, báo khách có hàng, chuyển phiếu thành hóa đơn giữ hàng",3,"Medium",(2,3,3,3)),
 ("PB-39","RQ-07","KH","Là khách hàng, tôi muốn xem trang chi tiết sản phẩm","Chọn màu/size; còn hàng thì mua, hết hàng thì đặt trước",2,"High",(3,2,3,1)),
 ("PB-23","RQ-08","KT","Là kế toán, tôi muốn xem KPI tổng quan","Doanh thu, lợi nhuận, số đơn theo trạng thái, đơn TB",1,"High",(1,3,3,1)),
 ("PB-24","RQ-08","KT","Là kế toán, tôi muốn xem biểu đồ dòng tiền theo tháng","Doanh thu / giá vốn / lợi nhuận 12 tháng, lọc theo năm",2,"High",(2,3,3,1)),
 ("PB-25","RQ-08","KT","Là kế toán, tôi muốn xem ROI theo sản phẩm","(Doanh thu - giá vốn)/giá vốn, xếp hạng",2,"Medium",(2,3,3,1)),
 ("PB-26","RQ-08","KT","Là kế toán, tôi muốn xem tỷ lệ giữ chân khách hàng","Khách quay lại vs khách mới",2,"Medium",(1,3,2,1)),
 ("PB-27","RQ-08","KT","Là kế toán, tôi muốn xem SP bán chạy / bán chậm / xu hướng","Xếp hạng theo số bán, tăng trưởng so kỳ trước",2,"Medium",(2,3,3,1)),
 ("PB-28","RQ-08","KT","Là kế toán, tôi muốn xuất báo cáo ra Excel","Kết xuất KPI + sản phẩm bán chạy ra file",3,"Low",(1,1,2,1)),
 ("PB-18","RQ-09","BH","Là NV bảo hành, tôi muốn tiếp nhận đơn bảo hành","Tạo phiếu BH gắn khách + sản phẩm + hóa đơn + serial",1,"High",(2,2,3,2)),
 ("PB-19","RQ-09","BH","Là NV bảo hành, tôi muốn cập nhật trạng thái xử lý bảo hành","6 trạng thái: chưa xử lý -> đã trả, lọc theo tab",1,"High",(2,3,2,2)),
 ("PB-20","RQ-09","BH","Là NV bảo hành, tôi muốn in phiếu bảo hành","Kết xuất phiếu bảo hành",3,"Low",(1,1,2,1)),
 ("PB-32","RQ-09","QL","Là quản lý, tôi muốn quản lý phiếu giảm giá","CRUD phiếu, thời hạn, điều kiện, trạng thái",2,"Medium",(2,3,1,3)),
]
```

Ngay sau `_PB`, viết hàm renumber (thứ tự khai báo trong `_PB` đã đúng theo sprint vì nhóm sẵn RQ-01..RQ-09; chỉ cần gán số mới và map key cũ):

```python
# ---- renumber PB theo thu tu khai bao (da nhom theo RQ/sprint) ----
ROLE_NAME = {"NV":"Nhân viên","QL":"Quản lý","NK":"Nhân viên kho","BH":"NV bảo hành",
             "KT":"Kế toán","ADMIN":"Quản trị","KH":"Khách hàng"}
_rq_sprint = {r["id"]: r["sprint"] for r in RQS}

PBS = []          # list dict da danh so
_oldkey_to_new = {}
for i, (oldkey, rq, role, story, mo_ta, prio, bv, up4) in enumerate(_PB, 1):
    new_id = "PB-%02d" % i
    _oldkey_to_new[oldkey] = new_id
    PBS.append({"id":new_id, "old":oldkey, "rq":rq, "role":role, "user_story":story,
                "mo_ta":mo_ta, "priority":prio, "bv":bv, "sprint":_rq_sprint[rq],
                "tuong_tac":up4[0], "quy_tac":up4[1], "thuc_the":up4[2], "thao_tac":up4[3]})
```

- [ ] **Step 4: Đổi khối `_T` sang dạng dùng key cũ + build TASKS renumber**

Giữ nguyên toàn bộ nội dung 90 dòng `_T` hiện có (cột 0 = PB key cũ). Chỉ thay dòng build `TASKS` ở cuối. Xóa 2 dòng task trùng của PB-30 v1.2 (đã gộp) nếu muốn, hoặc giữ vì cùng key PB-30 sẽ tự gom. Thay:

```python
# TASKS gom theo PB moi, danh so lai T-01..
_task_by_new = {}
for oldkey, task, mo_ta, est, who in _T:
    new_pb = _oldkey_to_new[oldkey]
    _task_by_new.setdefault(new_pb, []).append((task, mo_ta, est, who))

TASKS = []
_n = 0
for p in PBS:                       # duyet theo thu tu PB moi
    for task, mo_ta, est, who in _task_by_new.get(p["id"], []):
        _n += 1
        TASKS.append({"id":"T-%02d" % _n, "story":p["id"], "backlog":p["rq"],
                      "sprint":p["sprint"], "task":task, "mo_ta":mo_ta,
                      "est":est, "who":who})
```

- [ ] **Step 5: Cập nhật `SPRINTS` + xóa `REQS` cũ + giữ `ED`, `fib`, hằng số**

```python
SPRINTS = [
 (1, "Nền tảng: đăng nhập, nhân viên và phân quyền"),
 (2, "Quản lý sản phẩm: sản phẩm, biến thể, nhập kho, danh mục, thuộc tính"),
 (3, "Bán hàng tại quầy: giỏ hàng trừ kho, thanh toán, in hóa đơn, quét QR"),
 (4, "Đơn đặt hàng, giao hàng, trả hàng và quản lý khách hàng"),
 (5, "Cửa hàng online: trang chủ, chi tiết, giỏ hàng, đặt hàng COD, đặt trước"),
 (6, "Dashboard doanh thu, bảo hành và khuyến mãi"),
]
```

Giữ nguyên `ED`, `ED_TOTAL`, `ED_MAX`, `C_DEFAULT`, `fib`, `TEAM`, `PROJECT`, `VERSION`, `DEADLINE`.

- [ ] **Step 6: Chạy test, xác nhận PASS**

Run: `cd doc-school/_generator && PYTHONIOENCODING=utf-8 python check_ids.py`
Expected: `OK: 9 RQ, 40 PB, 90 Task. Bat bien ID dat.` (số task có thể khác nếu gộp PB-30; chấp nhận miễn bất biến pass)

- [ ] **Step 7: Commit**

```bash
git add doc-school/_generator/bshoes_data.py doc-school/_generator/check_ids.py
git commit -m "refactor(doc): mo hinh RQ->PB->Task, renumber ID theo sprint"
```

---

## Task 2: Cập nhật generator dùng API dict mới (không đổi output)

Các generator cũ đọc PBS/TASKS dạng tuple (pb[0], t[4]...). Sau Task 1 chúng là dict. Phải sửa mọi truy cập trước khi thêm nội dung mới, nếu không generator vỡ.

**Files:**
- Modify: `doc-school/_generator/gen_docx.py`
- Modify: `doc-school/_generator/gen_ws2_xlsx.py`

- [ ] **Step 1: Sửa `gen_docx.py` mọi chỗ đọc PBS/TASKS/REQS**

Thay công thức tổng ở đầu file:

```python
TONG_GIO = sum(t["est"] for t in TASKS)
TONG_SP = sum(fib((p["tuong_tac"]+p["quy_tac"]+p["thuc_the"]+p["thao_tac"]) * C_DEFAULT * ED_TOTAL / 36) for p in PBS)
```

Thay vòng lặp chương 3 (phân rã) sang duyệt RQS thay REQS, và đọc field dict. Thay vòng chương 4 (sprint) và WS2 chương 2/5 tương tự: `p[6]`→`p["sprint"]`, `p[0]`→`p["id"]`, `p[3]`→`p["user_story"]`, `t[4]`→`t["est"]`, `t[1]`→`t["story"]`, `t[2]`→`t["task"]`. Chi tiết cột mới nằm ở Task 3-4; bước này chỉ cần generator chạy lại được.

- [ ] **Step 2: Sửa `gen_ws2_xlsx.py` tương tự sang field dict**

Các sheet đọc `pb[0..10]`, `t[0..5]`, `REQS`. Đổi sang key dict và `RQS`. Giữ công thức Excel (SUM/COUNTIF/SUMIF/VLOOKUP) nguyên vẹn, chỉ đổi nguồn cột.

- [ ] **Step 3: Chạy sinh thử, xác nhận không lỗi**

Run:
```
cd doc-school/_generator && PYTHONIOENCODING=utf-8 python gen_docx.py .. && PYTHONIOENCODING=utf-8 python gen_ws2_xlsx.py ../WORKSHOP_2_BShoes.xlsx
```
Expected: in `OK ->` cho từng file, không traceback.

- [ ] **Step 4: Verify format docx**

Run: `PYTHONIOENCODING=utf-8 python verify_docx.py ../WORKSHOP_1_BShoes.docx ../WORKSHOP_2_MucTieu_BShoes.docx`
Expected: PASS, không phát hiện gạch ngang dài.

- [ ] **Step 5: Commit**

```bash
git add doc-school/_generator/gen_docx.py doc-school/_generator/gen_ws2_xlsx.py
git commit -m "refactor(doc): generator doc field dict moi, output tuong duong"
```

---

## Task 3: Workshop 1 - thêm Product Backlog (RQ) + Release Backlog

**Files:**
- Modify: `doc-school/_generator/gen_docx.py` (khối WS1)

- [ ] **Step 1: Chương 3 mới - Product Backlog cấp Request**

Sau chương "Danh sách chức năng", thêm chương Product Backlog in bảng RQ theo đúng cột file mẫu (`ID | As a/an [role] | I want to [goal] | So that [reason] | Priority | Business Value | State`). Acceptance Criteria để cột ngắn hoặc bỏ nếu chật; tối thiểu 7 cột trên.

```python
c = Chuong(doc, 3, "Product Backlog cấp Request")
c.para("Chương này liệt kê yêu cầu gốc từ các actor dưới dạng user story chuẩn "
       "As a, I want, So that. Mỗi request là một dòng của Product Backlog và là "
       "gốc để bóc tách thành các user story chi tiết ở chương sau.")
c.h2(1, "Bảng Product Backlog")
rows = [[r["id"], "Là %s" % r["role"], r["goal"], r["so_that"], r["priority"], r["bv"], "New"] for r in RQS]
c.bang("Product Backlog cấp Request (RQ)",
       ["ID", "As a/an (vai trò)", "I want to (mục tiêu)", "So that (lý do)", "Ưu tiên", "Business Value", "State"],
       rows, [1.3, 2.6, 5.2, 4.6, 1.2, 1.6, 1.2])
```

- [ ] **Step 2: Chương 4 - Quy trình + phân rã RQ→PB→Task**

Đổi chương phân rã cũ: duyệt `RQS`, mỗi RQ in H3 + bảng các PB thuộc RQ kèm task. Cột bảng: `PB | User Story | Task | Nội dung | Giờ | Phụ trách`.

```python
for idx, r in enumerate(RQS, 1):
    c4.h3(2, idx, "%s. %s (actor: %s)" % (r["id"], r["ten"], r["role"]))
    rows = []
    for pb in [p for p in PBS if p["rq"] == r["id"]]:
        tks = [t for t in TASKS if t["story"] == pb["id"]]
        for i, t in enumerate(tks):
            rows.append([pb["id"] if i==0 else "", pb["user_story"] if i==0 else "",
                         t["id"], t["task"], t["est"], t["who"]])
    c4.bang("Phân rã %s thành user story và task" % r["id"],
            ["PB", "User Story", "Task", "Nội dung task", "Giờ", "Phụ trách"],
            rows, [1.4, 4.8, 1.2, 5.0, 1.0, 1.6])
```

- [ ] **Step 3: Chương 5 - Release Backlog + chia Sprint**

In bảng Release Backlog theo cột file mẫu: `Backlog ID(RQ) | Backlog | User Story | Story ID(PB) | Priority | Business Value | Sprint#`. Sau đó bảng tóm tắt Sprint (giữ như cũ, đọc field dict).

```python
rows = []
for r in RQS:
    for pb in [p for p in PBS if p["rq"] == r["id"]]:
        rows.append([r["id"], r["ten"], pb["user_story"], pb["id"], pb["priority"], pb["bv"], "Sprint %d" % pb["sprint"]])
c5.bang("Release Backlog: user story theo Backlog và Sprint",
        ["Backlog ID", "Backlog", "User Story", "Story ID", "Ưu tiên", "Business Value", "Sprint"],
        rows, [1.3, 2.6, 5.0, 1.3, 1.1, 1.6, 1.3])
```

- [ ] **Step 4: Sinh + verify + kiểm mắt**

Run: `PYTHONIOENCODING=utf-8 python gen_docx.py .. && PYTHONIOENCODING=utf-8 python verify_docx.py ../WORKSHOP_1_BShoes.docx ../WORKSHOP_2_MucTieu_BShoes.docx`
Expected: OK, verify pass. Mở docx kiểm 3 bảng mới đúng cột.

- [ ] **Step 5: Commit**

```bash
git add doc-school/_generator/gen_docx.py
git commit -m "feat(ws1): them Product Backlog (RQ) va Release Backlog theo mau truong"
```

---

## Task 4: Workshop 2 - Sprint Backlog tách từng Sprint (docx + xlsx)

**Files:**
- Modify: `doc-school/_generator/gen_docx.py` (khối WS2)
- Modify: `doc-school/_generator/gen_ws2_xlsx.py` (sheet Product Backlog, Release Backlog, Sprint Backlog)

- [ ] **Step 1: WS2 docx - thêm chương Sprint Backlog theo sprint**

Thêm sau chương ước lượng: mỗi Sprint một H2, in bảng task của sprint đó theo cột mẫu WS2 PDF (`Task ID | Product Backlog | Task | Mô tả công việc | Estimate (giờ) | Trạng thái`, Trạng thái = To Do).

```python
cX.h2(1, "Sprint Backlog theo từng Sprint")
for sp, goal in SPRINTS:
    cX.h3(1, sp, "Sprint %d: %s" % (sp, goal))
    rows = [[t["id"], t["story"], t["task"], t["mo_ta"], t["est"], "To Do"]
            for t in TASKS if t["sprint"] == sp]
    cX.bang("Sprint Backlog Sprint %d" % sp,
            ["Task ID", "Product Backlog", "Task", "Mô tả công việc", "Estimate (giờ)", "Trạng thái"],
            rows, [1.2, 1.8, 3.8, 4.6, 1.6, 1.4])
```

- [ ] **Step 2: xlsx sheet "1. Product Backlog" = RQ**

Đổi sheet đầu list REQ sang RQ với cột mẫu Product Backlog: `ID | As a/an | I want to | So that | Priority | Business Value | Acceptance Criteria | State`.

```python
cols = ["ID", "As a/an [role]", "I want to [goal]", "So that [reason]", "Priority", "Business Value", "Acceptance Criteria", "State"]
for r in RQS: ... ws append [r["id"], "Là "+r["role"], r["goal"], r["so_that"], r["priority"], r["bv"], "", "New"]
```

- [ ] **Step 2b: xlsx sheet "2. Release Backlog" = PB có Sprint#**

Cột: `Backlog ID | Backlog | User Story | Story ID | Priority | Business Value | Sprint# | State`. Story point vẫn VLOOKUP từ sheet Ước lượng qua Story ID (PB).

- [ ] **Step 3: xlsx sheet "Sprint Backlog" tách tiêu đề từng sprint**

Trong sheet Sprint Backlog, thay vì đổ phẳng, chèn 1 dòng tiêu đề "Sprint N: mục tiêu" trước nhóm task của sprint đó (giống file mẫu gom theo module). Giữ cột `Task ID | Product Backlog | Task | Mô tả | Estimate | Phụ trách | Sprint | Trạng thái`, Trạng thái = To Do. Duyệt theo `SPRINTS`, lọc `TASKS` theo sprint.

- [ ] **Step 4: Sinh + verify**

Run:
```
cd doc-school/_generator && PYTHONIOENCODING=utf-8 python gen_docx.py .. && PYTHONIOENCODING=utf-8 python gen_ws2_xlsx.py ../WORKSHOP_2_BShoes.xlsx && PYTHONIOENCODING=utf-8 python verify_docx.py ../WORKSHOP_1_BShoes.docx ../WORKSHOP_2_MucTieu_BShoes.docx
```
Expected: OK. Mở xlsx kiểm sheet Sprint Backlog có tiêu đề từng sprint và mọi task có Sprint# đúng.

- [ ] **Step 5: Commit**

```bash
git add doc-school/_generator/gen_docx.py doc-school/_generator/gen_ws2_xlsx.py
git commit -m "feat(ws2): Sprint Backlog tach tung sprint + Product/Release Backlog theo mau"
```

---

## Task 5: Workshop 3 - Kiến trúc + Trello (docx mới)

**Files:**
- Create: `doc-school/_generator/gen_ws3.py`
- Modify: `doc-school/_generator/README.md` (thêm lệnh chạy gen_ws3)

- [ ] **Step 1: Viết `gen_ws3.py`**

Dùng lại `docx_format` (setup/title/Chuong/ket_luan). 3 chương theo `Yêu cầu workshop 3.docx`:

```python
# -*- coding: utf-8 -*-
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from bshoes_data import *
from docx import Document
from docx_format import setup, title, Chuong, ket_luan

OUTDIR = sys.argv[1]
doc = setup(Document())
title(doc, "Workshop 3: Tầm nhìn kiến trúc và bảng Trello " + PROJECT, "WORKSHOP 3, " + VERSION)

c1 = Chuong(doc, 1, "Tầm nhìn kiến trúc")
c1.para("Chương này trình bày khái niệm, vai trò và lợi ích của tầm nhìn kiến trúc, "
        "kèm ví dụ kiến trúc thực tế của BShoes.")
c1.h2(1, "Khái niệm và vai trò")
c1.para("Tầm nhìn kiến trúc là bức tranh tổng thể mô tả cách hệ thống được chia thành "
        "các thành phần, cách chúng giao tiếp và các ràng buộc công nghệ. Nó định hướng "
        "cho cả nhóm hiểu chung một cấu trúc trước khi lập trình chi tiết.")
c1.h2(2, "Kiến trúc BShoes")
c1.bang("Các tầng kiến trúc của BShoes", ["Tầng", "Công nghệ", "Trách nhiệm"], [
    ["Giao diện (Client)", "Vue 3 SPA, Chart.js", "Màn hình POS, quản trị, cửa hàng online, biểu đồ"],
    ["Dịch vụ (Server)", "Spring Boot, REST API", "Nghiệp vụ, xác thực, phân quyền, trừ kho nguyên tử"],
    ["Truy cập dữ liệu", "Spring Data JPA", "Ánh xạ thực thể, truy vấn, giao dịch"],
    ["Cơ sở dữ liệu", "SQL Server", "Lưu trữ sản phẩm, hóa đơn, khách hàng, bảo hành"],
], [3.0, 4.0, 8.0])
c1.h2(3, "Lợi ích")
for it in ["Tách giao diện và nghiệp vụ nên hai phía phát triển song song",
           "Trừ kho nguyên tử ở tầng dịch vụ nên không bán vượt tồn",
           "Phân quyền tập trung nên kiểm soát truy cập theo từng nhân viên"]:
    c1.bullet_item(it)

c2 = Chuong(doc, 2, "Báo cáo dự án")
c2.para("Chương này tổng hợp khối lượng và tiến độ dự án theo Scrum.")
c2.h2(1, "Khối lượng")
c2.bang("Tổng hợp khối lượng", ["Chỉ số", "Giá trị"], [
    ["Số Request (RQ)", len(RQS)],
    ["Số Product Backlog (PB)", len(PBS)],
    ["Số Task", len(TASKS)],
    ["Tổng giờ", sum(t["est"] for t in TASKS)],
    ["Số Sprint", len(SPRINTS)],
], [6.0, 4.0])

c3 = Chuong(doc, 3, "Bảng Trello dự án")
c3.para("Chương này mô tả bảng Trello gồm cột danh sách thành viên, cột Product Backlog "
        "và 6 cột Sprint. Mỗi thẻ Sprint là một user story (PB) thuộc Sprint đó.")
c3.h2(1, "Cột thành viên")
c3.bang("Thành viên và vai trò", ["Vai trò", "Thành viên", "Trách nhiệm"],
        [[a, n, r] for a, n, r in TEAM], [2.5, 3.5, 9.0])
c3.h2(2, "Cột Product Backlog và các cột Sprint")
rows = []
for sp, goal in SPRINTS:
    the = "; ".join("%s %s" % (p["id"], p["user_story"][:40]) for p in PBS if p["sprint"] == sp)
    rows.append(["Sprint %d" % sp, goal, the])
c3.bang("Thẻ trên các cột Sprint", ["Cột", "Mục tiêu", "Các thẻ (user story)"],
        rows, [1.6, 4.4, 9.0])

ket_luan(doc, [
    "Bảng Trello phản ánh đúng backlog: %d request, %d user story trải trên %d Sprint."
    % (len(RQS), len(PBS), len(SPRINTS)),
    "Kiến trúc tách tầng giúp nhóm phát triển song song và kiểm soát tồn kho, phân quyền tập trung.",
])
f = os.path.join(OUTDIR, "WORKSHOP_3_BShoes.docx")
doc.save(f); print("OK ->", f)
```

Lưu ý: nếu `Chuong.h3` cần 3 tham số (mục, thứ tự, tiêu đề) thì gọi đúng chữ ký hiện có trong `docx_format.py`; kiểm trước khi dùng.

- [ ] **Step 2: Sinh WS3 + verify**

Run: `PYTHONIOENCODING=utf-8 python gen_ws3.py .. && PYTHONIOENCODING=utf-8 python verify_docx.py ../WORKSHOP_3_BShoes.docx`
Expected: `OK -> ...WORKSHOP_3_BShoes.docx`, verify pass.

- [ ] **Step 3: Cập nhật README lệnh chạy**

Thêm dòng `python gen_ws3.py ..` vào mục Chạy, và WS3 vào bảng file.

- [ ] **Step 4: Commit**

```bash
git add doc-school/_generator/gen_ws3.py doc-school/_generator/README.md doc-school/WORKSHOP_3_BShoes.docx
git commit -m "feat(ws3): tam nhin kien truc + bang Trello"
```

---

## Task 6: Sinh lại toàn bộ + nghiệm thu cuối

**Files:** không sửa code, chỉ regenerate + verify.

- [ ] **Step 1: Sinh lại tất cả**

Run:
```
cd doc-school/_generator && PYTHONIOENCODING=utf-8 python check_ids.py && PYTHONIOENCODING=utf-8 python gen_docx.py .. && PYTHONIOENCODING=utf-8 python gen_ws2_xlsx.py ../WORKSHOP_2_BShoes.xlsx && PYTHONIOENCODING=utf-8 python gen_ws3.py ..
```
Expected: mọi bước OK.

- [ ] **Step 2: Verify format toàn bộ docx**

Run: `PYTHONIOENCODING=utf-8 python verify_docx.py ../WORKSHOP_1_BShoes.docx ../WORKSHOP_2_MucTieu_BShoes.docx ../WORKSHOP_3_BShoes.docx`
Expected: PASS toàn bộ.

- [ ] **Step 3: Đối chiếu tay với file mẫu**

Mở 3 docx + xlsx, checklist: (a) Product Backlog có cột As a/I want/So that; (b) Release Backlog có Story ID + Sprint#; (c) Sprint Backlog tách từng sprint, Task có Story ID(PB)+Backlog ID(RQ)+Sprint#; (d) PB và Task chạy đúng thứ tự sprint; (e) không có gạch ngang dài; (f) POS ở Sprint 3, online ở Sprint 5.

- [ ] **Step 4: Commit các file thành phẩm**

```bash
git add doc-school/WORKSHOP_1_BShoes.docx doc-school/WORKSHOP_2_MucTieu_BShoes.docx doc-school/WORKSHOP_2_BShoes.xlsx doc-school/WORKSHOP_3_BShoes.docx
git commit -m "docs: sinh lai workshop 1-2-3 chuan Scrum (RQ->PB->Task theo sprint)"
```

---

## Notes cho người thực thi

- Nếu `Chuong` chưa có method `h3` với chữ ký như dùng ở trên, đọc `docx_format.py` để lấy đúng API (h2/h3/bang/bullet_item/so_thu_tu) trước khi viết.
- Số Task cuối có thể là 90 hoặc ít hơn 2 (nếu gộp 2 task PB-30 trùng). Không sao, miễn `check_ids.py` pass và tổng giờ hợp lý.
- Tuyệt đối không gõ em dash hay en dash trong bất kỳ chuỗi tiếng Việt nào; dùng dấu phẩy hoặc dấu ngoặc.
