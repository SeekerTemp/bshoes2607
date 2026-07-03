package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    @Query("select s from SanPhamChiTiet s where s.idSanPham.id = ?1 and (s.trangThaiXoa = false or s.trangThaiXoa is null)")
    List<SanPhamChiTiet> findActiveByProduct(int idSanPham);

    @Query("select s from SanPhamChiTiet s where s.trangThaiXoa = false or s.trangThaiXoa is null")
    List<SanPhamChiTiet> findActive();

    @Query("select s from SanPhamChiTiet s where s.trangThai = true and s.soLuongTon > 0")
    List<SanPhamChiTiet> findAvailable();
}
