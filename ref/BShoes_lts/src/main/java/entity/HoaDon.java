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
 *///17.HoaDon
public class HoaDon {

    private int id_hoa_don;//	int	Unchecked
    private int id_khach_hang;//	int	Checked
    private int id_phieu_giam_gia;//	int	Checked
    private String ma_hoa_don;//	varchar(20)	Checked
    private BigDecimal tong_tien_ban_dau;//	money	Checked
    private BigDecimal tien_giam_gia;//	money	Checked
    private BigDecimal tong_tien_phai_tra;//	money	Checked
    private String ten_nguoi_nhan;//	nvarchar(100)	Checked
    private String so_dien_thoai;//	nvarchar(20)	Checked
    private String dia_chi;//	nvarchar(255)	Checked
    private String phuong_thuc_thanh_toan;//	nvarchar(50)	Checked
    private String ghi_chu;//	nvarchar(255)	Checked
    private String nguoi_tao_ma;//	nvarchar(50)	Checked
    private String nguoi_cap_nhat;//	nvarchar(50)	Checked
    private Timestamp ngay_tao_ma;//	datetime	Checked
    private Timestamp ngay_cap_nhat;//	datetime	Checked
    private int trang_thai;	//int	Checked
    private boolean loai_hoa_don;//	bit	Checked
    private Integer id_nhan_vien;

public Integer getId_nhan_vien() {
    return id_nhan_vien;
}

public void setId_nhan_vien(Integer id_nhan_vien) {
    this.id_nhan_vien = id_nhan_vien;
}
    //constructor

    public HoaDon() {
    }

public HoaDon(int id_hoa_don, int id_khach_hang, int id_phieu_giam_gia, Integer id_nhan_vien,String ma_hoa_don, BigDecimal tong_tien_ban_dau, BigDecimal tien_giam_gia,BigDecimal tong_tien_phai_tra, String ten_nguoi_nhan, String so_dien_thoai,String dia_chi, String phuong_thuc_thanh_toan, String ghi_chu, String nguoi_tao_ma, String nguoi_cap_nhat,Timestamp ngay_tao_ma, Timestamp ngay_cap_nhat,int trang_thai, boolean loai_hoa_don) {

    this.id_hoa_don = id_hoa_don;
    this.id_khach_hang = id_khach_hang;
    this.id_phieu_giam_gia = id_phieu_giam_gia;
    this.id_nhan_vien = id_nhan_vien; // ⭐ GÁN GIÁ TRỊ
    this.ma_hoa_don = ma_hoa_don;
    this.tong_tien_ban_dau = tong_tien_ban_dau;
    this.tien_giam_gia = tien_giam_gia;
    this.tong_tien_phai_tra = tong_tien_phai_tra;
    this.ten_nguoi_nhan = ten_nguoi_nhan;
    this.so_dien_thoai = so_dien_thoai;
    this.dia_chi = dia_chi;
    this.phuong_thuc_thanh_toan = phuong_thuc_thanh_toan;
    this.ghi_chu = ghi_chu;
    this.nguoi_tao_ma = nguoi_tao_ma;
    this.nguoi_cap_nhat = nguoi_cap_nhat;
    this.ngay_tao_ma = ngay_tao_ma;
    this.ngay_cap_nhat = ngay_cap_nhat;
    this.trang_thai = trang_thai;
    this.loai_hoa_don = loai_hoa_don;
}

    public HoaDon(int i, boolean b) {
        this.trang_thai = i;
        this.loai_hoa_don = b;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getId_hoa_don() {
        return id_hoa_don;
    }

    public void setId_hoa_don(int id_hoa_don) {
        this.id_hoa_don = id_hoa_don;
    }

    public int getId_khach_hang() {
        return id_khach_hang;
    }

    public void setId_khach_hang(int id_khach_hang) {
        this.id_khach_hang = id_khach_hang;
    }

    public int getId_phieu_giam_gia() {
        return id_phieu_giam_gia;
    }

    public void setId_phieu_giam_gia(int id_phieu_giam_gia) {
        this.id_phieu_giam_gia = id_phieu_giam_gia;
    }

    public String getMa_hoa_don() {
        return ma_hoa_don;
    }

    public void setMa_hoa_don(String ma_hoa_don) {
        this.ma_hoa_don = ma_hoa_don;
    }

    public BigDecimal getTong_tien_ban_dau() {
        return tong_tien_ban_dau;
    }

    public void setTong_tien_ban_dau(BigDecimal tong_tien_ban_dau) {
        this.tong_tien_ban_dau = tong_tien_ban_dau;
    }

    public BigDecimal getTien_giam_gia() {
        return tien_giam_gia;
    }

    public void setTien_giam_gia(BigDecimal tien_giam_gia) {
        this.tien_giam_gia = tien_giam_gia;
    }

    public BigDecimal getTong_tien_phai_tra() {
        return tong_tien_phai_tra;
    }

    public void setTong_tien_phai_tra(BigDecimal tong_tien_phai_tra) {
        this.tong_tien_phai_tra = tong_tien_phai_tra;
    }

    public String getTen_nguoi_nhan() {
        return ten_nguoi_nhan;
    }

    public void setTen_nguoi_nhan(String ten_nguoi_nhan) {
        this.ten_nguoi_nhan = ten_nguoi_nhan;
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

    public String getPhuong_thuc_thanh_toan() {
        return phuong_thuc_thanh_toan;
    }

    public void setPhuong_thuc_thanh_toan(String phuong_thuc_thanh_toan) {
        this.phuong_thuc_thanh_toan = phuong_thuc_thanh_toan;
    }

    public String getGhi_chu() {
        return ghi_chu;
    }

    public void setGhi_chu(String ghi_chu) {
        this.ghi_chu = ghi_chu;
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

    public int getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(int trang_thai) {
        this.trang_thai = trang_thai;
    }

    public boolean isLoai_hoa_don() {
        return loai_hoa_don;
    }

    public void setLoai_hoa_don(boolean loai_hoa_don) {
        this.loai_hoa_don = loai_hoa_don;
    }
    
}
