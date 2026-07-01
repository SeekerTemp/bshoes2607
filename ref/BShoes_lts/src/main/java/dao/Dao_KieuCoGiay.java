/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.KieuCoGiay;

/**
 *
 * @author dungcc
 */
public interface Dao_KieuCoGiay extends CrudDAO<KieuCoGiay, Integer>{
    KieuCoGiay findByName(String name);
}
