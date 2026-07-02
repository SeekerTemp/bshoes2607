package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// NOTE: legacy DiaChi DAO was unimplemented; basic CRUD provided.
public interface DiaChiRepository extends JpaRepository<DiaChi, Integer> {

    @Query(value = "SELECT * FROM dia_chi WHERE id_khach_hang = ?1", nativeQuery = true)
    java.util.List<DiaChi> findByKhachHang(int idKhachHang);
}
