# BShoes Phase 3 — Reconcile Backend to New JPA Entities (DTO) + Wire Frontend

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:subagent-driven-development. Checkbox steps.

**Context:** The user regenerated the `entity` package as a proper JPA model (camelCase fields; `getId()`, `getMaChatLieu()`, `getTrangThai()`, `getTrangThaiXoa()`; FKs are `@ManyToOne` objects like `getIdThuongHieu()` → `ThuongHieu`) and added `sqlBshoes.sql`. They deleted `SanPham_ql`, `SanPhamChiTiet_ql`, `ThongKeDoanhThu`, `ThongKeSanPham`, `Nam`, `HoaDonChiTiet_advanced` and renamed `XuatXu`→`XuatSu`. The Phase-2 **services + controllers** (unchanged) still use old snake_case getters and deleted types, so they do not compile.

**Goal:** Make the whole backend compile & run against the new entities, exposing **DTO-shaped REST** matching the Vue frontend, then wire the frontend composables to those endpoints.

**Decisions (locked with user):** DTOs shaped to the frontend; **keep** `/api/san-pham-ql` & `/api/san-pham-chi-tiet-ql` endpoints (re-backed by canonical entities); all domains.

**Cannot compile here (no JDK)** — align exactly to entity accessors; user builds in IntelliJ.

**DB facts (from sqlBshoes.sql):** function `dbo.tinh_tien_giam_gia`, view `view_phieu_giam_gia_hoat_dong` exist. **No stored procedures** — the Phase-2 `EXEC sp_insert_hoa_don_placeholder` / `tao_hoa_don_va_chi_tiet` / `sp_cap_nhat_trang_thai_phieu_giam_gia` do NOT exist; replace those with real JPA/SQL or drop.

---

## Conventions

**Packages:** add `com.vn.test.bshoes.dto`. Keep `repository`, `service`, `service.impl`, `controller`.

**Entity accessor rules (Lombok @Getter/@Setter):** field `xxxYyy` → `getXxxYyy()/setXxxYyy()`. PK is always `getId()/setId()`. Relations return entity objects: `sanPham.getIdThuongHieu().getTenThuongHieu()`, `sanPham.getIdChatLieu().getTenChatLieu()`, `spct.getIdKichCo().getTenKichCo()`, `spct.getIdMauSac().getTenMauSac()`, `spct.getIdSanPham().getTenSanPham()`, `nhanVien.getIdVaiTro().getTenVaiTro()`, `hoaDon.getIdKhachHang()`, `hoaDon.getIdNhanVien()`. Guard nulls when a relation may be null.

**DTO:** plain class with Lombok `@Data @NoArgsConstructor @AllArgsConstructor` in `dto`. Fields named to match the frontend (`id, ma, ten, trangThai, ...`).

**Service pattern:** each `*ServiceImpl` gets private `toDto(entity)` and (for writes) `apply(dto, entity)` helpers. Public methods return DTOs / accept DTOs. Use `JpaRepository` derived methods + JPQL; keep the existing native `@Query`s only where they still return a valid managed entity type. Auto-code on create: `save` → `set<Ma>(PREFIX + saved.getId())` → `save` again (simpler than the old updateMa native call, and works with real IDENTITY).

**Controller:** `@RestController @RequestMapping("/api/...")`, return DTOs / `ResponseEntity`. Keep existing route paths.

**Soft-delete:** entities have `trangThaiXoa` (Boolean). "Active" = `trangThaiXoa` is false/null. Prefer derived `findByTrangThaiXoaFalse()` (add to repo) or `findByTrangThaiXoaIsNot(true)`; a native `... where trang_thai_xoa = 0` also works.

**Jackson:** returning DTOs avoids lazy/recursion issues; do NOT serialize entities directly.

---

## Task 1: DTO package

Create these DTOs under `src/main/java/com/vn/test/bshoes/dto/` (all `@Data @NoArgsConstructor @AllArgsConstructor`). Types: use `Integer/String/Boolean/BigDecimal`; dates as `String` (ISO) unless noted.

