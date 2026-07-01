/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.KieuDang;
import java.util.List;
import util.XQuery;
import dao.Dao_KieuDang;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.XJdbc;

/**
 *
 * @author dungcc
 */
public class DaoImpl_KieuDang implements Dao_KieuDang{
private String sqlFillAll = "select * from kieu_dang";
private String sqlFillById = "select * from kieu_dang where id_kieu_dang = ?";
private String sqlFillByName = "select * from kieu_dang where ten_kieu_dang = ?";
private String sqlupdate="UPDATE kieu_dang SET ten_kieu_dang = ? WHERE ma_kieu_dang = ?";
 private String sqldelete="DELETE FROM kieu_dang WHERE id_kieu_dang = ?";
    @Override
    public KieuDang create(KieuDang entity) {
    try {
        // 1) Insert KHÔNG có mã (để DB sinh ID)
        String sqlInsert = "INSERT INTO kieu_dang(ten_kieu_dang) VALUES (?)";
        PreparedStatement ps = XJdbc.getConnection()
                .prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, entity.getTen_kieu_dang());
        
        ps.executeUpdate();

        // 2) Lấy ID vừa sinh
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            entity.setId_kieu_dang(id);

            // 3) Tự sinh mã: LSP + id
            String ma = "KD" + id;
            entity.setMa_kieu_dang(ma);

            // 4) Update mã vào DB
            String sqlUpdate = "UPDATE kieu_dang SET ma_kieu_dang = ? WHERE id_kieu_dang = ?";
            XJdbc.executeUpdate(sqlUpdate, ma, id);
        }

        return entity;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    @Override
    public void update(KieuDang entity) {
        XJdbc.executeUpdate(sqlupdate, entity.getTen_kieu_dang(),entity.getMa_kieu_dang());
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
           XJdbc.executeUpdate(sqldelete, id);
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<KieuDang> findAll() {
        return XQuery.getBeanList(KieuDang.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KieuDang findById(Integer id) {
        return XQuery.getSingleBean(KieuDang.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KieuDang findByName(String name) {
        return XQuery.getSingleBean(KieuDang.class, sqlFillByName, name);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
