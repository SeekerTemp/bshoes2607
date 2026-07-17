# -*- coding: utf-8 -*-
"""Sinh DBML (dbdiagram.io) trực tiếp từ sqlBshoes.sql — luôn khớp schema hiện tại."""
import re, sys

SQL, OUT = sys.argv[1], sys.argv[2]
sql = open(SQL, encoding='utf-8').read()
# bỏ comment dòng để không lẫn vào parser
body_all = "\n".join(l for l in sql.split("\n") if not l.strip().startswith("--"))

GROUPS = [
    ("A_TaiKhoan_PhanQuyen", "Tài khoản & Phân quyền", ["vai_tro", "nhan_vien", "nhan_vien_quyen"]),
    ("B_DanhMuc_ThuocTinh", "Danh mục & Thuộc tính",
     ["loai_san_pham", "chat_lieu", "kieu_dang", "kieu_co_giay", "kieu_day_giay",
      "thuong_hieu", "xuat_su", "kich_co", "mau_sac"]),
    ("C_SanPham", "Sản phẩm", ["san_pham", "san_pham_chi_tiet"]),
    ("D_KhachHang", "Khách hàng", ["khach_hang", "dia_chi"]),
    ("E_BanHang", "Bán hàng", ["phieu_giam_gia", "hoa_don", "hoa_don_chi_tiet", "lich_su_hoa_don"]),
    ("F_SauBanHang", "Sau bán hàng", ["bao_hanh"]),
    ("G_DatTruoc", "Đặt trước", ["dat_truoc"]),
]

tables, refs = [], []
for m in re.finditer(r'create\s+table\s+(\w+)\s*\(([\s\S]*?)\n\)\s*;', body_all, re.I):
    name, body = m.group(1), m.group(2)
    cols = []
    # tách theo dấu phẩy ở cuối dòng (mỗi định nghĩa 1 dòng trong file đã chuẩn hoá)
    for raw in body.split("\n"):
        line = raw.strip().rstrip(",").strip()
        if not line:
            continue
        fk = re.match(r'foreign\s+key\s*\(\s*(\w+)\s*\)\s*references\s+(\w+)\s*\(\s*(\w+)\s*\)', line, re.I)
        if fk:
            refs.append((name, fk.group(1), fk.group(2), fk.group(3)))
            continue
        if re.match(r'(primary|foreign|constraint|unique)\s', line, re.I):
            continue
        cm = re.match(r'^(\w+)\s+([A-Za-z]+(?:\s*\([\d,\s]+\))?)\s*(.*)$', line)
        if not cm:
            continue
        col, typ, rest = cm.group(1), cm.group(2).strip(), cm.group(3).lower()
        attrs = []
        if 'primary key' in rest:
            attrs.append('pk')
        if 'identity' in rest:
            attrs.append('increment')
        if 'unique' in rest:
            attrs.append('unique')
        dm = re.search(r'default\s+([^\s,]+)', rest)
        if dm:
            attrs.append("default: `%s`" % dm.group(1).rstrip(','))
        cols.append((col, typ, attrs))
    tables.append((name, cols))

names = [t[0] for t in tables]
out = []
out.append("// " + "=" * 58)
out.append("// BShoes — Hệ thống Quản lý & Bán hàng cửa hàng giày")
out.append("// DBML cho https://dbdiagram.io (dán toàn bộ file vào editor bên trái)")
out.append("// SINH TỰ ĐỘNG từ sqlBshoes.sql — %d bảng" % len(tables))
out.append("// " + "=" * 58)
out.append("")
out.append("Project BShoes {")
out.append("  database_type: 'SQL Server'")
out.append("  Note: '''")
out.append("  # BShoes — Quản lý cửa hàng giày")
out.append("  %d bảng, chia %d nhóm chức năng." % (len(tables), len(GROUPS)))
out.append("  '''")
out.append("}")
out.append("")

emitted = set()
for gid, glabel, gtables in GROUPS:
    out.append("// " + "=" * 58)
    out.append("// %s" % glabel.upper())
    out.append("// " + "=" * 58)
    for tn in gtables:
        t = next((x for x in tables if x[0] == tn), None)
        if not t:
            continue
        emitted.add(tn)
        out.append("")
        out.append("Table %s {" % tn)
        for col, typ, attrs in t[1]:
            s = "  %-24s %-16s" % (col, typ)
            if attrs:
                s += " [%s]" % ", ".join(attrs)
            out.append(s.rstrip())
        out.append("}")
    out.append("")

leftovers = [t for t in tables if t[0] not in emitted]
if leftovers:
    out.append("// ===== Bảng chưa xếp nhóm =====")
    for tn, cols in leftovers:
        out.append("")
        out.append("Table %s {" % tn)
        for col, typ, attrs in cols:
            s = "  %-24s %-16s" % (col, typ)
            if attrs:
                s += " [%s]" % ", ".join(attrs)
            out.append(s.rstrip())
        out.append("}")
    out.append("")

out.append("// " + "=" * 58)
out.append("// QUAN HỆ (Ref)")
out.append("// " + "=" * 58)
for src, sc, dst, dc in refs:
    out.append("Ref: %s.%s > %s.%s" % (src, sc, dst, dc))
out.append("")

for gid, glabel, gtables in GROUPS:
    present = [t for t in gtables if t in names]
    if not present:
        continue
    out.append("TableGroup %s {" % gid)
    for t in present:
        out.append("  %s" % t)
    out.append("}")
    out.append("")

open(OUT, "w", encoding="utf-8").write("\n".join(out))
print("Tables (%d): %s" % (len(tables), ", ".join(names)))
print("Refs:", len(refs))
print("OK ->", OUT)
