/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.KhachHang;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Quynh Anh
 */
public interface Dao_KhachHang extends CrudDAO<KhachHang, Integer> {
    List<KhachHang> findByName(String name);
    public KhachHang findBySDT(String sdt);
}
