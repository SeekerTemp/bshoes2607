/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.XuatXu;

/**
 *
 * @author dungcc
 */
public interface DAO_XuatXu extends CrudDAO<XuatXu, Integer>{
    XuatXu findByName(String name);
}
