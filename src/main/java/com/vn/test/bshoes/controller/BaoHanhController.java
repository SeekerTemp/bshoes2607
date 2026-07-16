package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.BaoHanhDto;
import com.vn.test.bshoes.service.BaoHanhService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bao-hanh")
public class BaoHanhController {

    private final BaoHanhService service;

    public BaoHanhController(BaoHanhService service) {
        this.service = service;
    }

    @GetMapping
    public List<BaoHanhDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public BaoHanhDto findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public BaoHanhDto create(@RequestBody BaoHanhDto dto) { return service.create(dto); }

    @PutMapping("/{id}/trang-thai")
    public BaoHanhDto updateTrangThai(@PathVariable int id, @RequestParam String trangThai) {
        return service.updateTrangThai(id, trangThai);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable int id) { service.softDelete(id); }
}
