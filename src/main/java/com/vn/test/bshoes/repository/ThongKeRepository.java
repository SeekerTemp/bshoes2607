package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ThongKeRepository extends JpaRepository<HoaDon, Integer> {

    @Query(value = "SELECT DAY(hd.ngay_tao_ma) AS thang, COUNT(DISTINCT hd.id_hoa_don) AS soDon, COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong, COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy, SUM(hd.tong_tien_phai_tra) AS tongDoanhThu FROM hoa_don hd JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don WHERE CAST(hd.ngay_tao_ma AS DATE) = CAST(GETDATE() AS DATE) AND loai_hoa_don = 1 GROUP BY DAY(hd.ngay_tao_ma)", nativeQuery = true)
    List<Object[]> homNay();

    @Query(value = "SELECT COUNT(DISTINCT hd.id_hoa_don) AS soDon, COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong, COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy, SUM(hd.tong_tien_phai_tra) AS tongDoanhThu FROM hoa_don hd JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don WHERE CAST(hd.ngay_tao_ma AS DATE) BETWEEN ?1 AND ?2 AND loai_hoa_don = 1", nativeQuery = true)
    List<Object[]> theoNgay(Date from, Date to);

    @Query(value = "SELECT MONTH(hd.ngay_tao_ma) AS thang, SUM(hdct.so_luong) AS soSanPhamBan, SUM(hd.tong_tien_ban_dau) AS tongGiaBan, SUM(hd.tien_giam_gia) AS tongGiamGia, SUM(hd.tong_tien_phai_tra) AS tongDoanhThu, COUNT(DISTINCT hd.id_hoa_don) AS soDon, COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong, COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy FROM hoa_don_chi_tiet hdct JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don WHERE MONTH(hd.ngay_tao_ma) = ?1 AND YEAR(hd.ngay_tao_ma) = ?2 AND loai_hoa_don = 1 GROUP BY MONTH(hd.ngay_tao_ma) ORDER BY MONTH(hd.ngay_tao_ma)", nativeQuery = true)
    List<Object[]> theoThang(int thang, int nam);

    @Query(value = "SELECT MONTH(hd.ngay_tao_ma) AS thang, SUM(hdct.so_luong) AS soSanPhamBan, SUM(hd.tong_tien_ban_dau) AS tongGiaBan, SUM(hd.tien_giam_gia) AS tongGiamGia, SUM(hd.tong_tien_phai_tra) AS tongDoanhThu FROM hoa_don_chi_tiet hdct JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don WHERE YEAR(hd.ngay_tao_ma) = ?1 AND loai_hoa_don = 1 GROUP BY MONTH(hd.ngay_tao_ma) ORDER BY MONTH(hd.ngay_tao_ma)", nativeQuery = true)
    List<Object[]> theoNam(int nam);

    @Query(value = "SELECT sp.ma_san_pham as maSP, lsp.ten_loai_san_pham as loaiSP, sp.ten_san_pham as tenSP, cl.ten_chat_lieu as chatLieu, ms.ten_mau_sac as mauSac, kc.ten_kich_co as kichThuoc, spct.so_luong_ton as soLuongTon, "
            + "ISNULL(sales.soLuongBan, 0) as soLuongBan, ISNULL(sales.doanhThu, 0) as doanhThu "
            + "FROM san_pham_chi_tiet spct "
            + "JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham "
            + "JOIN loai_san_pham lsp ON lsp.id_loai_san_pham = sp.id_loai_san_pham "
            + "JOIN chat_lieu cl ON cl.id_chat_lieu = sp.id_chat_lieu "
            + "JOIN mau_sac ms ON ms.id_mau_sac = spct.id_mau_sac "
            + "JOIN kich_co kc ON kc.id_kich_co = spct.id_kich_co "
            + "LEFT JOIN (SELECT hdct.id_san_pham_chi_tiet AS spctId, SUM(hdct.so_luong) AS soLuongBan, SUM(hdct.thanh_tien) AS doanhThu "
            + "           FROM hoa_don_chi_tiet hdct JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don "
            + "           WHERE hd.loai_hoa_don = 1 GROUP BY hdct.id_san_pham_chi_tiet) sales "
            + "  ON sales.spctId = spct.id_san_pham_chi_tiet "
            + "ORDER BY soLuongBan DESC, spct.so_luong_ton ASC", nativeQuery = true)
    List<Object[]> tatCaSanPham();

    @Query(value = "SELECT DISTINCT YEAR(ngay_tao_ma) AS nam FROM hoa_don ORDER BY nam DESC", nativeQuery = true)
    List<Integer> loatNam();

    /** Single-row dashboard summary across all invoices (+ current-month revenue). */
    @Query(value = "SELECT "
            + "ISNULL(SUM(CASE WHEN hd.trang_thai = 1 THEN hd.tong_tien_phai_tra ELSE 0 END), 0) AS doanhThu, "
            + "ISNULL(SUM(CASE WHEN hd.trang_thai = 1 AND MONTH(hd.ngay_tao_ma) = MONTH(GETDATE()) AND YEAR(hd.ngay_tao_ma) = YEAR(GETDATE()) THEN hd.tong_tien_phai_tra ELSE 0 END), 0) AS doanhThuThang, "
            + "COUNT(*) AS soDon, "
            + "SUM(CASE WHEN hd.trang_thai = 1 THEN 1 ELSE 0 END) AS donThanhCong, "
            + "SUM(CASE WHEN hd.trang_thai = 0 THEN 1 ELSE 0 END) AS donCho, "
            + "SUM(CASE WHEN hd.trang_thai = 2 THEN 1 ELSE 0 END) AS donHuy "
            + "FROM hoa_don hd", nativeQuery = true)
    List<Object[]> tongQuanRaw();

    /** Total units sold across paid invoices. */
    @Query(value = "SELECT ISNULL(SUM(hdct.so_luong), 0) FROM hoa_don_chi_tiet hdct "
            + "JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don WHERE hd.loai_hoa_don = 1", nativeQuery = true)
    Long soSanPhamBan();

    /** Total cost of goods sold (giá vốn) across paid invoices — needs san_pham_chi_tiet.gia_nhap. */
    @Query(value = "SELECT ISNULL(SUM(hdct.so_luong * spct.gia_nhap), 0) "
            + "FROM hoa_don_chi_tiet hdct "
            + "JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don "
            + "JOIN san_pham_chi_tiet spct ON spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet "
            + "WHERE hd.loai_hoa_don = 1", nativeQuery = true)
    java.math.BigDecimal giaVonTong();

    /** Monthly cash flow for a year: revenue, cost of goods (giá vốn), profit. */
    @Query(value = "SELECT m.thang, m.doanhThu, ISNULL(c.giaVon,0) AS giaVon, (m.doanhThu - ISNULL(c.giaVon,0)) AS loiNhuan FROM "
            + "(SELECT MONTH(ngay_tao_ma) AS thang, SUM(tong_tien_phai_tra) AS doanhThu FROM hoa_don "
            + "  WHERE YEAR(ngay_tao_ma) = ?1 AND loai_hoa_don = 1 GROUP BY MONTH(ngay_tao_ma)) m "
            + "LEFT JOIN (SELECT MONTH(hd.ngay_tao_ma) AS thang, SUM(hdct.so_luong * spct.gia_nhap) AS giaVon "
            + "  FROM hoa_don hd JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don "
            + "  JOIN san_pham_chi_tiet spct ON spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet "
            + "  WHERE YEAR(hd.ngay_tao_ma) = ?1 AND hd.loai_hoa_don = 1 GROUP BY MONTH(hd.ngay_tao_ma)) c ON c.thang = m.thang "
            + "ORDER BY m.thang", nativeQuery = true)
    List<Object[]> dongTien(int nam);

    /** ROI per product: revenue, cost, ordered by ROI% desc. */
    @Query(value = "SELECT TOP 8 sp.ten_san_pham AS ten, SUM(hdct.thanh_tien) AS doanhThu, "
            + "SUM(hdct.so_luong * spct.gia_nhap) AS giaVon "
            + "FROM hoa_don_chi_tiet hdct "
            + "JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don "
            + "JOIN san_pham_chi_tiet spct ON spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet "
            + "JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham "
            + "WHERE hd.loai_hoa_don = 1 "
            + "GROUP BY sp.ten_san_pham "
            + "HAVING SUM(hdct.so_luong * spct.gia_nhap) > 0 "
            + "ORDER BY (SUM(hdct.thanh_tien) - SUM(hdct.so_luong * spct.gia_nhap)) / SUM(hdct.so_luong * spct.gia_nhap) DESC",
            nativeQuery = true)
    List<Object[]> roiSanPham();

    /** Customer retention: returning (>1 paid invoice) vs new (exactly 1). */
    @Query(value = "SELECT "
            + "ISNULL(SUM(CASE WHEN t.cnt > 1 THEN 1 ELSE 0 END), 0) AS quayLai, "
            + "ISNULL(SUM(CASE WHEN t.cnt = 1 THEN 1 ELSE 0 END), 0) AS moi "
            + "FROM (SELECT id_khach_hang, COUNT(*) AS cnt FROM hoa_don "
            + "      WHERE loai_hoa_don = 1 AND id_khach_hang IS NOT NULL GROUP BY id_khach_hang) t",
            nativeQuery = true)
    List<Object[]> retention();

    /** Trending products: units sold in @nam vs @nam-1, ordered by growth %. */
    @Query(value = "SELECT TOP 6 t.ten, t.latest AS soLuong, "
            + "CASE WHEN t.prior = 0 THEN 100 ELSE (t.latest - t.prior) * 100 / t.prior END AS growth "
            + "FROM (SELECT sp.ten_san_pham AS ten, "
            + "        SUM(CASE WHEN YEAR(hd.ngay_tao_ma) = ?1 THEN hdct.so_luong ELSE 0 END) AS latest, "
            + "        SUM(CASE WHEN YEAR(hd.ngay_tao_ma) = ?1 - 1 THEN hdct.so_luong ELSE 0 END) AS prior "
            + "      FROM hoa_don_chi_tiet hdct "
            + "      JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don "
            + "      JOIN san_pham_chi_tiet spct ON spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet "
            + "      JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham "
            + "      WHERE hd.loai_hoa_don = 1 AND YEAR(hd.ngay_tao_ma) IN (?1, ?1 - 1) "
            + "      GROUP BY sp.ten_san_pham) t "
            + "WHERE t.latest > 0 ORDER BY growth DESC",
            nativeQuery = true)
    List<Object[]> trending(int nam);
}
