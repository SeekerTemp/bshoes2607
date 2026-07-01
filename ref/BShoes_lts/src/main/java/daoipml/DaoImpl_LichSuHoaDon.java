/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import dao.Dao_LichSuHoaDon;
import entity.LichSuHoaDon;
import java.util.List;
import util.XQuery;

/**
 *
 * @author PC
 */
public class DaoImpl_LichSuHoaDon implements Dao_LichSuHoaDon{
    private String sqlFindAll = "select *from lich_su_hoa_don where trang_thai =1";
    private String sqlFindAllByDate = "select *from lich_su_hoa_don where ngay_tao_ma like ? or ngay_cap_nhap like ";
    private String sqlFindAllCancel = "select *from lich_su_hoa_don where trang_thai =3";
    private String sqlFindById = "select *from lich_su_hoa_don where id =?";

    @Override
    public LichSuHoaDon create(LichSuHoaDon entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(LichSuHoaDon entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<LichSuHoaDon> findAll() {
        return XQuery.getBeanList(LichSuHoaDon.class, sqlFindAll);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    @Override
    public List<LichSuHoaDon> findAllByDate(String date) {
        return XQuery.getBeanList(LichSuHoaDon.class, sqlFindAllByDate,date,date);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LichSuHoaDon findById(Integer id) {
        return XQuery.getSingleBean(LichSuHoaDon.class, sqlFindById,id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<LichSuHoaDon> findAllCancel() {
        return XQuery.getBeanList(LichSuHoaDon.class, sqlFindAllCancel);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
