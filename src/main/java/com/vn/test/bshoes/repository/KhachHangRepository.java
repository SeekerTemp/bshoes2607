package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    @Query(value = "select id_khach_hang,ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai,nguoi_tao_ma,nguoi_cap_nhat from khach_hang where trang_thai_xoa = 0", nativeQuery = true)
    java.util.List<KhachHang> findAllActive();

    @Query(value = "select id_khach_hang,ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai,nguoi_tao_ma,nguoi_cap_nhat from khach_hang where id_khach_hang = ?1 and trang_thai_xoa = 0", nativeQuery = true)
    KhachHang findByIdActive(int id);

    @Query(value = "select id_khach_hang,ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai,nguoi_tao_ma,nguoi_cap_nhat from khach_hang where ten_khach_hang like ?1 and trang_thai_xoa = 0", nativeQuery = true)
    java.util.List<KhachHang> searchByName(String ten);

    @Query(value = "SELECT * FROM khach_hang WHERE so_dien_thoai = ?1", nativeQuery = true)
    KhachHang findBySdt(String sdt);

    @Modifying
    @Transactional
    @Query(value = "UPDATE khach_hang set ten_khach_hang=?1,gioi_tinh=?2,so_dien_thoai=?3,dia_chi=?4,email=?5,trang_thai=?6 where ma_khach_hang=?7", nativeQuery = true)
    void updateByMa(String ten, String gioiTinh, String sdt, String diaChi, String email, boolean trangThai, String ma);

    @Modifying
    @Transactional
    @Query(value = "update khach_hang set trang_thai_xoa = 1 where id_khach_hang = ?1", nativeQuery = true)
    void softDelete(int id);

    @Query(value = "SELECT COUNT(*) FROM khach_hang WHERE ma_khach_hang = ?1 AND trang_thai_xoa = 0", nativeQuery = true)
    long existsMa(String ma);

    @Query(value = "SELECT COUNT(*) FROM khach_hang WHERE email = ?1 AND trang_thai_xoa = 0", nativeQuery = true)
    long existsEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM khach_hang WHERE so_dien_thoai = ?1 AND trang_thai_xoa = 0", nativeQuery = true)
    long existsSdt(String sdt);

    @Query(value = "SELECT COUNT(*) FROM khach_hang WHERE ma_khach_hang=?1 AND id_khach_hang <> ?2 AND trang_thai_xoa = 0", nativeQuery = true)
    long existsMaExcludingId(String ma, int id);
}
