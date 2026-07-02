package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.ChatLieu;
import com.vn.test.bshoes.service.ChatLieuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-lieu")
public class ChatLieuController {

    private final ChatLieuService service;

    public ChatLieuController(ChatLieuService service) {
        this.service = service;
    }

    @GetMapping
    public List<ChatLieu> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ChatLieu findById(@PathVariable int id) { return service.findById(id); }

    @PostMapping
    public ChatLieu create(@RequestBody ChatLieu e) { return service.create(e); }

    @PutMapping
    public ChatLieu update(@RequestBody ChatLieu e) { service.update(e); return e; }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }
}
