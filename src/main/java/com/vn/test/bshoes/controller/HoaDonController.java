package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.AddItemRequest;
import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.dto.PhieuGiamGiaDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;
import com.vn.test.bshoes.dto.ThanhToanRequest;
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

    /** Open a new empty pending invoice (optionally attach the staff who opened it). */
    @PostMapping("/create-empty")
    public HoaDonDto createEmpty(@RequestParam(required = false) Integer idNhanVien) {
        return service.createEmpty(idNhanVien);
    }

    /** Add a variant to the invoice's cart — decrements stock and refreshes totals. */
    @PostMapping("/{id}/items")
    public HoaDonDto addItem(@PathVariable int id, @RequestBody AddItemRequest req) {
        return service.addItem(id, req);
    }

    /** Set a cart line's quantity (0 removes it); adjusts stock by the delta. */
    @PutMapping("/items/{idChiTiet}")
    public HoaDonDto updateItem(@PathVariable int idChiTiet, @RequestParam int soLuong) {
        return service.updateItemQuantity(idChiTiet, soLuong);
    }

    /** Remove a cart line and return its units to stock. */
    @DeleteMapping("/items/{idChiTiet}")
    public HoaDonDto removeItem(@PathVariable int idChiTiet) {
        return service.removeItem(idChiTiet);
    }

    /** Finalize payment for a pending invoice. */
    @PostMapping("/{id}/thanh-toan")
    public HoaDonDto thanhToan(@PathVariable int id, @RequestBody ThanhToanRequest req) {
        return service.thanhToan(id, req);
    }

    /** Cancel a pending invoice (restores stock). */
    @PostMapping("/{id}/huy")
    public HoaDonDto huy(@PathVariable int id, @RequestParam(required = false) Integer idNhanVien) {
        return service.huy(id, idNhanVien);
    }

    /** Mark a delivery order as delivered. */
    @PostMapping("/{id}/da-giao")
    public HoaDonDto daGiao(@PathVariable int id) { return service.daGiao(id); }

    /** Return goods (restores stock). */
    @PostMapping("/{id}/tra-hang")
    public HoaDonDto traHang(@PathVariable int id, @RequestParam(required = false) Integer idNhanVien) {
        return service.traHang(id, idNhanVien);
    }
}
