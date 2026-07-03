package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.PhieuGiamGiaDto;
import com.vn.test.bshoes.service.PhieuGiamGiaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-giam-gia")
public class PhieuGiamGiaController {

    private final PhieuGiamGiaService service;

    public PhieuGiamGiaController(PhieuGiamGiaService service) {
        this.service = service;
    }

    @GetMapping
    public List<PhieuGiamGiaDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public PhieuGiamGiaDto findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public PhieuGiamGiaDto create(@RequestBody PhieuGiamGiaDto dto) { return service.create(dto); }

    @PutMapping
    public PhieuGiamGiaDto update(@RequestBody PhieuGiamGiaDto dto) { return service.update(dto); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }

    @GetMapping("/search")
    public List<PhieuGiamGiaDto> search(@RequestParam String keyword) { return service.search(keyword); }
}
