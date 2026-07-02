package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.HoaDon;
import com.vn.test.bshoes.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    // NOTE(legacy): create() referenced an unfinished placeholder proc.
    @Modifying
    @Transactional
    @Query(value = "EXEC sp_insert_hoa_don_placeholder ?1,?2,?3", nativeQuery = true)
    void create(int trangThai, int loaiHoaDon, int idNhanVien);

    @Modifying
    @Transactional
    @Query(value = "UPDATE hoa_don SET tong_tien_ban_dau = ?1, tien_giam_gia = ?2, tong_tien_phai_tra = ?3, trang_thai = ?4, loai_hoa_don = ?5, nguoi_cap_nhat = ?6, ngay_cap_nhat = GETDATE() WHERE id_hoa_don = ?7", nativeQuery = true)
    void update(BigDecimal tongTienBanDau, BigDecimal tienGiamGia, BigDecimal tongTienPhaiTra, int trangThai, boolean loaiHoaDon, String nguoiCapNhat, int idHoaDon);

    @Query(value = "select id_hoa_don, ma_hoa_don, id_khach_hang, id_nhan_vien, tong_tien_ban_dau, tien_giam_gia, tong_tien_phai_tra, trang_thai, loai_hoa_don, phuong_thuc_thanh_toan, ngay_tao_ma, ngay_cap_nhat, nguoi_tao_ma, nguoi_cap_nhat from hoa_don where trang_thai = 0 order by id_hoa_don desc", nativeQuery = true)
    List<HoaDon> findAllCart();

    @Query(value = "select * from hoa_don where ma_hoa_don = ?1", nativeQuery = true)
    HoaDon findByMa(String maHoaDon);

    @Modifying
    @Transactional
    @Query(value = "UPDATE hoa_don SET trang_thai = 1, loai_hoa_don = 1, ngay_cap_nhat = GETDATE(), nguoi_cap_nhat = ?2 WHERE id_hoa_don = ?1", nativeQuery = true)
    void markPaid(int idHoaDon, String nguoiCapNhat);

    @Modifying
    @Transactional
    @Query(value = "UPDATE hoa_don SET id_khach_hang = ?2 WHERE id_hoa_don = ?1", nativeQuery = true)
    void updateKhachHang(int idHoaDon, int idKhachHang);

    @Query(value = "SELECT dbo.tinh_tien_giam_gia(?1, ?2)", nativeQuery = true)
    BigDecimal tinhGiamGia(int idPhieu, BigDecimal tongTien);

    @Query(value = "SELECT * FROM view_phieu_giam_gia_hoat_dong", nativeQuery = true)
    List<PhieuGiamGia> getPhieuGiamGiaHoatDong();
}
