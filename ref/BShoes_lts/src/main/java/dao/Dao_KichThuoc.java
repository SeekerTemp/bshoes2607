/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.KichCo;

/**
 *
 * @author dungcc
 */
public interface Dao_KichThuoc extends CrudDAO<KichCo, Integer>{
    KichCo findByName(String name);
}
