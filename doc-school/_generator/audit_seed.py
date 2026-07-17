# -*- coding: utf-8 -*-
"""
Kiểm tra toàn vẹn dữ liệu seed trong sqlBshoes.sql mà không cần SQL Server.

Mô phỏng IDENTITY: đếm số dòng insert cho mỗi bảng để biết id hợp lệ chạy từ 1..n,
rồi kiểm mọi khóa ngoại trong seed có trỏ tới id tồn tại hay không. Đây đúng loại
lỗi từng xảy ra: hoa_don_chi_tiet trỏ id_hoa_don 1..24 trong khi IDENTITY đã lệch 3.
"""
import io, re, sys

path = sys.argv[1]
sql_raw = io.open(path, encoding='utf-8').read()

def strip_comments(text):
    """
    Bỏ comment '--' ngoài chuỗi nháy. Cần thiết vì comment có thể chứa dấu ngoặc
    (ví dụ "biến thể còn hàng (SPCT001, 002, 003)") làm regex bắt dòng values nhầm,
    sinh báo động giả.
    """
    out, q, i = [], False, 0
    while i < len(text):
        ch = text[i]
        if q:
            out.append(ch)
            if ch == "'":
                if i + 1 < len(text) and text[i + 1] == "'":
                    out.append(text[i + 1]); i += 2; continue
                q = False
        else:
            if ch == "'":
                q = True; out.append(ch)
            elif ch == '-' and i + 1 < len(text) and text[i + 1] == '-':
                while i < len(text) and text[i] != '\n':
                    i += 1
                out.append('\n')
                continue
            else:
                out.append(ch)
        i += 1
    return ''.join(out)

sql = strip_comments(sql_raw)

# ---- 1. FK khai báo trong DDL: bang.cot -> bang_cha
fks = {}          # (bang, cot) -> bang_cha
tables_cols = {}  # bang -> [cot theo thu tu]
for m in re.finditer(r'create table\s+([a-z_]+)\s*\((.*?)\n\);', sql, re.S | re.I):
    t, body = m.group(1), m.group(2)
    cols = []
    for line in body.split('\n'):
        s = line.strip().rstrip(',')
        if not s or s.startswith('--'):
            continue
        fk = re.match(r'foreign key\s*\(\s*([a-z_]+)\s*\)\s*references\s+([a-z_]+)', s, re.I)
        if fk:
            fks[(t, fk.group(1).lower())] = fk.group(2).lower()
            continue
        if re.match(r'(primary key|constraint|unique|check)', s, re.I):
            continue
        c = re.match(r'([a-z_][a-z0-9_]*)\s', s, re.I)
        if c:
            cols.append(c.group(1).lower())
    tables_cols[t] = cols

# ---- 2. đếm số dòng seed mỗi bảng -> id 1..n (IDENTITY)
counts = {}
inserts = []      # (bang, [cot], [dong gia tri])
for m in re.finditer(r'insert\s+into\s+([a-z_]+)\s*\(([^)]*)\)\s*values(.*?);', sql, re.S | re.I):
    t = m.group(1).lower()
    cols = [c.strip().lower() for c in m.group(2).split(',')]
    body = m.group(3)
    rows = re.findall(r'\((.*?)\)(?=\s*[,;]|\s*$)', body, re.S)
    rows = [r for r in rows if r.strip()]
    counts[t] = counts.get(t, 0) + len(rows)
    inserts.append((t, cols, rows))

print("%-22s %s" % ("BANG", "SO DONG SEED"))
print("-" * 44)
for t in sorted(counts):
    print("%-22s %d" % (t, counts[t]))

# ---- 3. tách giá trị từng dòng (tôn trọng dấu nháy)
def split_vals(row):
    out, cur, q, depth = [], '', False, 0
    i = 0
    while i < len(row):
        ch = row[i]
        if q:
            if ch == "'":
                if i + 1 < len(row) and row[i + 1] == "'":
                    cur += "''"; i += 2; continue
                q = False
            cur += ch
        else:
            if ch == "'":
                q = True; cur += ch
            elif ch == '(':
                depth += 1; cur += ch
            elif ch == ')':
                depth -= 1; cur += ch
            elif ch == ',' and depth == 0:
                out.append(cur.strip()); cur = ''
            else:
                cur += ch
        i += 1
    if cur.strip():
        out.append(cur.strip())
    return out

# ---- 4. kiểm FK trong seed
loi = []
for t, cols, rows in inserts:
    for ri, row in enumerate(rows, 1):
        vals = split_vals(row)
        if len(vals) != len(cols):
            loi.append("%s dong %d: %d gia tri nhung %d cot" % (t, ri, len(vals), len(cols)))
            continue
        for c, v in zip(cols, vals):
            parent = fks.get((t, c))
            if not parent:
                continue
            v = v.strip()
            if v.upper() == 'NULL':
                continue
            if not re.match(r'^\d+$', v):
                continue
            n = int(v)
            maxid = counts.get(parent, 0)
            if n < 1 or n > maxid:
                loi.append("%s dong %d: %s = %s nhung %s chi co id 1..%d"
                           % (t, ri, c, v, parent, maxid))

print("-" * 44)
if loi:
    print("LOI TOAN VEN SEED:")
    for e in loi:
        print("  -", e)
    sys.exit(1)
print("Moi khoa ngoai trong seed deu tro toi id ton tai.")
