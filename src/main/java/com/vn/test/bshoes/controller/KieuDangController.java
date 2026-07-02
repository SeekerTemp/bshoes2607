package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.KieuDang;
import com.vn.test.bshoes.service.KieuDangService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kieu-dang")
public class KieuDangController {

    private final KieuDangService service;

    public KieuDangController(KieuDangService service) {
        this.service = service;
    }

    @GetMapping
    public List<KieuDang> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public KieuDang findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public KieuDang create(@RequestBody KieuDang e) { return service.create(e); }

    @PutMapping
    public KieuDang update(@RequestBody KieuDang e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
