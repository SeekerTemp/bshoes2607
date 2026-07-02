package com.vn.test.bshoes.entity;

import java.sql.Timestamp;

/**
 *
 * @author DELL
 *///19.LichSuHoaDon
public class LichSuHoaDon {

    private int id;
    private int id_nhan_vien;
    private int id_hoa_don;

    private String ghi_chu;
    private Timestamp thoi_gian_thay_doi;

    private String nguoi_tao_ma;
    private String nguoi_cap_nhat;

    private Timestamp ngay_tao_ma;
    private Timestamp ngay_cap_nhat;

    private boolean trang_thai;
    //Constructor

    public LichSuHoaDon() {
    }

    public LichSuHoaDon(int id, int id_nhan_vien, int id_hoa_don, String ghi_chu, Timestamp thoi_gian_thay_doi, String nguoi_tao_ma, String nguoi_cap_nhat, Timestamp ngay_tao_ma, Timestamp ngay_cap_nhat, boolean trang_thai) {
        this.id = id;
        this.id_nhan_vien = id_nhan_vien;
        this.id_hoa_don = id_hoa_don;
        this.ghi_chu = ghi_chu;
        this.thoi_gian_thay_doi = thoi_gian_thay_doi;
        this.nguoi_tao_ma = nguoi_tao_ma;
        this.nguoi_cap_nhat = nguoi_cap_nhat;
        this.ngay_tao_ma = ngay_tao_ma;
        this.ngay_cap_nhat = ngay_cap_nhat;
        this.trang_thai = trang_thai;
    }

    //getters and settes

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_nhan_vien() {
        return id_nhan_vien;
    }

    public void setId_nhan_vien(int id_nhan_vien) {
        this.id_nhan_vien = id_nhan_vien;
    }

    public int getId_hoa_don() {
        return id_hoa_don;
    }

    public void setId_hoa_don(int id_hoa_don) {
        this.id_hoa_don = id_hoa_don;
    }

    public String getGhi_chu() {
        return ghi_chu;
    }

    public void setGhi_chu(String ghi_chu) {
        this.ghi_chu = ghi_chu;
    }

    public Timestamp getThoi_gian_thay_doi() {
        return thoi_gian_thay_doi;
    }

    public void setThoi_gian_thay_doi(Timestamp thoi_gian_thay_doi) {
        this.thoi_gian_thay_doi = thoi_gian_thay_doi;
    }

    public String getNguoi_tao_ma() {
        return nguoi_tao_ma;
    }

    public void setNguoi_tao_ma(String nguoi_tao_ma) {
        this.nguoi_tao_ma = nguoi_tao_ma;
    }

    public String getNguoi_cap_nhat() {
        return nguoi_cap_nhat;
    }

    public void setNguoi_cap_nhat(String nguoi_cap_nhat) {
        this.nguoi_cap_nhat = nguoi_cap_nhat;
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
