package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.KichCo;
import com.vn.test.bshoes.service.KichCoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kich-co")
public class KichCoController {

    private final KichCoService service;

    public KichCoController(KichCoService service) {
        this.service = service;
    }

    @GetMapping
    public List<KichCo> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public KichCo findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public KichCo create(@RequestBody KichCo e) { return service.create(e); }

    @PutMapping
    public KichCo update(@RequestBody KichCo e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
