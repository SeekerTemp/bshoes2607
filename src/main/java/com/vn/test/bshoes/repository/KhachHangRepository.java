package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    @Query("select k from KhachHang k where k.trangThaiXoa = false or k.trangThaiXoa is null")
    List<KhachHang> findActive();

    @Query("select k from KhachHang k where (k.trangThaiXoa = false or k.trangThaiXoa is null) and " +
            "(lower(k.maKhachHang) like lower(concat('%',?1,'%')) or lower(k.tenKhachHang) like lower(concat('%',?1,'%')) or k.soDienThoai like concat('%',?1,'%'))")
    List<KhachHang> search(String kw);

    boolean existsByMaKhachHang(String ma);

    boolean existsByEmail(String email);

    boolean existsBySoDienThoai(String sdt);
}
