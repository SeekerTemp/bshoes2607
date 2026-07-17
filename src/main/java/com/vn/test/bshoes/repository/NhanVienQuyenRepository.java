package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.NhanVienQuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhanVienQuyenRepository extends JpaRepository<NhanVienQuyen, NhanVienQuyen.Key> {

    @Query("select q.manHinh from NhanVienQuyen q where q.idNhanVien = ?1")
    List<String> findManHinhByNhanVien(int idNhanVien);

    @Modifying
    @Query("delete from NhanVienQuyen q where q.idNhanVien = ?1")
    void deleteByNhanVien(int idNhanVien);
}
