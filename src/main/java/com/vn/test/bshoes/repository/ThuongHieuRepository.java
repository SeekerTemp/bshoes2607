package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    @Query(value = "select * from thuong_hieu where ten_thuong_hieu = ?1", nativeQuery = true)
    ThuongHieu findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE thuong_hieu SET ma_thuong_hieu = ?1 WHERE id_thuong_hieu = ?2", nativeQuery = true)
    void updateMa(String ma, int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE thuong_hieu SET ten_thuong_hieu = ?1 WHERE ma_thuong_hieu = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
