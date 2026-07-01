/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.XuatXu;
import java.util.List;
import util.XQuery;
import dao.DAO_XuatXu;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.XJdbc;

/**
 *
 * @author dungcc
 */
public class DaoImpl_XuatXu implements DAO_XuatXu{
private String sqlFillAll = "select * from xuat_su";
private String sqlFillById = "select * from xuat_su where id_xuat_su = ?";
private String sqlFillByName = "select * from xuat_su where ten_xuat_su = ?";
private String sqlupdate="UPDATE xuat_su SET ten_xuat_su = ? WHERE ma_xuat_su = ?";
   private String sqldelete="DELETE FROM xuat_su WHERE id_xuat_su = ?";
    @Override
    public XuatXu create(XuatXu entity) {
    try {
        // 1) Insert KHÔNG có mã (để DB sinh ID)
        String sqlInsert = "INSERT INTO xuat_su(ten_xuat_su, mo_ta) VALUES (?,?)";
        PreparedStatement ps = XJdbc.getConnection()
                .prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, entity.getTen_xuat_su());
        ps.setString(2, entity.getMo_ta());
        ps.executeUpdate();

        // 2) Lấy ID vừa sinh
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            entity.setId_xuat_su(id);

            // 3) Tự sinh mã: LSP + id
            String ma = "XX" + id;
            entity.setMa_xuat_su(ma);

            // 4) Update mã vào DB
            String sqlUpdate = "UPDATE xuat_su SET ma_xuat_xu = ? WHERE id_xuat_xu = ?";
            XJdbc.executeUpdate(sqlUpdate, ma, id);
        }

        return entity;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    @Override
    public void update(XuatXu entity) {
         XJdbc.executeUpdate(sqlupdate, entity.getTen_xuat_su(),entity.getMa_xuat_su());
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
         XJdbc.executeUpdate(sqldelete, id);
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<XuatXu> findAll() {
        return XQuery.getBeanList(XuatXu.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public XuatXu findById(Integer id) {
        return XQuery.getSingleBean(XuatXu.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public XuatXu findByName(String name) {
        return XQuery.getSingleBean(XuatXu.class, sqlFillByName, name);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
