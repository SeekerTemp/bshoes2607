/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.SanPhamChiTiet;
import java.util.List;
import util.XJdbc;
import util.XQuery;
import dao.Dao_SanPhamChiTiet_hd;

/**
 *
 * @author DELL
 */
public class DaoImpl_SanPhamChiTiet_hd implements Dao_SanPhamChiTiet_hd {

    private String Sql = "";
    private String SqlUpdate = "";
    private String SqlUpdateStock =  " update san_pham_chi_tiet set so_luong_ton=? where id_san_pham_chi_tiet=? and trang_thai=1";
    private String SqlUpdateSlKhiHetHang = "update san_pham_chi_tiet set trang_thai=0 where so_luong_ton=0 and id_san_pham_chi_tiet=?";

    private String SqlFindAll = "SELECT *  FROM [BShoes].[dbo].[san_pham_chi_tiet]";
    private String SqlFindAllAvaillable = "SELECT * FROM [BShoes].[dbo].[san_pham_chi_tiet] Where [trang_thai] =1 AND so_luong_ton > 0";
    private String SqlFindAllAvaillableByName = "SELECT san_pham_chi_tiet.*, san_pham.ten_san_pham\n"
            + "FROM san_pham INNER JOIN san_pham_chi_tiet ON san_pham.id_san_pham = san_pham_chi_tiet.id_san_pham\n"
            + "where san_pham_chi_tiet.trang_thai =1 AND spct.so_luong_ton > 0  and san_pham_chi_tiet.ma_san_pham_chi_tiet like ? or san_pham.ten_san_pham like ?";

    private String SqlFindById = "SELECT * from [BShoes].[dbo].[san_pham_chi_tiet] where id_san_pham_chi_tiet = ?";
    private String SqlFindByMa = "SELECT * FROM [BShoes].[dbo].[san_pham_chi_tiet] where ma_san_pham_chi_tiet = ?";
    private String SqlFindByTT = "SELECT * FROM [BShoes].[dbo].[san_pham_chi_tiet]  Where [trang_thai] =1";
    private String SqlFindByAllById = "SELECT * from [BShoes].[dbo].[san_pham_chi_tiet] where id_hoa_don = ?";

    @Override
    public SanPhamChiTiet create(SanPhamChiTiet entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(SanPhamChiTiet entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPhamChiTiet> findAll() {
        return XQuery.getBeanList(SanPhamChiTiet.class, SqlFindAll);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SanPhamChiTiet findById(Integer id) {
        return XQuery.getSingleBean(SanPhamChiTiet.class, SqlFindById,id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPhamChiTiet> findAllAvailable() {
        return XQuery.getBeanList(SanPhamChiTiet.class, SqlFindAllAvaillable);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SanPhamChiTiet findByMa(String string) {
        return XQuery.getSingleBean(SanPhamChiTiet.class, SqlFindByMa, string);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPhamChiTiet> findAllById(Integer id) {
        return XQuery.getBeanList(SanPhamChiTiet.class, SqlFindByAllById,id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPhamChiTiet> findByIDSanPham(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPhamChiTiet> filter(String danhMuc, String xuatXu, String nhaSX, String thuTuGia) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteTT() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Integer> getIdSanPhamBiAnhHuong(String tenCot, int idThuocTinh) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void xoaMemTheoThuocTinh(String tenCot, int idThuocTinh) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void UpdateStock(SanPhamChiTiet entity) {
        Object[] value = {
            entity.getSo_luong_ton(),
            entity.getId_san_pham_chi_tiet()
        };
        XJdbc.executeUpdate(SqlUpdateStock, value);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void UpdateSLKhiHetHang(SanPhamChiTiet entity) {
        Object[] values = {
            entity.getId_san_pham_chi_tiet()
        };
        XJdbc.executeUpdate(SqlUpdateSlKhiHetHang, values);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateSlKhiXoa(SanPhamChiTiet entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateSlTrangThai(SanPhamChiTiet entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateSlKhiThemVaoGhtt(SanPhamChiTiet entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPhamChiTiet> findAllAvailableByName(String ma) {
        return XQuery.getBeanList(SanPhamChiTiet.class, SqlFindAllAvaillableByName,ma,ma);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
