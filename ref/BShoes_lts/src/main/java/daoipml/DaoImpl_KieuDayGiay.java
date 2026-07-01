/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.KieuDayGiay;
import java.util.List;
import util.XQuery;
import dao.Dao_KieuDayGiay;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.XJdbc;

/**
 *
 * @author dungcc
 */
public class DaoImpl_KieuDayGiay implements Dao_KieuDayGiay{
private String sqlFillAll = "select * from kieu_day_giay";
private String sqlFillById = "select * from kieu_day_giay where id_kieu_day_giay = ?";
private String sqlFillByName = "select * from kieu_day_giay where ten_day_giay = ?";
private String sqlupdate="UPDATE kieu_day_giay SET ten_day_giay = ? WHERE ma_day_giay = ?";
    private String sqldelete="DELETE FROM kieu_day_giay WHERE id_kieu_day_giay = ?";
    @Override
    public KieuDayGiay create(KieuDayGiay entity) {
    try {
        // 1) Insert KHÔNG có mã (để DB sinh ID)
        String sqlInsert = "INSERT INTO kieu_day_giay(ten_day_giay) VALUES (?)";
        PreparedStatement ps = XJdbc.getConnection()
                .prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, entity.getTen_day_giay());
        
        ps.executeUpdate();

        // 2) Lấy ID vừa sinh
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            entity.setId_kieu_day_giay(id);

            // 3) Tự sinh mã: LSP + id
            String ma = "DG" + id;
            entity.setMa_day_giay(ma);

            // 4) Update mã vào DB
            String sqlUpdate = "UPDATE kieu_day_giay SET ma_day_giay = ? WHERE id_kieu_day_giay = ?";
            XJdbc.executeUpdate(sqlUpdate, ma, id);
        }

        return entity;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    @Override
    public void update(KieuDayGiay entity) {
         XJdbc.executeUpdate(sqlupdate, entity.getTen_day_giay(),entity.getMa_day_giay());
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
         XJdbc.executeUpdate(sqldelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<KieuDayGiay> findAll() {
        return XQuery.getBeanList(KieuDayGiay.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KieuDayGiay findById(Integer id) {
        return XQuery.getSingleBean(KieuDayGiay.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KieuDayGiay findByName(String name) {
        return XQuery.getSingleBean(KieuDayGiay.class, sqlFillByName, name);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
