package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.SanPhamDto;
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
    public List<SanPhamDto> findAll() { return service.findAllActive(); }

    @GetMapping("/{id}")
    public SanPhamDto findById(@PathVariable int id) { return service.findById(id); }

    @GetMapping("/search")
    public List<SanPhamDto> search(@RequestParam String keyword) { return service.search(keyword); }

    @GetMapping("/recycle")
    public List<SanPhamDto> findRecycle() { return service.findRecycle(); }

    @PostMapping
    public SanPhamDto create(@RequestBody SanPhamDto dto) { return service.create(dto); }

    @PutMapping
    public SanPhamDto update(@RequestBody SanPhamDto dto) { return service.update(dto); }

    @PostMapping("/restore/{ma}")
    public void restore(@PathVariable String ma) { service.restore(ma); }

    /** Assign a product to a category (idLoai omitted → remove from category). */
    @PutMapping("/{id}/danh-muc")
    public SanPhamDto setDanhMuc(@PathVariable int id, @RequestParam(required = false) Integer idLoai) {
        return service.setDanhMuc(id, idLoai);
    }

    @DeleteMapping("/soft/{ma}")
    public void softDelete(@PathVariable String ma) { service.softDelete(ma); }
}
