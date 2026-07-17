package com.vn.test.bshoes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Một dòng = một nhân viên vào được một màn hình. Khóa chính kép (id_nhan_vien, man_hinh).
 * {@code man_hinh} khớp với key trong frontend/src/config/screens.js.
 *
 * <p>Bảng này là quyền hiệu lực, KHÔNG phải template — xem {@link VaiTro#getQuyen()}.
 * Riêng vai trò ADMIN không đọc bảng này (xem NhanVienQuyenService).
 */
@Entity
@Table(name = "nhan_vien_quyen", schema = "dbo")
@IdClass(NhanVienQuyen.Key.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienQuyen {

    @Id
    @Column(name = "id_nhan_vien")
    private Integer idNhanVien;

    @Id
    @Column(name = "man_hinh", length = 30)
    private String manHinh;

    /** Khóa kép cho @IdClass — cần equals/hashCode, Lombok @Data lo phần đó. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key implements Serializable {
        private Integer idNhanVien;
        private String manHinh;
    }
}
