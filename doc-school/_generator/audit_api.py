# -*- coding: utf-8 -*-
"""
Đối chiếu hai chiều giữa API frontend gọi và endpoint backend khai.

  Chiều 1 (NGHIEM TRONG): frontend gọi endpoint backend không có -> 404 lúc chạy.
  Chiều 2 (thong tin)   : backend có endpoint không ai gọi -> code thừa hoặc thiếu UI.
"""
import io, os, re, sys

ROOT = sys.argv[1] if len(sys.argv) > 1 else '.'
CTRL = os.path.join(ROOT, 'src/main/java/com/vn/test/bshoes/controller')
APIDIR = os.path.join(ROOT, 'frontend/src/api')

VERBS = {'GetMapping': 'GET', 'PostMapping': 'POST', 'PutMapping': 'PUT',
         'DeleteMapping': 'DELETE', 'PatchMapping': 'PATCH'}

def norm(p):
    """Chuẩn hóa: bỏ / thừa, biến {id} và ${...} thành {}"""
    p = '/' + p.strip('/')
    p = re.sub(r'\{[^}]*\}', '{}', p)
    p = re.sub(r'\$\{[^}]*\}', '{}', p)
    p = re.sub(r'//+', '/', p)
    return p.rstrip('/') or '/'

# ---------------- backend
backend = set()
for f in sorted(os.listdir(CTRL)):
    if not f.endswith('.java'):
        continue
    src = io.open(os.path.join(CTRL, f), encoding='utf-8').read()
    base = re.search(r'@RequestMapping\s*\(\s*"([^"]+)"', src)
    base = base.group(1) if base else ''
    for m in re.finditer(r'@(\w+Mapping)\s*(?:\(\s*(?:value\s*=\s*)?"([^"]*)"[^)]*\))?', src):
        ann, path = m.group(1), m.group(2) or ''
        if ann == 'RequestMapping':
            continue
        verb = VERBS.get(ann)
        if not verb:
            continue
        backend.add((verb, norm(base + '/' + path)))

# ---------------- frontend
# catalog.js dung duong dan dong `/${path}` cho 8 nhom thuoc tinh; phai bung ra theo
# cac key that su duoc goi, khong thi vua bao thieu nham vua ke 40 endpoint "khong ai goi".
catalog_src = io.open(os.path.join(APIDIR, 'catalog.js'), encoding='utf-8').read()
CATALOG_KEYS = set(re.findall(r"key:\s*'([^']+)'", catalog_src))
CATALOG_KEYS |= set(re.findall(r"catalogApi\('([^']+)'\)", catalog_src))

frontend = set()
for f in sorted(os.listdir(APIDIR)):
    if not f.endswith('.js'):
        continue
    src = io.open(os.path.join(APIDIR, f), encoding='utf-8').read()
    for m in re.finditer(
            r"http\.(get|post|put|delete|patch)\s*\(\s*[`'\"]([^`'\"]*)[`'\"]\s*(\+)?", src):
        verb, path, concat = m.group(1).upper(), m.group(2), m.group(3)
        path = path.split('?')[0]
        # noi chuoi: '/by-ma/' + encodeURIComponent(ma) -> doan sau la mot bien duong dan
        if concat and path.endswith('/'):
            path += '{}'
        if path.startswith('/${') or path.startswith('${'):
            # duong dan dong cua catalogApi -> bung theo tung nhom thuoc tinh
            suffix = re.sub(r'^/?\$\{[^}]*\}', '', path)
            for k in sorted(CATALOG_KEYS):
                frontend.add((verb, norm('/api/' + k + suffix), f))
            continue
        frontend.add((verb, norm('/api/' + path), f))

print("Backend khai %d endpoint | Frontend goi %d endpoint" % (len(backend), len(frontend)))
print("-" * 78)

thieu = []
for verb, path, f in sorted(frontend, key=lambda x: x[1]):
    if (verb, path) not in backend:
        thieu.append("%-6s %-46s  (goi tu api/%s)" % (verb, path, f))

goi = {(v, p) for v, p, _ in frontend}
thua = [(v, p) for (v, p) in backend if (v, p) not in goi]

if thieu:
    print("LOI: frontend goi ma backend KHONG CO -> 404 luc chay:")
    for t in thieu:
        print("  -", t)
    print()
print("Backend co nhung frontend chua goi (%d):" % len(thua))
for v, p in sorted(thua, key=lambda x: x[1]):
    print("  %-6s %s" % (v, p))

print("-" * 78)
if thieu:
    sys.exit(1)
print("Moi endpoint frontend goi deu ton tai o backend.")
