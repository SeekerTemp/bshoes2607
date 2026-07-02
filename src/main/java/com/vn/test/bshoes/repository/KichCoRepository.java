package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.KichCo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface KichCoRepository extends JpaRepository<KichCo, Integer> {

    @Query(value = "select * from kich_co where ten_kich_co = ?1", nativeQuery = true)
    KichCo findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE kich_co SET ten_kich_co = ?1 WHERE ma_kich_co = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
