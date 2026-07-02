package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.HoaDon;
import com.vn.test.bshoes.entity.PhieuGiamGia;
import com.vn.test.bshoes.service.HoaDonService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    private final HoaDonService service;

    public HoaDonController(HoaDonService service) {
        this.service = service;
    }

    @GetMapping
    public List<HoaDon> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public HoaDon findById(@PathVariable int id) { return service.findById(id); }

    @GetMapping("/cart")
    public List<HoaDon> findAllCart() { return service.findAllCart(); }

    @GetMapping("/by-ma/{ma}")
    public HoaDon findByMa(@PathVariable String ma) { return service.findByMa(ma); }

    @PostMapping
    public void create(@RequestBody(required = false) Map<String, Object> body,
                        @RequestParam(required = false) Integer trangThai,
                        @RequestParam(required = false) Integer loaiHoaDon,
                        @RequestParam(required = false) Integer idNhanVien) {
        int trangThaiVal = trangThai != null ? trangThai : ((Number) body.get("trangThai")).intValue();
        int loaiHoaDonVal = loaiHoaDon != null ? loaiHoaDon : ((Number) body.get("loaiHoaDon")).intValue();
        int idNhanVienVal = idNhanVien != null ? idNhanVien : ((Number) body.get("idNhanVien")).intValue();
        service.create(trangThaiVal, loaiHoaDonVal, idNhanVienVal);
    }

    @PutMapping
    public HoaDon update(@RequestBody HoaDon e) { service.update(e); return e; }

    @PutMapping("/mark-paid/{id}")
    public void markPaid(@PathVariable int id, @RequestParam String nguoiCapNhat) {
        service.markPaid(id, nguoiCapNhat);
    }

    @PutMapping("/{id}/khach-hang/{idKhachHang}")
    public void updateKhachHang(@PathVariable int id, @PathVariable int idKhachHang) {
        service.updateKhachHang(id, idKhachHang);
    }

    @GetMapping("/{idPhieu}/giam-gia")
    public BigDecimal tinhGiamGia(@PathVariable int idPhieu, @RequestParam BigDecimal tongTien) {
        return service.tinhGiamGia(idPhieu, tongTien);
    }

    @GetMapping("/vouchers-active")
    public List<PhieuGiamGia> getPhieuGiamGiaHoatDong() { return service.getPhieuGiamGiaHoatDong(); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.deleteById(id); }
}
