/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package controller;

import entity.ThongKeDoanhThu;
import entity.ThongKeSanPham;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Nguyen Trung Nghia
 */
public interface Controller_ThongKe {
    void thongKeHomNay();
    void thongKeTheoNgay(LocalDate from, LocalDate to);
    void thongKeTheoThang(int month, int year);
    void thongKeTheoNam(int year);
    void fillTableSanPham();
    void fillTableDoanhThu(List<ThongKeDoanhThu> list);
}
