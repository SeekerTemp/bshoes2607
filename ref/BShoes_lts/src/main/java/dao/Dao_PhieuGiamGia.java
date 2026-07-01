/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.PhieuGiamGia;
import java.util.List;

/**
 *
 * @author Nguyen Trung Nghia
 */
public interface Dao_PhieuGiamGia extends CrudDAO<PhieuGiamGia, Integer>{
      List<PhieuGiamGia> findByName(String name);
}
