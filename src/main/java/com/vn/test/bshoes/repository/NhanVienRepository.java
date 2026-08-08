package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    // `left join fetch n.idVaiTro` is load-bearing, not an optimisation:
    // NhanVien.idVaiTro is LAZY and spring.jpa.open-in-view=false, so mapping the
    // role name in the service (outside the transaction) threw
    // LazyInitializationException and GET /api/nhan-vien returned 500.
    @Query("select n from NhanVien n left join fetch n.idVaiTro " +
            "where n.trangThaiXoa = false or n.trangThaiXoa is null")
    List<NhanVien> findActive();

    @Query("select n from NhanVien n left join fetch n.idVaiTro " +
            "where (n.trangThaiXoa = false or n.trangThaiXoa is null) and " +
            "lower(n.tenNhanVien) like lower(concat('%',?1,'%')) and (?2 = 'all' or n.gioiTinh = ?2)")
    List<NhanVien> search(String ten, String gioiTinh);

    @Query("select n from NhanVien n left join fetch n.idVaiTro where n.id = ?1")
    NhanVien findWithVaiTro(Integer id);

    NhanVien findByTaiKhoanAndMatKhau(String taiKhoan, String matKhau);

    boolean existsByMaNhanVien(String ma);

    boolean existsByTaiKhoan(String tk);

    /**
     * Dùng để chặn trùng tài khoản khi tạo / sửa nhân viên. Trả về entity chứ không
     * phải boolean: khi SỬA cần biết bản ghi trùng có phải chính nó hay không.
     */
    NhanVien findByTaiKhoan(String taiKhoan);
}
