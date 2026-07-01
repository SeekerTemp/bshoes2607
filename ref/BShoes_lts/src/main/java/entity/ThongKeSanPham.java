/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Nguyen Trung Nghia
 */
public class ThongKeSanPham {
    private String maSP;
    private String tenSP;
    private String loaiSP;
    private String chatLieu;
    private String mauSac;
    private String kichThuoc;
    private int soLuongTon;
    private int soLuongBan;

    private double tongGiaBan;
    private double tongGiaGiam;
    private double doanhThu;

    public ThongKeSanPham() {
    }

    public ThongKeSanPham(String maSP, String tenSP, String loaiSP, String chatLieu, String mauSac, String kichThuoc, int soLuongTon, int soLuongBan, double tongGiaBan, double tongGiaGiam, double doanhThu) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.loaiSP = loaiSP;
        this.chatLieu = chatLieu;
        this.mauSac = mauSac;
        this.kichThuoc = kichThuoc;
        this.soLuongTon = soLuongTon;
        this.soLuongBan = soLuongBan;
        this.tongGiaBan = tongGiaBan;
        this.tongGiaGiam = tongGiaGiam;
        this.doanhThu = doanhThu;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getLoaiSP() {
        return loaiSP;
    }

    public void setLoaiSP(String loaiSP) {
        this.loaiSP = loaiSP;
    }

    public String getChatLieu() {
        return chatLieu;
    }

    public void setChatLieu(String chatLieu) {
        this.chatLieu = chatLieu;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public String getKichThuoc() {
        return kichThuoc;
    }

    public void setKichThuoc(String kichThuoc) {
        this.kichThuoc = kichThuoc;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public int getSoLuongBan() {
        return soLuongBan;
    }

    public void setSoLuongBan(int soLuongBan) {
        this.soLuongBan = soLuongBan;
    }

    public double getTongGiaBan() {
        return tongGiaBan;
    }

    public void setTongGiaBan(double tongGiaBan) {
        this.tongGiaBan = tongGiaBan;
    }

    public double getTongGiaGiam() {
        return tongGiaGiam;
    }

    public void setTongGiaGiam(double tongGiaGiam) {
        this.tongGiaGiam = tongGiaGiam;
    }

    public double getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(double doanhThu) {
        this.doanhThu = doanhThu;
    }
    
}
