package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.SanPham_ql;
import com.vn.test.bshoes.service.SanPhamQlService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham-ql")
public class SanPhamQlController {

    private final SanPhamQlService service;

    public SanPhamQlController(SanPhamQlService service) {
        this.service = service;
    }

    @GetMapping
    public List<SanPham_ql> findAll() { return service.findAllActive(); }

    @GetMapping("/{id}")
    public SanPham_ql findById(@PathVariable int id) { return service.findByIdJoined(id); }

    @GetMapping("/recycle")
    public List<SanPham_ql> findRecycle() { return service.findRecycle(); }

    @GetMapping("/search")
    public List<SanPham_ql> search(@RequestParam String keyword) { return service.search(keyword); }

    @PostMapping
    public SanPham_ql create(@RequestBody SanPham_ql e) { return service.create(e); }

    @PutMapping
    public SanPham_ql update(@RequestBody SanPham_ql e) { service.update(e); return e; }

    @PostMapping("/restore/{ma}")
    public void restore(@PathVariable String ma) { service.restore(ma); }

    @DeleteMapping("/soft/{ma}")
    public void softDelete(@PathVariable String ma) { service.softDelete(ma); }
}
