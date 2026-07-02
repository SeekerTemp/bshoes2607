package com.vn.test.bshoes.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author DELL
 *///11.SanPhamChiTiet
public class SanPhamChiTiet {
    private int id_san_pham_chi_tiet;
    private int id_san_pham;
    private int id_kich_co;
    private int id_mau_sac;

    private String ma_san_pham_chi_tiet;
    private int so_luong_ton;
    private BigDecimal don_gia;

    private Timestamp ngay_tao;
    private Timestamp ngay_cap_nhat;
    private String nguoi_tao;
    private String nguoi_cap_nhat;
    private boolean trang_thai;

    //constructor

    public SanPhamChiTiet() {
    }

    public SanPhamChiTiet(int id_san_pham_chi_tiet, int id_san_pham, int id_kich_co, int id_mau_sac, String ma_san_pham_chi_tiet, int so_luong_ton, BigDecimal don_gia, Timestamp ngay_tao, Timestamp ngay_cap_nhat, String nguoi_tao, String nguoi_cap_nhat, boolean trang_thai) {
        this.id_san_pham_chi_tiet = id_san_pham_chi_tiet;
        this.id_san_pham = id_san_pham;
        this.id_kich_co = id_kich_co;
        this.id_mau_sac = id_mau_sac;
        this.ma_san_pham_chi_tiet = ma_san_pham_chi_tiet;
        this.so_luong_ton = so_luong_ton;
        this.don_gia = don_gia;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
        this.nguoi_tao = nguoi_tao;
        this.nguoi_cap_nhat = nguoi_cap_nhat;
        this.trang_thai = trang_thai;
    }

    public SanPhamChiTiet(int idspct, int i) {
        this.id_san_pham_chi_tiet = idspct;
        this.so_luong_ton = i;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public SanPhamChiTiet(int idspct) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    //getnset

    public int getId_san_pham_chi_tiet() {
        return id_san_pham_chi_tiet;
    }

    public void setId_san_pham_chi_tiet(int id_san_pham_chi_tiet) {
        this.id_san_pham_chi_tiet = id_san_pham_chi_tiet;
    }

    public int getId_san_pham() {
        return id_san_pham;
    }

    public void setId_san_pham(int id_san_pham) {
        this.id_san_pham = id_san_pham;
    }

    public int getId_kich_co() {
        return id_kich_co;
    }

    public void setId_kich_co(int id_kich_co) {
        this.id_kich_co = id_kich_co;
    }

    public int getId_mau_sac() {
        return id_mau_sac;
    }

    public void setId_mau_sac(int id_mau_sac) {
        this.id_mau_sac = id_mau_sac;
    }

    public String getMa_san_pham_chi_tiet() {
        return ma_san_pham_chi_tiet;
    }

    public void setMa_san_pham_chi_tiet(String ma_san_pham_chi_tiet) {
        this.ma_san_pham_chi_tiet = ma_san_pham_chi_tiet;
    }

    public int getSo_luong_ton() {
        return so_luong_ton;
    }

    public void setSo_luong_ton(int so_luong_ton) {
        this.so_luong_ton = so_luong_ton;
    }

    public BigDecimal getDon_gia() {
        return don_gia;
    }

    public void setDon_gia(BigDecimal don_gia) {
        this.don_gia = don_gia;
    }

    public Timestamp getNgay_tao() {
        return ngay_tao;
    }

    public void setNgay_tao(Timestamp ngay_tao) {
        this.ngay_tao = ngay_tao;
    }

    public Timestamp getNgay_cap_nhat() {
        return ngay_cap_nhat;
    }

    public void setNgay_cap_nhat(Timestamp ngay_cap_nhat) {
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public String getNguoi_tao() {
        return nguoi_tao;
    }

    public void setNguoi_tao(String nguoi_tao) {
        this.nguoi_tao = nguoi_tao;
    }

    public String getNguoi_cap_nhat() {
        return nguoi_cap_nhat;
    }

    public void setNguoi_cap_nhat(String nguoi_cap_nhat) {
        this.nguoi_cap_nhat = nguoi_cap_nhat;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }

}
