/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.ThuongHieu;

/**
 *
 * @author dungcc
 */
public interface DAO_ThuongHieu extends CrudDAO<ThuongHieu, Integer>{
    ThuongHieu findByName(String name);
}
