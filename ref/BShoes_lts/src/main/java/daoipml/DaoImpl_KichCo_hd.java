/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.KichCo;
import java.util.List;
import util.XQuery;
import dao.Dao_KichCo_hd;

/**
 *
 * @author dungcc
 */
public class DaoImpl_KichCo_hd implements Dao_KichCo_hd{
private String sqlFillAll = "select * from kich_co";
private String sqlFillById = "select * from kich_co where id_kich_co = ?";
private String sqlFillByName = "select * from kich_co where ten_kich_co = ?";
    @Override
    public KichCo create(KichCo entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(KichCo entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
