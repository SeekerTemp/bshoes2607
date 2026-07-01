/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.KhachHang;
import java.util.Date;
import java.util.List;
import dao.Dao_KhachHang;
import util.XQuery;
import util.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author Quynh Anh
 */
public class DaoImpl_KhachHang implements Dao_KhachHang{
String sqlAll="select id_khach_hang,ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai,nguoi_tao_ma,nguoi_cap_nhat\n" +
"from khach_hang where trang_thai_xoa = 0";
String sqlAllID="select id_khach_hang,ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai,nguoi_tao_ma,nguoi_cap_nhat\n" +
"from khach_hang where id_khach_hang=? and trang_thai_xoa = 0";
String sqlTK="select id_khach_hang,ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai,nguoi_tao_ma,nguoi_cap_nhat\n" +
"from khach_hang where ten_khach_hang like ? and  trang_thai_xoa = 0";
String sqlCreate ="INSERT INTO khach_hang(ma_khach_hang,ten_khach_hang,gioi_tinh,so_dien_thoai,dia_chi,email,trang_thai)values (?,?,?,?,?,?,?)";
String sqlUpdate ="UPDATE khach_hang set ten_khach_hang=?,gioi_tinh=?,so_dien_thoai=?,dia_chi=?,email=?,trang_thai=? where ma_khach_hang=?";
String sqlDelete = "update khach_hang set trang_thai_xoa = 1 where id_khach_hang = ?";
String sqltimsdt = "SELECT * FROM khach_hang WHERE so_dien_thoai = ?";
    @Override
    public KhachHang create(KhachHang entity) {
        XJdbc.executeUpdate(sqlCreate,
        entity.getMa_khach_hang(),
        entity.getTen_khach_hang(),
        entity.getGioi_tinh(),
        entity.getSo_dien_thoai(),
        entity.getDia_chi(),
        entity.getEmail(),
        entity.isTrang_thai()
    );
    return null;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(KhachHang entity) {
            XJdbc.executeUpdate(sqlUpdate,
            entity.getTen_khach_hang(),
            entity.getGioi_tinh(),
            entity.getSo_dien_thoai(),
            entity.getDia_chi(),
            entity.getEmail(),
            entity.isTrang_thai(),
            entity.getMa_khach_hang()   // IMPORTANT: PK CUỐI CÙNG
    );
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqlDelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<KhachHang> findAll() {
        return XQuery.getBeanList(KhachHang.class, sqlAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KhachHang findById(Integer id) {
        return XQuery.getSingleBean(KhachHang.class, sqlAllID, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<KhachHang> findByName(String name) {
        return XQuery.getBeanList(KhachHang.class,sqlTK,"%"+name+"%");
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
     public boolean existsMa(String ma) {
        try {
            String sql = "SELECT COUNT(*) FROM khach_hang WHERE ma_khach_hang = ? AND trang_thai_xoa = 0";
            ResultSet rs = XJdbc.executeQuery(sql, ma);
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Trùng email
    public boolean existsEmail(String email) {
        try {
            String sql = "SELECT COUNT(*) FROM khach_hang WHERE email = ? AND trang_thai_xoa = 0";
            ResultSet rs = XJdbc.executeQuery(sql, email);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

 

    // Trùng số điện thoại
    public boolean existsSDT(String sdt) {
        try {
            String sql = "SELECT COUNT(*) FROM khach_hang WHERE so_dien_thoai = ? AND trang_thai_xoa = 0";
            ResultSet rs = XJdbc.executeQuery(sql, sdt);
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Trùng mã khi UPDATE (loại trừ chính nó)
    public boolean existsMaUpdate(String ma, Integer id) {
        try {
            String sql = "SELECT COUNT(*) FROM khach_hang WHERE ma_khach_hang=? AND id_khach_hang <> ? AND trang_thai_xoa = 0";
            ResultSet rs = XJdbc.executeQuery(sql, ma, id);
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public KhachHang findBySDT(String sdt) {
         return XQuery.getSingleBean(KhachHang.class, sqltimsdt,sdt);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
