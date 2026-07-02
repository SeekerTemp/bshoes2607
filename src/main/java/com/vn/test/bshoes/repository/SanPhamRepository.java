package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    @Query(value = "select id_san_pham,ma_san_pham,ten_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su from san_pham where ma_san_pham like ?1", nativeQuery = true)
    java.util.List<SanPham> findByMa(String ma);
}
