/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.SanPham_ql;
import java.util.List;
import util.XJdbc;
import util.XQuery;
import dao.DAO_SanPham_ql;

/**
 *
 * @author dungcc
 */
public class DaoImpl_SanPham_ql implements DAO_SanPham_ql {

    private String sqlFillAll
            = "SELECT san_pham.id_san_pham, san_pham.ma_san_pham,loai_san_pham.id_loai_san_pham, san_pham.ten_san_pham, san_pham.id_chat_lieu, san_pham.id_kieu_dang, san_pham.id_kieu_co_giay, san_pham.id_kieu_day_giay, san_pham.id_thuong_hieu, san_pham.id_xuat_su, san_pham.mo_ta, san_pham.trang_thai\n"
            + "FROM   loai_san_pham INNER JOIN\n"
            + "             san_pham ON loai_san_pham.id_loai_san_pham = san_pham.id_loai_san_pham\n"
            + "WHERE san_pham.trang_thai_xoa = 0";
    private String sqlRecycle
            = "SELECT san_pham.id_san_pham, san_pham.ma_san_pham, loai_san_pham.id_loai_san_pham, "
            + "san_pham.ten_san_pham, san_pham.id_chat_lieu, san_pham.id_kieu_dang, "
            + "san_pham.id_kieu_co_giay, san_pham.id_kieu_day_giay, san_pham.id_thuong_hieu, "
            + "san_pham.id_xuat_su, san_pham.mo_ta, san_pham.trang_thai "
            + "FROM loai_san_pham INNER JOIN san_pham "
            + "ON loai_san_pham.id_loai_san_pham = san_pham.id_loai_san_pham "
            + "WHERE san_pham.trang_thai_xoa = 1";

    private String sqlFillById = "SELECT san_pham.id_san_pham, san_pham.ma_san_pham,loai_san_pham.id_loai_san_pham, san_pham.ten_san_pham, san_pham.id_chat_lieu, san_pham.id_kieu_dang, san_pham.id_kieu_co_giay, san_pham.id_kieu_day_giay, san_pham.id_thuong_hieu, san_pham.id_xuat_su, san_pham.mo_ta, san_pham.trang_thai\n"
            + "FROM   loai_san_pham INNER JOIN\n"
            + "             san_pham ON loai_san_pham.id_loai_san_pham = san_pham.id_loai_san_pham\n"
            + "where san_pham.id_san_pham =?";
//private String sqlcreate = "insert into san_pham(ma_san_pham, id_loai_san_pham,ten_san_pham, id_chat_lieu, id_kieu_dang, id_kieu_co_giay, id_kieu_day_giay, id_thuong_hieu, id_xuat_su,trang_thai,mo_ta) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
//private String sqlcreate = "EXEC sp_insert_san_pham_with_placeholder ?,?,?,?,?,?,?,?,?,?,?";
    private String sqlupdate = "update san_pham set ten_san_pham=?, id_chat_lieu=?, id_kieu_dang=?, id_kieu_co_giay=?, id_kieu_day_giay=?, id_thuong_hieu=?, id_xuat_su=? where ma_san_pham = ?";
    private String sqlSoftDelete
            = "UPDATE san_pham SET trang_thai_xoa = 1 WHERE ma_san_pham = ?";
    // 🔥 Restore
    private String sqlRestore
            = "UPDATE san_pham SET trang_thai_xoa = 0 WHERE ma_san_pham = ?";

    private String sqlfindbyma = "select id_san_pham,ma_san_pham,ten_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su from san_pham where ma_san_pham like ? or ten_san_pham like ?";
    private String sqlfindbyname = "select id_san_pham from san_pham where ten_san_pham = ?";

    @Override

    public SanPham_ql create(SanPham_ql entity) {
        // SQL không dùng OUTPUT vì chúng ta dùng PreparedStatement + RETURN_GENERATED_KEYS trong XJdbc
        String sql = "INSERT INTO san_pham ("
                + "id_loai_san_pham, id_chat_lieu, id_kieu_dang, id_kieu_co_giay, "
                + "id_kieu_day_giay, id_thuong_hieu, id_xuat_su, "
                + "ma_san_pham, ten_san_pham, mo_ta, trang_thai_xoa"
                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        Object[] values = new Object[]{
            entity.getId_loai_san_pham(),
            entity.getId_chat_lieu(),
            entity.getId_kieu_dang(),
            entity.getId_kieu_co_giay(),
            entity.getId_kieu_day_giay(),
            entity.getId_thuong_hieu(),
            entity.getId_xuat_su(),
            entity.getMa_san_pham(),
            entity.getTen_san_pham(),
            entity.getMo_ta(),
            entity.isTrang_thai_xoa()
        };

        // Lấy ID vừa insert
        int newId = XJdbc.insertAndReturnId(sql, values);
        entity.setId_san_pham(newId);
        return entity;

//throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(SanPham_ql entity) {
        XJdbc.executeUpdate(sqlupdate, entity.getTen_san_pham(), entity.getId_chat_lieu(), entity.getId_kieu_dang(), entity.getId_kieu_co_giay(), entity.getId_kieu_day_giay(), entity.getId_thuong_hieu(), entity.getId_xuat_su(), entity.getMa_san_pham());
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
      
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
     public void softDelete(String maSP) {
        XJdbc.executeUpdate(sqlSoftDelete, maSP);
         DaoImpl_SanPhamChiTiet_ql spctDao = new DaoImpl_SanPhamChiTiet_ql();
    spctDao.softDeleteBySanPham(maSP);
    }
     public void restore(String maSP) {
    
    XJdbc.executeUpdate(sqlRestore, maSP);
}
    @Override
    public List<SanPham_ql> findAll() {
        return XQuery.getBeanList(SanPham_ql.class, sqlFillAll);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public List<SanPham_ql> findRecycle() {
        return XQuery.getBeanList(SanPham_ql.class, sqlRecycle);
    }


    @Override
    public SanPham_ql findById(Integer id) {
        return XQuery.getSingleBean(SanPham_ql.class, sqlFillById, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPham_ql> findByMa(String ma) {
        return XQuery.getBeanList(SanPham_ql.class, sqlfindbyma, ma, ma);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SanPham_ql insertAndReturnId(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SanPham_ql findByName(String ma) {
        return XQuery.getSingleBean(SanPham_ql.class, sqlfindbyname, ma);
        // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
