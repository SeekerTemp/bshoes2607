package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.ChatLieu;
import com.vn.test.bshoes.repository.ChatLieuRepository;
import com.vn.test.bshoes.service.ChatLieuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatLieuServiceImpl implements ChatLieuService {

    private final ChatLieuRepository repo;

    public ChatLieuServiceImpl(ChatLieuRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ChatLieu> findAll() {
        return repo.findAll();
    }

    @Override
    public ChatLieu findById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public ChatLieu findByTen(String ten) {
        return repo.findByTen(ten);
    }

    @Override
    @Transactional
    public ChatLieu create(ChatLieu e) {
        // Legacy pattern: insert, then set business code "CL" + generated id.
        ChatLieu saved = repo.save(e);
        String ma = "CL" + saved.getId_chat_lieu();
        saved.setMa_chat_lieu(ma);
        repo.updateMa(ma, saved.getId_chat_lieu());
        return saved;
    }

    @Override
    @Transactional
    public void update(ChatLieu e) {
        repo.updateByMa(e.getTen_chat_lieu(), e.getMa_chat_lieu());
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
