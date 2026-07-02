package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.SanPham_ql;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SanPhamQlRepository extends JpaRepository<SanPham_ql, Integer> {

    @Query(value = "SELECT san_pham.id_san_pham, san_pham.ma_san_pham, loai_san_pham.id_loai_san_pham, san_pham.ten_san_pham, san_pham.id_chat_lieu, san_pham.id_kieu_dang, san_pham.id_kieu_co_giay, san_pham.id_kieu_day_giay, san_pham.id_thuong_hieu, san_pham.id_xuat_su, san_pham.mo_ta, san_pham.trang_thai FROM loai_san_pham INNER JOIN san_pham ON loai_san_pham.id_loai_san_pham = san_pham.id_loai_san_pham WHERE san_pham.trang_thai_xoa = 0", nativeQuery = true)
    List<SanPham_ql> findAllActive();

    @Query(value = "SELECT san_pham.id_san_pham, san_pham.ma_san_pham, loai_san_pham.id_loai_san_pham, san_pham.ten_san_pham, san_pham.id_chat_lieu, san_pham.id_kieu_dang, san_pham.id_kieu_co_giay, san_pham.id_kieu_day_giay, san_pham.id_thuong_hieu, san_pham.id_xuat_su, san_pham.mo_ta, san_pham.trang_thai FROM loai_san_pham INNER JOIN san_pham ON loai_san_pham.id_loai_san_pham = san_pham.id_loai_san_pham WHERE san_pham.trang_thai_xoa = 1", nativeQuery = true)
    List<SanPham_ql> findRecycle();

    @Query(value = "SELECT san_pham.id_san_pham, san_pham.ma_san_pham, loai_san_pham.id_loai_san_pham, san_pham.ten_san_pham, san_pham.id_chat_lieu, san_pham.id_kieu_dang, san_pham.id_kieu_co_giay, san_pham.id_kieu_day_giay, san_pham.id_thuong_hieu, san_pham.id_xuat_su, san_pham.mo_ta, san_pham.trang_thai FROM loai_san_pham INNER JOIN san_pham ON loai_san_pham.id_loai_san_pham = san_pham.id_loai_san_pham where san_pham.id_san_pham = ?1", nativeQuery = true)
    SanPham_ql findByIdJoined(int id);

    @Query(value = "select id_san_pham,ma_san_pham,ten_san_pham,id_chat_lieu,id_kieu_dang,id_kieu_co_giay,id_kieu_day_giay,id_thuong_hieu,id_xuat_su from san_pham where ma_san_pham like ?1 or ten_san_pham like ?2", nativeQuery = true)
    List<SanPham_ql> search(String ma, String ten);

    @Modifying
    @Transactional
    @Query(value = "update san_pham set ten_san_pham=?1, id_chat_lieu=?2, id_kieu_dang=?3, id_kieu_co_giay=?4, id_kieu_day_giay=?5, id_thuong_hieu=?6, id_xuat_su=?7 where ma_san_pham = ?8", nativeQuery = true)
    void updateByMa(String tenSanPham, int idChatLieu, int idKieuDang, int idKieuCoGiay, int idKieuDayGiay, int idThuongHieu, int idXuatSu, String maSanPham);

    @Modifying
    @Transactional
    @Query(value = "UPDATE san_pham SET trang_thai_xoa = 1 WHERE ma_san_pham = ?1", nativeQuery = true)
    void softDelete(String ma);

    @Modifying
    @Transactional
    @Query(value = "UPDATE san_pham SET trang_thai_xoa = 0 WHERE ma_san_pham = ?1", nativeQuery = true)
    void restore(String ma);

    @Query(value = "SELECT id_san_pham FROM san_pham WHERE ma_san_pham = ?1", nativeQuery = true)
    Integer findIdByMa(String ma);
}
