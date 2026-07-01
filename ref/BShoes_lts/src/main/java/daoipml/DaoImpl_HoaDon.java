/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import dao.Dao_HoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.PhieuGiamGia;
import java.math.BigDecimal;
import java.util.List;
import util.XJdbc;
import util.XQuery;

/**
 *
 * @author PC
 */
public class DaoImpl_HoaDon implements Dao_HoaDon {

    String sql = "";
    String sqlCreate = "EXEC sp_insert_hoa_don_placeholder ?,?,?";
    String sqlUpdate =
    "UPDATE hoa_don SET "
            + "tong_tien_ban_dau = ?, "
            + "tien_giam_gia = ?, "
            + "tong_tien_phai_tra = ?, "
            + "trang_thai = ?, "
            + "loai_hoa_don = ?, "
            + "nguoi_cap_nhat = ?, "
            + "ngay_cap_nhat = GETDATE() "
            + "WHERE id_hoa_don = ?";
    String sqlFindAll = "select id_hoa_don, ma_hoa_don, id_khach_hang, id_nhan_vien,\n" +
"       tong_tien_ban_dau, tien_giam_gia, tong_tien_phai_tra,\n" +
"       trang_thai, loai_hoa_don, phuong_thuc_thanh_toan,\n" +
"       ngay_tao_ma, ngay_cap_nhat, nguoi_tao_ma, nguoi_cap_nhat\n" +
"from hoa_don\n" +
"where trang_thai = 0\n" +
"order by id_hoa_don desc ";
    String sqlFindByID = "select *from hoa_don where id_hoa_don=?";
    String sqlFindByMa = "select *from hoa_don where ma_hoa_don=?";
    String sqlUpDateQR = "UPDATE hoa_don SET "
            + "trang_thai = 1, "
            + "loai_hoa_don = 1, "
            + "ngay_cap_nhat = GETDATE(), "
            + "nguoi_cap_nhat='admin' "
            + "WHERE id_hoa_don = ?";
    String sqlTonggiamgia = "SELECT dbo.tinh_tien_giam_gia(?, ?)";
    String sqlGetPGGHD = "SELECT * FROM view_phieu_giam_gia_hoat_dong";
    String sqlupdateidkh = "UPDATE hoa_don SET id_khach_hang = ? WHERE id_hoa_don = ?";
    String sqlDelete = "delete from hoa_don WHERE id_hoa_don = ?";
    @Override
    public HoaDon create(HoaDon entity) {
        Object[] values ={
            entity.getTrang_thai(),
            entity.isLoai_hoa_don(),
            entity.getId_nhan_vien() 
        //            EXEC tao_hoa_don_va_chi_tiet @id_san_pham_chi_tiet = 5,@so_luong = 3,@gia_giam = 15000,@nguoi_tao 
    };
        XJdbc.executeUpdate(sqlCreate, values);
    return entity;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(HoaDon entity) {
            XJdbc.executeUpdate(sqlUpdate,
            entity.getTong_tien_ban_dau(),
            entity.getTien_giam_gia(),
            entity.getTong_tien_phai_tra(),
            entity.getTrang_thai(),
            entity.isLoai_hoa_don(),
            entity.getNguoi_cap_nhat(),
            entity.getId_hoa_don()
    );
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqlDelete, id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDon> findAll() {
        return XQuery.getBeanList(HoaDon.class, sqlFindAll);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HoaDon findById(Integer id) {
        return XQuery.getSingleBean(HoaDon.class, sqlFindByID, id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HoaDon findByMa(String madh) {
        return XQuery.getSingleBean(HoaDon.class, sqlFindByMa, madh);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateTrangThaiThanhToan(int idHoaDon) {
        XJdbc.executeUpdate(sqlUpDateQR, idHoaDon);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public BigDecimal tinhGiamGia(int idPhieu, BigDecimal tongTien) {
        return (BigDecimal) XJdbc.getValue(sqlTonggiamgia, idPhieu, tongTien);
    }

    @Override
    public List<PhieuGiamGia> getPGGHoatDong() {
        return XQuery.getBeanList(PhieuGiamGia.class, sqlGetPGGHD);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public void updateKhachHang(int idHoaDon, int idKhachHang) {
    XJdbc.executeUpdate(sqlupdateidkh, idKhachHang, idHoaDon);
}
}