- **AttributeDto** (reused for ChatLieu, MauSac, KichCo, KieuCoGiay, KieuDang, KieuDayGiay, VaiTro): `{ Integer id; String ma; String ten; Boolean trangThai; }`
- **AttributeMoTaDto** (ThuongHieu, XuatSu, LoaiSanPham): `{ Integer id; String ma; String ten; String moTa; Boolean trangThai; }`
- **KhachHangDto**: `{ Integer id; String ma; String ten; String gioiTinh; String sdt; String email; String diaChi; Boolean trangThai; }`
- **NhanVienDto**: `{ Integer id; String ma; String ten; String taiKhoan; String email; String sdt; String cccd; String chucVu; String gioiTinh; String vaiTro; Boolean trangThai; }`
- **LoginRequest**: `{ String taiKhoan; String matKhau; }` / **LoginResponse**: `{ Integer id; String ma; String ten; String vaiTro; }`
- **DiaChiDto**: `{ Integer id; Integer idKhachHang; String thanhPho; String phuong; String diaChiThem; Boolean trangThai; }`
- **PhieuGiamGiaDto**: `{ Integer id; String ma; String ten; Integer loai; BigDecimal giaTri; BigDecimal donToiThieu; BigDecimal giamToiDa; Integer soLuong; String batDau; String ketThuc; Boolean trangThai; }` (loai←loaiGiamGia, giaTri←giaTriGiam, batDau←thoiGianBatDau, ketThuc←thoiGianKetThuc)
- **SanPhamDto**: `{ Integer id; String ma; String ten; String thuongHieu; String chatLieu; BigDecimal gia; String moTa; java.util.List<BienTheDto> bienThe; }`
- **BienTheDto** (= SanPhamChiTiet view): `{ Integer id; String ma; String mau; String size; Integer ton; BigDecimal gia; }`
- **HoaDonDto**: `{ Integer id; String ma; String khach; String nhanVien; String ngayTao; BigDecimal tongTien; String trangThai; java.util.List<HoaDonChiTietDto> chiTiet; }` (trangThai as label: map int 0→"Chờ",1→"Thành công",2→"Huỷ")
- **HoaDonChiTietDto**: `{ Integer id; String ten; Integer soLuong; BigDecimal donGia; BigDecimal thanhTien; }`
- **PosSanPhamDto**: `{ Integer id; String ten; String mau; String size; Integer ton; BigDecimal gia; }` (from SanPhamChiTiet: ten=idSanPham.tenSanPham, mau=idMauSac.tenMauSac, size=idKichCo.tenKichCo, ton=soLuongTon, gia=donGia)
- **ThongKeDoanhThuDto**: `{ Integer thang; Integer soSanPhamBan; BigDecimal tongGiaBan; BigDecimal tongGiamGia; BigDecimal tongDoanhThu; Integer soDon; Integer donThanhCong; Integer donHuy; }`
- **ThongKeSanPhamDto**: `{ String maSP; String tenSP; String loaiSP; String chatLieu; String mauSac; String kichThuoc; Integer soLuongTon; }`

Commit: `git add src/main/java/com/vn/test/bshoes/dto && git commit -m "feat(phase3): add frontend-shaped DTO package"`

---

## Task 2: Catalog attributes + VaiTro (canonical = ChatLieu)

Domains: **ChatLieu, MauSac, KichCo, KieuCoGiay, KieuDang, KieuDayGiay** → `AttributeDto`; **ThuongHieu, XuatSu, LoaiSanPham** → `AttributeMoTaDto`; **VaiTro** → `AttributeDto`. Rename **XuatXu → XuatSu** across repo/service/impl/controller (delete the XuatXu-named files, create XuatSu-named ones; entity is `XuatSu`).

Repos: the existing native `@Query`s (findByTen/updateMa/updateByMa) still compile (native SQL, entity return type unchanged) — KEEP them; ADD `List<Entity> findByTrangThaiXoaFalse()` for the active list if desired (optional). For VaiTro repo: plain `JpaRepository<VaiTro,Integer>`.

**Canonical — ChatLieu (apply the same shape to all, swapping entity/table/accessors/prefix/api/DTO):**

