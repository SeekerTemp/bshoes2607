# Generator tài liệu workshop

Sinh lại 2 file Word + 1 file Excel trong `doc-school/` từ một nguồn dữ liệu duy nhất.
Sửa nội dung ở `bshoes_data.py`, đừng sửa tay file Word rồi sinh lại, vì sinh lại sẽ đè mất.

## Chạy

```bash
pip install python-docx openpyxl
cd doc-school/_generator

python gen_docx.py ..                          # -> WORKSHOP_1_BShoes.docx, WORKSHOP_2_MucTieu_BShoes.docx
python gen_ws2_xlsx.py ../WORKSHOP_2_BShoes.xlsx

python verify_docx.py ../WORKSHOP_1_BShoes.docx ../WORKSHOP_2_MucTieu_BShoes.docx
```

Trên Windows nếu console báo `UnicodeEncodeError` thì đặt `PYTHONIOENCODING=utf-8`.

## Các file

| File | Vai trò |
|---|---|
| `bshoes_data.py` | Nguồn dữ liệu: REQ, product backlog, task, yếu tố ED, sprint |
| `docx_format.py` | Khuôn định dạng Word (font, heading, caption, kiểm tra gạch ngang) |
| `gen_docx.py` | Nội dung 2 file Word |
| `gen_ws2_xlsx.py` | Nội dung file Excel ước lượng |
| `verify_docx.py` | Đọc ngược file đã sinh để kiểm chứng định dạng |

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
