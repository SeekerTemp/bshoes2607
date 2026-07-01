/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import dao.Dao_HoaDonChiTiet;
import entity.HoaDonChiTiet;
import java.util.List;
import util.XJdbc;
import util.XQuery;

/**
 *
 * @author PC
 */
public class DaoImpl_HoaDonChiTiet implements Dao_HoaDonChiTiet {

    String sql = "";
    String sqlCreate = "INSERT INTO hoa_don_chi_tiet\n" +
"(id_san_pham_chi_tiet, id_hoa_don, so_luong, nguoi_tao, nguoi_cap_nhat, trang_thai, thanh_tien, gia_giam)\n" +
"VALUES (?, ?, ?, ?, ?, 0, ?, 0)";
    String sqlCreateNew = "EXEC tao_hoa_don_va_chi_tiet ?,?,?,? ";
    String sqlUpdate = "update hoa_don_chi_tiet set so_luong=?,thanh_tien=? where id_hoa_don_chi_tiet=? and trang_thai=0";
    String sqlUpdateStock = "update hoa_don_chi_tiet set so_luong=? where id_hoa_don_chi_tiet=?";
    String sqlDelete = "";
    String sqlDeleteById = "DELETE FROM [dbo].[hoa_don_chi_tiet] WHERE id_hoa_don_chi_tiet = ?";
    
    String sqlFindAll = "select * from hoa_don_chi_tiet";
    String sqlFindById = "select * from hoa_don_chi_tiet where id_hoa_don_chi_tiet=?";
    String sqlFindAllById = "select *from hoa_don_chi_tiet where id_hoa_don = ?";
    String sqlFindAllGioHang = "  select * from hoa_don_chi_tiet where id_hoa_don=?";
    String sqlFindhdct = "  select * from hoa_don_chi_tiet where id_hoa_don=? and id_san_pham_chi_tiet = ?";
    String sqlHoaDon =  "  SELECT \n" +
"    hdct.id_hoa_don_chi_tiet,\n" +
"    hdct.id_hoa_don,\n" +
"    hdct.id_san_pham_chi_tiet,\n" +
"\n" +
"    sp.ten_san_pham AS ten_san_pham,\n" +
"    spct.don_gia AS don_gia,\n" +
"\n" +
"    hdct.so_luong,\n" +
"    hdct.thanh_tien\n" +
"FROM hoa_don_chi_tiet hdct\n" +
"JOIN san_pham_chi_tiet spct ON spct.id_san_pham_chi_tiet = hdct.id_san_pham_chi_tiet\n" +
"JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham\n" +
"WHERE hdct.id_hoa_don = ?";

    @Override
    public HoaDonChiTiet create(HoaDonChiTiet entity) {
        Object[] values = {
            entity.getId_san_pham_chi_tiet(),
            entity.getId_hoa_don(),
            entity.getSo_luong(),
            entity.getNguoi_tao(),
            entity.getNguoi_cap_nhat(),
//            entity.isTrang_thai(),
            entity.getThanh_tien()
        };
        XJdbc.executeUpdate(sqlCreate, values);
        return entity;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(HoaDonChiTiet entity) {
        Object[] values = {
            entity.getSo_luong(),
            entity.getThanh_tien(),
            entity.getId_hoa_don_chi_tiet()

        };
        XJdbc.executeUpdate(sqlUpdate, values);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqlDeleteById, id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDonChiTiet> findAll() {
        return XQuery.getBeanList(HoaDonChiTiet.class, sqlFindAll);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HoaDonChiTiet findById(Integer id) {
        return XQuery.getSingleBean(HoaDonChiTiet.class, sqlFindById, id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDonChiTiet> findAllByID(Integer id) {
        return XQuery.getBeanList(HoaDonChiTiet.class, sqlFindAllById, id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDonChiTiet> timhdct(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDonChiTiet> timtenspcuahdct(int id, String ten) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDonChiTiet> findAllGioHang(int id) {
        return XQuery.getBeanList(HoaDonChiTiet.class, sqlFindAllGioHang, id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HoaDonChiTiet findidhdctbyidhdvaidspct(int idhd, int idspct) {
        return XQuery.getSingleBean(HoaDonChiTiet.class, sqlFindhdct,idhd,idspct);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDonChiTiet> findbytimkiemallgiohang(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HoaDonChiTiet createNew(HoaDonChiTiet entity) {
        Object[] values = {
            entity.getId_san_pham_chi_tiet(),
            entity.getSo_luong(),
            entity.getGia_giam(),
            entity.getNguoi_tao()
//EXEC tao_hoa_don_va_chi_tiet @id_san_pham_chi_tiet = 5,@so_luong = 3,@gia_giam = 15000,@nguoi_tao = N'admin';

        };
        XJdbc.executeUpdate(sqlCreateNew, values);
        return entity;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateStock(HoaDonChiTiet entity) {
        Object[] values = {
            entity.getSo_luong(),
            entity.getId_hoa_don_chi_tiet()
        };
        XJdbc.executeUpdate(sqlUpdateStock, values);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<HoaDonChiTiet> findBYHoaDon(int idHD) {
         return XQuery.getBeanList(HoaDonChiTiet.class, sqlHoaDon, idHD);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
