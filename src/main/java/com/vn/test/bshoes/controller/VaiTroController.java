package com.vn.test.bshoes.controller;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.dto.PhanQuyenDto;
import com.vn.test.bshoes.service.VaiTroService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vai-tro")
public class VaiTroController {
    private final VaiTroService service;
    public VaiTroController(VaiTroService service) { this.service = service; }
    @GetMapping public List<AttributeDto> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public AttributeDto findById(@PathVariable int id) { return service.findById(id); }

    /** Roles + their allowed-screen permission set (phân quyền panel). */
    @GetMapping("/quyen") public List<PhanQuyenDto> findAllQuyen() { return service.findAllQuyen(); }

    /** Save a role's permission set. Body: { "quyen": "dashboard,hoa-don,..." } or "*". */
    @PutMapping("/{id}/quyen")
    public PhanQuyenDto updateQuyen(@PathVariable int id, @RequestBody Map<String, String> body) {
        return service.updateQuyen(id, body.getOrDefault("quyen", ""));
    }
}
