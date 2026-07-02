package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.PhieuGiamGia;
import com.vn.test.bshoes.service.PhieuGiamGiaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-giam-gia")
public class PhieuGiamGiaController {

    private final PhieuGiamGiaService service;

    public PhieuGiamGiaController(PhieuGiamGiaService service) {
        this.service = service;
    }

    @GetMapping
    public List<PhieuGiamGia> findAll() { return service.findAllActive(); }

    @GetMapping("/{id}")
    public PhieuGiamGia findById(@PathVariable int id) { return service.findByIdActive(id); }

    @GetMapping("/search")
    public List<PhieuGiamGia> search(@RequestParam String keyword) { return service.search(keyword); }

    @PostMapping
    public PhieuGiamGia create(@RequestBody PhieuGiamGia e) { return service.create(e); }

    @PutMapping
    public PhieuGiamGia update(@RequestBody PhieuGiamGia e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }

    @PostMapping("/cap-nhat-trang-thai")
    public void capNhatTrangThai() { service.capNhatTrangThai(); }
}
