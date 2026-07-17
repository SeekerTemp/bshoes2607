package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.NhanVienQuyenDto;

import java.util.List;

/**
 * Quyền hiệu lực = rows trong nhan_vien_quyen. Ngoại lệ: vai trò ADMIN (vai_tro.quyen = '*')
 * không đọc rows, luôn toàn quyền.
 *
 * <p>Backend cố tình KHÔNG giữ danh sách màn hình — nó chỉ trả '*' và để
 * frontend/src/config/screens.js giãn ra. Giữ registry ở một chỗ duy nhất.
 */
public interface NhanVienQuyenService {

    /** Quyền hiệu lực dạng CSV cho LoginResponse.quyen; ADMIN -> "*" (useAuth tự giãn). */
    String quyenCsv(int idNhanVien);

    /** Lưới phân quyền: mọi nhân viên + quyền của họ. */
    List<NhanVienQuyenDto> bangQuyen();

    /** Ghi đè quyền của một nhân viên (xóa rows cũ, ghi rows mới). */
    NhanVienQuyenDto luuQuyen(int idNhanVien, List<String> manHinhs);

    /** Chép lại bộ quyền mặc định từ vai_tro.quyen của vai trò hiện tại. */
    NhanVienQuyenDto apTemplate(int idNhanVien);
}
