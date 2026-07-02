package com.vn.test.bshoes.entity;

/**
 *
 * @author DELL
 *///4.KieuCoGiay
public class KieuCoGiay {
    private int id_kieu_co_giay;
    private String ma_co_giay;
    private String ten_co_giay;
    private boolean trang_thai;

    //constructor

    public KieuCoGiay() {
    }

    public KieuCoGiay(int id_kieu_co_giay, String ma_co_giay, String ten_co_giay, boolean trang_thai) {
        this.id_kieu_co_giay = id_kieu_co_giay;
        this.ma_co_giay = ma_co_giay;
        this.ten_co_giay = ten_co_giay;
        this.trang_thai = trang_thai;
    }
    // Getters & Setters

    public int getId_kieu_co_giay() {
        return id_kieu_co_giay;
    }

    public void setId_kieu_co_giay(int id_kieu_co_giay) {
        this.id_kieu_co_giay = id_kieu_co_giay;
    }

    public String getMa_co_giay() {
        return ma_co_giay;
    }

    public void setMa_co_giay(String ma_co_giay) {
        this.ma_co_giay = ma_co_giay;
    }

    public String getTen_co_giay() {
        return ten_co_giay;
    }

    public void setTen_co_giay(String ten_co_giay) {
        this.ten_co_giay = ten_co_giay;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }

}
