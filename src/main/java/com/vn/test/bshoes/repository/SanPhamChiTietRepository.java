package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    @Query("select s from SanPhamChiTiet s where s.idSanPham.id = ?1 and (s.trangThaiXoa = false or s.trangThaiXoa is null)")
    List<SanPhamChiTiet> findActiveByProduct(int idSanPham);

    @Query("select s from SanPhamChiTiet s where s.trangThaiXoa = false or s.trangThaiXoa is null")
    List<SanPhamChiTiet> findActive();

    @Query("select s from SanPhamChiTiet s where s.trangThai = true and s.soLuongTon > 0")
    List<SanPhamChiTiet> findAvailable();

    /**
     * Storefront listing: every variant on sale, including the ones with no stock —
     * those are what customers đặt trước (pre-order).
     */
    @Query("select s from SanPhamChiTiet s where s.trangThai = true and (s.trangThaiXoa = false or s.trangThaiXoa is null)")
    List<SanPhamChiTiet> findForStore();

    /**
     * Scan/lookup by variant code (POS). Ignores inactive (trangThai=false) and
     * soft-deleted variants — but deliberately does NOT filter on soLuongTon, so a
     * sold-out variant is still FOUND (the caller reports "hết hàng" rather than the
     * misleading "không tìm thấy").
     */
    @Query("select s from SanPhamChiTiet s where s.maSanPhamChiTiet = :ma and s.trangThai = true " +
           "and (s.trangThaiXoa = false or s.trangThaiXoa is null)")
    SanPhamChiTiet findByMaSanPhamChiTiet(@Param("ma") String ma);

    /**
     * Atomically decrement stock. Returns rows affected: 1 = ok, 0 = not enough stock
     * (guarded by {@code so_luong_ton >= :n}). Prefer this over read-modify-write.
     */
    @Modifying
    @Query("update SanPhamChiTiet s set s.soLuongTon = s.soLuongTon - :n, s.ngayCapNhat = CURRENT_TIMESTAMP " +
           "where s.id = :id and s.soLuongTon >= :n")
    int decrementStock(@Param("id") int id, @Param("n") int n);

    /**
     * Ẩn (xóa mềm) một biến thể bằng bulk update, KHÔNG qua repo.save().
     *
     * Cố ý như vậy: entity giờ có Bean Validation (phải có màu, kích cỡ, đơn giá > 0),
     * mà validation chạy lúc flush nên nó áp cho cả những dòng cũ được tạo từ trước khi
     * có ràng buộc. Nếu ẩn cũng đi qua save() thì đúng những dòng dữ liệu hỏng — thứ
     * người dùng muốn dọn nhất — lại là thứ không thể ẩn. Dọn dẹp phải luôn thực hiện được.
     */
    @Modifying
    @Query("update SanPhamChiTiet s set s.trangThaiXoa = true, s.ngayCapNhat = CURRENT_TIMESTAMP where s.id = :id")
    int softDeleteById(@Param("id") int id);

    /** Atomically return {@code n} units to stock (invoice line removed / cancelled). */
    @Modifying
    @Query("update SanPhamChiTiet s set s.soLuongTon = s.soLuongTon + :n, s.ngayCapNhat = CURRENT_TIMESTAMP " +
           "where s.id = :id")
    int incrementStock(@Param("id") int id, @Param("n") int n);
}
