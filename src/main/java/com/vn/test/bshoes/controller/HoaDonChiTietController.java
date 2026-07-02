package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.HoaDonChiTiet;
import com.vn.test.bshoes.service.HoaDonChiTietService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/hoa-don-chi-tiet")
public class HoaDonChiTietController {

    private final HoaDonChiTietService service;

    public HoaDonChiTietController(HoaDonChiTietService service) {
        this.service = service;
    }

    @GetMapping("/by-hoa-don/{id}")
    public List<HoaDonChiTiet> findDetailByHoaDon(@PathVariable int id) { return service.findDetailByHoaDon(id); }

    @GetMapping("/raw/by-hoa-don/{id}")
    public List<HoaDonChiTiet> findByHoaDon(@PathVariable int id) { return service.findByHoaDon(id); }

    @GetMapping("/{id}")
    public HoaDonChiTiet findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public void create(@RequestParam int idSanPhamChiTiet,
                        @RequestParam int idHoaDon,
                        @RequestParam int soLuong,
                        @RequestParam String nguoiTao,
                        @RequestParam String nguoiCapNhat,
                        @RequestParam BigDecimal thanhTien) {
        service.create(idSanPhamChiTiet, idHoaDon, soLuong, nguoiTao, nguoiCapNhat, thanhTien);
    }

    @PostMapping("/via-proc")
    public void createViaProc(@RequestParam int idSanPhamChiTiet,
                               @RequestParam int soLuong,
                               @RequestParam BigDecimal giaGiam,
                               @RequestParam String nguoiTao) {
        service.createViaProc(idSanPhamChiTiet, soLuong, giaGiam, nguoiTao);
    }

    @PutMapping
    public HoaDonChiTiet update(@RequestBody HoaDonChiTiet e) { service.update(e); return e; }

    @PutMapping("/stock")
    public void updateStock(@RequestParam int soLuong, @RequestParam int id) {
        service.updateStock(soLuong, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
