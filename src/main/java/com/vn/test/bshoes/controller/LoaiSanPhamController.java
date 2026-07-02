package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.LoaiSanPham;
import com.vn.test.bshoes.service.LoaiSanPhamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loai-san-pham")
public class LoaiSanPhamController {

    private final LoaiSanPhamService service;

    public LoaiSanPhamController(LoaiSanPhamService service) {
        this.service = service;
    }

    @GetMapping
    public List<LoaiSanPham> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public LoaiSanPham findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public LoaiSanPham create(@RequestBody LoaiSanPham e) { return service.create(e); }

    @PutMapping
    public LoaiSanPham update(@RequestBody LoaiSanPham e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
