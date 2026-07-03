package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    @Query("select h from HoaDonChiTiet h where h.idHoaDon.id = ?1")
    List<HoaDonChiTiet> findByHoaDon(int idHoaDon);
}
