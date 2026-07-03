package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.HoaDonChiTietDto;
import com.vn.test.bshoes.service.HoaDonChiTietService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoa-don-chi-tiet")
public class HoaDonChiTietController {

    private final HoaDonChiTietService service;

    public HoaDonChiTietController(HoaDonChiTietService service) {
        this.service = service;
    }

    @GetMapping("/by-hoa-don/{id}")
    public List<HoaDonChiTietDto> findByHoaDon(@PathVariable int id) { return service.findByHoaDon(id); }

    @GetMapping("/{id}")
    public HoaDonChiTietDto findById(@PathVariable int id) { return service.findById(id); }
}
