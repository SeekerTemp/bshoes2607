package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String ma;
    private String ten;
    private String vaiTro;
    private Integer idVaiTro;
    private String quyen;    // CSV of allowed screen keys; '*' = all
}
