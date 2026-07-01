/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.sql.Timestamp;

/**
 *
 * @author DELL
 *///14.KhachHang
public class KhachHang {
    private int id_khach_hang;
    private String ma_khach_hang;
    private String ten_khach_hang;
    private String gioi_tinh;
    private String so_dien_thoai;
    private String dia_chi;
    private String email;

    private String nguoi_tao_ma;
    private String nguoi_cap_nhat_ma;
    private Timestamp ngay_tao_ma;
    private Timestamp ngay_cap_nhat;

    private boolean trang_thai;

    //Constructor

    public KhachHang() {
    }

    public KhachHang(int id_khach_hang, String ma_khach_hang, String ten_khach_hang, String gioi_tinh, String so_dien_thoai, String dia_chi, String email, String nguoi_tao_ma, String nguoi_cap_nhat_ma, Timestamp ngay_tao_ma, Timestamp ngay_cap_nhat, boolean trang_thai) {
        this.id_khach_hang = id_khach_hang;
        this.ma_khach_hang = ma_khach_hang;
        this.ten_khach_hang = ten_khach_hang;
        this.gioi_tinh = gioi_tinh;
        this.so_dien_thoai = so_dien_thoai;
        this.dia_chi = dia_chi;
        this.email = email;
        this.nguoi_tao_ma = nguoi_tao_ma;
        this.nguoi_cap_nhat_ma = nguoi_cap_nhat_ma;
        this.ngay_tao_ma = ngay_tao_ma;
        this.ngay_cap_nhat = ngay_cap_nhat;
        this.trang_thai = trang_thai;
    }
    
    // Getters & Setters

    public int getId_khach_hang() {
        return id_khach_hang;
    }

    public void setId_khach_hang(int id_khach_hang) {
        this.id_khach_hang = id_khach_hang;
    }

    public String getMa_khach_hang() {
        return ma_khach_hang;
    }

    public void setMa_khach_hang(String ma_khach_hang) {
        this.ma_khach_hang = ma_khach_hang;
    }

    public String getTen_khach_hang() {
        return ten_khach_hang;
    }

    public void setTen_khach_hang(String ten_khach_hang) {
        this.ten_khach_hang = ten_khach_hang;
    }

    public String getGioi_tinh() {
        return gioi_tinh;
    }

    public void setGioi_tinh(String gioi_tinh) {
        this.gioi_tinh = gioi_tinh;
    }

    public String getSo_dien_thoai() {
        return so_dien_thoai;
    }

    public void setSo_dien_thoai(String so_dien_thoai) {
        this.so_dien_thoai = so_dien_thoai;
    }

    public String getDia_chi() {
        return dia_chi;
    }

    public void setDia_chi(String dia_chi) {
        this.dia_chi = dia_chi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNguoi_tao_ma() {
        return nguoi_tao_ma;
    }

    public void setNguoi_tao_ma(String nguoi_tao_ma) {
        this.nguoi_tao_ma = nguoi_tao_ma;
    }

    public String getNguoi_cap_nhat_ma() {
        return nguoi_cap_nhat_ma;
    }

    public void setNguoi_cap_nhat_ma(String nguoi_cap_nhat_ma) {
        this.nguoi_cap_nhat_ma = nguoi_cap_nhat_ma;
    }

    public Timestamp getNgay_tao_ma() {
        return ngay_tao_ma;
    }

    public void setNgay_tao_ma(Timestamp ngay_tao_ma) {
        this.ngay_tao_ma = ngay_tao_ma;
    }

    public Timestamp getNgay_cap_nhat() {
        return ngay_cap_nhat;
    }

    public void setNgay_cap_nhat(Timestamp ngay_cap_nhat) {
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }
    
}
