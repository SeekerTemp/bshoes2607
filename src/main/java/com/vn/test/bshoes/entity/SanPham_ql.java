package com.vn.test.bshoes.entity;

import java.time.LocalDate;

/**
 *
 * @author dungcc
 */
public class SanPham_ql {

    private int id_san_pham;
    private int id_loai_san_pham;
    private int id_chat_lieu;
    private int id_kieu_dang;
    private int id_kieu_co_giay;
    private int id_kieu_day_giay;
    private int id_thuong_hieu;
    private int id_xuat_su;
    private int id_mau_sac;
    private int id_kich_co;
    private String ma_san_pham;
    private String ten_san_pham;
    private String mo_ta;
    private LocalDate ngay_tao;
    private LocalDate ngay_cap_nhat;
    private boolean trang_thai;
    private boolean trang_thai_xoa;
    private int id_dang_chon;

    public SanPham_ql() {
    }

    public SanPham_ql(int id_loai_san_pham, int id_chat_lieu, int id_kieu_dang, int id_kieu_co_giay, int id_kieu_day_giay, int id_thuong_hieu, int id_xuat_su, int id_mau_sac, int id_kich_co, String ma_san_pham, String ten_san_pham, String mo_ta, LocalDate ngay_tao, LocalDate ngay_cap_nhat, boolean trang_thai, boolean trang_thai_xoa, int id_dang_chon) {
        this.id_loai_san_pham = id_loai_san_pham;
        this.id_chat_lieu = id_chat_lieu;
        this.id_kieu_dang = id_kieu_dang;
        this.id_kieu_co_giay = id_kieu_co_giay;
        this.id_kieu_day_giay = id_kieu_day_giay;
        this.id_thuong_hieu = id_thuong_hieu;
        this.id_xuat_su = id_xuat_su;
        this.id_mau_sac = id_mau_sac;
        this.id_kich_co = id_kich_co;
        this.ma_san_pham = ma_san_pham;
        this.ten_san_pham = ten_san_pham;
        this.mo_ta = mo_ta;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
        this.trang_thai = trang_thai;
        this.trang_thai_xoa = trang_thai_xoa;
        this.id_dang_chon = id_dang_chon;
    }

    public SanPham_ql(int id_san_pham, int id_loai_san_pham, int id_chat_lieu, int id_kieu_dang, int id_kieu_co_giay, int id_kieu_day_giay, int id_thuong_hieu, int id_xuat_su, int id_mau_sac, int id_kich_co, String ma_san_pham, String ten_san_pham, String mo_ta, LocalDate ngay_tao, LocalDate ngay_cap_nhat, boolean trang_thai, boolean trang_thai_xoa, int id_dang_chon) {
        this.id_san_pham = id_san_pham;
        this.id_loai_san_pham = id_loai_san_pham;
        this.id_chat_lieu = id_chat_lieu;
        this.id_kieu_dang = id_kieu_dang;
        this.id_kieu_co_giay = id_kieu_co_giay;
        this.id_kieu_day_giay = id_kieu_day_giay;
        this.id_thuong_hieu = id_thuong_hieu;
        this.id_xuat_su = id_xuat_su;
        this.id_mau_sac = id_mau_sac;
        this.id_kich_co = id_kich_co;
        this.ma_san_pham = ma_san_pham;
        this.ten_san_pham = ten_san_pham;
        this.mo_ta = mo_ta;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
        this.trang_thai = trang_thai;
        this.trang_thai_xoa = trang_thai_xoa;
        this.id_dang_chon = id_dang_chon;
    }

    public int getId_san_pham() {
        return id_san_pham;
    }

    public void setId_san_pham(int id_san_pham) {
        this.id_san_pham = id_san_pham;
    }

    public int getId_loai_san_pham() {
        return id_loai_san_pham;
    }

    public void setId_loai_san_pham(int id_loai_san_pham) {
        this.id_loai_san_pham = id_loai_san_pham;
    }

    public int getId_chat_lieu() {
        return id_chat_lieu;
    }

    public void setId_chat_lieu(int id_chat_lieu) {
        this.id_chat_lieu = id_chat_lieu;
    }

    public int getId_kieu_dang() {
        return id_kieu_dang;
    }

    public void setId_kieu_dang(int id_kieu_dang) {
        this.id_kieu_dang = id_kieu_dang;
    }

    public int getId_kieu_co_giay() {
        return id_kieu_co_giay;
    }

    public void setId_kieu_co_giay(int id_kieu_co_giay) {
        this.id_kieu_co_giay = id_kieu_co_giay;
    }

    public int getId_kieu_day_giay() {
        return id_kieu_day_giay;
    }

    public void setId_kieu_day_giay(int id_kieu_day_giay) {
        this.id_kieu_day_giay = id_kieu_day_giay;
    }

    public int getId_thuong_hieu() {
        return id_thuong_hieu;
    }

    public void setId_thuong_hieu(int id_thuong_hieu) {
        this.id_thuong_hieu = id_thuong_hieu;
    }

    public int getId_xuat_su() {
        return id_xuat_su;
    }

    public void setId_xuat_su(int id_xuat_su) {
        this.id_xuat_su = id_xuat_su;
    }

    public int getId_mau_sac() {
        return id_mau_sac;
    }

    public void setId_mau_sac(int id_mau_sac) {
        this.id_mau_sac = id_mau_sac;
    }

    public int getId_kich_co() {
        return id_kich_co;
    }

    public void setId_kich_co(int id_kich_co) {
        this.id_kich_co = id_kich_co;
    }

    public String getMa_san_pham() {
        return ma_san_pham;
    }

    public void setMa_san_pham(String ma_san_pham) {
        this.ma_san_pham = ma_san_pham;
    }

    public String getTen_san_pham() {
        return ten_san_pham;
    }

    public void setTen_san_pham(String ten_san_pham) {
        this.ten_san_pham = ten_san_pham;
    }

    public String getMo_ta() {
        return mo_ta;
    }

    public void setMo_ta(String mo_ta) {
        this.mo_ta = mo_ta;
    }

    public LocalDate getNgay_tao() {
        return ngay_tao;
    }

    public void setNgay_tao(LocalDate ngay_tao) {
        this.ngay_tao = ngay_tao;
    }

    public LocalDate getNgay_cap_nhat() {
        return ngay_cap_nhat;
    }

    public void setNgay_cap_nhat(LocalDate ngay_cap_nhat) {
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }

    public boolean isTrang_thai_xoa() {
        return trang_thai_xoa;
    }

    public void setTrang_thai_xoa(boolean trang_thai_xoa) {
        this.trang_thai_xoa = trang_thai_xoa;
    }

    public int getId_dang_chon() {
        return id_dang_chon;
    }

    public void setId_dang_chon(int id_dang_chon) {
        this.id_dang_chon = id_dang_chon;
    }

}