`service/ChatLieuService.java`:
```java
package com.vn.test.bshoes.service;
import com.vn.test.bshoes.dto.AttributeDto;
import java.util.List;
public interface ChatLieuService {
    List<AttributeDto> findAll();
    AttributeDto findById(int id);
    AttributeDto create(AttributeDto dto);
    AttributeDto update(AttributeDto dto);
    void delete(int id);
}
```

`service/impl/ChatLieuServiceImpl.java`:
```java
package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.entity.ChatLieu;
import com.vn.test.bshoes.repository.ChatLieuRepository;
import com.vn.test.bshoes.service.ChatLieuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChatLieuServiceImpl implements ChatLieuService {
    private final ChatLieuRepository repo;
    public ChatLieuServiceImpl(ChatLieuRepository repo) { this.repo = repo; }

    private AttributeDto toDto(ChatLieu e) {
        return new AttributeDto(e.getId(), e.getMaChatLieu(), e.getTenChatLieu(), e.getTrangThai());
    }
    @Override public List<AttributeDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeDto create(AttributeDto dto) {
        ChatLieu e = new ChatLieu();
        e.setTenChatLieu(dto.getTen());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        e = repo.save(e);
        e.setMaChatLieu("CL" + e.getId());
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeDto update(AttributeDto dto) {
        ChatLieu e = repo.findById(dto.getId()).orElseThrow();
        e.setTenChatLieu(dto.getTen());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
```

`controller/ChatLieuController.java`:
```java
package com.vn.test.bshoes.controller;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.service.ChatLieuService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat-lieu")
public class ChatLieuController {
    private final ChatLieuService service;
    public ChatLieuController(ChatLieuService service) { this.service = service; }
    @GetMapping public List<AttributeDto> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public AttributeDto findById(@PathVariable int id) { return service.findById(id); }
    @PostMapping public AttributeDto create(@RequestBody AttributeDto dto) { return service.create(dto); }
    @PutMapping public AttributeDto update(@RequestBody AttributeDto dto) { return service.update(dto); }
    @DeleteMapping("/{id}") public void delete(@PathVariable int id) { service.delete(id); }
}
```

**Substitutions per domain** (entity, ma-getter, ten-getter, prefix, api path, DTO):
| Domain | ma getter/setter | ten getter/setter | moTa | prefix | api |
|---|---|---|---|---|---|
| MauSac | MaMauSac | TenMauSac | — | (none, caller code) | /api/mau-sac |
| KichCo | MaKichCo | TenKichCo | — | (none) | /api/kich-co |
| KieuCoGiay | MaCoGiay | TenCoGiay | — | KC | /api/kieu-co-giay |
| KieuDang | MaKieuDang | TenKieuDang | — | KD | /api/kieu-dang |
| KieuDayGiay | MaDayGiay | TenDayGiay | — | DG | /api/kieu-day-giay |
| ThuongHieu | MaThuongHieu | TenThuongHieu | MoTa | TH | /api/thuong-hieu (AttributeMoTaDto) |
| XuatSu | MaXuatSu | TenXuatSu | MoTa | XX | /api/xuat-su (AttributeMoTaDto) |
| LoaiSanPham | MaLoaiSanPham | TenLoaiSanPham | MoTa | LSP | /api/loai-san-pham (AttributeMoTaDto) |
| VaiTro | MaVaiTro | TenVaiTro | — | (none) | /api/vai-tro (read-only: findAll/findById) |

KichCo/MauSac create: no prefix — set ma from `dto.getMa()` (caller supplies) and save once. ThuongHieu/XuatSu/LoaiSanPham use AttributeMoTaDto (map moTa too).

Commit: `git add src/main/java/com/vn/test/bshoes && git rm the old XuatXu*.java && git commit -m "feat(phase3): catalog-attribute + VaiTro services/controllers on new entities + DTO; XuatXu->XuatSu"`

---

## Task 3: KhachHang, NhanVien, Auth, DiaChi

