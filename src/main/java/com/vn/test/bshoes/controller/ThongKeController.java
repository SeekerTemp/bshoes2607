package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.ThongKeDoanhThuDto;
import com.vn.test.bshoes.dto.ThongKeSanPhamDto;
import com.vn.test.bshoes.service.ThongKeService;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/thong-ke")
public class ThongKeController {

    private final ThongKeService service;

    public ThongKeController(ThongKeService service) {
        this.service = service;
    }

    @GetMapping("/hom-nay")
    public List<ThongKeDoanhThuDto> homNay() {
        return service.homNay();
    }

    @GetMapping("/theo-ngay")
    public List<ThongKeDoanhThuDto> theoNgay(@RequestParam String tuNgay, @RequestParam String denNgay) {
        Date from = Date.valueOf(tuNgay);
        Date to = Date.valueOf(denNgay);
        return service.theoNgay(from, to);
    }

    @GetMapping("/theo-thang")
    public List<ThongKeDoanhThuDto> theoThang(@RequestParam int thang, @RequestParam int nam) {
        return service.theoThang(thang, nam);
    }

    @GetMapping("/theo-nam")
    public List<ThongKeDoanhThuDto> theoNam(@RequestParam int nam) {
        return service.theoNam(nam);
    }

    @GetMapping("/san-pham")
    public List<ThongKeSanPhamDto> tatCaSanPham() {
        return service.tatCaSanPham();
    }

    @GetMapping("/nam")
    public List<Integer> loatNam() {
        return service.loatNam();
    }
}
