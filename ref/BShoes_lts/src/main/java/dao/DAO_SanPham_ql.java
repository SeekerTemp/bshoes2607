/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.SanPham_ql;
import java.util.List;

/**
 *
 * @author dungcc
 */
public interface DAO_SanPham_ql extends CrudDAO<SanPham_ql, Integer>{
    List<SanPham_ql> findByMa(String ma);
     SanPham_ql findByName(String ma);
     public SanPham_ql insertAndReturnId(Integer id);
    
}
