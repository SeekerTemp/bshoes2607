package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.DatTruocDto;
import com.vn.test.bshoes.dto.DatTruocRequest;
import com.vn.test.bshoes.service.DatTruocService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dat-truoc")
public class DatTruocController {

    private final DatTruocService service;

    public DatTruocController(DatTruocService service) {
        this.service = service;
    }

    @GetMapping
    public List<DatTruocDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public DatTruocDto findById(@PathVariable int id) { return service.findById(id); }

    /** Khách đăng ký đặt trước từ trang cửa hàng. */
    @PostMapping
    public DatTruocDto dangKy(@RequestBody DatTruocRequest req) { return service.dangKy(req); }

    /** Số người đang chờ hàng của một biến thể (hiện trên trang cửa hàng). */
    @GetMapping("/dem")
    public Map<String, Long> demChoHang(@RequestParam int idSanPhamChiTiet) {
        return Map.of("choHang", service.demChoHang(idSanPhamChiTiet));
    }

    @PutMapping("/{id}/trang-thai")
    public DatTruocDto capNhatTrangThai(@PathVariable int id, @RequestParam String trangThai) {
        return service.capNhatTrangThai(id, trangThai);
    }

    /** Chuyển phiếu đặt trước thành hóa đơn chờ (giữ hàng cho khách). */
    @PostMapping("/{id}/chuyen-don")
    public DatTruocDto chuyenDon(@PathVariable int id, @RequestParam(required = false) Integer idNhanVien) {
        return service.chuyenDon(id, idNhanVien);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable int id) { service.softDelete(id); }
}
