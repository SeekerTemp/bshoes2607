/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import dao.Dao_Thongke;
import entity.Nam;
import entity.ThongKeDoanhThu;
import entity.ThongKeSanPham;
import java.time.LocalDate;
import java.util.List;
import util.XQuery;

/**
 *
 * @author Nguyen Trung Nghia
 */
public class Daoipm_ThongKe implements Dao_Thongke{
String sqlAllNay =
"    SELECT DAY(hd.ngay_tao_ma) AS thang, \n" +
"    COUNT(DISTINCT hd.id_hoa_don) AS soDon, \n" +
"    COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong, \n" +
"    COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy, \n" +
"    SUM(hd.tong_tien_phai_tra) AS tongDoanhThu \n" +
"FROM hoa_don hd \n" +
"JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don \n" +
"WHERE CAST(hd.ngay_tao_ma AS DATE) = CAST(GETDATE() AS DATE) \n" +
"AND loai_hoa_don = 1 \n"  +        
"GROUP BY DAY(hd.ngay_tao_ma)";
String SqlToAndDate =
"    SELECT COUNT(DISTINCT hd.id_hoa_don) AS soDon,\n" +
"    COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong,\n" +
"    COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy,\n" +
"    SUM(hd.tong_tien_phai_tra) AS tongDoanhThu\n" +
"FROM hoa_don hd\n" +
"JOIN hoa_don_chi_tiet hdct \n" +
"    ON hd.id_hoa_don = hdct.id_hoa_don\n" +
"WHERE CAST(hd.ngay_tao_ma AS DATE) BETWEEN ? AND ?\n"  +
"AND loai_hoa_don = 1 \n";
String sqkThangNam =
"    SELECT MONTH(hd.ngay_tao_ma) AS thang,\n" +
"    SUM(hdct.so_luong) AS soSanPhamBan,\n" +
"    SUM(hd.tong_tien_ban_dau) AS tongGiaBan,\n" +
"    SUM(hd.tien_giam_gia) AS tongGiamGia,\n" +
"    SUM(hd.tong_tien_phai_tra) AS tongDoanhThu,\n" +
"    COUNT(DISTINCT hd.id_hoa_don) AS soDon,\n" +
"    COUNT(DISTINCT CASE WHEN hd.trang_thai = 1 THEN hd.id_hoa_don END) AS donThanhCong,\n" +
"    COUNT(DISTINCT CASE WHEN hd.trang_thai = 2 THEN hd.id_hoa_don END) AS donHuy\n" +
"FROM hoa_don_chi_tiet hdct\n" +
"JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don\n" +
"WHERE MONTH(hd.ngay_tao_ma) = ?\n" +
"AND YEAR(hd.ngay_tao_ma) = ?\n" +
"AND loai_hoa_don = 1 \n" +
"GROUP BY MONTH(hd.ngay_tao_ma)\n" +
"ORDER BY MONTH(hd.ngay_tao_ma)";
String sqlTheoNam =
"SELECT MONTH(hd.ngay_tao_ma) AS thang, " +
"SUM(hdct.so_luong) AS soSanPhamBan, " +
"SUM(hd.tong_tien_ban_dau) AS tongGiaBan, " +
"SUM(hd.tien_giam_gia) AS tongGiamGia, " +
"SUM(hd.tong_tien_phai_tra) AS tongDoanhThu " +
"FROM hoa_don_chi_tiet hdct " +
"JOIN hoa_don hd ON hd.id_hoa_don = hdct.id_hoa_don " +
"WHERE YEAR(hd.ngay_tao_ma) = ? " +
"AND loai_hoa_don = 1 \n" +
"GROUP BY MONTH(hd.ngay_tao_ma) " +
"ORDER BY MONTH(hd.ngay_tao_ma)";
String sqlCBO = "SELECT DISTINCT YEAR(ngay_tao_ma) AS nam FROM hoa_don ORDER BY nam DESC";
String sqlSP = "SELECT \n" +
"    sp.ma_san_pham as maSP,\n" +
"    lsp.ten_loai_san_pham as loaiSP,\n" +
"    sp.ten_san_pham as tenSP,\n" +
"    cl.ten_chat_lieu as chatLieu,\n" +
"    ms.ten_mau_sac as mauSac,\n" +
"    kc.ten_kich_co as kichThuoc,\n" +
"    spct.so_luong_ton as soLuongTon\n" +
"FROM san_pham_chi_tiet spct\n" +
"JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham\n" +
"JOIN loai_san_pham lsp ON lsp.id_loai_san_pham = sp.id_loai_san_pham\n" +
"JOIN chat_lieu cl ON cl.id_chat_lieu = sp.id_chat_lieu\n" +
"JOIN mau_sac ms ON ms.id_mau_sac = spct.id_mau_sac\n" +
"JOIN kich_co kc ON kc.id_kich_co = spct.id_kich_co\n" +
"ORDER BY spct.so_luong_ton ASC";
    @Override
    public List<ThongKeDoanhThu> homNay() {
        return XQuery.getBeanList(ThongKeDoanhThu.class, sqlAllNay);
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ThongKeDoanhThu> theoNgay(LocalDate from, LocalDate to) {
        java.sql.Date sqlFrom = java.sql.Date.valueOf(from);
        java.sql.Date sqlTo = java.sql.Date.valueOf(to);
        return XQuery.getBeanList(ThongKeDoanhThu.class, SqlToAndDate,sqlFrom,sqlTo);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ThongKeDoanhThu> theoThang(int month, int year) {
        return XQuery.getBeanList(ThongKeDoanhThu.class, sqkThangNam, month,year);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ThongKeSanPham> tatCaSanPham() {
        return XQuery.getBeanList(ThongKeSanPham.class, sqlSP);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ThongKeSanPham> sanPhamTheoNgay(LocalDate from, LocalDate to) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ThongKeSanPham> sanPhamTheoThang(int month, int year) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ThongKeDoanhThu> theoNam(int year) {
        return XQuery.getBeanList(ThongKeDoanhThu.class,sqlTheoNam,year);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Nam> loatNam() {
        return XQuery.getBeanList(Nam.class, sqlCBO);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
