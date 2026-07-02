package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.KhachHang;
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
    public List<KhachHang> findAll() { return service.findAllActive(); }

    @GetMapping("/{id}")
    public KhachHang findById(@PathVariable int id) { return service.findByIdActive(id); }

    @PostMapping
    public KhachHang create(@RequestBody KhachHang e) { return service.create(e); }

    @PutMapping
    public KhachHang update(@RequestBody KhachHang e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }

    @GetMapping("/search")
    public List<KhachHang> search(@RequestParam String keyword) { return service.search(keyword); }

    @GetMapping("/exists/ma")
    public boolean existsMa(@RequestParam String value) { return service.existsMa(value); }

    @GetMapping("/exists/email")
    public boolean existsEmail(@RequestParam String value) { return service.existsEmail(value); }

    @GetMapping("/exists/sdt")
    public boolean existsSdt(@RequestParam String value) { return service.existsSdt(value); }
}
