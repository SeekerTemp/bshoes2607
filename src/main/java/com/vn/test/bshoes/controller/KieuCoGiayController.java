package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.KieuCoGiay;
import com.vn.test.bshoes.service.KieuCoGiayService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kieu-co-giay")
public class KieuCoGiayController {

    private final KieuCoGiayService service;

    public KieuCoGiayController(KieuCoGiayService service) {
        this.service = service;
    }

    @GetMapping
    public List<KieuCoGiay> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public KieuCoGiay findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public KieuCoGiay create(@RequestBody KieuCoGiay e) { return service.create(e); }

    @PutMapping
    public KieuCoGiay update(@RequestBody KieuCoGiay e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
