package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    @Query(value = "select id_phieu_giam_gia,ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai from phieu_giam_gia where trang_thai_xoa = 0", nativeQuery = true)
    List<PhieuGiamGia> findAllActive();

    @Query(value = "select id_phieu_giam_gia,ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai from phieu_giam_gia where id_phieu_giam_gia = ?1 and trang_thai_xoa = 0", nativeQuery = true)
    PhieuGiamGia findByIdActive(int id);

    @Query(value = "select id_phieu_giam_gia,ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai from phieu_giam_gia where trang_thai_xoa = 0 and ten_phieu_giam like ?1", nativeQuery = true)
    List<PhieuGiamGia> searchByName(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE phieu_giam_gia set ten_phieu_giam=?1,loai_giam_gia=?2,gia_tri_giam=?3,don_toi_thieu=?4,giam_toi_da=?5,so_luong=?6,thoi_gian_bat_dau=?7,thoi_gian_ket_thuc=?8,trang_thai=?9 where ma_phieu_giam=?10", nativeQuery = true)
    void updateByMa(String tenPhieuGiam, int loaiGiamGia, BigDecimal giaTriGiam, BigDecimal donToiThieu, BigDecimal giamToiDa, int soLuong, Timestamp thoiGianBatDau, Timestamp thoiGianKetThuc, boolean trangThai, String maPhieuGiam);

    @Modifying
    @Transactional
    @Query(value = "update phieu_giam_gia set trang_thai_xoa = 1 where id_phieu_giam_gia=?1", nativeQuery = true)
    void softDelete(int id);

    @Query(value = "SELECT COUNT(*) FROM phieu_giam_gia WHERE ma_phieu_giam = ?1", nativeQuery = true)
    long existsMa(String ma);

    @Query(value = "SELECT COUNT(*) FROM phieu_giam_gia WHERE ten_phieu_giam = ?1", nativeQuery = true)
    long existsTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "EXEC sp_cap_nhat_trang_thai_phieu_giam_gia", nativeQuery = true)
    void capNhatTrangThai();
}
