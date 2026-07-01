/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.SanPham_ql;
import entity.SanPhamChiTiet_ql;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface Dao_SanPhamChiTiet_ql extends CrudDAO<SanPhamChiTiet_ql,Integer>{
    List<SanPhamChiTiet_ql>findallbyid(Integer id);
         List<SanPhamChiTiet_ql> findByMa(String ma);
}
