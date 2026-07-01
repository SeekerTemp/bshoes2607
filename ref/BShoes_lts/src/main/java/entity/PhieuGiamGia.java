/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author DELL
 *///16.PhieuGiamGia
public class PhieuGiamGia {

    private int id_phieu_giam_gia;
    private String ma_phieu_giam;
    private String ten_phieu_giam;
    private int loai_giam_gia;

    private BigDecimal gia_tri_giam;
    private BigDecimal don_toi_thieu;
    private BigDecimal giam_toi_da;

    private int so_luong;
    private Timestamp thoi_gian_bat_dau;
    private Timestamp thoi_gian_ket_thuc;
    private boolean trang_thai;

    private boolean trang_thai_xoa;
    private String nguoi_tao_ma;
    private String nguoi_cap_nhat_ma;
    private Timestamp ngay_tao_ma;
    private Timestamp ngay_cap_nhat;
    
    public PhieuGiamGia() {
    }


    public int getId_phieu_giam_gia() {
        return id_phieu_giam_gia;
    }

    public PhieuGiamGia(int id_phieu_giam_gia, String ma_phieu_giam, String ten_phieu_giam, int loai_giam_gia, BigDecimal gia_tri_giam, BigDecimal don_toi_thieu, BigDecimal giam_toi_da, int so_luong, Timestamp thoi_gian_bat_dau, Timestamp thoi_gian_ket_thuc, boolean trang_thai, boolean trang_thai_xoa, String nguoi_tao_ma, String nguoi_cap_nhat_ma, Timestamp ngay_tao_ma, Timestamp ngay_cap_nhat) {
        this.id_phieu_giam_gia = id_phieu_giam_gia;
        this.ma_phieu_giam = ma_phieu_giam;
        this.ten_phieu_giam = ten_phieu_giam;
        this.loai_giam_gia = loai_giam_gia;
        this.gia_tri_giam = gia_tri_giam;
        this.don_toi_thieu = don_toi_thieu;
        this.giam_toi_da = giam_toi_da;
        this.so_luong = so_luong;
        this.thoi_gian_bat_dau = thoi_gian_bat_dau;
        this.thoi_gian_ket_thuc = thoi_gian_ket_thuc;
        this.trang_thai = trang_thai;
        this.trang_thai_xoa = trang_thai_xoa;
        this.nguoi_tao_ma = nguoi_tao_ma;
        this.nguoi_cap_nhat_ma = nguoi_cap_nhat_ma;
        this.ngay_tao_ma = ngay_tao_ma;
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public void setId_phieu_giam_gia(int id_phieu_giam_gia) {
        this.id_phieu_giam_gia = id_phieu_giam_gia;
    }

    public String getMa_phieu_giam() {
        return ma_phieu_giam;
    }

    public void setMa_phieu_giam(String ma_phieu_giam) {
        this.ma_phieu_giam = ma_phieu_giam;
    }

    public String getTen_phieu_giam() {
        return ten_phieu_giam;
    }

    public void setTen_phieu_giam(String ten_phieu_giam) {
        this.ten_phieu_giam = ten_phieu_giam;
    }

    public BigDecimal getGia_tri_giam() {
        return gia_tri_giam;
    }

    public void setGia_tri_giam(BigDecimal gia_tri_giam) {
        this.gia_tri_giam = gia_tri_giam;
    }

    public BigDecimal getDon_toi_thieu() {
        return don_toi_thieu;
    }

    public void setDon_toi_thieu(BigDecimal don_toi_thieu) {
        this.don_toi_thieu = don_toi_thieu;
    }

    public BigDecimal getGiam_toi_da() {
        return giam_toi_da;
    }

    public void setGiam_toi_da(BigDecimal giam_toi_da) {
        this.giam_toi_da = giam_toi_da;
    }

    public int getSo_luong() {
        return so_luong;
    }

    public void setSo_luong(int so_luong) {
        this.so_luong = so_luong;
    }

    public Timestamp getThoi_gian_bat_dau() {
        return thoi_gian_bat_dau;
    }

    public void setThoi_gian_bat_dau(Timestamp thoi_gian_bat_dau) {
        this.thoi_gian_bat_dau = thoi_gian_bat_dau;
    }

    public Timestamp getThoi_gian_ket_thuc() {
        return thoi_gian_ket_thuc;
    }

    public void setThoi_gian_ket_thuc(Timestamp thoi_gian_ket_thuc) {
        this.thoi_gian_ket_thuc = thoi_gian_ket_thuc;
    }

    public boolean isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(boolean trang_thai) {
        this.trang_thai = trang_thai;
    }

    public String getNguoi_tao_ma() {
        return nguoi_tao_ma;
    }

    public void setNguoi_tao_ma(String nguoi_tao_ma) {
        this.nguoi_tao_ma = nguoi_tao_ma;
    }

    public String getNguoi_cap_nhat_ma() {
        return nguoi_cap_nhat_ma;
    }

    public void setNguoi_cap_nhat_ma(String nguoi_cap_nhat_ma) {
        this.nguoi_cap_nhat_ma = nguoi_cap_nhat_ma;
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

    public int getLoai_giam_gia() {
        return loai_giam_gia;
    }

    public void setLoai_giam_gia(int loai_giam_gia) {
        this.loai_giam_gia = loai_giam_gia;
    }
        public boolean isTrang_thai_xoa() {
        return trang_thai_xoa;
    }

    public void setTrang_thai_xoa(boolean trang_thai_xoa) {
        this.trang_thai_xoa = trang_thai_xoa;
    }
}
