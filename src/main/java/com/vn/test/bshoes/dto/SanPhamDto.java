package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDto {
    private Integer id;
    private String ma;
    private String ten;
    private Integer idLoaiSanPham;   // category id
    private String loaiSP;           // category name
    private String thuongHieu;       // brand (hãng giày)
    private String kieuDang;         // style (kiểu dáng)
    private String chatLieu;
    private String moTa;
    private String imageUrl;

    /*
     * Giá, tồn và trạng thái là thuộc tính của BIẾN THỂ. Trước đây DTO này có một
     * trường `gia` lấy từ bienThe.get(0) — sản phẩm nhiều biến thể vì thế hiển thị sai
     * giá, và sản phẩm chưa có biến thể nào thì hiển thị giá rỗng. Các trường dưới đây
     * là số liệu TỔNG HỢP chỉ đọc, tính từ danh sách biến thể; không bao giờ ghi ngược
     * xuống sản phẩm cha.
     */
    private BigDecimal giaTu;     // giá bán thấp nhất trong các biến thể
    private BigDecimal giaDen;    // giá bán cao nhất
    private Integer tongTon;      // tổng số lượng tồn
    private Integer soBienThe;    // số biến thể còn hiệu lực
    private Boolean dangBan;      // có ít nhất một biến thể đang bán

    private List<BienTheDto> bienThe;
}