Rewrite services/controllers to new entities + DTOs (`KhachHangDto`, `NhanVienDto`, `LoginRequest/Response`, `DiaChiDto`).
- **KhachHang** `/api/khach-hang`: list=active (`trangThaiXoa` false), search by ma/ten/sdt, create (`KH`+id), update, soft-delete (`setTrangThaiXoa(true)` + save). DTO mapping: ma←maKhachHang, ten←tenKhachHang, sdt←soDienThoai.
- **NhanVien** `/api/nhan-vien`: same; DTO vaiTro←`getIdVaiTro()!=null ? getIdVaiTro().getTenVaiTro() : null`. On create/update, resolve vaiTro name → a VaiTro (look up via VaiTroRepository `findByTenVaiTro`, or leave null if not found). Code `NV`+id. search by ten + gioiTinh.
- **Auth** `/api/auth/login`: `AuthService.login(taiKhoan, matKhau)` → find NhanVien by taiKhoan+matKhau (repo derived `findByTaiKhoanAndMatKhau`) → LoginResponse (vaiTro from relation) or null→401. `// NOTE: plaintext; BCrypt later.`
- **DiaChi** `/api/dia-chi`: basic CRUD + `findByKhachHang(int)` (derived `findByIdKhachHang_Id` or JPQL). DTO idKhachHang←`getIdKhachHang().getId()`.

Repos: replace broken native column-list queries with derived methods (`findByTrangThaiXoaFalse`, `findByTaiKhoanAndMatKhau`, `existsByMaKhachHang`, etc.). Keep it compiling against the entity field names.

Commit: `... -m "feat(phase3): customer, employee, auth, address on new entities + DTO"`

---

## Task 4: PhieuGiamGia `/api/phieu-giam-gia`
DTO `PhieuGiamGiaDto`. list=active, search by ma/ten, create (`PGG`+id), update, soft-delete. Map loai←loaiGiamGia, giaTri←giaTriGiam, batDau/ketThuc←thoiGian* (Instant→String via `.toString()`, and parse back on write with `Instant.parse` or accept null). Drop the missing `sp_cap_nhat_trang_thai...` proc endpoint (or implement as a bulk update `update phieu_giam_gia set trang_thai=0 where thoi_gian_ket_thuc < GETDATE()` native — optional). Commit.

---

## Task 5: Product domain — SanPham, SanPhamQl, SanPhamChiTiet, SanPhamChiTietQl
Keep endpoints `/api/san-pham`, `/api/san-pham-ql`, `/api/san-pham-chi-tiet`, `/api/san-pham-chi-tiet-ql` (per decision). Back them all by the canonical `SanPham`/`SanPhamChiTiet` entities.
- **SanPhamQlRepository**: replace `SanPham_ql` with derived queries on `SanPham`: `findByTrangThaiXoaFalse()`, `findByTrangThaiXoaTrue()` (recycle), `findByMaSanPham(String)`, search via JPQL `@Query("select s from SanPham s where s.maSanPham like ?1 or s.tenSanPham like ?2")`. Soft-delete/restore: derived get + set `trangThaiXoa`.
- **SanPhamQlService** returns `SanPhamDto`: ma←maSanPham, ten←tenSanPham, thuongHieu←idThuongHieu?.tenThuongHieu, chatLieu←idChatLieu?.tenChatLieu, moTa, gia← min/first variant donGia (or null), bienThe← that product's active SanPhamChiTiet mapped to `BienTheDto` (ma←maSanPhamChiTiet, mau←idMauSac?.tenMauSac, size←idKichCo?.tenKichCo, ton←soLuongTon, gia←donGia). Inject SanPhamChiTietRepository to fetch variants (derived `findByIdSanPham_IdAndTrangThaiXoaFalse(int)`).
- create/update SanPham from DTO: resolve thuongHieu/chatLieu names → entities via their repos (or accept ids — simplest: look up ThuongHieu by ten, ChatLieu by ten). Code `SP`+id.
- **SanPhamChiTietQl** `/api/san-pham-chi-tiet-ql`: list variants (BienTheDto), by-product, create (`SPCT`+parent maSanPham), soft-delete. Derived queries on SanPhamChiTiet.
- **SanPham/SanPhamChiTiet** (non-ql) `/api/san-pham`, `/api/san-pham-chi-tiet`: simple read (findAll active → SanPhamDto / BienTheDto), findById. (These can delegate to the same service logic.)

