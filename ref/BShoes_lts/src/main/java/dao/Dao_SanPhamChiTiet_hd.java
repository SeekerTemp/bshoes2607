/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.SanPhamChiTiet;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface Dao_SanPhamChiTiet_hd extends CrudDAO<SanPhamChiTiet, Integer> {

    public List<SanPhamChiTiet> findAllAvailable();
    public List<SanPhamChiTiet> findAllAvailableByName(String ma);

    public SanPhamChiTiet findByMa(String string);

    public List<SanPhamChiTiet> findAllById(Integer id);

    List<SanPhamChiTiet> findByIDSanPham(int id);

    List<SanPhamChiTiet> filter(String danhMuc, String xuatXu, String nhaSX, String thuTuGia);

    public void deleteTT();

    public List<Integer> getIdSanPhamBiAnhHuong(String tenCot, int idThuocTinh);

    public void xoaMemTheoThuocTinh(String tenCot, int idThuocTinh);

    void UpdateStock(SanPhamChiTiet entity);

    void UpdateSLKhiHetHang(SanPhamChiTiet entity);

    void updateSlKhiXoa(SanPhamChiTiet entity);

    void updateSlTrangThai(SanPhamChiTiet entity);

    void updateSlKhiThemVaoGhtt(SanPhamChiTiet entity);
}
