package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.VaiTro;
import com.vn.test.bshoes.service.VaiTroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vai-tro")
public class VaiTroController {

    private final VaiTroService service;

    public VaiTroController(VaiTroService service) {
        this.service = service;
    }

    @GetMapping
    public List<VaiTro> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public VaiTro findById(@PathVariable int id) { return service.findById(id); }
}
