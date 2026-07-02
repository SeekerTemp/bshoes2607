package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    @Query(value = "SELECT id_nhan_vien,ma_nhan_vien, ten_nhan_vien, cccd, email, so_dien_thoai, gioi_tinh, dia_chi,ngay_sinh FROM nhan_vien where trang_thai_xoa = 0", nativeQuery = true)
    List<NhanVien> findAllActive();

    @Query(value = "SELECT id_nhan_vien,ma_nhan_vien, ten_nhan_vien, cccd, email, so_dien_thoai, gioi_tinh, dia_chi,ngay_sinh,tai_khoan, mat_khau FROM nhan_vien WHERE id_nhan_vien = ?1 and trang_thai_xoa = 0", nativeQuery = true)
    NhanVien findByIdActive(int id);

    // NOTE(legacy-bug): collapses the legacy Java if/else branch for gender filter into a single conditional expression.
    @Query(value = "SELECT id_nhan_vien,ma_nhan_vien,ten_nhan_vien,cccd,email,so_dien_thoai,gioi_tinh,dia_chi,ngay_sinh FROM nhan_vien WHERE trang_thai_xoa = 0 AND ten_nhan_vien LIKE ?1 AND (?2 = 'all' OR gioi_tinh = ?2)", nativeQuery = true)
    List<NhanVien> searchByName(String ten, String gioiTinh);

    @Modifying
    @Transactional
    @Query(value = "UPDATE nhan_vien SET ten_nhan_vien=?1, cccd=?2, email=?3, so_dien_thoai=?4, gioi_tinh=?5, dia_chi=?6, ngay_sinh=?7, tai_khoan=?8, mat_khau=?9 WHERE ma_nhan_vien = ?10", nativeQuery = true)
    void updateByMa(String ten, String cccd, String email, String sdt, String gioiTinh, String diaChi, Timestamp ngaySinh, String taiKhoan, String matKhau, String ma);

    @Modifying
    @Transactional
    @Query(value = "update nhan_vien set trang_thai_xoa = 1 where id_nhan_vien = ?1", nativeQuery = true)
    void softDelete(int id);

    @Query(value = "SELECT COUNT(*) FROM nhan_vien WHERE ma_nhan_vien = ?1", nativeQuery = true)
    long existsMa(String ma);

    @Query(value = "SELECT COUNT(*) FROM nhan_vien WHERE tai_khoan = ?1", nativeQuery = true)
    long existsTaiKhoan(String taiKhoan);

    @Query(value = "SELECT COUNT(*) FROM nhan_vien WHERE cccd = ?1", nativeQuery = true)
    long existsCCCD(String cccd);

    @Query(value = "SELECT COUNT(*) FROM nhan_vien WHERE email = ?1", nativeQuery = true)
    long existsEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM nhan_vien WHERE so_dien_thoai = ?1", nativeQuery = true)
    long existsSdt(String sdt);

    @Query(value = "SELECT nv.id_nhan_vien, nv.ma_nhan_vien, nv.ten_nhan_vien, vt.ma_vai_tro, vt.ten_vai_tro FROM nhan_vien nv JOIN vai_tro vt ON nv.id_vai_tro = vt.id_vai_tro WHERE nv.tai_khoan = ?1 AND nv.mat_khau = ?2", nativeQuery = true)
    NhanVien login(String taiKhoan, String matKhau);
}
