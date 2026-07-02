package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.KieuCoGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface KieuCoGiayRepository extends JpaRepository<KieuCoGiay, Integer> {

    @Query(value = "select * from kieu_co_giay where ten_co_giay = ?1", nativeQuery = true)
    KieuCoGiay findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE kieu_co_giay SET ma_co_giay = ?1 WHERE id_kieu_co_giay = ?2", nativeQuery = true)
    void updateMa(String ma, int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE kieu_co_giay SET ten_co_giay = ?1 WHERE ma_co_giay = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
