/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import dao.Dao_PhieuGiamGia;
import entity.PhieuGiamGia;
import java.sql.ResultSet;
import java.util.List;
import util.XJdbc;
import util.XQuery;

/**
 *
 * @author PC
 */
public class DaoImpl_PhieuGiamGia implements Dao_PhieuGiamGia{
  String sqlFindAll = "select id_phieu_giam_gia,ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai from phieu_giam_gia where trang_thai_xoa = 0";
    String sqlTK = "select id_phieu_giam_gia,ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai from phieu_giam_gia where trang_thai_xoa = 0 and ten_phieu_giam like ?";
  String sqlFindID = "select id_phieu_giam_gia,ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai from phieu_giam_gia where id_phieu_giam_gia= ? and trang_thai_xoa = 0";
  String sqlCreate = "insert into phieu_giam_gia(ma_phieu_giam,ten_phieu_giam,loai_giam_gia,gia_tri_giam,don_toi_thieu,giam_toi_da,so_luong,thoi_gian_bat_dau,thoi_gian_ket_thuc,trang_thai) values (?,?,?,?,?,?,?,?,?,?)";
  String sqlUpdate="UPDATE phieu_giam_gia set ten_phieu_giam=?,loai_giam_gia=?,gia_tri_giam=?,don_toi_thieu=?,giam_toi_da=?,so_luong=?,thoi_gian_bat_dau=?,thoi_gian_ket_thuc=?,trang_thai=? where ma_phieu_giam=?";
  String sqlDelete="update phieu_giam_gia set trang_thai_xoa = 1 where id_phieu_giam_gia=?";
   String sqlTg = "EXEC sp_cap_nhat_trang_thai_phieu_giam_gia";
    @Override
    public PhieuGiamGia create(PhieuGiamGia entity) {
        XJdbc.executeUpdate(sqlCreate,
        entity.getMa_phieu_giam(),
        entity.getTen_phieu_giam(),
        entity.getLoai_giam_gia(),
        entity.getGia_tri_giam(),
        entity.getDon_toi_thieu(),
        entity.getGiam_toi_da(),
        entity.getSo_luong(),
        entity.getThoi_gian_bat_dau(),
        entity.getThoi_gian_ket_thuc(),
        entity.isTrang_thai()
    );
    return null;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(PhieuGiamGia entity) {
        XJdbc.executeUpdate(sqlUpdate,
        entity.getTen_phieu_giam(),
        entity.getLoai_giam_gia(),
        entity.getGia_tri_giam(),
        entity.getDon_toi_thieu(),
        entity.getGiam_toi_da(),
        entity.getSo_luong(),
        entity.getThoi_gian_bat_dau(),
        entity.getThoi_gian_ket_thuc(),
        entity.isTrang_thai(),
        entity.getMa_phieu_giam()
    );
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqlDelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<PhieuGiamGia> findAll() {
         return XQuery.getBeanList(PhieuGiamGia.class, sqlFindAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PhieuGiamGia findById(Integer id) {
        return XQuery.getSingleBean(PhieuGiamGia.class, sqlFindID, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<PhieuGiamGia> findByName(String name) {
        return XQuery.getBeanList(PhieuGiamGia.class,sqlTK,"%"+name+"%");
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
        // Check trùng mã phiếu
        public boolean existsMa(String ma) {
            String sql = "SELECT COUNT(*) FROM phieu_giam_gia WHERE ma_phieu_giam = ?";
            try (ResultSet rs = XJdbc.executeQuery(sql, ma)) {
                return rs.next() && rs.getInt(1) > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // Check trùng tên phiếu
        public boolean existsTen(String ten) {
            String sql = "SELECT COUNT(*) FROM phieu_giam_gia WHERE ten_phieu_giam = ?";
            try (ResultSet rs = XJdbc.executeQuery(sql, ten)) {
                return rs.next() && rs.getInt(1) > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    public void capNhatTrangThai() {
        XJdbc.executeUpdate(sqlTg);    
    }
}
