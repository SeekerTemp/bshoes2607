/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import dao.Dao_DangNhap;
import entity.NhanVien;
import java.util.List;
import util.XJdbc;
import util.XQuery;

/**
 *
 * @author PC
 */
public class DaoImpl_DangNhap implements Dao_DangNhap{
       String sql = """
        SELECT nv.id_nhan_vien, nv.ma_nhan_vien, nv.ten_nhan_vien,
               vt.ma_vai_tro, vt.ten_vai_tro
        FROM nhan_vien nv
        JOIN vai_tro vt ON nv.id_vai_tro = vt.id_vai_tro
        WHERE nv.tai_khoan = ? AND nv.mat_khau = ?
    """;  

    @Override
    public NhanVien create(NhanVien entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(NhanVien entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<NhanVien> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public NhanVien findById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String layMatKhauTheoTaiKhoan(String taiKhoan) {
         String sql = """
            SELECT mat_khau
            FROM nhan_vien
            WHERE tai_khoan = ?
        """;

        return XQuery.getSingleBean(String.class, sql, taiKhoan);
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String layVaiTroTheoTaiKhoan(String taiKhoan) {
          String sql = """
            SELECT vai_tro
            FROM nhan_vien
            WHERE tai_khoan = ?
        """;
        return XQuery.getSingleBean(String.class, sql, taiKhoan);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public NhanVien login(String taiKhoan, String matKhau) {
    List<NhanVien> list = XQuery.getBeanList(NhanVien.class, sql, taiKhoan, matKhau);

    return list.isEmpty() ? null : list.get(0);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    

    

   
    
}
