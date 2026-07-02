package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    @Query(value = "select * from mau_sac where ten_mau_sac = ?1", nativeQuery = true)
    MauSac findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE mau_sac SET ten_mau_sac = ?1 WHERE ma_mau_sac = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
