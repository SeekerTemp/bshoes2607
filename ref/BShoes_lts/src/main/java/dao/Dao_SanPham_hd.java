/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.SanPham;
import java.util.List;

/**
 *
 * @author dungcc
 */
public interface Dao_SanPham_hd extends CrudDAO<SanPham, Integer>{
    List<SanPham> findByMa(String ma);
}
