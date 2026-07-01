/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.HoaDon;
import entity.PhieuGiamGia;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface Dao_HoaDon extends CrudDAO<HoaDon, Integer> {
    public HoaDon findByMa(String madh);
    public void updateTrangThaiThanhToan(int idHoaDon);
    List<PhieuGiamGia> getPGGHoatDong();
}
