/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.NhanVien;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface Dao_NhanVien extends CrudDAO< NhanVien, Integer> {
    List<NhanVien> findByName(String name, String gender);}
