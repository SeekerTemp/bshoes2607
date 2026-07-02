package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.NhanVien;
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
    public List<NhanVien> findAll() { return service.findAllActive(); }

    @GetMapping("/{id}")
    public NhanVien findById(@PathVariable int id) { return service.findByIdActive(id); }

    @PostMapping
    public NhanVien create(@RequestBody NhanVien e) { return service.create(e); }

    @PutMapping
    public NhanVien update(@RequestBody NhanVien e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }

    @GetMapping("/search")
    public List<NhanVien> search(@RequestParam String ten, @RequestParam(defaultValue = "all") String gioiTinh) {
        return service.search(ten, gioiTinh);
    }
}
