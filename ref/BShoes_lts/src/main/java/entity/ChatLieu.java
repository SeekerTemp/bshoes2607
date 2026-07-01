/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author DELL
 *///2. ChatLieu
public class ChatLieu {
    private int id_chat_lieu;
    private String ma_chat_lieu;
    private String ten_chat_lieu;
    private boolean trang_thai;

    // constructor
    public ChatLieu() {
    }

    public ChatLieu(int id_chat_lieu, String ma_chat_lieu, String ten_chat_lieu, boolean trang_thai) {
        this.id_chat_lieu = id_chat_lieu;
        this.ma_chat_lieu = ma_chat_lieu;
        this.ten_chat_lieu = ten_chat_lieu;
        this.trang_thai = trang_thai;
    }
    //Getters & Setters

    public int getId_chat_lieu() {
        return id_chat_lieu;
    }

    public void setId_chat_lieu(int id_chat_lieu) {
        this.id_chat_lieu = id_chat_lieu;
    }

    public String getMa_chat_lieu() {
        return ma_chat_lieu;
    }

    public void setMa_chat_lieu(String ma_chat_lieu) {
        this.ma_chat_lieu = ma_chat_lieu;
    }

    public String getTen_chat_lieu() {
        return ten_chat_lieu;
    }

    public void setTen_chat_lieu(String ten_chat_lieu) {
        this.ten_chat_lieu = ten_chat_lieu;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }

}
