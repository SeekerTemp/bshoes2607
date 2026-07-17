# -*- coding: utf-8 -*-
"""
Đối chiếu KIỂU dữ liệu và QUAN HỆ giữa JPA entity và DDL, bổ sung cho audit_schema.py
(vốn chỉ so tên cột).

Bắt các lỗi chỉ lộ ra lúc chạy thật:
  - Java type khong khop SQL type   (vd Integer <-> nvarchar)
  - @Nationalized dat sai cho       (nvarchar phai co, varchar khong duoc co)
  - @JoinColumn tro toi cot khong phai FK trong DDL
  - length khai lech voi DDL
  - schema="dbo" khai khong dong deu
"""
import io, os, re, sys

ROOT = sys.argv[1] if len(sys.argv) > 1 else '.'
ENT_DIR = os.path.join(ROOT, 'src/main/java/com/vn/test/bshoes/entity')
SQL = os.path.join(ROOT, 'sqlBshoes.sql')

# Java type nào chấp nhận SQL type nào
OK = {
    'Integer': {'int', 'smallint', 'tinyint'},
    'int':     {'int', 'smallint', 'tinyint'},
    'Long':    {'bigint', 'int'},
    'String':  {'varchar', 'nvarchar', 'char', 'nchar', 'text', 'ntext'},
    'BigDecimal': {'decimal', 'numeric', 'money', 'smallmoney', 'float'},
    'Boolean': {'bit'},
    'boolean': {'bit'},
    'Instant': {'datetime', 'datetime2', 'date', 'smalldatetime'},
    'LocalDate': {'date', 'datetime'},
    'LocalDateTime': {'datetime', 'datetime2'},
    'Double':  {'float', 'real', 'decimal'},
}

# ---------- đọc DDL: cột -> (kiểu, độ dài), và tập FK
sql = io.open(SQL, encoding='utf-8').read()
sql = re.sub(r'--[^\n]*', '', sql)      # bỏ comment
ddl, fkcols = {}, {}
for m in re.finditer(r'create table\s+([a-z_]+)\s*\((.*?)\n\);', sql, re.S | re.I):
    t, body = m.group(1).lower(), m.group(2)
    cols = {}
    for line in body.split('\n'):
        s = line.strip().rstrip(',')
        if not s:
            continue
        fk = re.match(r'foreign key\s*\(\s*([a-z_]+)\s*\)\s*references\s+([a-z_]+)', s, re.I)
        if fk:
            fkcols[(t, fk.group(1).lower())] = fk.group(2).lower()
            continue
        if re.match(r'(primary key|constraint|unique|check|foreign)', s, re.I):
            continue
        c = re.match(r'([a-z_][a-z0-9_]*)\s+([a-z_]+)(?:\s*\(\s*(\d+|max)\s*(?:,\s*\d+\s*)?\))?', s, re.I)
        if c:
            cols[c.group(1).lower()] = (c.group(2).lower(), c.group(3))
    ddl[t] = cols

# ---------- đọc entity
def snake(f):
    return re.sub(r'(?<!^)(?=[A-Z])', '_', f).lower()

loi, canh_bao = [], []
so_cot = 0
for f in sorted(os.listdir(ENT_DIR)):
    if not f.endswith('.java'):
        continue
    src = io.open(os.path.join(ENT_DIR, f), encoding='utf-8').read()
    tm = re.search(r'@Table\s*\(([^)]*)\)', src)
    if not tm:
        continue
    targs = tm.group(1)
    tname = re.search(r'name\s*=\s*"([^"]+)"', targs).group(1).lower()
    if 'schema' not in targs:
        canh_bao.append("%s: @Table khong khai schema (cac entity khac deu co schema=\"dbo\")" % f)
    if tname not in ddl:
        loi.append("%s: bang '%s' khong co trong DDL" % (f, tname))
        continue

    for m in re.finditer(
            r'((?:@[\w.]+(?:\((?:[^()]|\([^()]*\))*\))?\s*)*)private\s+([\w<>,\[\]\.]+)\s+(\w+)\s*;', src):
        ann, jtype, field = m.group(1), m.group(2), m.group(3)
        if '@Transient' in ann or '@OneToMany' in ann:
            continue
        jtype = jtype.split('.')[-1].split('<')[0]

        jc = re.search(r'@JoinColumn\s*\([^)]*name\s*=\s*"([^"]+)"', ann)
        cc = re.search(r'@Column\s*\((?:[^()]|\([^()]*\))*?name\s*=\s*"([^"]+)"', ann)
        col = (jc.group(1) if jc else (cc.group(1) if cc else snake(field))).lower()
        if col not in ddl[tname]:
            continue    # audit_schema.py lo phan thieu cot
        sqltype, sqllen = ddl[tname][col]
        so_cot += 1

        # 1. quan hệ: @JoinColumn phải là FK thật trong DDL
        if jc or '@ManyToOne' in ann:
            if (tname, col) not in fkcols:
                canh_bao.append("%s.%s: @ManyToOne toi cot '%s' nhung DDL khong khai FK"
                                % (f, field, col))
            continue

        # 2. kiểu
        exp = OK.get(jtype)
        if exp is None:
            continue
        if sqltype not in exp:
            loi.append("%-22s %-24s Java %-12s <-> SQL %-10s  (khong tuong thich)"
                       % (tname, col, jtype, sqltype))
            continue

        # 3. @Nationalized phải khớp nvarchar/nchar
        if jtype == 'String':
            nat = '@Nationalized' in ann
            is_n = sqltype.startswith('n')
            if is_n and not nat:
                canh_bao.append("%s.%s: cot %s la %s nhung thieu @Nationalized (co the mat dau tieng Viet)"
                                % (f, field, col, sqltype))
            if nat and not is_n:
                canh_bao.append("%s.%s: co @Nationalized nhung cot %s chi la %s"
                                % (f, field, col, sqltype))
            # 4. length
            lm = re.search(r'length\s*=\s*(\d+)', ann)
            if lm and sqllen and sqllen != 'max' and int(lm.group(1)) != int(sqllen):
                loi.append("%s.%s: length=%s nhung DDL %s(%s)"
                           % (f, field, lm.group(1), sqltype, sqllen))

print("Da doi chieu %d cot co kieu." % so_cot)
print("-" * 78)
if canh_bao:
    print("CANH BAO:")
    for c in canh_bao:
        print("  -", c)
    print()
if loi:
    print("LOI (se chet luc chay that):")
    for e in loi:
        print("  -", e)
    sys.exit(1)
print("Kieu du lieu va quan he: khop het.")
