/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author DELL
 *///9.KichCo
public class KichCo {
    private int id_kich_co;
    private String ma_kich_co;
    private String ten_kich_co;
    private boolean trang_thai;

    //Constructor

    public KichCo() {
    }

    public KichCo(int id_kich_co, String ma_kich_co, String ten_kich_co, boolean trang_thai) {
        this.id_kich_co = id_kich_co;
        this.ma_kich_co = ma_kich_co;
        this.ten_kich_co = ten_kich_co;
        this.trang_thai = trang_thai;
    }
    // Getters & Setters

    public int getId_kich_co() {
        return id_kich_co;
    }

    public void setId_kich_co(int id_kich_co) {
        this.id_kich_co = id_kich_co;
    }

    public String getMa_kich_co() {
        return ma_kich_co;
    }

    public void setMa_kich_co(String ma_kich_co) {
        this.ma_kich_co = ma_kich_co;
    }

    public String getTen_kich_co() {
        return ten_kich_co;
    }

    public void setTen_kich_co(String ten_kich_co) {
        this.ten_kich_co = ten_kich_co;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }
    
}
