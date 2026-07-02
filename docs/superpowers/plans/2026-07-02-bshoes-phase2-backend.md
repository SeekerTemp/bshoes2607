# BShoes Phase 2 — Backend Conversion (Repository + Service + REST) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax.

**Goal:** Convert the legacy `dao`/`daoipml` raw-SQL layer into a Spring Boot backend: Spring Data JPA repositories (`@Query`, native where needed), `@Service` classes holding the business logic, and `@RestController` JSON APIs for the Vue front-end. Uses the existing entity POJOs as-is.

**Architecture:** Standard 3-layer per domain: `repository` (Spring Data JPA) → `service` (interface + impl, `@Transactional` for multi-step) → `controller` (`@RestController` under `/api/...`). Entities are the user's existing POJOs, copied into `com.vn.test.bshoes.entity` **without** JPA annotations (the user adds `@Entity`/`@Id`/`@Column` later). Therefore **this code compiles but does not run** until entities are annotated and a SQL Server `BShoes` DB is available — this is expected and accepted.

**Tech Stack:** Spring Boot 4.1 (Java 17), Spring Data JPA, MS SQL Server (mssql-jdbc), Lombok (already in pom).

**Source of truth for SQL/methods:** the DAO catalog captured during planning (all legacy SQL, business rules, and known bugs). Decisions locked with the user:
- Web layer = **`@RestController`** (JSON for Vue).
- Stored procs / views / scalar functions = **faithful** native `@Query` / `@Procedure` (do not reimplement).
- Known legacy SQL bugs = **fix the obvious runtime-breakers**, adding a `// NOTE(legacy-bug): ...` comment citing the original.
- Scope = **all domains**, in dependency order.

**Design spec:** `docs/superpowers/specs/2026-07-02-bshoes-spring-boot-port-design.md`

---

## Conventions (apply to every domain)

**Packages:**
- `com.vn.test.bshoes.entity` — copied POJOs (no annotations yet).
- `com.vn.test.bshoes.repository` — `XxxRepository extends JpaRepository<Entity, Integer>`.
- `com.vn.test.bshoes.service` — `XxxService` (interface) + `impl/XxxServiceImpl`.
- `com.vn.test.bshoes.controller` — `XxxController` (`@RestController`). (The Phase-1 `PageController` stays.)

**Repository rules:**
- CRUD-by-PK (`findAll`, `findById`, `save`, `deleteById`) come from `JpaRepository` — do not redeclare.
- Everything else = `@Query(value = "<SQL>", nativeQuery = true)` using the legacy SQL verbatim (with bug-fixes per policy). Use positional binding `?1, ?2, ...`.
- Writes (`UPDATE`/`DELETE`/`EXEC`) get `@Modifying` + `@Transactional`.
- LIKE searches: pass the `%...%` wrapping from the service layer (keep SQL as `... like ?1`).

**Service rules:**
- Constructor injection (no field `@Autowired`).
- `@Transactional` on any method doing >1 statement or a write.
- Auto-code generation (legacy pattern `PREFIX + id`): in `create`, `save()` to get the identity, set the code, then persist the code via the repo's `updateMa`-style `@Query` (or a second `save`). Prefixes: ChatLieu `CL`, KieuCoGiay `KC`, KieuDang `KD`, KieuDayGiay `DG`, ThuongHieu `TH`, XuatXu `XX`, LoaiSanPham `LSP`, SanPhamChiTiet `SPCT`+parentProductCode.

**Controller rules:**
- `@RestController @RequestMapping("/api/<kebab-domain>")`.
- `GET /` → list, `GET /{id}` → one, `POST /` → create, `PUT /` → update, `DELETE /{id}` → delete, plus domain-specific endpoints named in each task.
- Return entities/DTOs directly (Jackson serializes). No DTO mapping layer in this pass.

**Bug-fix policy (apply, with a `// NOTE(legacy-bug)` comment):**
- `xuat_su`: post-insert code update must use `ma_xuat_su`/`id_xuat_su` (legacy wrongly wrote `ma_xuat_xu`/`id_xuat_xu`).
- `lich_su_hoa_don.findAllByDate`: use `ngay_tao_ma LIKE ?1 OR ngay_cap_nhat LIKE ?2` (legacy truncated the 2nd `like` and misspelled `ngay_cap_nhap`).
- `SanPhamChiTiet_hd.findAllById`: legacy queried `id_hoa_don` on `san_pham_chi_tiet` (no such column) — drop/repurpose; do NOT reproduce. If a "details for an invoice" lookup is needed it belongs on `hoa_don_chi_tiet`.
- `SanPhamChiTiet_hd.findAllAvailableByName`: rewrite the join with a declared alias and parenthesized `OR` (legacy used undeclared alias `spct` and unparenthesized `OR`).
- `DangNhap.layVaiTroTheoTaiKhoan`: legacy selected non-existent `vai_tro` column on `nhan_vien`; use the join to `vai_tro` instead (or omit — role comes from the `login` join).
- `create()` returning `null` (KhachHang, NhanVien, PhieuGiamGia): return the persisted entity instead.

