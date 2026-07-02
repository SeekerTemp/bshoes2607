package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham, Integer> {

    @Query(value = "select * from loai_san_pham where ten_loai_san_pham = ?1", nativeQuery = true)
    LoaiSanPham findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE loai_san_pham SET ma_loai_san_pham = ?1 WHERE id_loai_san_pham = ?2", nativeQuery = true)
    void updateMa(String ma, int id);

    @Modifying
    @Transactional
    @Query(value = "update loai_san_pham set ten_loai_san_pham=?1, mo_ta=?2 where ma_loai_san_pham = ?3", nativeQuery = true)
    void updateByMa(String ten, String moTa, String ma);
}
