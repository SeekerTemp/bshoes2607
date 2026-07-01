/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.KichCo;

/**
 *
 * @author DELL
 */
public interface Dao_KichCo_hd extends CrudDAO<KichCo, Integer> {
    public KichCo findByName(String name);
}
