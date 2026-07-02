package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.SanPham;
import com.vn.test.bshoes.service.SanPhamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham")
public class SanPhamController {

    private final SanPhamService service;

    public SanPhamController(SanPhamService service) {
        this.service = service;
    }

    @GetMapping
    public List<SanPham> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public SanPham findById(@PathVariable int id) { return service.findById(id); }

    @GetMapping("/search")
    public List<SanPham> findByMa(@RequestParam String ma) { return service.findByMa(ma); }

    @PostMapping
    public SanPham create(@RequestBody SanPham e) { return service.create(e); }

    @PutMapping
    public SanPham update(@RequestBody SanPham e) { return service.update(e); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
