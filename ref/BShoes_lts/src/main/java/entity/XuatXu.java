/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author DELL
 *///7. XuatSu
public class XuatXu {

    private int id_xuat_su;
    private String ma_xuat_su;
    private String ten_xuat_su;
    private String mo_ta;
    private boolean trang_thai;

    //Constructor

    public XuatXu() {
    }

    public XuatXu(int id_xuat_su, String ma_xuat_su, String ten_xuat_su, String mo_ta, boolean trang_thai) {
        this.id_xuat_su = id_xuat_su;
        this.ma_xuat_su = ma_xuat_su;
        this.ten_xuat_su = ten_xuat_su;
        this.mo_ta = mo_ta;
        this.trang_thai = trang_thai;
    }
    // Getters & Setters

    public int getId_xuat_su() {
        return id_xuat_su;
    }

    public void setId_xuat_su(int id_xuat_su) {
        this.id_xuat_su = id_xuat_su;
    }

    public String getMa_xuat_su() {
        return ma_xuat_su;
    }

    public void setMa_xuat_su(String ma_xuat_su) {
        this.ma_xuat_su = ma_xuat_su;
    }

    public String getTen_xuat_su() {
        return ten_xuat_su;
    }

    public void setTen_xuat_su(String ten_xuat_su) {
        this.ten_xuat_su = ten_xuat_su;
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
