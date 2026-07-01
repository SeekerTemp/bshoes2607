/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.ChatLieu;
import java.util.List;
import util.XQuery;
import dao.Dao_ChatLieu;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import util.XJdbc;

/**
 *
 * @author dungcc
 */
public class DaoImpl_ChatLieu implements Dao_ChatLieu{
    private String sqlFillAll = "select * from chat_lieu";
    private String sqlFillById = "select * from chat_lieu where id_chat_lieu = ?";
    private String sqlFillByName = "select * from chat_lieu where ten_chat_lieu = ?";
    private String sqlupdate="UPDATE chat_lieu SET ten_chat_lieu = ? WHERE ma_chat_lieu = ?";
    private String sqldelete="DELETE FROM chat_lieu WHERE id_chat_lieu = ?";

    @Override
    public ChatLieu create(ChatLieu entity) {
    try {
        // 1) Insert KHÔNG có mã (để DB sinh ID)
        String sqlInsert = "INSERT INTO chat_lieu(ten_chat_lieu) VALUES (?)";
        PreparedStatement ps = XJdbc.getConnection()
                .prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, entity.getTen_chat_lieu());
        
        ps.executeUpdate();

        // 2) Lấy ID vừa sinh
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            entity.setId_chat_lieu(id);

            // 3) Tự sinh mã: LSP + id
            String ma = "CL" + id;
            entity.setMa_chat_lieu(ma);

            // 4) Update mã vào DB
            String sqlUpdate = "UPDATE chat_lieu SET ma_chat_lieu = ? WHERE id_chat_lieu = ?";
            XJdbc.executeUpdate(sqlUpdate, ma, id);
        }

        return entity;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    @Override
    public void update(ChatLieu entity) {
        XJdbc.executeUpdate(sqlupdate, entity.getTen_chat_lieu(),entity.getMa_chat_lieu());
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqldelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ChatLieu> findAll() {
        return XQuery.getBeanList(ChatLieu.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ChatLieu findById(Integer id) {
        return XQuery.getSingleBean(ChatLieu.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ChatLieu findByName(String name) {
        return XQuery.getSingleBean(ChatLieu.class,sqlFillByName,name);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
