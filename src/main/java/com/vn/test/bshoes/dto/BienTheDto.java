package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BienTheDto {
    private Integer id;
    private String ma;
    private String mau;
    private String size;
    private Integer ton;
    private BigDecimal gia;       // giá bán (don_gia)
    private BigDecimal giaNhap;   // giá vốn (cost)
    private String imageUrl;
    private Boolean trangThai;

    /**
     * Tên sản phẩm cha. Chỉ điền ở danh sách thùng rác (findRecycle) — ở đó biến thể
     * đứng một mình nên chỉ thấy mã/màu/cỡ thì không biết là của sản phẩm nào. Các
     * đường đọc khác để null có chủ ý: điền nó sẽ deref proxy LAZY idSanPham cho từng
     * dòng, tức là N+1 truy vấn trên đúng những endpoint nóng nhất.
     */
    private String tenSanPham;
}
