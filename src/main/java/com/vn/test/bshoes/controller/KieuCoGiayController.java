package com.vn.test.bshoes.controller;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.service.KieuCoGiayService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kieu-co-giay")
public class KieuCoGiayController {
    private final KieuCoGiayService service;
    public KieuCoGiayController(KieuCoGiayService service) { this.service = service; }
    @GetMapping public List<AttributeDto> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public AttributeDto findById(@PathVariable int id) { return service.findById(id); }
    @PostMapping public AttributeDto create(@RequestBody AttributeDto dto) { return service.create(dto); }
    @PutMapping public AttributeDto update(@RequestBody AttributeDto dto) { return service.update(dto); }
    @DeleteMapping("/{id}") public void delete(@PathVariable int id) { service.delete(id); }
}