**Datasource note:** `application.properties` gets real SQL Server config (Task 0) and the Phase-1 `spring.autoconfigure.exclude` datasource/JPA exclusions are removed. Consequence: the Spring Boot app will not start until entities are annotated + DB reachable. The `preview/` folder remains the way to view the UI meanwhile.

---

## Task 0: Config + copy entities

**Files:**
- Modify: `src/main/resources/application.properties`
- Create: `src/main/java/com/vn/test/bshoes/entity/*.java` (copied from `ref/BShoes_lts/src/main/java/entity/*.java`)

- [ ] **Step 1: Update application.properties**

Replace contents with:

```properties
spring.application.name=bshoes
server.port=8085

# Thymeleaf
spring.thymeleaf.cache=false

# SQL Server datasource (matches legacy util/XJdbc target). App will not start until
# entities are JPA-annotated and this DB is reachable. Adjust credentials as needed.
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=BShoes;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

- [ ] **Step 2: Copy every entity POJO into the new package**

For each file in `ref/BShoes_lts/src/main/java/entity/*.java`, create the same-named file under `src/main/java/com/vn/test/bshoes/entity/`, changing ONLY the package declaration to `package com.vn.test.bshoes.entity;` and removing the NetBeans license header comment. Do NOT add JPA annotations, do NOT change fields/getters/setters. (24 files: ChatLieu, DiaChi, HoaDon, HoaDonChiTiet, HoaDonChiTiet_advanced, KhachHang, KichCo, KieuCoGiay, KieuDang, KieuDayGiay, LichSuHoaDon, LoaiSanPham, MauSac, Nam, NhanVien, PhieuGiamGia, SanPham, SanPhamChiTiet, SanPhamChiTiet_ql, SanPham_ql, ThongKeDoanhThu, ThongKeSanPham, ThuongHieu, VaiTro, XuatXu.)

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/application.properties src/main/java/com/vn/test/bshoes/entity
git commit -m "chore(phase2): datasource+JPA config and copy entity POJOs into project"
```

---

## Task 1: Catalog-attributes domain (canonical pattern) — ChatLieu fully worked

Applies the pattern to 9 lookup tables. **ChatLieu is the complete reference; the other 8 repeat it with the substitutions in the table below.**

**Files (per attribute):** `repository/XxxRepository.java`, `service/XxxService.java`, `service/impl/XxxServiceImpl.java`, `controller/XxxController.java`.

- [ ] **Step 1: ChatLieu repository**

`src/main/java/com/vn/test/bshoes/repository/ChatLieuRepository.java`:

```java
package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {

    @Query(value = "select * from chat_lieu where ten_chat_lieu = ?1", nativeQuery = true)
    ChatLieu findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE chat_lieu SET ma_chat_lieu = ?1 WHERE id_chat_lieu = ?2", nativeQuery = true)
    void updateMa(String ma, int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE chat_lieu SET ten_chat_lieu = ?1 WHERE ma_chat_lieu = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
```

- [ ] **Step 2: ChatLieu service interface**

`src/main/java/com/vn/test/bshoes/service/ChatLieuService.java`:

```java
package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.ChatLieu;
import java.util.List;

public interface ChatLieuService {
    List<ChatLieu> findAll();
    ChatLieu findById(int id);
    ChatLieu findByTen(String ten);
    ChatLieu create(ChatLieu e);
    void update(ChatLieu e);
    void delete(int id);
}
```

- [ ] **Step 3: ChatLieu service impl**

`src/main/java/com/vn/test/bshoes/service/impl/ChatLieuServiceImpl.java`:

```java
package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.ChatLieu;
import com.vn.test.bshoes.repository.ChatLieuRepository;
import com.vn.test.bshoes.service.ChatLieuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatLieuServiceImpl implements ChatLieuService {

    private final ChatLieuRepository repo;

    public ChatLieuServiceImpl(ChatLieuRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ChatLieu> findAll() {
        return repo.findAll();
    }

    @Override
    public ChatLieu findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public ChatLieu findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public ChatLieu create(ChatLieu e) {
        // Legacy pattern: insert, then set business code "CL" + generated id.
        ChatLieu saved = repo.save(e);
        String ma = "CL" + saved.getId_chat_lieu();
        saved.setMa_chat_lieu(ma);
        repo.updateMa(ma, saved.getId_chat_lieu());
        return saved;
    }

    @Override
    @Transactional
    public void update(ChatLieu e) {
        repo.updateByMa(e.getTen_chat_lieu(), e.getMa_chat_lieu());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
```

- [ ] **Step 4: ChatLieu REST controller**

`src/main/java/com/vn/test/bshoes/controller/ChatLieuController.java`:

```java
package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.ChatLieu;
import com.vn.test.bshoes.service.ChatLieuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-lieu")
public class ChatLieuController {

    private final ChatLieuService service;

    public ChatLieuController(ChatLieuService service) {
        this.service = service;
    }

    @GetMapping
    public List<ChatLieu> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ChatLieu findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public ChatLieu create(@RequestBody ChatLieu e) { return service.create(e); }

    @PutMapping
    public ChatLieu update(@RequestBody ChatLieu e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
```

- [ ] **Step 5: Repeat the 4 files for the other 8 attributes** using these substitutions (entity, table, id column, name column, code prefix, api path). Each `create` sets `PREFIX + id`; each `updateByMa` updates the name column by the code column; `findByTen` selects by the name column. **KichCo and MauSac have NO auto-code** (code supplied by caller) — their `create` is a plain `save` (no `updateMa` step), and they also expose `findByTen`.

| Entity | table | id col | name col | code col | prefix | api path | insert cols | notes |
|---|---|---|---|---|---|---|---|---|
| KieuCoGiay | kieu_co_giay | id_kieu_co_giay | ten_co_giay | ma_co_giay | KC | /api/kieu-co-giay | (ten_co_giay) | 2-step code |
| KieuDang | kieu_dang | id_kieu_dang | ten_kieu_dang | ma_kieu_dang | KD | /api/kieu-dang | (ten_kieu_dang) | 2-step code |
| KieuDayGiay | kieu_day_giay | id_kieu_day_giay | ten_day_giay | ma_day_giay | DG | /api/kieu-day-giay | (ten_day_giay) | 2-step code |
| ThuongHieu | thuong_hieu | id_thuong_hieu | ten_thuong_hieu | ma_thuong_hieu | TH | /api/thuong-hieu | (ten_thuong_hieu, mo_ta) | 2-step code |
| XuatXu | xuat_su | id_xuat_su | ten_xuat_su | ma_xuat_su | XX | /api/xuat-su | (ten_xuat_su, mo_ta) | 2-step code; **bug-fix:** update uses `ma_xuat_su`/`id_xuat_su` |
| LoaiSanPham | loai_san_pham | id_loai_san_pham | ten_loai_san_pham | ma_loai_san_pham | LSP | /api/loai-san-pham | (ten_loai_san_pham, mo_ta) | 2-step code; update sets `ten_loai_san_pham=?, mo_ta=?` by `ma` |
| KichCo | kich_co | id_kich_co | ten_kich_co | ma_kich_co | (none) | /api/kich-co | (ma_kich_co, ten_kich_co) | **no auto-code**; create = plain save |
| MauSac | mau_sac | id_mau_sac | ten_mau_sac | ma_mau_sac | (none) | /api/mau-sac | (ma_mau_sac, ten_mau_sac) | **no auto-code**; create = plain save |

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/vn/test/bshoes/repository src/main/java/com/vn/test/bshoes/service src/main/java/com/vn/test/bshoes/controller
git commit -m "feat(phase2): catalog-attribute repositories, services, REST controllers"
```

---

## Task 2: Product domain (SanPham, SanPham_ql, SanPhamChiTiet, SanPhamChiTiet_ql)

**Files:** repository + service (iface+impl) + controller for each of the 4 product entities (`/api/san-pham`, `/api/san-pham-ql`, `/api/san-pham-chi-tiet`, `/api/san-pham-chi-tiet-ql`).

Use these native queries (fixed per bug policy). Inherited CRUD from JpaRepository is not redeclared.

- [ ] **Step 1: SanPhamRepository (`SanPham`, read path)**
  - `findByMa`: `select id_san_pham,ma_san_pham,ten_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su from san_pham where ma_san_pham like ?1` (native). Service wraps `%..%`.
  - Inherited `findAll`/`findById`/`deleteById` used directly.

- [ ] **Step 2: SanPhamQlRepository (`SanPham_ql`, admin path, joined + soft-delete)**
  - `findAllActive`: `SELECT san_pham.id_san_pham, san_pham.ma_san_pham, loai_san_pham.id_loai_san_pham, san_pham.ten_san_pham, san_pham.id_chat_lieu, san_pham.id_kieu_dang, san_pham.id_kieu_co_giay, san_pham.id_kieu_day_giay, san_pham.id_thuong_hieu, san_pham.id_xuat_su, san_pham.mo_ta, san_pham.trang_thai FROM loai_san_pham INNER JOIN san_pham ON loai_san_pham.id_loai_san_pham = san_pham.id_loai_san_pham WHERE san_pham.trang_thai_xoa = 0` (native)
  - `findRecycle`: same with `WHERE san_pham.trang_thai_xoa = 1` (native)
  - `findByIdJoined(int)`: same join `where san_pham.id_san_pham = ?1` (native)
  - `search(String)`: `select id_san_pham,ma_san_pham,ten_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su from san_pham where ma_san_pham like ?1 or ten_san_pham like ?2` (native)
  - `@Modifying insert` create: `INSERT INTO san_pham (id_loai_san_pham, id_chat_lieu, id_kieu_dang, id_kieu_co_giay, id_kieu_day_giay, id_thuong_hieu, id_xuat_su, ma_san_pham, ten_san_pham, mo_ta, trang_thai_xoa) VALUES (?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11)` — OR use `save`. Prefer a `save` in service (identity) to mirror `insertAndReturnId`.
  - `@Modifying updateByMa`: `update san_pham set ten_san_pham=?1, id_chat_lieu=?2, id_kieu_dang=?3, id_kieu_co_giay=?4, id_kieu_day_giay=?5, id_thuong_hieu=?6, id_xuat_su=?7 where ma_san_pham = ?8`
  - `@Modifying softDelete`: `UPDATE san_pham SET trang_thai_xoa = 1 WHERE ma_san_pham = ?1`
  - `@Modifying restore`: `UPDATE san_pham SET trang_thai_xoa = 0 WHERE ma_san_pham = ?1`

- [ ] **Step 3: SanPhamChiTietRepository (`SanPhamChiTiet`, stock/read path)**
  - `findAllAvailable`: `SELECT * FROM san_pham_chi_tiet WHERE trang_thai = 1 AND so_luong_ton > 0` (native; dropped the SQL-Server 3-part name)
  - `findByMa`: `SELECT * FROM san_pham_chi_tiet WHERE ma_san_pham_chi_tiet = ?1` (native)
  - `findAvailableByName`: **bug-fixed** `SELECT spct.* FROM san_pham_chi_tiet spct INNER JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE spct.trang_thai = 1 AND spct.so_luong_ton > 0 AND (spct.ma_san_pham_chi_tiet like ?1 OR sp.ten_san_pham like ?2)` (native) — `// NOTE(legacy-bug): original used undeclared alias spct and unparenthesized OR`
  - `@Modifying updateStock`: `update san_pham_chi_tiet set so_luong_ton = ?1 where id_san_pham_chi_tiet = ?2 and trang_thai = 1`
  - `@Modifying deactivateWhenEmpty`: `update san_pham_chi_tiet set trang_thai = 0 where so_luong_ton = 0 and id_san_pham_chi_tiet = ?1`
  - (Do NOT port `findAllById`-by-`id_hoa_don` — `// NOTE(legacy-bug): san_pham_chi_tiet has no id_hoa_don column; omitted.`)

- [ ] **Step 4: SanPhamChiTietQlRepository (`SanPhamChiTiet_ql`, admin, joined + soft-delete)**
  - `findAllActive`: `SELECT spct.id_san_pham_chi_tiet, spct.id_san_pham, spct.id_kich_co, spct.id_mau_sac, spct.ma_san_pham_chi_tiet, spct.so_luong_ton, spct.don_gia, spct.ngay_tao, spct.ngay_cap_nhat, spct.nguoi_tao, spct.nguoi_cap_nhat, spct.trang_thai, sp.ten_san_pham, sp.ma_san_pham FROM san_pham_chi_tiet spct LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE spct.trang_thai_xoa = 0` (native)
  - `findByIdJoined(int)`: same select `WHERE spct.id_san_pham_chi_tiet = ?1`
  - `findByProduct(int idSanPham)`: same select `WHERE spct.id_san_pham = ?1`
  - `search(String)`: same select `WHERE spct.ma_san_pham_chi_tiet like ?1 or sp.ten_san_pham like ?2`
  - `findParentCode(int idSanPham)`: `SELECT ma_san_pham FROM san_pham WHERE id_san_pham = ?1` (native, returns String)
  - `@Modifying update`: `update san_pham_chi_tiet set ma_san_pham_chi_tiet = ?1, id_mau_sac = ?2, id_kich_co = ?3, don_gia = ?4, so_luong_ton = ?5, trang_thai = ?6 where id_san_pham_chi_tiet = ?7`
  - `@Modifying softDeleteById`: `UPDATE san_pham_chi_tiet SET trang_thai_xoa = 1 WHERE id_san_pham_chi_tiet = ?1`
  - `@Modifying softDeleteByProduct`: `UPDATE san_pham_chi_tiet SET trang_thai_xoa = 1 WHERE id_san_pham = ?1`

- [ ] **Step 5: Services** — `SanPhamService`, `SanPhamQlService`, `SanPhamChiTietService`, `SanPhamChiTietQlService` (+ impls). Business logic:
  - `SanPhamQlServiceImpl.softDelete(String maSP)` is `@Transactional` and calls **both** `sanPhamQlRepo.softDelete(maSP)` and `sanPhamChiTietQlRepo.softDeleteByProduct(idSanPham)` — resolve `idSanPham` via `sanPhamChiTietQlRepo.findParentCode` is the reverse; instead add `SanPhamQlRepository.findIdByMa`: `SELECT id_san_pham FROM san_pham WHERE ma_san_pham = ?1` and pass that id to `softDeleteByProduct`. (Mirrors legacy cascade `DaoImpl_SanPham_ql.softDelete` → `spctDao.softDeleteBySanPham`.)
  - `SanPhamChiTietQlServiceImpl.create(SanPhamChiTiet_ql e)` is `@Transactional`: if `e.getMa_san_pham_chi_tiet()` is blank, fetch parent code via `findParentCode(e.getId_san_pham())` and set `e.setMa_san_pham_chi_tiet("SPCT" + parentCode)`; then `save(e)` (identity). (Mirrors the legacy `SCOPE_IDENTITY()` create.)
  - Other services: standard delegation; `create` uses `save`.

- [ ] **Step 6: Controllers** — 4 `@RestController`s. Include the extra endpoints: `/api/san-pham-ql/recycle` (GET → findRecycle), `POST /api/san-pham-ql/restore/{ma}`, `DELETE /api/san-pham-ql/soft/{ma}`; `/api/san-pham-chi-tiet/available` (GET), `PUT /api/san-pham-chi-tiet/stock`; `/api/san-pham-chi-tiet-ql/by-product/{idSanPham}` (GET).

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/vn/test/bshoes
git commit -m "feat(phase2): product + product-detail repositories, services, REST controllers"
```

---

## Task 3: Customer, Employee, Auth, VaiTro, DiaChi

**Files:** repo+service+controller per domain. APIs: `/api/khach-hang`, `/api/nhan-vien`, `/api/auth`, `/api/vai-tro`, `/api/dia-chi`.

- [ ] **Step 1: KhachHangRepository** (soft-delete `trang_thai_xoa`)
  - `findAllActive`: `select id_khach_hang,ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai,nguoi_tao_ma,nguoi_cap_nhat from khach_hang where trang_thai_xoa = 0`
  - `findByIdActive`: same cols `where id_khach_hang = ?1 and trang_thai_xoa = 0`
  - `searchByName`: same cols `where ten_khach_hang like ?1 and trang_thai_xoa = 0`
  - `findBySdt`: `SELECT * FROM khach_hang WHERE so_dien_thoai = ?1`
  - `@Modifying create insert`: `INSERT INTO khach_hang(ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai) values (?1,?2,?3,?4,?5,?6,?7)` (or use `save`; service returns the entity — **bug-fix:** not null)
  - `@Modifying updateByMa`: `UPDATE khach_hang set ten_khach_hang=?1,gioi_tinh=?2,so_dien_thoai=?3,dia_chi=?4,email=?5,trang_thai=?6 where ma_khach_hang=?7`
  - `@Modifying softDelete`: `update khach_hang set trang_thai_xoa = 1 where id_khach_hang = ?1`
  - exists checks (return long/int): `existsMa` `SELECT COUNT(*) FROM khach_hang WHERE ma_khach_hang = ?1 AND trang_thai_xoa = 0`; `existsEmail` (email); `existsSdt` (so_dien_thoai); `existsMaExcludingId` `SELECT COUNT(*) FROM khach_hang WHERE ma_khach_hang=?1 AND id_khach_hang <> ?2 AND trang_thai_xoa = 0`. All native, return `long`.

- [ ] **Step 2: NhanVienRepository** (soft-delete)
  - `findAllActive`: `SELECT id_nhan_vien,ma_nhan_vien, ten_nhan_vien, cccd, email, so_dien_thoai, gioi_tinh, dia_chi,ngay_sinh FROM nhan_vien where trang_thai_xoa = 0`
  - `findByIdActive`: `SELECT id_nhan_vien,ma_nhan_vien, ten_nhan_vien, cccd, email, so_dien_thoai, gioi_tinh, dia_chi,ngay_sinh,tai_khoan, mat_khau FROM nhan_vien WHERE id_nhan_vien = ?1 and trang_thai_xoa = 0`
  - `searchByName`: **bug-fixed conditional** `SELECT id_nhan_vien,ma_nhan_vien,ten_nhan_vien,cccd,email,so_dien_thoai,gioi_tinh,dia_chi,ngay_sinh FROM nhan_vien WHERE trang_thai_xoa = 0 AND ten_nhan_vien LIKE ?1 AND (?2 = 'all' OR gioi_tinh = ?2)` (native) — collapses the legacy Java if/else branch.
  - `@Modifying create insert`: `INSERT INTO nhan_vien (ma_nhan_vien, ten_nhan_vien, cccd, email, so_dien_thoai, gioi_tinh, dia_chi, ngay_sinh, tai_khoan, mat_khau) VALUES (?1,?2,?3,?4,?5,?6,?7,?8,?9,?10)` (or `save`; return entity)
  - `@Modifying updateByMa`: `UPDATE nhan_vien SET ten_nhan_vien=?1, cccd=?2, email=?3, so_dien_thoai=?4, gioi_tinh=?5, dia_chi=?6, ngay_sinh=?7, tai_khoan=?8, mat_khau=?9 WHERE ma_nhan_vien = ?10`
  - `@Modifying softDelete`: `update nhan_vien set trang_thai_xoa = 1 where id_nhan_vien = ?1`
  - exists checks (native, `long`): `existsMa`, `existsTaiKhoan`, `existsCCCD`, `existsEmail`, `existsSdt` — each `SELECT COUNT(*) FROM nhan_vien WHERE <col> = ?1`.
  - `login`: `SELECT nv.id_nhan_vien, nv.ma_nhan_vien, nv.ten_nhan_vien, vt.ma_vai_tro, vt.ten_vai_tro FROM nhan_vien nv JOIN vai_tro vt ON nv.id_vai_tro = vt.id_vai_tro WHERE nv.tai_khoan = ?1 AND nv.mat_khau = ?2` (native, returns `NhanVien`).

- [ ] **Step 3: AuthService + AuthController**
  - `AuthService.login(String taiKhoan, String matKhau)` delegates to `NhanVienRepository.login`. `// NOTE: plaintext password comparison retained from legacy; replace with Spring Security + BCrypt in a later pass.`
  - `AuthController`: `POST /api/auth/login` (body `{taiKhoan, matKhau}` → returns the `NhanVien` or 401). Keep minimal.

- [ ] **Step 4: VaiTroRepository/Service/Controller** — legacy DAO was all stubs. Provide plain CRUD: `JpaRepository<VaiTro,Integer>` (inherited methods only), a thin service, `/api/vai-tro` with GET list + GET/{id}.

- [ ] **Step 5: DiaChiRepository/Service/Controller** — legacy all stubs; table `dia_chi` (entity `DiaChi`). Provide plain CRUD (`JpaRepository<DiaChi,Integer>`), plus `findByKhachHang(int)`: `SELECT * FROM dia_chi WHERE id_khach_hang = ?1` (native). `// NOTE: legacy DiaChi DAO was unimplemented; basic CRUD provided.`

- [ ] **Step 6: Services + Controllers** for KhachHang and NhanVien following the canonical pattern. Service `create` returns the entity; `search` wraps `%..%`; exists-endpoints exposed as `GET /api/khach-hang/exists/ma?value=...` etc. (or omit if not needed by UI — keep `existsMa`, `existsEmail`, `existsSdt`). Controllers: standard CRUD + `GET /api/nhan-vien/search?ten=&gioiTinh=`, `POST /api/auth/login`.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/vn/test/bshoes
git commit -m "feat(phase2): customer, employee, auth, role, address layers"
```

---

## Task 4: Voucher domain (PhieuGiamGia)

**Files:** repo+service+controller, `/api/phieu-giam-gia`.

- [ ] **Step 1: PhieuGiamGiaRepository** (soft-delete)
  - `findAllActive`: `select id_phieu_giam_gia,ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai from phieu_giam_gia where trang_thai_xoa = 0`
  - `findByIdActive`: same cols `where id_phieu_giam_gia = ?1 and trang_thai_xoa = 0`
  - `searchByName`: same cols `where trang_thai_xoa = 0 and ten_phieu_giam like ?1`
  - `@Modifying create insert`: `insert into phieu_giam_gia(ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai) values (?1,?2,?3,?4,?5,?6,?7,?8,?9,?10)` (or `save`; return entity)
  - `@Modifying updateByMa`: `UPDATE phieu_giam_gia set ten_phieu_giam=?1,loai_giam_gia=?2,gia_tri_giam=?3,don_toi_thieu=?4,giam_toi_da=?5,so_luong=?6,thoi_gian_bat_dau=?7,thoi_gian_ket_thuc=?8,trang_thai=?9 where ma_phieu_giam=?10`
  - `@Modifying softDelete`: `update phieu_giam_gia set trang_thai_xoa = 1 where id_phieu_giam_gia=?1`
  - `existsMa`/`existsTen` (native `long`): `SELECT COUNT(*) FROM phieu_giam_gia WHERE ma_phieu_giam = ?1` / `... WHERE ten_phieu_giam = ?1`
  - `@Modifying capNhatTrangThai` (**faithful proc call**): `@Query(value = "EXEC sp_cap_nhat_trang_thai_phieu_giam_gia", nativeQuery = true)` void.

- [ ] **Step 2: Service + Controller** — canonical CRUD; `create` returns entity; expose `POST /api/phieu-giam-gia/cap-nhat-trang-thai` → `capNhatTrangThai()`. `// NOTE: legacy uses a scheduled proc; a @Scheduled Java job could replace it later.`

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/vn/test/bshoes
git commit -m "feat(phase2): voucher (phieu giam gia) layer"
```

---

## Task 5: Invoice / POS domain (HoaDon, HoaDonChiTiet)

**Files:** repo+service+controller for each, `/api/hoa-don`, `/api/hoa-don-chi-tiet`.

- [ ] **Step 1: HoaDonRepository**
  - `@Modifying create` (**faithful proc**): `@Query(value = "EXEC sp_insert_hoa_don_placeholder ?1,?2,?3", nativeQuery = true)` void (params trang_thai, loai_hoa_don, id_nhan_vien). `// NOTE(legacy): create() referenced an unfinished placeholder proc.`
  - `@Modifying update`: `UPDATE hoa_don SET tong_tien_ban_dau = ?1, tien_giam_gia = ?2, tong_tien_phai_tra = ?3, trang_thai = ?4, loai_hoa_don = ?5, nguoi_cap_nhat = ?6, ngay_cap_nhat = GETDATE() WHERE id_hoa_don = ?7`
  - `findAllCart`: `select id_hoa_don, ma_hoa_don, id_khach_hang, id_nhan_vien, tong_tien_ban_dau, tien_giam_gia, tong_tien_phai_tra, trang_thai, loai_hoa_don, phuong_thuc_thanh_toan, ngay_tao_ma, ngay_cap_nhat, nguoi_tao_ma, nguoi_cap_nhat from hoa_don where trang_thai = 0 order by id_hoa_don desc`
  - `findByMa`: `select * from hoa_don where ma_hoa_don = ?1`
  - `@Modifying markPaid`: `UPDATE hoa_don SET trang_thai = 1, loai_hoa_don = 1, ngay_cap_nhat = GETDATE(), nguoi_cap_nhat = ?2 WHERE id_hoa_don = ?1` (parameterize `nguoi_cap_nhat` instead of hardcoded 'admin')
  - `@Modifying updateKhachHang`: `UPDATE hoa_don SET id_khach_hang = ?2 WHERE id_hoa_don = ?1`
  - `tinhGiamGia` (**faithful scalar fn**): `@Query(value = "SELECT dbo.tinh_tien_giam_gia(?1, ?2)", nativeQuery = true)` returns `BigDecimal`.
  - `getPhieuGiamGiaHoatDong` (**faithful view**): `@Query(value = "SELECT * FROM view_phieu_giam_gia_hoat_dong", nativeQuery = true)` returns `List<PhieuGiamGia>`.

- [ ] **Step 2: HoaDonChiTietRepository**
  - `@Modifying create`: `INSERT INTO hoa_don_chi_tiet (id_san_pham_chi_tiet, id_hoa_don, so_luong, nguoi_tao, nguoi_cap_nhat, trang_thai, thanh_tien, gia_giam) VALUES (?1, ?2, ?3, ?4, ?5, 0, ?6, 0)`
  - `@Modifying createViaProc` (**faithful proc**): `@Query(value = "EXEC tao_hoa_don_va_chi_tiet ?1,?2,?3,?4", nativeQuery = true)` void (id_san_pham_chi_tiet, so_luong, gia_giam, nguoi_tao).
  - `@Modifying update`: `update hoa_don_chi_tiet set so_luong=?1,thanh_tien=?2 where id_hoa_don_chi_tiet=?3 and trang_thai=0`
  - `@Modifying updateStock`: `update hoa_don_chi_tiet set so_luong=?1 where id_hoa_don_chi_tiet=?2`
  - `findByHoaDon(int)`: `select * from hoa_don_chi_tiet where id_hoa_don = ?1`
  - `findByHoaDonAndSpct(int,int)`: `select * from hoa_don_chi_tiet where id_hoa_don=?1 and id_san_pham_chi_tiet = ?2`
  - `findDetailByHoaDon(int)` (join → maps to `HoaDonChiTiet_advanced` shape; return `HoaDonChiTiet` with the joined `ten_san_pham`/`don_gia` columns populated by setter-name matching): `SELECT hdct.id_hoa_don_chi_tiet, hdct.id_hoa_don, hdct.id_san_pham_chi_tiet, sp.ten_san_pham AS ten_san_pham, spct.don_gia AS don_gia, hdct.so_luong, hdct.thanh_tien FROM hoa_don_chi_tiet hdct JOIN san_pham_chi_tiet spct ON spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE hdct.id_hoa_don = ?1` (native, returns `List<HoaDonChiTiet>`)
  - Inherited `deleteById` used for hard delete.

- [ ] **Step 3: Services + Controllers**
  - `HoaDonService`/`HoaDonChiTietService` (+impls) delegate; `@Transactional` on writes.
  - Controllers: `/api/hoa-don` CRUD + `GET /cart` (findAllCart), `PUT /mark-paid/{id}?nguoiCapNhat=`, `PUT /{id}/khach-hang/{idKhachHang}`, `GET /{idPhieu}/giam-gia?tongTien=` (tinhGiamGia), `GET /vouchers-active` (getPhieuGiamGiaHoatDong). `/api/hoa-don-chi-tiet` CRUD + `GET /by-hoa-don/{id}` (findDetailByHoaDon), `POST /via-proc`, `PUT /stock`.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/vn/test/bshoes
git commit -m "feat(phase2): invoice/POS (hoa don + chi tiet) layer with faithful proc/view/function queries"
```

---

## Task 6: Invoice history (LichSuHoaDon)

**Files:** repo+service+controller, `/api/lich-su-hoa-don`.

- [ ] **Step 1: LichSuHoaDonRepository**
  - `findAllActive`: `select * from lich_su_hoa_don where trang_thai = 1`
  - `findAllCancel`: `select * from lich_su_hoa_don where trang_thai = 3`
  - `findByDate(String,String)`: **bug-fixed** `select * from lich_su_hoa_don where ngay_tao_ma LIKE ?1 OR ngay_cap_nhat LIKE ?2` (native) — `// NOTE(legacy-bug): original SQL was truncated and misspelled ngay_cap_nhap`.
  - Inherited `findById` used.

- [ ] **Step 2: Service + Controller** — `/api/lich-su-hoa-don` GET list (active), `GET /cancelled`, `GET /by-date?tuNgay=&denNgay=`, `GET /{id}`.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/vn/test/bshoes
git commit -m "feat(phase2): invoice history (lich su hoa don) layer"
```

---

## Task 7: Statistics (ThongKe) — native aggregation + DTO projections

**Files:** `repository/ThongKeRepository.java`, `service/ThongKeService.java` + impl, `controller/ThongKeController.java`, `/api/thong-ke`. This repository is NOT a `JpaRepository` for a single table — make it `@Repository` over a base entity but only declaring native `@Query`s. Simplest: `interface ThongKeRepository extends JpaRepository<HoaDon, Integer>` (reuses a managed type) and add native queries returning DTO projections. Return types: `ThongKeDoanhThu`, `ThongKeSanPham`, and `Integer` for years. Because these DTOs are populated by column-alias→setter matching, native queries with matching aliases work once entities/DTOs are managed; mark DTOs with `@Query`'s native result mapping later if needed (accepted: won't run until ORM).

- [ ] **Step 1: ThongKeRepository** — native `@Query`s (copy verbatim from catalog, aliases preserved):
  - `homNay()` → `List<ThongKeDoanhThu>`: today's revenue (uses `GETDATE()`), grouped by `DAY(...)`, `loai_hoa_don = 1`.
  - `theoNgay(java.sql.Date from, java.sql.Date to)` → `List<ThongKeDoanhThu>`: `... WHERE CAST(hd.ngay_tao_ma AS DATE) BETWEEN ?1 AND ?2 AND loai_hoa_don = 1`.
  - `theoThang(int thang, int nam)` → `List<ThongKeDoanhThu>`: `... WHERE MONTH(...) = ?1 AND YEAR(...) = ?2 AND loai_hoa_don = 1 GROUP BY MONTH(...)`.
  - `theoNam(int nam)` → `List<ThongKeDoanhThu>`: `... WHERE YEAR(...) = ?1 AND loai_hoa_don = 1 GROUP BY MONTH(...) ORDER BY MONTH(...)`.
  - `tatCaSanPham()` → `List<ThongKeSanPham>`: the 5-table stock join, `ORDER BY spct.so_luong_ton ASC`.
  - `loatNam()` → `List<Integer>`: `SELECT DISTINCT YEAR(ngay_tao_ma) AS nam FROM hoa_don ORDER BY nam DESC`.
  (Use the exact SQL strings from the catalog for each; all `nativeQuery = true`.)

- [ ] **Step 2: Service + Controller** — `ThongKeService` delegates; `ThongKeController` `/api/thong-ke`: `GET /hom-nay`, `GET /theo-ngay?tuNgay=&denNgay=` (parse to `java.sql.Date`), `GET /theo-thang?thang=&nam=`, `GET /theo-nam?nam=`, `GET /san-pham`, `GET /nam`.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/vn/test/bshoes
git commit -m "feat(phase2): statistics (thong ke) native aggregation queries + REST"
```

---

## Task 8: Compile check + push

**Files:** none (verification + git)

- [ ] **Step 1: Attempt compile** (compilation does not require a DB or JPA annotations)

Run: `.\mvnw.cmd -o clean compile` (offline). If dependencies are not cached, run without `-o`.
Expected: `BUILD SUCCESS`. If it fails, the errors are compile-level reference issues (wrong import, entity getter name mismatch) — fix them; they are NOT the expected runtime/ORM gaps. If deps cannot be downloaded in this environment, record that compile was not verified and rely on the structural review instead.

- [ ] **Step 2: Push**

```bash
git push origin feature/spring-boot-port
```

---

## Self-review notes (author)

- **Catalog coverage:** every DAOImpl in the catalog maps to a task — catalog attrs (Task 1: ChatLieu, KieuCoGiay, KieuDang, KieuDayGiay, ThuongHieu, XuatXu, LoaiSanPham, KichCo, MauSac), products (Task 2: SanPham, SanPham_ql, SanPhamChiTiet, SanPhamChiTiet_ql), people/auth (Task 3: KhachHang, NhanVien, DangNhap→Auth, VaiTro, DiaChi), vouchers (Task 4), invoices (Task 5: HoaDon, HoaDonChiTiet), history (Task 6: LichSuHoaDon), stats (Task 7: ThongKe). Config+entities in Task 0.
- **Decisions honored:** REST controllers; procs/views/functions kept as faithful native `@Query`/`EXEC`; listed legacy bugs fixed with `NOTE(legacy-bug)` comments; entities used as-is (copied, unannotated).
- **Placeholders:** none — each repository method has its concrete SQL; the canonical ChatLieu domain is fully coded and the substitution table + per-domain SQL make the rest concrete.
- **Won't-run acceptance:** compiles but won't start until the user annotates entities + provides the DB — explicitly scoped.
```
