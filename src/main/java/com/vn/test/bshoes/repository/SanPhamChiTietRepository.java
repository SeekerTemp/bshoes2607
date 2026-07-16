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

    SanPhamChiTiet findByMaSanPhamChiTiet(String ma);

    /**
     * Atomically decrement stock. Returns rows affected: 1 = ok, 0 = not enough stock
     * (guarded by {@code so_luong_ton >= :n}). Prefer this over read-modify-write.
     */
    @Modifying
    @Query("update SanPhamChiTiet s set s.soLuongTon = s.soLuongTon - :n, s.ngayCapNhat = CURRENT_TIMESTAMP " +
           "where s.id = :id and s.soLuongTon >= :n")
    int decrementStock(@Param("id") int id, @Param("n") int n);

    /** Atomically return {@code n} units to stock (invoice line removed / cancelled). */
    @Modifying
    @Query("update SanPhamChiTiet s set s.soLuongTon = s.soLuongTon + :n, s.ngayCapNhat = CURRENT_TIMESTAMP " +
           "where s.id = :id")
    int incrementStock(@Param("id") int id, @Param("n") int n);
}
