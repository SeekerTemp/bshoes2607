package com.vn.test.bshoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A role with its allowed-screen permission set (CSV of screen keys; '*' = all). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanQuyenDto {
    private Integer id;
    private String ma;
    private String ten;
    private String quyen;
}
