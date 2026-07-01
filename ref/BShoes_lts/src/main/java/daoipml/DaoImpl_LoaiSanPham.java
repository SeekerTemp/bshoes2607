/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import dao.DAO_LoaiSanPham;
import entity.LoaiSanPham;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import util.XJdbc;
import util.XQuery;

/**
 *
 * @author dungcc
 */
public class DaoImpl_LoaiSanPham implements dao.DAO_LoaiSanPham{
    private String sqlFillAll = "select * from loai_san_pham";
    private String sqlFillById = "select * from loai_san_pham where id_loai_san_pham = ?";
    private String sqlFillByName = "select * from loai_san_pham where ten_loai_san_pham = ?";
//    private String sqlcreate = "insert into loai_san_pham(ma_loai_san_pham, ten_loai_san_pham, mo_ta) values (?,?,?)";
    private String sqlupdate = "update loai_san_pham set ten_loai_san_pham=?, mo_ta=? where ma_loai_san_pham = ?";
    private String sqldelete = "DELETE FROM loai_san_pham WHERE id_loai_san_pham = ?";

    @Override
    public LoaiSanPham findByName(String name) {
        return XQuery.getSingleBean(LoaiSanPham.class, sqlFillByName, name);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
public LoaiSanPham create(LoaiSanPham entity) {
    try {
        // 1) Insert KHÔNG có mã (để DB sinh ID)
        String sqlInsert = "INSERT INTO loai_san_pham(ten_loai_san_pham, mo_ta) VALUES (?,?)";
        PreparedStatement ps = XJdbc.getConnection()
                .prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, entity.getTen_loai_san_pham());
        ps.setString(2, entity.getMo_ta());
        ps.executeUpdate();

        // 2) Lấy ID vừa sinh
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            entity.setId_loai_san_pham(id);

            // 3) Tự sinh mã: LSP + id
            String ma = "LSP" + id;
            entity.setMa_loai_san_pham(ma);

            // 4) Update mã vào DB
            String sqlUpdate = "UPDATE loai_san_pham SET ma_loai_san_pham = ? WHERE id_loai_san_pham = ?";
            XJdbc.executeUpdate(sqlUpdate, ma, id);
        }

        return entity;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


    @Override
    public void update(LoaiSanPham entity) {
         XJdbc.executeUpdate(sqlupdate, entity.getTen_loai_san_pham(),entity.getMo_ta(),entity.getMa_loai_san_pham());
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqldelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<LoaiSanPham> findAll() {
        return XQuery.getBeanList(LoaiSanPham.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LoaiSanPham findById(Integer id) {
        return XQuery.getSingleBean(LoaiSanPham.class  , sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    
}
