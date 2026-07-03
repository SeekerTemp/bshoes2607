package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.BienTheDto;
import com.vn.test.bshoes.service.SanPhamChiTietQlService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham-chi-tiet-ql")
public class SanPhamChiTietQlController {

    private final SanPhamChiTietQlService service;

    public SanPhamChiTietQlController(SanPhamChiTietQlService service) {
        this.service = service;
    }

    @GetMapping("/by-product/{idSanPham}")
    public List<BienTheDto> findByProduct(@PathVariable int idSanPham) { return service.findByProduct(idSanPham); }

    @PostMapping("/product/{idSanPham}")
    public BienTheDto create(@PathVariable int idSanPham, @RequestBody BienTheDto dto) {
        return service.create(idSanPham, dto);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable int id) { service.softDelete(id); }
}
