/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.KieuDayGiay;

/**
 *
 * @author DELL
 */
public interface Dao_KieuDayGiay extends CrudDAO<KieuDayGiay,Integer> {
    KieuDayGiay findByName(String name);
}
