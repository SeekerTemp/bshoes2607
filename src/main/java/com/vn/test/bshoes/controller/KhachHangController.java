package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.KhachHangDto;
import com.vn.test.bshoes.service.KhachHangService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    private final KhachHangService service;

    public KhachHangController(KhachHangService service) {
        this.service = service;
    }

    @GetMapping
    public List<KhachHangDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public KhachHangDto findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public KhachHangDto create(@RequestBody KhachHangDto dto) { return service.create(dto); }

    @PutMapping
    public KhachHangDto update(@RequestBody KhachHangDto dto) { return service.update(dto); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }

    @GetMapping("/search")
    public List<KhachHangDto> search(@RequestParam String keyword) { return service.search(keyword); }
}
