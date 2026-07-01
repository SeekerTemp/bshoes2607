/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.sql.Timestamp;
/**
 *
 * @author DELL
 *///13.NhanVien
//@Builder
public class NhanVien {
    private int id_nhan_vien;
    private int id_vai_tro;

    private String ma_nhan_vien;
    private String ten_nhan_vien;
    private String tai_khoan;
    private String email;
    private String mat_khau;
    private String cccd;
    private String so_dien_thoai;
    private String dia_chi;
    private String chuc_vu;

    private Timestamp ngay_sinh;
    private String gioi_tinh;
    private String nguoi_tao_ma;
    private String nguoi_cap_nhat_ma;
    private boolean trang_thai;
    private boolean trang_thai_xoa;

    private Timestamp ngay_tao_ma;
    private Timestamp ngay_cap_nhat;
    private String ma_vai_tro;
    private String ten_vai_tro;

    public String getMa_vai_tro() {
        return ma_vai_tro;
    }

    public void setMa_vai_tro(String ma_vai_tro) {
        this.ma_vai_tro = ma_vai_tro;
    }

    public String getTen_vai_tro() {
        return ten_vai_tro;
    }

    public void setTen_vai_tro(String ten_vai_tro) {
        this.ten_vai_tro = ten_vai_tro;
    }
    public NhanVien() {
    }
        public NhanVien(int id_nhan_vien, int id_vai_tro, String ma_nhan_vien, String ten_nhan_vien, String tai_khoan, String email, String mat_khau, String cccd, String so_dien_thoai, String dia_chi, String chuc_vu, Timestamp ngay_sinh, String gioi_tinh, String nguoi_tao_ma, String nguoi_cap_nhat_ma, boolean trang_thai, boolean trang_thai_xoa, Timestamp ngay_tao_ma, Timestamp ngay_cap_nhat) {
        this.id_nhan_vien = id_nhan_vien;
        this.id_vai_tro = id_vai_tro;
        this.ma_nhan_vien = ma_nhan_vien;
        this.ten_nhan_vien = ten_nhan_vien;
        this.tai_khoan = tai_khoan;
        this.email = email;
        this.mat_khau = mat_khau;
        this.cccd = cccd;
        this.so_dien_thoai = so_dien_thoai;
        this.dia_chi = dia_chi;
        this.chuc_vu = chuc_vu;
        this.ngay_sinh = ngay_sinh;
        this.gioi_tinh = gioi_tinh;
        this.nguoi_tao_ma = nguoi_tao_ma;
        this.nguoi_cap_nhat_ma = nguoi_cap_nhat_ma;
        this.trang_thai = trang_thai;
        this.trang_thai_xoa = trang_thai_xoa;
        this.ngay_tao_ma = ngay_tao_ma;
        this.ngay_cap_nhat = ngay_cap_nhat;
    }


    public int getId_nhan_vien() {
        return id_nhan_vien;
    }

    public void setId_nhan_vien(int id_nhan_vien) {
        this.id_nhan_vien = id_nhan_vien;
    }

    public int getId_vai_tro() {
        return id_vai_tro;
    }

    public void setId_vai_tro(int id_vai_tro) {
        this.id_vai_tro = id_vai_tro;
    }

    public String getMa_nhan_vien() {
        return ma_nhan_vien;
    }

    public void setMa_nhan_vien(String ma_nhan_vien) {
        this.ma_nhan_vien = ma_nhan_vien;
    }

    public String getTen_nhan_vien() {
        return ten_nhan_vien;
    }

    public void setTen_nhan_vien(String ten_nhan_vien) {
        this.ten_nhan_vien = ten_nhan_vien;
    }

    public String getTai_khoan() {
        return tai_khoan;
    }

    public void setTai_khoan(String tai_khoan) {
        this.tai_khoan = tai_khoan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMat_khau() {
        return mat_khau;
    }

    public void setMat_khau(String mat_khau) {
        this.mat_khau = mat_khau;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getSo_dien_thoai() {
        return so_dien_thoai;
    }

    public void setSo_dien_thoai(String so_dien_thoai) {
        this.so_dien_thoai = so_dien_thoai;
    }

    public String getDia_chi() {
        return dia_chi;
    }

    public void setDia_chi(String dia_chi) {
        this.dia_chi = dia_chi;
    }

    public String getChuc_vu() {
        return chuc_vu;
    }

    public void setChuc_vu(String chuc_vu) {
        this.chuc_vu = chuc_vu;
    }

    public String getGioi_tinh() {
        return gioi_tinh;
    }

    public void setGioi_tinh(String gioi_tinh) {
        this.gioi_tinh = gioi_tinh;
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
        public Timestamp getNgay_sinh() {
        return ngay_sinh;
    }

    public void setNgay_sinh(Timestamp ngay_sinh) {
        this.ngay_sinh = ngay_sinh;
    }
}
