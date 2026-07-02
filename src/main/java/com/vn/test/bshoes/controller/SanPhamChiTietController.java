package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.SanPhamChiTiet;
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

    @GetMapping
    public List<SanPhamChiTiet> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public SanPhamChiTiet findById(@PathVariable int id) { return service.findById(id); }

    @GetMapping("/available")
    public List<SanPhamChiTiet> findAllAvailable(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return service.findAllAvailable();
        }
        return service.findAvailableByName(keyword);
    }

    @GetMapping("/ma/{ma}")
    public SanPhamChiTiet findByMa(@PathVariable String ma) { return service.findByMa(ma); }

    @PostMapping
    public SanPhamChiTiet create(@RequestBody SanPhamChiTiet e) { return service.create(e); }

    @PutMapping
    public SanPhamChiTiet update(@RequestBody SanPhamChiTiet e) { return service.update(e); }

    @PutMapping("/stock")
    public void updateStock(@RequestParam int soLuongTon, @RequestParam int idSanPhamChiTiet) {
        service.updateStock(soLuongTon, idSanPhamChiTiet);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
