package com.vn.test.bshoes.entity;

/**
 *
 * @author DELL
 *///6. ThuongHieu
public class ThuongHieu {

    private int id_thuong_hieu;
    private String ma_thuong_hieu;
    private String ten_thuong_hieu;
    private String mo_ta;
    private boolean trang_thai;
//constructor

    public ThuongHieu() {
    }

    public ThuongHieu(int id_thuong_hieu, String ma_thuong_hieu, String ten_thuong_hieu, String mo_ta, boolean trang_thai) {
        this.id_thuong_hieu = id_thuong_hieu;
        this.ma_thuong_hieu = ma_thuong_hieu;
        this.ten_thuong_hieu = ten_thuong_hieu;
        this.mo_ta = mo_ta;
        this.trang_thai = trang_thai;
    }
    // Getters & Setters

    public int getId_thuong_hieu() {
        return id_thuong_hieu;
    }

    public void setId_thuong_hieu(int id_thuong_hieu) {
        this.id_thuong_hieu = id_thuong_hieu;
    }

    public String getMa_thuong_hieu() {
        return ma_thuong_hieu;
    }

    public void setMa_thuong_hieu(String ma_thuong_hieu) {
        this.ma_thuong_hieu = ma_thuong_hieu;
    }

    public String getTen_thuong_hieu() {
        return ten_thuong_hieu;
    }

    public void setTen_thuong_hieu(String ten_thuong_hieu) {
        this.ten_thuong_hieu = ten_thuong_hieu;
    }

    public String getMo_ta() {
        return mo_ta;
    }

    public void setMo_ta(String mo_ta) {
        this.mo_ta = mo_ta;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }

}
