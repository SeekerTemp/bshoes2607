package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.BienTheDto;
import com.vn.test.bshoes.service.SanPhamChiTietService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham-chi-tiet")
public class SanPhamChiTietController {

    private final SanPhamChiTietService service;

    public SanPhamChiTietController(SanPhamChiTietService service) {
        this.service = service;
    }

    @GetMapping("/available")
    public List<BienTheDto> findAvailable() { return service.findAvailable(); }

    @GetMapping("/by-product/{idSanPham}")
    public List<BienTheDto> findByProduct(@PathVariable int idSanPham) { return service.findByProduct(idSanPham); }

    @GetMapping("/{id}")
    public BienTheDto findById(@PathVariable int id) { return service.findById(id); }
}
