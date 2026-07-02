package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.HoaDon;
import com.vn.test.bshoes.entity.ThongKeDoanhThu;
import com.vn.test.bshoes.entity.ThongKeSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

// NOTE: reporting repository. Native aggregation queries return DTO types (ThongKeDoanhThu/ThongKeSanPham); these require a @SqlResultSetMapping or projection to map at runtime — deferred until entities/ORM are finalized (accepted: won't run yet).
public interface ThongKeRepository extends JpaRepository<HoaDon, Integer> {

    @Query(value = "SELECT DAY(hd.ngay_tao_ma) AS thang, COUNT(DISTINCT hd.id_hoa_don) AS soDon, COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong, COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy, SUM(hd.tong_tien_phai_tra) AS tongDoanhThu FROM hoa_don hd JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don WHERE CAST(hd.ngay_tao_ma AS DATE) = CAST(GETDATE() AS DATE) AND loai_hoa_don = 1 GROUP BY DAY(hd.ngay_tao_ma)", nativeQuery = true)
    List<ThongKeDoanhThu> homNay();

    @Query(value = "SELECT COUNT(DISTINCT hd.id_hoa_don) AS soDon, COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong, COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy, SUM(hd.tong_tien_phai_tra) AS tongDoanhThu FROM hoa_don hd JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don WHERE CAST(hd.ngay_tao_ma AS DATE) BETWEEN ?1 AND ?2 AND loai_hoa_don = 1", nativeQuery = true)
    List<ThongKeDoanhThu> theoNgay(Date from, Date to);

    @Query(value = "SELECT MONTH(hd.ngay_tao_ma) AS thang, SUM(hdct.so_luong) AS soSanPhamBan, SUM(hd.tong_tien_ban_dau) AS tongGiaBan, SUM(hd.tien_giam_gia) AS tongGiamGia, SUM(hd.tong_tien_phai_tra) AS tongDoanhThu, COUNT(DISTINCT hd.id_hoa_don) AS soDon, COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong, COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy FROM hoa_don_chi_tiet hdct JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don WHERE MONTH(hd.ngay_tao_ma) = ?1 AND YEAR(hd.ngay_tao_ma) = ?2 AND loai_hoa_don = 1 GROUP BY MONTH(hd.ngay_tao_ma) ORDER BY MONTH(hd.ngay_tao_ma)", nativeQuery = true)
    List<ThongKeDoanhThu> theoThang(int thang, int nam);

    @Query(value = "SELECT MONTH(hd.ngay_tao_ma) AS thang, SUM(hdct.so_luong) AS soSanPhamBan, SUM(hd.tong_tien_ban_dau) AS tongGiaBan, SUM(hd.tien_giam_gia) AS tongGiamGia, SUM(hd.tong_tien_phai_tra) AS tongDoanhThu FROM hoa_don_chi_tiet hdct JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don WHERE YEAR(hd.ngay_tao_ma) = ?1 AND loai_hoa_don = 1 GROUP BY MONTH(hd.ngay_tao_ma) ORDER BY MONTH(hd.ngay_tao_ma)", nativeQuery = true)
    List<ThongKeDoanhThu> theoNam(int nam);

    @Query(value = "SELECT sp.ma_san_pham as maSP, lsp.ten_loai_san_pham as loaiSP, sp.ten_san_pham as tenSP, cl.ten_chat_lieu as chatLieu, ms.ten_mau_sac as mauSac, kc.ten_kich_co as kichThuoc, spct.so_luong_ton as soLuongTon FROM san_pham_chi_tiet spct JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham JOIN loai_san_pham lsp ON lsp.id_loai_san_pham = sp.id_loai_san_pham JOIN chat_lieu cl ON cl.id_chat_lieu = sp.id_chat_lieu JOIN mau_sac ms ON ms.id_mau_sac = spct.id_mau_sac JOIN kich_co kc ON kc.id_kich_co = spct.id_kich_co ORDER BY spct.so_luong_ton ASC", nativeQuery = true)
    List<ThongKeSanPham> tatCaSanPham();

    @Query(value = "SELECT DISTINCT YEAR(ngay_tao_ma) AS nam FROM hoa_don ORDER BY nam DESC", nativeQuery = true)
    List<Integer> loatNam();
}
