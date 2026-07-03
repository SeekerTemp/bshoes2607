package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDto {
    private Integer id;
    private String ma;
    private String ten;
    private Boolean trangThai;
}
