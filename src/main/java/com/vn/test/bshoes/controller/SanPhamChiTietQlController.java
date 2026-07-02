package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.SanPhamChiTiet_ql;
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

    @GetMapping
    public List<SanPhamChiTiet_ql> findAll() { return service.findAllActive(); }

    @GetMapping("/{id}")
    public SanPhamChiTiet_ql findById(@PathVariable int id) { return service.findByIdJoined(id); }

    @GetMapping("/by-product/{idSanPham}")
    public List<SanPhamChiTiet_ql> findByProduct(@PathVariable int idSanPham) { return service.findByProduct(idSanPham); }

    @GetMapping("/search")
    public List<SanPhamChiTiet_ql> search(@RequestParam String keyword) { return service.search(keyword); }

    @PostMapping
    public SanPhamChiTiet_ql create(@RequestBody SanPhamChiTiet_ql e) { return service.create(e); }

    @PutMapping
    public SanPhamChiTiet_ql update(@RequestBody SanPhamChiTiet_ql e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
