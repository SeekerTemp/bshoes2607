/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.ThuongHieu;
import java.util.List;
import util.XQuery;
import dao.DAO_ThuongHieu;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.XJdbc;

/**
 *
 * @author dungcc
 */
public class DaoImpl_ThuongHieu implements DAO_ThuongHieu{
private String sqlFillAll = "select * from thuong_hieu";
private String sqlFillById = "select * from thuong_hieu where id_thuong_hieu = ?";
private String sqlFillByName = "select * from thuong_hieu where ten_thuong_hieu = ?";
private String sqlupdate="UPDATE thuong_hieu SET ten_thuong_hieu = ? WHERE ma_thuong_hieu = ?";
    private String sqldelete="DELETE FROM thuong_hieu WHERE id_thuong_hieu = ?";
    @Override
    public ThuongHieu  create(ThuongHieu entity) {
    try {
        // 1) Insert KHÔNG có mã (để DB sinh ID)
        String sqlInsert = "INSERT INTO thuong_hieu(ten_thuong_hieu, mo_ta) VALUES (?,?)";
        PreparedStatement ps = XJdbc.getConnection()
                .prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, entity.getTen_thuong_hieu());
        ps.setString(2, entity.getMo_ta());
        ps.executeUpdate();

        // 2) Lấy ID vừa sinh
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            entity.setId_thuong_hieu(id);

            // 3) Tự sinh mã: LSP + id
            String ma = "TH" + id;
            entity.setMa_thuong_hieu(ma);

            // 4) Update mã vào DB
            String sqlUpdate = "UPDATE thuong_hieu SET ma_thuong_hieu = ? WHERE id_thuong_hieu = ?";
            XJdbc.executeUpdate(sqlUpdate, ma, id);
        }

        return entity;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    @Override
    public void update(ThuongHieu entity) {
         XJdbc.executeUpdate(sqlupdate, entity.getTen_thuong_hieu(),entity.getMa_thuong_hieu());
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
         XJdbc.executeUpdate(sqldelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ThuongHieu> findAll() {
        return XQuery.getBeanList(ThuongHieu.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ThuongHieu findById(Integer id) {
        return XQuery.getSingleBean(ThuongHieu.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ThuongHieu findByName(String name) {
        return XQuery.getSingleBean(ThuongHieu.class, sqlFillByName, name);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
