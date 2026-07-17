package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.NhanVienDto;
import com.vn.test.bshoes.dto.NhanVienQuyenDto;
import com.vn.test.bshoes.service.NhanVienQuyenService;
import com.vn.test.bshoes.service.NhanVienService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhan-vien")
public class NhanVienController {

    private final NhanVienService service;
    private final NhanVienQuyenService quyenService;

    public NhanVienController(NhanVienService service, NhanVienQuyenService quyenService) {
        this.service = service;
        this.quyenService = quyenService;
    }

    @GetMapping
    public List<NhanVienDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public NhanVienDto findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public NhanVienDto create(@RequestBody NhanVienDto dto) { return service.create(dto); }

    @PutMapping
    public NhanVienDto update(@RequestBody NhanVienDto dto) { return service.update(dto); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }

    @GetMapping("/search")
    public List<NhanVienDto> search(@RequestParam String ten, @RequestParam(defaultValue = "all") String gioiTinh) {
        return service.search(ten, gioiTinh);
    }

    // --- phân quyền theo từng nhân viên (tab trong màn Nhân Viên) ---

    /** Lưới phân quyền: mọi nhân viên + quyền hiệu lực. Khai báo trước /{id} không sao —
     *  Spring ưu tiên segment tĩnh hơn biến đường dẫn. */
    @GetMapping("/quyen")
    public List<NhanVienQuyenDto> bangQuyen() { return quyenService.bangQuyen(); }

    @PutMapping("/{id}/quyen")
    public NhanVienQuyenDto luuQuyen(@PathVariable int id, @RequestBody List<String> manHinhs) {
        return quyenService.luuQuyen(id, manHinhs);
    }

    @PostMapping("/{id}/quyen/ap-template")
    public NhanVienQuyenDto apTemplate(@PathVariable int id) { return quyenService.apTemplate(id); }
}
