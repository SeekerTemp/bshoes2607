package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.DatTruocDto;
import com.vn.test.bshoes.dto.DatTruocRequest;

import java.util.List;

public interface DatTruocService {
    List<DatTruocDto> findAll();
    DatTruocDto findById(int id);

    /** Khách đăng ký đặt trước một biến thể đang hết hàng. */
    DatTruocDto dangKy(DatTruocRequest req);

    /** Đổi trạng thái: Chờ hàng / Đã có hàng / Đã chuyển đơn / Đã hủy. */
    DatTruocDto capNhatTrangThai(int id, String trangThai);

    /** Chuyển phiếu đặt trước thành hóa đơn chờ (giữ hàng cho khách). */
    DatTruocDto chuyenDon(int id, Integer idNhanVien);

    /** Số phiếu đang chờ hàng của một biến thể. */
    long demChoHang(int idSanPhamChiTiet);

    void softDelete(int id);
}
