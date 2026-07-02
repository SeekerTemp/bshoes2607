package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.KieuDayGiay;
import com.vn.test.bshoes.service.KieuDayGiayService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kieu-day-giay")
public class KieuDayGiayController {

    private final KieuDayGiayService service;

    public KieuDayGiayController(KieuDayGiayService service) {
        this.service = service;
    }

    @GetMapping
    public List<KieuDayGiay> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public KieuDayGiay findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public KieuDayGiay create(@RequestBody KieuDayGiay e) { return service.create(e); }

    @PutMapping
    public KieuDayGiay update(@RequestBody KieuDayGiay e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
