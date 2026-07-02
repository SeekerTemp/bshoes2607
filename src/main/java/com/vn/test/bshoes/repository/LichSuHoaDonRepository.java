package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {

    @Query(value = "select * from lich_su_hoa_don where trang_thai = 1", nativeQuery = true)
    List<LichSuHoaDon> findAllActive();

    @Query(value = "select * from lich_su_hoa_don where trang_thai = 3", nativeQuery = true)
    List<LichSuHoaDon> findAllCancel();

    // NOTE(legacy-bug): original SQL was truncated and misspelled ngay_cap_nhap
    @Query(value = "select * from lich_su_hoa_don where ngay_tao_ma LIKE ?1 OR ngay_cap_nhat LIKE ?2", nativeQuery = true)
    List<LichSuHoaDon> findByDate(String tuNgay, String denNgay);
}
