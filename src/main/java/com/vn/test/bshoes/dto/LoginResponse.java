package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
    private Integer id;
    private String ma;
    private String ten;
    private String vaiTro;
    private Integer idVaiTro;
    private String quyen;    // CSV of allowed screen keys; '*' = all

    // Opaque session token, minted by AuthController on a successful login and
    // sent back on every later call as the X-Auth-Token header. Not part of the
    // all-args constructor on purpose: the service authenticates, the controller
    // owns session creation.
    private String token;

    public LoginResponse(Integer id, String ma, String ten, String vaiTro, Integer idVaiTro, String quyen) {
        this.id = id;
        this.ma = ma;
        this.ten = ten;
        this.vaiTro = vaiTro;
        this.idVaiTro = idVaiTro;
        this.quyen = quyen;
    }
}
