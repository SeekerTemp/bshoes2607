package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.XuatXu;
import com.vn.test.bshoes.service.XuatXuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/xuat-su")
public class XuatXuController {

    private final XuatXuService service;

    public XuatXuController(XuatXuService service) {
        this.service = service;
    }

    @GetMapping
    public List<XuatXu> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public XuatXu findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public XuatXu create(@RequestBody XuatXu e) { return service.create(e); }

    @PutMapping
    public XuatXu update(@RequestBody XuatXu e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
