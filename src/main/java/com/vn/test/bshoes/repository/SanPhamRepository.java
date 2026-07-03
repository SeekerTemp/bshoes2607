package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    @Query("select s from SanPham s where s.trangThaiXoa = false or s.trangThaiXoa is null")
    List<SanPham> findActive();

    @Query("select s from SanPham s where s.trangThaiXoa = true")
    List<SanPham> findRecycle();

    SanPham findByMaSanPham(String ma);

    @Query("select s from SanPham s where (s.trangThaiXoa = false or s.trangThaiXoa is null) and (lower(s.maSanPham) like lower(concat('%',?1,'%')) or lower(s.tenSanPham) like lower(concat('%',?1,'%')))")
    List<SanPham> search(String kw);
}
