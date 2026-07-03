package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    @Query("select n from NhanVien n where n.trangThaiXoa = false or n.trangThaiXoa is null")
    List<NhanVien> findActive();

    @Query("select n from NhanVien n where (n.trangThaiXoa = false or n.trangThaiXoa is null) and " +
            "lower(n.tenNhanVien) like lower(concat('%',?1,'%')) and (?2 = 'all' or n.gioiTinh = ?2)")
    List<NhanVien> search(String ten, String gioiTinh);

    NhanVien findByTaiKhoanAndMatKhau(String taiKhoan, String matKhau);

    boolean existsByMaNhanVien(String ma);

    boolean existsByTaiKhoan(String tk);
}
