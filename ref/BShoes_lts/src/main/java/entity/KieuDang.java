/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author DELL
 *///3. KieuDang
public class KieuDang {
    private int id_kieu_dang;
    private String ma_kieu_dang;
    private String ten_kieu_dang;
    private boolean trang_thai;

    //constructor

    public KieuDang() {
    }

    public KieuDang(int id_kieu_dang, String ma_kieu_dang, String ten_kieu_dang, boolean trang_thai) {
        this.id_kieu_dang = id_kieu_dang;
        this.ma_kieu_dang = ma_kieu_dang;
        this.ten_kieu_dang = ten_kieu_dang;
        this.trang_thai = trang_thai;
    }
    //Getter n Setter

    public int getId_kieu_dang() {
        return id_kieu_dang;
    }

    public void setId_kieu_dang(int id_kieu_dang) {
        this.id_kieu_dang = id_kieu_dang;
    }

    public String getMa_kieu_dang() {
        return ma_kieu_dang;
    }

    public void setMa_kieu_dang(String ma_kieu_dang) {
        this.ma_kieu_dang = ma_kieu_dang;
    }

    public String getTen_kieu_dang() {
        return ten_kieu_dang;
    }

    public void setTen_kieu_dang(String ten_kieu_dang) {
        this.ten_kieu_dang = ten_kieu_dang;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }
    
}
