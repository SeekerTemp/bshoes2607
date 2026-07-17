package com.vn.test.bshoes.dto;

import lombok.Data;

import java.util.List;

/** Một dòng của lưới phân quyền: nhân viên + quyền hiệu lực của họ. */
@Data
public class NhanVienQuyenDto {
    private Integer idNhanVien;
    private String ma;
    private String ten;
    private String vaiTro;
    private Integer idVaiTro;
    /** true = vai trò ADMIN, quyền tính động, lưới khóa ô không cho sửa. */
    private boolean toanQuyen;
    /** Các key màn hình vào được (đã gồm quy tắc ADMIN). */
    private List<String> quyen;
}
