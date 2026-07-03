package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.NhanVienDto;
import com.vn.test.bshoes.service.NhanVienService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhan-vien")
public class NhanVienController {

    private final NhanVienService service;

    public NhanVienController(NhanVienService service) {
        this.service = service;
    }

    @GetMapping
    public List<NhanVienDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public NhanVienDto findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public NhanVienDto create(@RequestBody NhanVienDto dto) { return service.create(dto); }

    @PutMapping
    public NhanVienDto update(@RequestBody NhanVienDto dto) { return service.update(dto); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }

    @GetMapping("/search")
    public List<NhanVienDto> search(@RequestParam String ten, @RequestParam(defaultValue = "all") String gioiTinh) {
        return service.search(ten, gioiTinh);
    }
}
