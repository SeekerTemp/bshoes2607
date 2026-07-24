# Generator tài liệu workshop

Sinh lại các file Word trong `doc-school/` từ nguồn dữ liệu, đừng sửa tay file Word
rồi sinh lại vì sinh lại sẽ đè mất.

## Pipeline chuẩn (nguồn sự thật = file Excel của nhóm)

Từ 2026-07-24, Workshop 1 + 2 và bản tổng hợp `asm1.docx` lấy dữ liệu TRỰC TIẾP từ
file Excel nhóm đã chỉnh tay: `doc-school/2607lts_nhóm 1_WebBshoes_Workshop1.xlsx`.

```bash
cd doc-school/_generator
export PYTHONIOENCODING=utf-8         # Windows: set PYTHONIOENCODING=utf-8

python ws_source.py                   # nạp + kiểm tra dữ liệu từ Excel (40 RQ, 13 PB, 6 sprint)
python gen_asm1.py ..                 # -> asm1.docx  (tổng hợp WS1 + WS2, mỗi mục có mở/thân/kết)
python gen_ws12.py ..                 # -> WORKSHOP_1_BShoes.docx, WORKSHOP_2_MucTieu_BShoes.docx
python verify_docx.py ../asm1.docx ../WORKSHOP_1_BShoes.docx ../WORKSHOP_2_MucTieu_BShoes.docx
```

Mô hình ID bám đúng Excel: **RQ-1..RQ-40 = Product Backlog** (user story, 24 New +
16 Removed), **PB-1..PB-13 = Release Backlog** (nhóm chức năng) gán vào Sprint. ED = 18/36
(hệ số môi trường 0.5), C = 1. Muốn đổi nội dung thì sửa file Excel rồi chạy lại, KHÔNG sửa
`ws_source.py` trừ khi bố cục sheet thay đổi.

`gen_docx.py` + `bshoes_data.py` là generator CŨ (mô hình 9 RQ / 40 PB, đánh số theo sprint),
chỉ còn dùng cho `gen_ws2_xlsx.py` và `gen_ws3.py`. **Đừng chạy `gen_docx.py`** cho WS1/WS2 nữa
vì nó đè file bằng mô hình cũ; dùng `gen_ws12.py`.

## Generator cũ (legacy)

Sửa nội dung ở `bshoes_data.py`, đừng sửa tay file Word rồi sinh lại, vì sinh lại sẽ đè mất.

## Chạy

```bash
pip install python-docx openpyxl
cd doc-school/_generator

python gen_docx.py ..                          # -> WORKSHOP_1_BShoes.docx, WORKSHOP_2_MucTieu_BShoes.docx
python gen_ws2_xlsx.py ../WORKSHOP_2_BShoes.xlsx
python gen_ws3.py ..                            # -> WORKSHOP_3_BShoes.docx

python check_ids.py                            # bat bien ID: RQ -> PB -> Task, dung thu tu sprint
python verify_docx.py ../WORKSHOP_1_BShoes.docx ../WORKSHOP_2_MucTieu_BShoes.docx ../WORKSHOP_3_BShoes.docx
```

Mô hình ID 3 cấp (chuẩn theo file mẫu `doc-school-request`): `RQ` (request, As a/I want/So that)
là Product Backlog; `PB` (user story) là Release Backlog, có Sprint#; `Task` là Sprint Backlog,
mỗi Task mang Story ID (PB) + Backlog ID (RQ) + Sprint#. PB và Task được đánh số lại theo thứ
tự Sprint trong `bshoes_data.py`, `check_ids.py` chặn nếu lệch.

Trên Windows nếu console báo `UnicodeEncodeError` thì đặt `PYTHONIOENCODING=utf-8`.

## Các file

| File | Vai trò |
|---|---|
| `bshoes_data.py` | Nguồn dữ liệu: RQ, PB (user story), Task, yếu tố ED, sprint + hàm renumber |
| `docx_format.py` | Khuôn định dạng Word (font, heading, caption, kiểm tra gạch ngang) |
| `gen_docx.py` | Nội dung Workshop 1 và Workshop 2 (Word) |
| `gen_ws2_xlsx.py` | Nội dung file Excel Workshop 2 (Product/Release/Sprint Backlog, ước lượng) |
| `gen_ws3.py` | Nội dung Workshop 3 (Word): tầm nhìn kiến trúc + bảng Trello |
| `check_ids.py` | Kiểm bất biến ID: RQ/PB/Task liên tục, truy vết đủ, đúng thứ tự sprint |
| `verify_docx.py` | Đọc ngược file đã sinh để kiểm chứng định dạng |
| `gen_dbml.py` | Sinh sơ đồ DBML cho dbdiagram.io thẳng từ `sqlBshoes.sql` |
| `audit_schema.py` | Đối chiếu cột của JPA entity với DDL |
| `audit_seed.py` | Kiểm khóa ngoại trong dữ liệu seed |

## Kiểm tra CSDL khi chưa có SQL Server

`ddl-auto=none` nên Hibernate không tự sửa schema: entity khai một cột mà DDL không có là
chết ngay câu SQL đầu tiên. Chưa cài được SQL Server thì hai script này thay cho việc chạy thật:

```bash
python audit_schema.py ../..                 # entity vs DDL
python audit_seed.py ../../sqlBshoes.sql     # FK trong seed co tro toi id ton tai khong
python gen_dbml.py ../../sqlBshoes.sql ../BShoes-dbdiagram.txt
```

`audit_seed.py` mô phỏng IDENTITY bằng cách đếm số dòng insert mỗi bảng, nên bắt được đúng
loại lỗi từng xảy ra thật: `hoa_don_chi_tiet` trỏ `id_hoa_don` 1..24 trong khi IDENTITY đã
lệch 3 vì HD001 đến HD003 chèn trước.

## Định dạng đã chốt (2026-07-17)

- Times New Roman, cỡ 13, giãn dòng 1.15 đến 1.5 (đang dùng 1.3)
- TITLE: đậm, canh giữa, màu thương hiệu `#0B895A`
- `CHƯƠNG n: TÊN CHƯƠNG` dùng số Ả Rập, **không dùng số La Mã**, H1 đậm màu logo
- `n.n.` H2 đậm, lùi 2 ký tự; `n.n.n.` H3 đậm, nghiêng, có màu, lùi 4 đến 5 ký tự
- Tên hình dùng Heading 5, tên bảng dùng Heading 6, đánh số `<chương>.<thứ tự>`
- Chú thích bảng đặt trên bảng, chú thích hình đặt dưới hình
- Mỗi chương mở bài 2 đến 3 câu, thân bài có tối thiểu 1 hình hoặc 1 bảng minh họa
- Kết thúc tài liệu bằng phần Kết luận
- **Tuyệt đối không dùng dấu gạch ngang dài** (em dash `—` và en dash `–`).
  `docx_format.kiem_tra_gach_ngang()` sẽ ném lỗi ngay lúc sinh nếu lọt, thay vì để
  phát hiện sau khi mở Word.

## Lưu ý

`verify_docx.py` đọc lại file thành phẩm chứ không tin generator. Cách này từng bắt được
lỗi thật: công thức Excel trỏ `D24` (tỷ lệ ED/36) thay vì `D23` (tổng điểm ED), khiến PPS
nhỏ đi 36 lần.
