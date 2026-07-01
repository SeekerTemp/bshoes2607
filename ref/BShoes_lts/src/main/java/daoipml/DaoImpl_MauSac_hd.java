/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.MauSac;
import java.util.List;
import util.XQuery;
import dao.Dao_MauSac_hd;

/**
 *
 * @author dungcc
 */
public class DaoImpl_MauSac_hd implements Dao_MauSac_hd{
private String sqlFillAll = "select * from mau_sac";
private String sqlFillById = "select * from mau_sac where id_mau_sac = ?";
private String sqlFillByName = "select * from mau_sac where ten_mau_sac = ?";
    @Override
    public MauSac create(MauSac entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(MauSac entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
