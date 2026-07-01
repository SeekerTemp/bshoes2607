/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.LoaiSanPham;

/**
 *
 * @author dungcc
 */
public interface DAO_LoaiSanPham extends CrudDAO<LoaiSanPham, Integer>{
    LoaiSanPham findByName(String name);
}
