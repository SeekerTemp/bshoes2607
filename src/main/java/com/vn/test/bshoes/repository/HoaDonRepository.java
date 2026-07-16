package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    List<HoaDon> findByTrangThai(Integer trangThai);

    long countByTrangThai(Integer trangThai);

    /**
     * Khách tra cứu đơn của mình bằng SĐT (chưa có đăng nhập cho khách).
     * Bỏ hoá đơn nháp tại quầy (trang_thai = 0) — đó không phải đơn của khách.
     */
    @Query("select h from HoaDon h where h.soDienThoai = ?1 and h.trangThai <> 0 order by h.id desc")
    List<HoaDon> findBySoDienThoai(String soDienThoai);

    HoaDon findByMaHoaDon(String ma);

    @Query(value = "SELECT * FROM view_phieu_giam_gia_hoat_dong", nativeQuery = true)
    List<com.vn.test.bshoes.entity.PhieuGiamGia> getVouchersActive();

    @Query(value = "SELECT dbo.tinh_tien_giam_gia(?1, ?2)", nativeQuery = true)
    java.math.BigDecimal tinhGiamGia(int idPhieu, BigDecimal tongTien);
}
