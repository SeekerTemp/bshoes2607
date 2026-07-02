package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.MauSac;
import com.vn.test.bshoes.service.MauSacService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mau-sac")
public class MauSacController {

    private final MauSacService service;

    public MauSacController(MauSacService service) {
        this.service = service;
    }

    @GetMapping
    public List<MauSac> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public MauSac findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public MauSac create(@RequestBody MauSac e) { return service.create(e); }

    @PutMapping
    public MauSac update(@RequestBody MauSac e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
