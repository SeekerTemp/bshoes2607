package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO hoa_don_chi_tiet (id_san_pham_chi_tiet, id_hoa_don, so_luong, nguoi_tao, nguoi_cap_nhat, trang_thai, thanh_tien, gia_giam) VALUES (?1, ?2, ?3, ?4, ?5, 0, ?6, 0)", nativeQuery = true)
    void create(int idSanPhamChiTiet, int idHoaDon, int soLuong, String nguoiTao, String nguoiCapNhat, BigDecimal thanhTien);

    @Modifying
    @Transactional
    @Query(value = "EXEC tao_hoa_don_va_chi_tiet ?1,?2,?3,?4", nativeQuery = true)
    void createViaProc(int idSanPhamChiTiet, int soLuong, BigDecimal giaGiam, String nguoiTao);

    @Modifying
    @Transactional
    @Query(value = "update hoa_don_chi_tiet set so_luong=?1,thanh_tien=?2 where id_hoa_don_chi_tiet=?3 and trang_thai=0", nativeQuery = true)
    void update(int soLuong, BigDecimal thanhTien, int idHoaDonChiTiet);

    @Modifying
    @Transactional
    @Query(value = "update hoa_don_chi_tiet set so_luong=?1 where id_hoa_don_chi_tiet=?2", nativeQuery = true)
    void updateStock(int soLuong, int idHoaDonChiTiet);

    @Query(value = "select * from hoa_don_chi_tiet where id_hoa_don = ?1", nativeQuery = true)
    List<HoaDonChiTiet> findByHoaDon(int idHoaDon);

    @Query(value = "select * from hoa_don_chi_tiet where id_hoa_don=?1 and id_san_pham_chi_tiet = ?2", nativeQuery = true)
    HoaDonChiTiet findByHoaDonAndSpct(int idHoaDon, int idSanPhamChiTiet);

    @Query(value = "SELECT hdct.id_hoa_don_chi_tiet, hdct.id_hoa_don, hdct.id_san_pham_chi_tiet, sp.ten_san_pham AS ten_san_pham, spct.don_gia AS don_gia, hdct.so_luong, hdct.thanh_tien FROM hoa_don_chi_tiet hdct JOIN san_pham_chi_tiet spct ON spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE hdct.id_hoa_don = ?1", nativeQuery = true)
    List<HoaDonChiTiet> findDetailByHoaDon(int idHoaDon);
}
