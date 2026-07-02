package com.vn.test.bshoes.entity;

/**
 *
 * @author DELL
 *///10.MauSac
public class MauSac {

    private int id_mau_sac;
    private String ma_mau_sac;
    private String ten_mau_sac;
    private boolean trang_thai;

    //Consstructor

    public MauSac() {
    }

    public MauSac(int id_mau_sac, String ma_mau_sac, String ten_mau_sac, boolean trang_thai) {
        this.id_mau_sac = id_mau_sac;
        this.ma_mau_sac = ma_mau_sac;
        this.ten_mau_sac = ten_mau_sac;
        this.trang_thai = trang_thai;
    }

    // Getters & Setters

    public int getId_mau_sac() {
        return id_mau_sac;
    }

    public void setId_mau_sac(int id_mau_sac) {
        this.id_mau_sac = id_mau_sac;
    }

    public String getMa_mau_sac() {
        return ma_mau_sac;
    }

    public void setMa_mau_sac(String ma_mau_sac) {
        this.ma_mau_sac = ma_mau_sac;
    }

    public String getTen_mau_sac() {
        return ten_mau_sac;
    }

    public void setTen_mau_sac(String ten_mau_sac) {
        this.ten_mau_sac = ten_mau_sac;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }


}
