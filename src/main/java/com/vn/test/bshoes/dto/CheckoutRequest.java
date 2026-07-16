package com.vn.test.bshoes.dto;

import lombok.Data;

import java.util.List;

/**
 * Khách đặt hàng từ trang cửa hàng (giỏ hàng nằm ở trình duyệt, không có bảng gio_hang).
 * Không cần đăng nhập: chỉ tên/SĐT/địa chỉ là đủ; idKhachHang tuỳ chọn nếu khách đã có hồ sơ.
 */
@Data
public class CheckoutRequest {
    private Integer idKhachHang;
    private String tenNguoiNhan;
    private String soDienThoai;
    private String diaChi;
    private String ghiChu;
    /** Dùng lại AddItemRequest của POS — cùng shape (idSanPhamChiTiet + soLuong). */
    private List<AddItemRequest> items;
}
