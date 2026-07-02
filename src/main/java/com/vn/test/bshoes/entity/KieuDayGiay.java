package com.vn.test.bshoes.entity;

/**
 *
 * @author DELL
 *///5. KieuDayGiay
public class KieuDayGiay {
    private int id_kieu_day_giay;
    private String ma_day_giay;
    private String ten_day_giay;
    private boolean trang_thai;

    //constructor

    public KieuDayGiay() {
    }

    public KieuDayGiay(int id_kieu_day_giay, String ma_day_giay, String ten_day_giay, boolean trang_thai) {
        this.id_kieu_day_giay = id_kieu_day_giay;
        this.ma_day_giay = ma_day_giay;
        this.ten_day_giay = ten_day_giay;
        this.trang_thai = trang_thai;
    }
    // Getters & Setters

    public int getId_kieu_day_giay() {
        return id_kieu_day_giay;
    }

    public void setId_kieu_day_giay(int id_kieu_day_giay) {
        this.id_kieu_day_giay = id_kieu_day_giay;
    }

    public String getMa_day_giay() {
        return ma_day_giay;
    }

    public void setMa_day_giay(String ma_day_giay) {
        this.ma_day_giay = ma_day_giay;
    }

    public String getTen_day_giay() {
        return ten_day_giay;
    }

    public void setTen_day_giay(String ten_day_giay) {
        this.ten_day_giay = ten_day_giay;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }

}
