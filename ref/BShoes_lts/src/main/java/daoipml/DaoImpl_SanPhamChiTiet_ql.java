/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daoipml;

import entity.SanPham_ql;
import entity.SanPhamChiTiet_ql;
import java.math.BigDecimal;
import java.util.List;
import util.XJdbc;
import util.XQuery;
import dao.Dao_SanPhamChiTiet_ql;

/**
 *
 * @author DELL
 */
public class DaoImpl_SanPhamChiTiet_ql implements Dao_SanPhamChiTiet_ql {

    private String sqlFindAllbyid = "SELECT spct.id_san_pham_chi_tiet,\n"
            + "spct.id_san_pham,\n"
            + " spct.id_kich_co,\n"
            + "  spct.id_mau_sac,\n"
            + "  spct.ma_san_pham_chi_tiet,\n"
            + " spct.so_luong_ton,\n"
            + " spct.don_gia,\n"
            + " spct.ngay_tao,\n"
            + " spct.ngay_cap_nhat,\n"
            + " spct.nguoi_tao,\n"
            + " spct.nguoi_cap_nhat,\n"
            + " spct.trang_thai,\n"
            + " sp.ten_san_pham\n"
            + "FROM san_pham_chi_tiet spct\n"
            + "LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham\n"
            + "WHERE spct.id_san_pham = ?";
    private String sqlFindAll = "SELECT \n"
            + "    spct.id_san_pham_chi_tiet,\n"
            + "    spct.id_san_pham,\n"
            + "    spct.id_kich_co,\n"
            + "    spct.id_mau_sac,\n"
            + "    spct.ma_san_pham_chi_tiet,\n"
            + "    spct.so_luong_ton,\n"
            + "    spct.don_gia,\n"
            + "    spct.ngay_tao,\n"
            + "    spct.ngay_cap_nhat,\n"
            + "    spct.nguoi_tao,\n"
            + "    spct.nguoi_cap_nhat,\n"
            + "    spct.trang_thai,\n"
            + "    sp.ten_san_pham,\n"
            + "sp.ma_san_pham\n"
            + "FROM san_pham_chi_tiet spct\n"
            + "LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham\n"
            + "LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id_mau_sac\n"
            + "LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id_kich_co\n"
            +"where spct.trang_thai_xoa = 0";
    private String sqlFindById
            = "SELECT spct.id_san_pham_chi_tiet,\n"
            + "spct.id_san_pham,\n"
            + " spct.id_kich_co,\n"
            + "  spct.id_mau_sac,\n"
            + "  spct.ma_san_pham_chi_tiet,\n"
            + " spct.so_luong_ton,\n"
            + " spct.don_gia,\n"
            + " spct.ngay_tao,\n"
            + " spct.ngay_cap_nhat,\n"
            + " spct.nguoi_tao,\n"
            + " spct.nguoi_cap_nhat,\n"
            + " spct.trang_thai,\n"
            + " sp.ten_san_pham,\n"
            + "sp.ma_san_pham\n"
            + "FROM san_pham_chi_tiet spct\n"
            + "LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham\n"
            + "WHERE spct.id_san_pham_chi_tiet = ?";

    private String sqldelete = "UPDATE san_pham_chi_tiet SET trang_thai_xoa = 1 WHERE id_san_pham_chi_tiet = ?";
    private String sqlupdate = "update san_pham_chi_tiet set ma_san_pham_chi_tiet = ?,id_mau_sac = ?, id_kich_co = ?, don_gia = ?, so_luong_ton = ?, trang_thai = ? where id_san_pham_chi_tiet = ?";
//private String sqlfindbyma = "select id_san_pham_chi_tiet,ma_san_pham_chi_tiet,ten_san_pham,mau_sac,kich_co,don_gia,so_luong,trang_thai,ngay_tao,ngay_cap_nhat from san_pham_chi_tiet where ma_san_pham_chi_tiet like ? or ten_san_pham like ?";
private String sqlfindbyma
            = "SELECT spct.id_san_pham_chi_tiet,\n"
            + "spct.id_san_pham,\n"
            + " spct.id_kich_co,\n"
            + "  spct.id_mau_sac,\n"
            + "  spct.ma_san_pham_chi_tiet,\n"
            + " spct.so_luong_ton,\n"
            + " spct.don_gia,\n"
            + " spct.ngay_tao,\n"
            + " spct.ngay_cap_nhat,\n"
            + " spct.nguoi_tao,\n"
            + " spct.nguoi_cap_nhat,\n"
            + " spct.trang_thai,\n"
            + " sp.ten_san_pham,\n"
            + "sp.ma_san_pham\n"
            + "FROM san_pham_chi_tiet spct\n"
            + "LEFT JOIN san_pham sp ON sp.id_san_pham = spct.id_san_pham\n"
            + "WHERE spct.ma_san_pham_chi_tiet like ? or sp.ten_san_pham like ?";
    @Override

