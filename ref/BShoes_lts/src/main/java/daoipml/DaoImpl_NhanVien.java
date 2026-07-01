/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import dao.Dao_NhanVien;
import entity.NhanVien;
import java.sql.ResultSet;
import java.util.List;
import util.XJdbc;
import util.XQuery;

/**
 *
 * @author DELL
 */
public class DaoImpl_NhanVien implements Dao_NhanVien{
       String sqlFindAll = """
    SELECT id_nhan_vien,ma_nhan_vien, ten_nhan_vien, cccd, email,
           so_dien_thoai, gioi_tinh, dia_chi,ngay_sinh
    FROM nhan_vien where  trang_thai_xoa = 0
""";

String sqlFindById = """
    SELECT id_nhan_vien,ma_nhan_vien, ten_nhan_vien, cccd, email,
           so_dien_thoai, gioi_tinh, dia_chi,ngay_sinh,tai_khoan, mat_khau
    FROM nhan_vien
    WHERE id_nhan_vien = ? and trang_thai_xoa = 0
""";

String sqlCreate = """
    INSERT INTO nhan_vien
        (ma_nhan_vien, ten_nhan_vien, cccd, email, so_dien_thoai,
         gioi_tinh, dia_chi,ngay_sinh,tai_khoan, mat_khau)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)
""";

String sqlUpdate = """
    UPDATE nhan_vien SET
        ten_nhan_vien=?, cccd=?, email=?, so_dien_thoai=?,
        gioi_tinh=?, dia_chi=?,ngay_sinh=?,tai_khoan=?, mat_khau=?
    WHERE ma_nhan_vien = ?
""";

String sqlDelete = """
    update nhan_vien set trang_thai_xoa = 1 where id_nhan_vien = ?
""";

   
    @Override
    public NhanVien create(NhanVien entity) {
      XJdbc.executeUpdate(sqlCreate,
        entity.getMa_nhan_vien(),
        entity.getTen_nhan_vien(),
        entity.getCccd(),
        entity.getEmail(),
        entity.getSo_dien_thoai(),
        entity.getGioi_tinh(),
        entity.getDia_chi(),
        entity.getNgay_sinh(),
        entity.getTai_khoan(),
        entity.getMat_khau()
    );
    return null;
      //  throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(NhanVien entity) {
         XJdbc.executeUpdate(sqlUpdate,
            entity.getTen_nhan_vien(),
            entity.getCccd(),
            entity.getEmail(),
            entity.getSo_dien_thoai(),
            entity.getGioi_tinh(),
            entity.getDia_chi(),
            entity.getNgay_sinh(),
            entity.getTai_khoan(),
            entity.getMat_khau(),
            entity.getMa_nhan_vien()   // IMPORTANT: PK CUỐI CÙNG
    );
      //  throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    @Override
    public List<NhanVien> findAll() {
         return XQuery.getBeanList(NhanVien.class, sqlFindAll);
      //  throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

  

    @Override
    public void deleteById(Integer id) {
         XJdbc.executeUpdate(sqlDelete, id);
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public NhanVien findById(Integer id) {
        return XQuery.getSingleBean(NhanVien.class, sqlFindById, id);
      //  throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<NhanVien> findByName(String name,  String gender) {
       
    // Trường hợp tìm tất cả giới tính
    if (gender.equalsIgnoreCase("all")) {
        String sql = """
            SELECT id_nhan_vien,ma_nhan_vien, ten_nhan_vien, cccd, email,
                   so_dien_thoai, gioi_tinh, dia_chi, tai_khoan, mat_khau
            FROM nhan_vien
            WHERE ten_nhan_vien LIKE ?
        """;
        return XQuery.getBeanList(NhanVien.class, sql, "%" + name + "%");
    }

    // Trường hợp có gender: Nam / Nữ
    String sql = """
        SELECT id_nhan_vien,ma_nhan_vien, ten_nhan_vien, cccd, email,
               so_dien_thoai, gioi_tinh, dia_chi, tai_khoan, mat_khau
        FROM nhan_vien
        WHERE ten_nhan_vien LIKE ? AND gioi_tinh = ?
    """;

    return XQuery.getBeanList(NhanVien.class, sql, "%" + name + "%", gender);


       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean existsMa(String ma) {
    try {
        String sql = "SELECT COUNT(*) FROM nhan_vien WHERE ma_nhan_vien = ?";
        ResultSet rs = XJdbc.executeQuery(sql, ma);
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
    return false;
}

public boolean existsTaiKhoan(String tk) {
    try {
        String sql = "SELECT COUNT(*) FROM nhan_vien WHERE tai_khoan = ?";
        ResultSet rs = XJdbc.executeQuery(sql, tk);
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
    return false;
}

public boolean existsCCCD(String cccd) {
    try {
        String sql = "SELECT COUNT(*) FROM nhan_vien WHERE cccd = ?";
        ResultSet rs = XJdbc.executeQuery(sql, cccd);
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
    return false;
}

public boolean existsEmail(String email) {
    try {
        String sql = "SELECT COUNT(*) FROM nhan_vien WHERE email = ?";
        ResultSet rs = XJdbc.executeQuery(sql, email);
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
    return false;
}

public boolean existsSdt(String sdt) {
    try {
        String sql = "SELECT COUNT(*) FROM nhan_vien WHERE so_dien_thoai = ?";
        ResultSet rs = XJdbc.executeQuery(sql, sdt);
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
    return false;
}
    
 

  
}
