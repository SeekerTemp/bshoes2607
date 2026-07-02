package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.XuatXu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface XuatXuRepository extends JpaRepository<XuatXu, Integer> {

    @Query(value = "select * from xuat_su where ten_xuat_su = ?1", nativeQuery = true)
    XuatXu findByTen(String ten);

    // NOTE(legacy-bug): original DaoImpl_XuatXu used wrong names ma_xuat_xu/id_xuat_xu
    @Modifying
    @Transactional
    @Query(value = "UPDATE xuat_su SET ma_xuat_su = ?1 WHERE id_xuat_su = ?2", nativeQuery = true)
    void updateMa(String ma, int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE xuat_su SET ten_xuat_su = ?1 WHERE ma_xuat_su = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
