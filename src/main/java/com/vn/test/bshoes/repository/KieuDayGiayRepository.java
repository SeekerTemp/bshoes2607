package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.KieuDayGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface KieuDayGiayRepository extends JpaRepository<KieuDayGiay, Integer> {

    @Query(value = "select * from kieu_day_giay where ten_day_giay = ?1", nativeQuery = true)
    KieuDayGiay findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE kieu_day_giay SET ma_day_giay = ?1 WHERE id_kieu_day_giay = ?2", nativeQuery = true)
    void updateMa(String ma, int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE kieu_day_giay SET ten_day_giay = ?1 WHERE ma_day_giay = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
