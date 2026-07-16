package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.CheckoutRequest;
import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.service.HoaDonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Giỏ hàng phía khách. Giỏ nằm ở trình duyệt (localStorage) nên ở đây chỉ có
 * 3 việc chạm DB: đặt hàng, xem đơn của mình, xác nhận đã nhận.
 */
@RestController
@RequestMapping("/api/gio-hang")
public class GioHangController {

    private final HoaDonService hoaDonService;

    public GioHangController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    /** Khách đặt hàng → hoá đơn "Chờ giao" (COD), nhân viên xử lý ở màn Giao Hàng. */
    @PostMapping("/checkout")
    public HoaDonDto checkout(@RequestBody CheckoutRequest req) { return hoaDonService.datHangOnline(req); }

    /** Tra cứu đơn của khách theo SĐT (chưa có đăng nhập cho khách). */
    @GetMapping("/don-hang")
    public List<HoaDonDto> donHang(@RequestParam String soDienThoai) {
        return hoaDonService.findBySoDienThoai(soDienThoai);
    }

    /** Khách xác nhận đã nhận hàng — dùng lại daGiao (Chờ giao → Đã giao, có ghi lịch sử). */
    @PutMapping("/nhan-hang/{idHoaDon}")
    public HoaDonDto nhanHang(@PathVariable int idHoaDon) { return hoaDonService.daGiao(idHoaDon); }
}
