package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.SanPhamDto;
import com.vn.test.bshoes.service.SanPhamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham")
public class SanPhamController {

    private final SanPhamService service;

    public SanPhamController(SanPhamService service) {
        this.service = service;
    }

    @GetMapping
    public List<SanPhamDto> findAll() { return service.findAllActive(); }

    @GetMapping("/{id}")
    public SanPhamDto findById(@PathVariable int id) { return service.findById(id); }

    @GetMapping("/search")
    public List<SanPhamDto> search(@RequestParam String keyword) { return service.search(keyword); }
}
