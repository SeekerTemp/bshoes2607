/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.LichSuHoaDon;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface Dao_LichSuHoaDon extends CrudDAO<LichSuHoaDon,Integer> {
    public List<LichSuHoaDon> findAllCancel();
    public List<LichSuHoaDon> findAllByDate(String date);
}
