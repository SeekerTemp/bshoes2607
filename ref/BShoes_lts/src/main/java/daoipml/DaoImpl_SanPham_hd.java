/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.SanPham;
import java.util.List;
import util.XJdbc;
import util.XQuery;
import dao.Dao_SanPham_hd;

/**
 *
 * @author dungcc
 */
public class DaoImpl_SanPham_hd implements Dao_SanPham_hd {

    private String sqlFillAll
            = "select id_san_pham,ma_san_pham,ten_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su from san_pham ";

    private String sqlFillById = "select id_san_pham,ma_san_pham,ten_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su from san_pham where id_san_pham = ?";
    private String sqlcreate = "insert into san_pham(ma_san_pham, ten_san_pham, id_chat_lieu, id_kieu_dang, id_kieu_co_giay, id_kieu_day_giay, id_thuong_hieu, id_xuat_su) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private String sqlupdate = "update san_pham set ten_san_pham=?, id_chat_lieu=?, id_kieu_dang=?, id_kieu_co_giay=?, id_kieu_day_giay=?, id_thuong_hieu=?, id_xuat_su=? where ma_san_pham = ?";
    private String sqldelete = "delete from san_pham where id_san_pham = ?";
    private String sqlfindbyma = "select id_san_pham,ma_san_pham,ten_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su from san_pham where ma_san_pham like ?";
   

    @Override
    public SanPham create(SanPham entity) {
//        XJdbc.executeUpdate(sqlcreate, entity.getMa_san_pham(), entity.getTen_san_pham(), entity.getId_chat_lieu(), entity.getId_kieu_dang(), entity.getId_kieu_co_giay(), entity.getId_kieu_day_giay(), entity.getId_thuong_hieu(), entity.getId_xuat_su());
//        return entity;
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(SanPham entity) {
//        XJdbc.executeUpdate(sqlupdate, entity.getTen_san_pham(), entity.getId_chat_lieu(),entity.getId_kieu_dang(), entity.getId_kieu_co_giay(), entity.getId_kieu_day_giay(), entity.getId_thuong_hieu(), entity.getId_xuat_su(), entity.getMa_san_pham());
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqldelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPham> findAll() {
        return XQuery.getBeanList(SanPham.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SanPham findById(Integer id) {
        return XQuery.getSingleBean(SanPham.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPham> findByMa(String ma) {
        return XQuery.getBeanList(SanPham.class, sqlfindbyma, ma);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}
