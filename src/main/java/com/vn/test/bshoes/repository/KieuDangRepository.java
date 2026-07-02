package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.KieuDang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface KieuDangRepository extends JpaRepository<KieuDang, Integer> {

    @Query(value = "select * from kieu_dang where ten_kieu_dang = ?1", nativeQuery = true)
    KieuDang findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE kieu_dang SET ma_kieu_dang = ?1 WHERE id_kieu_dang = ?2", nativeQuery = true)
    void updateMa(String ma, int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE kieu_dang SET ten_kieu_dang = ?1 WHERE ma_kieu_dang = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