    public SanPhamChiTiet_ql create(SanPhamChiTiet_ql entity) {

        // ----- 1. Nếu mã SPCT chưa có -> tự tạo -----
        if (entity.getMa_san_pham_chi_tiet() == null || entity.getMa_san_pham_chi_tiet().isBlank()) {

            String sqlMaSP = "SELECT ma_san_pham FROM san_pham WHERE id_san_pham = ?";
            String maSP = XJdbc.getValue(sqlMaSP, entity.getId_san_pham());

            if (maSP == null) {
                throw new RuntimeException("Không tìm thấy sản phẩm cha để tạo mã SPCT!");
            }

            entity.setMa_san_pham_chi_tiet("SPCT" + maSP);
        }

        // ----- 2. SQL INSERT (KHÔNG TRUYỀN NGÀY) -----
        String sql = "INSERT INTO san_pham_chi_tiet ("
                + "id_san_pham, id_mau_sac, id_kich_co, "
                + "ma_san_pham_chi_tiet, don_gia, so_luong_ton, trang_thai"
                + ") VALUES (?,?,?,?,?,?,?)";

        Object[] values = {
            entity.getId_san_pham(),
            entity.getId_mau_sac(),
            entity.getId_kich_co(),
            entity.getMa_san_pham_chi_tiet(),
            entity.getDon_gia(),
            entity.getSo_luong_ton(),
            entity.isTrang_thai()
        };

        // ----- 3. INSERT + LẤY ID MỚI TẠO -----
        String sqlInsertAndReturnId = sql + "; SELECT SCOPE_IDENTITY();";
        BigDecimal bd = XJdbc.getValue(sqlInsertAndReturnId, values);
        int newId = bd.intValue();

        entity.setId_san_pham_chi_tiet(newId);

        // ----- 4. SELECT LẠI ĐỂ LẤY ĐẦY ĐỦ THÔNG TIN (ngày tạo, ngày cập nhật, tên sản phẩm) -----
        String sqlSelect
                = "SELECT spct.id_san_pham_chi_tiet,\n"
                + "spct.id_san_pham,\n"
                + "spct.id_kich_co,\n"
                + "spct.id_mau_sac,\n"
                + " spct.ma_san_pham_chi_tiet,\n"
                + " spct.so_luong_ton,\n"
                + " spct.don_gia,\n"
                + " spct.ngay_tao,\n"
                + " spct.ngay_cap_nhat,\n"
                + " spct.nguoi_tao,\n"
                + " spct.nguoi_cap_nhat,\n"
                + " spct.trang_thai,\n"
                + " sp.ten_san_pham\n"
                + "FROM san_pham_chi_tiet spct\n"
                + "LEFT JOIN san_pham sp ON spct.id_san_pham = sp.id_san_pham\n"
                + "WHERE spct.id_san_pham_chi_tiet = ? ";

        SanPhamChiTiet_ql fullEntity = XQuery.getSingleBean(SanPhamChiTiet_ql.class, sqlSelect, newId);

        return fullEntity;

        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(SanPhamChiTiet_ql entity) {
        XJdbc.executeUpdate(sqlupdate, entity.getMa_san_pham_chi_tiet(), entity.getId_mau_sac(), entity.getId_kich_co(), entity.getDon_gia(), entity.getSo_luong_ton(), entity.isTrang_thai(),entity.getId_san_pham_chi_tiet());
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(sqldelete, id);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
   public void softDeleteBySanPham(String maSP) {

    // Lấy id_san_pham từ ma_san_pham
    String sqlGetId = "SELECT id_san_pham FROM san_pham WHERE ma_san_pham = ?";
    Integer idSP = XJdbc.getValue(sqlGetId, maSP);

    if (idSP == null) {
        System.out.println("Không tìm thấy ID sản phẩm cha để xóa mềm SPCT!");
        return;
    }

    // Xóa mềm SPCT
    String sql = "UPDATE san_pham_chi_tiet SET trang_thai_xoa = 1 WHERE id_san_pham = ?";
    XJdbc.executeUpdate(sql, idSP);
}



    @Override
    public List<SanPhamChiTiet_ql> findAll() {
        return XQuery.getBeanList(SanPhamChiTiet_ql.class, sqlFindAll);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SanPhamChiTiet_ql findById(Integer id) {
        return XQuery.getSingleBean(SanPhamChiTiet_ql.class, sqlFindById, id);
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPhamChiTiet_ql> findallbyid(Integer id) {
        return XQuery.getBeanList(SanPhamChiTiet_ql.class, sqlFindAllbyid, id);
        // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<SanPhamChiTiet_ql> findByMa(String ma) {
        return XQuery.getBeanList(SanPhamChiTiet_ql.class, sqlfindbyma, ma,ma);
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

   
}
