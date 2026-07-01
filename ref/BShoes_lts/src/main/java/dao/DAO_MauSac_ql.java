/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.MauSac;
import java.util.List;

/**
 *
 * @author dungcc
 */
public interface DAO_MauSac_ql extends CrudDAO<MauSac, Integer>{
    MauSac findByName(String name);
    // Lấy tất cả chất liệu để fill
    List<MauSac> getAll();

    // Lấy chất liệu theo id để fill lên bảng
    MauSac findById(int id);

    

   
}
