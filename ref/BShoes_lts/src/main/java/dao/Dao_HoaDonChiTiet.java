/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.HoaDonChiTiet;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface Dao_HoaDonChiTiet extends CrudDAO<HoaDonChiTiet, Integer> {
    public HoaDonChiTiet createNew(HoaDonChiTiet entity);
    
    public List<HoaDonChiTiet> findAllByID(Integer id);
    
    public void updateStock(HoaDonChiTiet entity);

    List<HoaDonChiTiet> timhdct(int id);

    List<HoaDonChiTiet> timtenspcuahdct(int id, String ten);

    List<HoaDonChiTiet> findAllGioHang(int id);

    HoaDonChiTiet findidhdctbyidhdvaidspct(int idhd, int idspct);

    List<HoaDonChiTiet> findbytimkiemallgiohang(int id);
    List<HoaDonChiTiet> findBYHoaDon(int idHD);
}
