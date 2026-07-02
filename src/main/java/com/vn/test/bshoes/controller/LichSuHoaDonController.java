package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.LichSuHoaDon;
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
    public List<LichSuHoaDon> findAll() { return service.findAllActive(); }

    @GetMapping("/cancelled")
    public List<LichSuHoaDon> findCancelled() { return service.findAllCancel(); }

    @GetMapping("/by-date")
    public List<LichSuHoaDon> findByDate(@RequestParam String tuNgay, @RequestParam String denNgay) {
        return service.findByDate(tuNgay, denNgay);
    }

    @GetMapping("/{id}")
    public LichSuHoaDon findById(@PathVariable int id) { return service.findById(id); }
}
