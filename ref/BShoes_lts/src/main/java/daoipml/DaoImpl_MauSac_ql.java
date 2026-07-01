/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.MauSac;
import java.util.List;
import util.XQuery;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.XJdbc;
import dao.DAO_MauSac_ql;

/**
 *
 * @author dungcc
 */
public class DaoImpl_MauSac_ql implements DAO_MauSac_ql{
private String sqlFillAll = "select * from mau_sac";
private String sqlFillById = "select * from mau_sac where id_mau_sac = ?";
private String sqlFillByName = "select * from mau_sac where ten_mau_sac = ?";
private String sqlcreate = "insert into mau_sac(ma_mau_sac,ten_mau_sac) values (?,?)";
private String sqlupdate="UPDATE mau_sac SET ten_mau_sac = ? WHERE ma_mau_sac = ?";
 private String sqldelete="DELETE FROM mau_sac WHERE id_mau_sac = ?";
    
@Override
    public MauSac create(MauSac entity) {
             XJdbc.executeUpdate(sqlcreate, entity.getMa_mau_sac(), entity.getTen_mau_sac());
        return entity;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    @Override
    public void update(MauSac entity) {
        XJdbc.executeUpdate(sqlupdate, entity.getTen_mau_sac(),entity.getMa_mau_sac());
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqldelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<MauSac> findAll() {
        return XQuery.getBeanList(MauSac.class, sqlFillAll);
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public MauSac findById(Integer id) {
        return XQuery.getSingleBean(MauSac.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public MauSac findByName(String name) {
        return XQuery.getSingleBean(MauSac.class, sqlFillByName, name);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<MauSac> getAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public MauSac findById(int id) {
        return XQuery.getSingleBean(MauSac.class, sqlFillById, id);
       //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    
}
