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
 *///18.HoaDon
public class HoaDonChiTiet {

    private int id_hoa_don_chi_tiet;
    private int id_san_pham_chi_tiet;
    private int id_hoa_don;
    private BigDecimal gia_giam;
    private int so_luong;
    private Timestamp ngay_tao_ma;
    private Timestamp ngay_cap_nhat;
    private String nguoi_tao;
    private String nguoi_cap_nhat;
    private boolean trang_thai;
    private boolean trang_thai_xoa;
    private BigDecimal thanh_tien;
    private BigDecimal don_gia;
    private String ten_san_pham;

    public BigDecimal getDon_gia() {
        return don_gia;
    }

    public void setDon_gia(BigDecimal don_gia) {
        this.don_gia = don_gia;
    }

    public String getTen_san_pham() {
        return ten_san_pham;
    }

    public void setTen_san_pham(String ten_san_pham) {
        this.ten_san_pham = ten_san_pham;
    }

    //constuctor

    public HoaDonChiTiet() {
    }

    public HoaDonChiTiet(int id_hoa_don_chi_tiet, int id_san_pham_chi_tiet, int id_hoa_don, BigDecimal gia_giam, int so_luong, Timestamp ngay_tao_ma, Timestamp ngay_cap_nhat, String nguoi_tao, String nguoi_cap_nhat, boolean trang_thai, boolean trang_thai_xoa, BigDecimal thanh_tien) {
        this.id_hoa_don_chi_tiet = id_hoa_don_chi_tiet;
        this.id_san_pham_chi_tiet = id_san_pham_chi_tiet;
        this.id_hoa_don = id_hoa_don;
        this.gia_giam = gia_giam;
        this.so_luong = so_luong;
        this.ngay_tao_ma = ngay_tao_ma;
        this.ngay_cap_nhat = ngay_cap_nhat;
        this.nguoi_tao = nguoi_tao;
        this.nguoi_cap_nhat = nguoi_cap_nhat;
        this.trang_thai = trang_thai;
        this.trang_thai_xoa = trang_thai_xoa;
        this.thanh_tien = thanh_tien;
    }


    public HoaDonChiTiet(int id_san_pham_chi_tiet, int id_hoa_don, int so_luong, String nguoi_tao, String nguoi_cap_nhat, BigDecimal thanh_tien) {
        this.id_san_pham_chi_tiet = id_san_pham_chi_tiet;
        this.id_hoa_don = id_hoa_don;
        this.so_luong = so_luong;
        this.nguoi_tao = nguoi_tao;
        this.nguoi_cap_nhat = nguoi_cap_nhat;
        this.thanh_tien = thanh_tien;
    }

    public HoaDonChiTiet(int idHdct,int slGio) {
        this.id_hoa_don_chi_tiet = idHdct;
        this.so_luong = slGio;
    }
   
    
    //getter and setter

    public int getId_hoa_don_chi_tiet() {
        return id_hoa_don_chi_tiet;
    }

    public void setId_hoa_don_chi_tiet(int id_hoa_don_chi_tiet) {
        this.id_hoa_don_chi_tiet = id_hoa_don_chi_tiet;
    }

    public int getId_san_pham_chi_tiet() {
        return id_san_pham_chi_tiet;
    }

    public void setId_san_pham_chi_tiet(int id_san_pham_chi_tiet) {
        this.id_san_pham_chi_tiet = id_san_pham_chi_tiet;
    }

    public int getId_hoa_don() {
        return id_hoa_don;
    }

    public void setId_hoa_don(int id_hoa_don) {
        this.id_hoa_don = id_hoa_don;
    }

    public BigDecimal getGia_giam() {
        return gia_giam;
    }

    public void setGia_giam(BigDecimal gia_giam) {
        this.gia_giam = gia_giam;
    }

    public int getSo_luong() {
        return so_luong;
    }

    public void setSo_luong(int so_luong) {
        this.so_luong = so_luong;
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

    public BigDecimal getThanh_tien() {
        return thanh_tien;
    }

    public void setThanh_tien(BigDecimal thanh_tien) {
        this.thanh_tien = thanh_tien;
    }

    public boolean isTrang_thai_xoa() {
        return trang_thai_xoa;
    }

    public void setTrang_thai_xoa(boolean trang_thai_xoa) {
        this.trang_thai_xoa = trang_thai_xoa;
    }
    
    
    
}
