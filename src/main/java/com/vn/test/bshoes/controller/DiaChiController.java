package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.DiaChiDto;
import com.vn.test.bshoes.service.DiaChiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dia-chi")
public class DiaChiController {

    private final DiaChiService service;

    public DiaChiController(DiaChiService service) {
        this.service = service;
    }

    @GetMapping
    public List<DiaChiDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public DiaChiDto findById(@PathVariable int id) { return service.findById(id); }

    @GetMapping("/by-khach-hang/{id}")
    public List<DiaChiDto> findByKhachHang(@PathVariable int id) { return service.findByKhachHang(id); }

    @PostMapping
    public DiaChiDto create(@RequestBody DiaChiDto dto) { return service.create(dto); }

    @PutMapping("/{id}")
    public DiaChiDto update(@PathVariable int id, @RequestBody DiaChiDto dto) { return service.update(id, dto); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
