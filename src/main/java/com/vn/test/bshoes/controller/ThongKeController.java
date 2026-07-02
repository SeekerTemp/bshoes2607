package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.ThongKeDoanhThu;
import com.vn.test.bshoes.entity.ThongKeSanPham;
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
    public List<ThongKeDoanhThu> homNay() {
        return service.homNay();
    }

    @GetMapping("/theo-ngay")
    public List<ThongKeDoanhThu> theoNgay(@RequestParam String tuNgay, @RequestParam String denNgay) {
        Date from = Date.valueOf(tuNgay);
        Date to = Date.valueOf(denNgay);
        return service.theoNgay(from, to);
    }

    @GetMapping("/theo-thang")
    public List<ThongKeDoanhThu> theoThang(@RequestParam int thang, @RequestParam int nam) {
        return service.theoThang(thang, nam);
    }

    @GetMapping("/theo-nam")
    public List<ThongKeDoanhThu> theoNam(@RequestParam int nam) {
        return service.theoNam(nam);
    }

    @GetMapping("/san-pham")
    public List<ThongKeSanPham> tatCaSanPham() {
        return service.tatCaSanPham();
    }

    @GetMapping("/nam")
    public List<Integer> loatNam() {
        return service.loatNam();
    }
}
