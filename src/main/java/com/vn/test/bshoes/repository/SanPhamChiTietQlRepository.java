package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.SanPhamChiTiet_ql;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SanPhamChiTietQlRepository extends JpaRepository<SanPhamChiTiet_ql, Integer> {

    @Query(value = "SELECT spct.id_san_pham_chi_tiet, spct.id_san_pham, spct.id_kich_co, spct.id_mau_sac, spct.ma_san_pham_chi_tiet, spct.so_luong_ton, spct.don_gia, spct.ngay_tao, spct.ngay_cap_nhat, spct.nguoi_tao, spct.nguoi_cap_nhat, spct.trang_thai, sp.ten_san_pham, sp.ma_san_pham FROM san_pham_chi_tiet spct LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE spct.trang_thai_xoa = 0", nativeQuery = true)
    List<SanPhamChiTiet_ql> findAllActive();

    @Query(value = "SELECT spct.id_san_pham_chi_tiet, spct.id_san_pham, spct.id_kich_co, spct.id_mau_sac, spct.ma_san_pham_chi_tiet, spct.so_luong_ton, spct.don_gia, spct.ngay_tao, spct.ngay_cap_nhat, spct.nguoi_tao, spct.nguoi_cap_nhat, spct.trang_thai, sp.ten_san_pham, sp.ma_san_pham FROM san_pham_chi_tiet spct LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE spct.id_san_pham_chi_tiet = ?1", nativeQuery = true)
    SanPhamChiTiet_ql findByIdJoined(int id);

    @Query(value = "SELECT spct.id_san_pham_chi_tiet, spct.id_san_pham, spct.id_kich_co, spct.id_mau_sac, spct.ma_san_pham_chi_tiet, spct.so_luong_ton, spct.don_gia, spct.ngay_tao, spct.ngay_cap_nhat, spct.nguoi_tao, spct.nguoi_cap_nhat, spct.trang_thai, sp.ten_san_pham, sp.ma_san_pham FROM san_pham_chi_tiet spct LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE spct.id_san_pham = ?1", nativeQuery = true)
    List<SanPhamChiTiet_ql> findByProduct(int idSanPham);

    @Query(value = "SELECT spct.id_san_pham_chi_tiet, spct.id_san_pham, spct.id_kich_co, spct.id_mau_sac, spct.ma_san_pham_chi_tiet, spct.so_luong_ton, spct.don_gia, spct.ngay_tao, spct.ngay_cap_nhat, spct.nguoi_tao, spct.nguoi_cap_nhat, spct.trang_thai, sp.ten_san_pham, sp.ma_san_pham FROM san_pham_chi_tiet spct LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham WHERE spct.ma_san_pham_chi_tiet like ?1 or sp.ten_san_pham like ?2", nativeQuery = true)
    List<SanPhamChiTiet_ql> search(String maLike, String tenLike);

    @Query(value = "SELECT ma_san_pham FROM san_pham WHERE id_san_pham = ?1", nativeQuery = true)
    String findParentCode(int idSanPham);

    @Modifying
    @Transactional
    @Query(value = "update san_pham_chi_tiet set ma_san_pham_chi_tiet = ?1, id_mau_sac = ?2, id_kich_co = ?3, don_gia = ?4, so_luong_ton = ?5, trang_thai = ?6 where id_san_pham_chi_tiet = ?7", nativeQuery = true)
    void update(String maSanPhamChiTiet, Integer idMauSac, Integer idKichCo, BigDecimal donGia, int soLuongTon, boolean trangThai, int idSanPhamChiTiet);

    @Modifying
    @Transactional
    @Query(value = "UPDATE san_pham_chi_tiet SET trang_thai_xoa = 1 WHERE id_san_pham_chi_tiet = ?1", nativeQuery = true)
    void softDeleteById(int idSanPhamChiTiet);

    @Modifying
    @Transactional
    @Query(value = "UPDATE san_pham_chi_tiet SET trang_thai_xoa = 1 WHERE id_san_pham = ?1", nativeQuery = true)
    void softDeleteByProduct(int idSanPham);
}
