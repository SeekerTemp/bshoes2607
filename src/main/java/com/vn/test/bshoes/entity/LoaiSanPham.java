package com.vn.test.bshoes.entity;

/**
 *
 * @author DELL
 *///1. LoaiSanPham
public class LoaiSanPham {
    private int id_loai_san_pham;
    private String ma_loai_san_pham;
    private String ten_loai_san_pham;
    private String mo_ta;
    private Boolean trang_thai_xoa;

    public LoaiSanPham() {
    }

    public LoaiSanPham(String ma_loai_san_pham, String ten_loai_san_pham, String mo_ta, Boolean trang_thai_xoa) {
        this.ma_loai_san_pham = ma_loai_san_pham;
        this.ten_loai_san_pham = ten_loai_san_pham;
        this.mo_ta = mo_ta;
        this.trang_thai_xoa = trang_thai_xoa;
    }

    public LoaiSanPham(int id_loai_san_pham, String ma_loai_san_pham, String ten_loai_san_pham, String mo_ta, Boolean trang_thai_xoa) {
        this.id_loai_san_pham = id_loai_san_pham;
        this.ma_loai_san_pham = ma_loai_san_pham;
        this.ten_loai_san_pham = ten_loai_san_pham;
        this.mo_ta = mo_ta;
        this.trang_thai_xoa = trang_thai_xoa;
    }

    public int getId_loai_san_pham() {
        return id_loai_san_pham;
    }

    public void setId_loai_san_pham(int id_loai_san_pham) {
        this.id_loai_san_pham = id_loai_san_pham;
    }

    public String getMa_loai_san_pham() {
        return ma_loai_san_pham;
    }

    public void setMa_loai_san_pham(String ma_loai_san_pham) {
        this.ma_loai_san_pham = ma_loai_san_pham;
    }

    public String getTen_loai_san_pham() {
        return ten_loai_san_pham;
    }

    public void setTen_loai_san_pham(String ten_loai_san_pham) {
        this.ten_loai_san_pham = ten_loai_san_pham;
    }

    public String getMo_ta() {
        return mo_ta;
    }

    public void setMo_ta(String mo_ta) {
        this.mo_ta = mo_ta;
    }

    public Boolean getTrang_thai_xoa() {
        return trang_thai_xoa != null ? trang_thai_xoa : false;
    }

    public void setTrang_thai_xoa(Boolean trang_thai_xoa) {
        this.trang_thai_xoa = trang_thai_xoa;
    }


}
