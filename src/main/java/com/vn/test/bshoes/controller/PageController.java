package com.vn.test.bshoes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Phase 1 view-only controller. Maps each demo route to its Thymeleaf template.
 * Replaced/augmented by real data controllers in Phase 2.
 */
@Controller
public class PageController {

    @GetMapping("/")               public String dashboard() { return "dashboard"; }
    @GetMapping("/login")          public String login()     { return "login"; }
    @GetMapping("/san-pham")       public String sanPham()   { return "san-pham"; }
    @GetMapping("/nhan-vien")      public String nhanVien()  { return "nhan-vien"; }
    @GetMapping("/khach-hang")     public String khachHang() { return "khach-hang"; }
    @GetMapping("/hoa-don")        public String hoaDon()    { return "hoa-don"; }
    @GetMapping("/lich-su")        public String lichSu()    { return "lich-su"; }
    @GetMapping("/phieu-giam-gia") public String phieuGiam() { return "phieu-giam-gia"; }
    @GetMapping("/he-thong")       public String heThong()   { return "he-thong"; }
}
