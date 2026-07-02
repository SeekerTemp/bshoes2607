package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.ChatLieu;
import java.util.List;

public interface ChatLieuService {
    List<ChatLieu> findAll();
    ChatLieu findById(int id);
    ChatLieu findByTen(String ten);
    ChatLieu create(ChatLieu e);
    void update(ChatLieu e);
    void delete(int id);
}
