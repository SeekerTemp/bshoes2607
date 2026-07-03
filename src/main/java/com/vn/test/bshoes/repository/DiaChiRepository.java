package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// NOTE: legacy DiaChi DAO was unimplemented; basic CRUD provided.
@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Integer> {

    @Query("select d from DiaChi d where d.idKhachHang.id = ?1")
    List<DiaChi> findByKhachHang(int idKhachHang);
}
