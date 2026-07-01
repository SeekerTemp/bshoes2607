/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.KieuCoGiay;
import java.util.List;
import util.XQuery;
import dao.Dao_KieuCoGiay;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.XJdbc;

/**
 *
 * @author dungcc
 */
public class DaoImpl_KieuCoGiay implements Dao_KieuCoGiay{
private String sqlFillAll = "select * from kieu_co_giay";
private String sqlFillById = "select * from kieu_co_giay where id_kieu_co_giay = ?";
private String sqlFillByName = "select * from kieu_co_giay where ten_co_giay = ?";
private String sqlupdate="UPDATE kieu_co_giay SET ten_co_giay = ? WHERE ma_co_giay = ?";
private String sqldelete="DELETE FROM kieu_co_giay WHERE id_kieu_co_giay = ?";
    @Override
    public KieuCoGiay create(KieuCoGiay entity) {
    try {
        // 1) Insert KHÔNG có mã (để DB sinh ID)
        String sqlInsert = "INSERT INTO kieu_co_giay(ten_co_giay) VALUES (?)";
        PreparedStatement ps = XJdbc.getConnection()
                .prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, entity.getTen_co_giay());
        
        ps.executeUpdate();

        // 2) Lấy ID vừa sinh
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            entity.setId_kieu_co_giay(id);

            // 3) Tự sinh mã: LSP + id
            String ma = "KC" + id;
            entity.setMa_co_giay(ma);

            // 4) Update mã vào DB
            String sqlUpdate = "UPDATE kieu_co_giay SET ma_co_giay = ? WHERE id_kieu_co_giay = ?";
            XJdbc.executeUpdate(sqlUpdate, ma, id);
        }

        return entity;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    @Override
    public void update(KieuCoGiay entity) {
         XJdbc.executeUpdate(sqlupdate, entity.getTen_co_giay(),entity.getMa_co_giay());
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
         XJdbc.executeUpdate(sqldelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<KieuCoGiay> findAll() {
        return XQuery.getBeanList(KieuCoGiay.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KieuCoGiay findById(Integer id) {
        return XQuery.getSingleBean(KieuCoGiay.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KieuCoGiay findByName(String name) {
        return XQuery.getSingleBean(KieuCoGiay.class, sqlFillByName, name);
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
