/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.NhanVien;

/**
 *
 * @author PC
 */
public interface Dao_DangNhap extends CrudDAO<NhanVien, Integer>{
  
    String layMatKhauTheoTaiKhoan(String taiKhoan);

   
    String layVaiTroTheoTaiKhoan(String taiKhoan);
    NhanVien login(String taiKhoan, String matKhau);
}
