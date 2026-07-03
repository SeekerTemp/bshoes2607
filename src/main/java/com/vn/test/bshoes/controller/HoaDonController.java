package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.dto.PhieuGiamGiaDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;
import com.vn.test.bshoes.service.HoaDonService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    private final HoaDonService service;

    public HoaDonController(HoaDonService service) {
        this.service = service;
    }

    @GetMapping
    public List<HoaDonDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public HoaDonDto findById(@PathVariable int id) { return service.findById(id); }

    @GetMapping("/cart")
    public List<HoaDonDto> findCart() { return service.findCart(); }

    @GetMapping("/pos-products")
    public List<PosSanPhamDto> posProducts() { return service.posProducts(); }

    @GetMapping("/vouchers-active")
    public List<PhieuGiamGiaDto> vouchersActive() { return service.vouchersActive(); }

    @GetMapping("/{idPhieu}/giam-gia")
    public BigDecimal tinhGiamGia(@PathVariable int idPhieu, @RequestParam BigDecimal tongTien) {
        return service.tinhGiamGia(idPhieu, tongTien);
    }

    @PostMapping
    public HoaDonDto create(@RequestBody HoaDonDto dto) { return service.create(dto); }
}
