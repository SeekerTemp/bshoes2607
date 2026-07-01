/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.KieuDang;

/**
 *
 * @author dungcc
 */
public interface Dao_KieuDang extends CrudDAO<KieuDang, Integer>{
    KieuDang findByName(String name);
}
