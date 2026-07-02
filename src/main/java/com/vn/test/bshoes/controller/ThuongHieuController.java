package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.ThuongHieu;
import com.vn.test.bshoes.service.ThuongHieuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thuong-hieu")
public class ThuongHieuController {

    private final ThuongHieuService service;

    public ThuongHieuController(ThuongHieuService service) {
        this.service = service;
    }

    @GetMapping
    public List<ThuongHieu> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ThuongHieu findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public ThuongHieu create(@RequestBody ThuongHieu e) { return service.create(e); }

    @PutMapping
    public ThuongHieu update(@RequestBody ThuongHieu e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
