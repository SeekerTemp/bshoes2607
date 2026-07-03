package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.service.LichSuHoaDonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lich-su-hoa-don")
public class LichSuHoaDonController {

    private final LichSuHoaDonService service;

    public LichSuHoaDonController(LichSuHoaDonService service) {
        this.service = service;
    }

    @GetMapping
    public List<HoaDonDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public HoaDonDto findById(@PathVariable int id) {
        return service.findById(id);
    }
}
