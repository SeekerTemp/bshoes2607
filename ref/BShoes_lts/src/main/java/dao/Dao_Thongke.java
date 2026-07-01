/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.Nam;
import entity.ThongKeDoanhThu;
import entity.ThongKeSanPham;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Nguyen Trung Nghia
 */
public interface Dao_Thongke {
        List<ThongKeDoanhThu> homNay();
    List<ThongKeDoanhThu> theoNgay(LocalDate from, LocalDate to);
    List<ThongKeDoanhThu> theoThang(int month, int year);
    List<ThongKeDoanhThu> theoNam(int year);
    List<Nam> loatNam();


    // sản phẩm
    List<ThongKeSanPham> tatCaSanPham();
    List<ThongKeSanPham> sanPhamTheoNgay(LocalDate from, LocalDate to);
    List<ThongKeSanPham> sanPhamTheoThang(int month, int year);
}
