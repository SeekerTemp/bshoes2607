package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    @Query("select p from PhieuGiamGia p where p.trangThaiXoa = false or p.trangThaiXoa is null")
    List<PhieuGiamGia> findActive();

    @Query("select p from PhieuGiamGia p where (p.trangThaiXoa = false or p.trangThaiXoa is null) " +
            "and (lower(p.maPhieuGiam) like lower(concat('%',?1,'%')) or lower(p.tenPhieuGiam) like lower(concat('%',?1,'%')))")
    List<PhieuGiamGia> search(String kw);

    boolean existsByMaPhieuGiam(String ma);

    /**
     * Consumes one use of a voucher. Conditional on so_luong > 0 so two tills
     * checking out at the same instant cannot both take the last one; returns 0
     * when there was nothing left to take.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PhieuGiamGia p set p.soLuong = p.soLuong - 1 where p.id = ?1 and p.soLuong > 0")
    int consumeOne(Integer id);
}
