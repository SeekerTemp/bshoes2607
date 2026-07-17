# -*- coding: utf-8 -*-
"""
Đối chiếu JPA entity với DDL trong sqlBshoes.sql.

Vì ddl-auto=none, Hibernate không tự sửa schema: entity khai một cột mà DDL không có
là chết ngay lúc chạy câu SQL đầu tiên. Chưa có SQL Server nên không ai phát hiện được,
script này thay cho việc đó.
"""
import io, os, re, sys

ROOT = sys.argv[1] if len(sys.argv) > 1 else '.'
ENT_DIR = os.path.join(ROOT, 'src/main/java/com/vn/test/bshoes/entity')
SQL = os.path.join(ROOT, 'sqlBshoes.sql')

# ---------------------------------------------------------------- đọc DDL
sql = io.open(SQL, encoding='utf-8').read()
tables = {}
for m in re.finditer(r'create table\s+([a-z_]+)\s*\((.*?)\n\);', sql, re.S | re.I):
    name, body = m.group(1), m.group(2)
    cols = set()
    for line in body.split('\n'):
        line = line.strip().rstrip(',')
        if not line or line.startswith('--'):
            continue
        low = line.lower()
        if low.startswith(('foreign key', 'primary key', 'constraint', 'unique (', 'check')):
            continue
        c = re.match(r'([a-z_][a-z0-9_]*)\s', line, re.I)
        if c:
            cols.add(c.group(1).lower())
    tables[name] = cols

# ---------------------------------------------------------------- đọc entity
def java_default_col(field):
    """Hibernate mặc định: camelCase -> snake_case."""
    return re.sub(r'(?<!^)(?=[A-Z])', '_', field).lower()

entities = {}
for f in sorted(os.listdir(ENT_DIR)):
    if not f.endswith('.java'):
        continue
    src = io.open(os.path.join(ENT_DIR, f), encoding='utf-8').read()
    t = re.search(r'@Table\s*\(\s*name\s*=\s*"([^"]+)"', src)
    if not t:
        continue
    tname = t.group(1)
    cols = {}
    # bắt cặp (annotation block, khai báo field)
    for m in re.finditer(
            r'((?:@[\w.]+(?:\([^)]*\))?\s*)*)\s*private\s+[\w<>,\[\]\.]+\s+(\w+)\s*;', src):
        ann, field = m.group(1), m.group(2)
        if '@Transient' in ann:
            continue
        jc = re.search(r'@JoinColumn\s*\(\s*name\s*=\s*"([^"]+)"', ann)
        cc = re.search(r'@Column\s*\([^)]*name\s*=\s*"([^"]+)"', ann)
        if jc:
            cols[jc.group(1).lower()] = field
        elif cc:
            cols[cc.group(1).lower()] = field
        elif '@ManyToOne' in ann or '@OneToMany' in ann or '@OneToOne' in ann:
            if '@OneToMany' in ann:
                continue  # phía nghịch, không có cột
            cols[java_default_col(field)] = field + ' (ManyToOne khong @JoinColumn)'
        else:
            cols[java_default_col(field)] = field
    entities[tname] = (f, cols)

# ---------------------------------------------------------------- so sánh
loi, canh_bao = [], []
print("%-22s %-8s %s" % ("BANG", "SO COT", "TRANG THAI"))
print("-" * 70)
for tname, (f, cols) in sorted(entities.items()):
    if tname not in tables:
        loi.append("Entity %s tro toi bang '%s' KHONG CO trong sqlBshoes.sql" % (f, tname))
        print("%-22s %-8s %s" % (tname, len(cols), "!! THIEU BANG"))
        continue
    ddl = tables[tname]
    thieu = sorted(c for c in cols if c not in ddl)
    status = "OK" if not thieu else "!! LECH"
    print("%-22s %-8s %s" % (tname, len(cols), status))
    for c in thieu:
        loi.append("Bang %-20s thieu cot '%s'  (entity %s, field %s)" % (tname, c, f, cols[c]))

# bảng có trong SQL nhưng không entity nào dùng
for t in sorted(tables):
    if t not in entities:
        canh_bao.append("Bang '%s' co trong SQL nhung khong entity nao map toi" % t)

print("-" * 70)
if canh_bao:
    print("CANH BAO:")
    for c in canh_bao:
        print("  -", c)
if loi:
    print("LOI (entity khai cot ma DDL khong co -> chet luc chay):")
    for e in loi:
        print("  -", e)
    sys.exit(1)
print("Khop het: moi cot entity deu co trong DDL.")