Commit.

---

## Task 6: HoaDon + HoaDonChiTiet `/api/hoa-don`, `/api/hoa-don-chi-tiet`
- Read endpoints return `HoaDonDto`: ma←maHoaDon, khach←idKhachHang?.tenKhachHang (or tenNguoiNhan), nhanVien←idNhanVien?.tenNhanVien, ngayTao←ngayTaoMa String, tongTien←tongTienPhaiTra, trangThai label from int, chiTiet← HoaDonChiTiet list mapped (ten←idSanPhamChiTiet.idSanPham.tenSanPham, soLuong, donGia←idSanPhamChiTiet.donGia, thanhTien).
- `GET /cart` = `findByTrangThai(0)`; `GET /vouchers-active` = native `SELECT * FROM view_phieu_giam_gia_hoat_dong` returning `PhieuGiamGia` → PhieuGiamGiaDto; `GET /{idPhieu}/giam-gia?tongTien=` = native `SELECT dbo.tinh_tien_giam_gia(?1,?2)` → BigDecimal.
- POS products: `GET /api/hoa-don/pos-products` → available SanPhamChiTiet mapped to `PosSanPhamDto` (trangThai true & soLuongTon>0).
- **Writes without procs:** implement `create`/checkout in Java: insert HoaDon (set fields, save) and HoaDonChiTiet rows (save), update stock — as plain repository saves in a `@Transactional` service. Keep minimal but real (no `EXEC` of missing procs). If full checkout is out of scope, expose at least create-empty-invoice + add-line + mark-paid via entity saves.

Commit.

---

## Task 7: LichSuHoaDon `/api/lich-su-hoa-don`
The frontend "lich su" screen shows invoices (ma, khach, nhanVien, ngayTao, tongTien, trangThai, chiTiet) — source that from **HoaDon** (history = all invoices), reusing `HoaDonDto`. Provide `GET /` (all/completed), `GET /{id}` (detail with chiTiet). (The `lich_su_hoa_don` audit table is separate; the screen needs invoice history, so back it with HoaDon.) Alternatively keep a LichSuHoaDon read too. Commit.

---

## Task 8: ThongKe `/api/thong-ke`
Create/keep DTOs `ThongKeDoanhThuDto`, `ThongKeSanPhamDto` (Task 1). Update `ThongKeRepository` native queries to return these DTOs via **interface projections** OR return `Object[]` and map in the service. Simplest robust path: change return types to `List<Object[]>` and map to DTOs in the service (documented), OR add `@SqlResultSetMapping`. Recommended: return `List<Object[]>` for the aggregation queries and map positionally in `ThongKeServiceImpl` to the DTOs; `loatNam()` stays `List<Integer>`. Endpoints unchanged (`/hom-nay,/theo-thang,/theo-nam,/san-pham,/nam`). Commit.

---

## Task 9: Wire frontend composables to REST
In `frontend/src/composables/use*.js`, replace the mock `useCrud(seed)` usage with calls to the matching `src/api/*.js` axios modules (which already exist), loading on mount and calling create/update/remove through the API. Keep a graceful fallback OR just switch to live calls. Ensure the DTO field names (`ma, ten, trangThai, thuongHieu, chatLieu, bienThe, ...`) match what the views read (they already do — DTOs were shaped to them). Adjust `src/api/*.js` paths/response mapping to the finalized endpoints. Update `vite.config.js` proxy already targets :8085. Run `npm run build` (must pass). Note in README that live data needs the backend running.

Commit.

---

## Task 10: Commit + push
`git push origin feature/vue-spa`. Report what compiles-by-inspection and what the user must verify in IntelliJ.

---

## Self-review notes
- Every broken file (all services/impls, all controllers referencing deleted types, XuatXu, ThongKe, _ql) is covered by Tasks 2–8; DTOs in Task 1; frontend in Task 9.
- No `EXEC` of non-existent procs; discount function + active-voucher view (which DO exist) kept as native.
- DTO shapes match the frontend field names so Task 9 wiring is a clean swap.
- Compile is the user's (no JDK here); subagents must read each entity to use exact accessors.
```
