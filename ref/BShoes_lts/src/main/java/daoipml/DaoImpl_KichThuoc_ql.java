/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.KichCo;
import java.util.List;
import util.XQuery;
import dao.Dao_KichThuoc;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.XJdbc;

/**
 *
 * @author dungcc
 */
public class DaoImpl_KichThuoc_ql implements Dao_KichThuoc{
private String sqlFillAll = "select * from kich_co";
private String sqlFillById = "select * from kich_co where id_kich_co = ?";
private String sqlFillByName = "select * from kich_co where ten_kich_co = ?";
private String sqlcreate = "insert into kich_co(ma_kich_co,ten_kich_co) values (?,?)";
private String sqlupdate="UPDATE kich_co SET ten_kich_co = ? WHERE ma_kich_co = ?";
 private String sqldelete="DELETE FROM kich_co WHERE id_kich_co = ?";
    @Override
     public KichCo create(KichCo entity) {
         XJdbc.executeUpdate(sqlcreate, entity.getMa_kich_co(), entity.getTen_kich_co());
        return entity;
   }

    @Override
    public void update(KichCo entity) {
         XJdbc.executeUpdate(sqlupdate, entity.getTen_kich_co(),entity.getMa_kich_co());
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqldelete,   id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<KichCo> findAll() {
        return XQuery.getBeanList(KichCo.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KichCo findById(Integer id) {
        return XQuery.getSingleBean(KichCo.class, sqlFillById , id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KichCo findByName(String name) {
        return XQuery.getSingleBean(KichCo.class, sqlFillByName, name);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
