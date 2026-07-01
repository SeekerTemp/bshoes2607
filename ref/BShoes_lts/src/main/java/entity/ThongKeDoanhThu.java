/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;

/**
 *
 * @author Nguyen Trung Nghia
 */
public class ThongKeDoanhThu {
    private int thang;
    private int soSanPhamBan;
    private BigDecimal tongGiaBan;
    private BigDecimal tongGiamGia;
    private BigDecimal tongDoanhThu;
    private int donCho;
    private int soDon;
    private int donThanhCong;
    private int donHuy;

    private BigDecimal doanhThuNgay;
    private BigDecimal doanhThuThang;
    private BigDecimal doanhThuNam;

    public int getSoDon() {
        return soDon;
    }

    public void setSoDon(int soDon) {
        this.soDon = soDon;
    }

    public int getDonThanhCong() {
        return donThanhCong;
    }

    public void setDonThanhCong(int donThanhCong) {
        this.donThanhCong = donThanhCong;
    }

    public int getDonHuy() {
        return donHuy;
    }

    public void setDonHuy(int donHuy) {
        this.donHuy = donHuy;
    }

    public ThongKeDoanhThu() {
    }
    
    public ThongKeDoanhThu(int thang, int soSanPhamBan, BigDecimal tongGiaBan, BigDecimal tongGiamGia, BigDecimal tongDoanhThu) {
        this.thang = thang;
        this.soSanPhamBan = soSanPhamBan;
        this.tongGiaBan = tongGiaBan;
        this.tongGiamGia = tongGiamGia;
        this.tongDoanhThu = tongDoanhThu;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getSoSanPhamBan() {
        return soSanPhamBan;
    }

    public void setSoSanPhamBan(int soSanPhamBan) {
        this.soSanPhamBan = soSanPhamBan;
    }

    public BigDecimal getTongGiaBan() {
        return tongGiaBan;
    }

    public void setTongGiaBan(BigDecimal tongGiaBan) {
        this.tongGiaBan = tongGiaBan;
    }

    public BigDecimal getTongGiamGia() {
        return tongGiamGia;
    }

    public void setTongGiamGia(BigDecimal tongGiamGia) {
        this.tongGiamGia = tongGiamGia;
    }

    public BigDecimal getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(BigDecimal tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public int getDonCho() {
        return donCho;
    }

    public void setDonCho(int donCho) {
        this.donCho = donCho;
    }
    
}
