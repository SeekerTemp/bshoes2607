package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.BaoHanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaoHanhRepository extends JpaRepository<BaoHanh, Integer> {

    @Query("select b from BaoHanh b where b.trangThaiXoa = false or b.trangThaiXoa is null")
    List<BaoHanh> findActive();

    BaoHanh findByMaBaoHanh(String ma);
}
