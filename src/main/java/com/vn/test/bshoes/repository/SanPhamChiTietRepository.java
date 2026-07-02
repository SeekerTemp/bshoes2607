package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    @Query(value = "SELECT * FROM san_pham_chi_tiet WHERE trang_thai = 1 AND so_luong_ton > 0", nativeQuery = true)
    List<SanPhamChiTiet> findAllAvailable();

    @Query(value = "SELECT * FROM san_pham_chi_tiet WHERE ma_san_pham_chi_tiet = ?1", nativeQuery = true)
    SanPhamChiTiet findByMa(String ma);

    // NOTE(legacy-bug): original used undeclared alias spct and unparenthesized OR
    @Query(value = "SELECT spct.* FROM san_pham_chi_tiet spct INNER JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE spct.trang_thai = 1 AND spct.so_luong_ton > 0 AND (spct.ma_san_pham_chi_tiet like ?1 OR sp.ten_san_pham like ?2)", nativeQuery = true)
    List<SanPhamChiTiet> findAvailableByName(String maLike, String tenLike);

    @Modifying
    @Transactional
    @Query(value = "update san_pham_chi_tiet set so_luong_ton = ?1 where id_san_pham_chi_tiet = ?2 and trang_thai = 1", nativeQuery = true)
    void updateStock(int soLuongTon, int idSanPhamChiTiet);

    @Modifying
    @Transactional
    @Query(value = "update san_pham_chi_tiet set trang_thai = 0 where so_luong_ton = 0 and id_san_pham_chi_tiet = ?1", nativeQuery = true)
    void deactivateWhenEmpty(int idSanPhamChiTiet);

    // NOTE(legacy-bug): san_pham_chi_tiet has no id_hoa_don column; omitted.
}
