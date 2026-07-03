package com.vn.test.bshoes.controller;
import com.vn.test.bshoes.dto.AttributeMoTaDto;
import com.vn.test.bshoes.service.XuatSuService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/xuat-su")
public class XuatSuController {
    private final XuatSuService service;
    public XuatSuController(XuatSuService service) { this.service = service; }
    @GetMapping public List<AttributeMoTaDto> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public AttributeMoTaDto findById(@PathVariable int id) { return service.findById(id); }
    @PostMapping public AttributeMoTaDto create(@RequestBody AttributeMoTaDto dto) { return service.create(dto); }
    @PutMapping public AttributeMoTaDto update(@RequestBody AttributeMoTaDto dto) { return service.update(dto); }
    @DeleteMapping("/{id}") public void delete(@PathVariable int id) { service.delete(id); }
}
