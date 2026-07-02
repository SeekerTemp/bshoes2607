package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.DiaChi;
import com.vn.test.bshoes.service.DiaChiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dia-chi")
public class DiaChiController {

    private final DiaChiService service;

    public DiaChiController(DiaChiService service) {
        this.service = service;
    }

    @GetMapping
    public List<DiaChi> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public DiaChi findById(@PathVariable int id) { return service.findById(id); }

    @GetMapping("/by-khach-hang/{id}")
    public List<DiaChi> findByKhachHang(@PathVariable int id) { return service.findByKhachHang(id); }

    @PostMapping
    public DiaChi create(@RequestBody DiaChi e) { return service.save(e); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
