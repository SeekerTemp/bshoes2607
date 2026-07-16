package com.vn.test.bshoes.dto;

import lombok.Data;

/** Request body to add a variant to a POS invoice's cart. */
@Data
public class AddItemRequest {
    private Integer idSanPhamChiTiet;
    private Integer soLuong;   // defaults to 1 when null
    private String nguoiTao;   // optional staff username for audit
}
