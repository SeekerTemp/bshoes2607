package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.DatTruoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatTruocRepository extends JpaRepository<DatTruoc, Integer> {

    @Query("select d from DatTruoc d where d.trangThaiXoa = false or d.trangThaiXoa is null")
    List<DatTruoc> findActive();

    /** Số phiếu đang chờ hàng của một biến thể — dùng để hiện "đang có N người đặt trước". */
    @Query("select count(d) from DatTruoc d where d.idSanPhamChiTiet.id = ?1 and d.trangThai = 'Chờ hàng' "
            + "and (d.trangThaiXoa = false or d.trangThaiXoa is null)")
    long countChoHang(int idSanPhamChiTiet);
}
