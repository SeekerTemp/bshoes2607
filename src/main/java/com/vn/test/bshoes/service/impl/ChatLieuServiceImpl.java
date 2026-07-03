package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.entity.ChatLieu;
import com.vn.test.bshoes.repository.ChatLieuRepository;
import com.vn.test.bshoes.service.ChatLieuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChatLieuServiceImpl implements ChatLieuService {
    private final ChatLieuRepository repo;
    public ChatLieuServiceImpl(ChatLieuRepository repo) { this.repo = repo; }

    private AttributeDto toDto(ChatLieu e) {
        return new AttributeDto(e.getId(), e.getMaChatLieu(), e.getTenChatLieu(), e.getTrangThai());
    }
    @Override public List<AttributeDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeDto create(AttributeDto dto) {
        ChatLieu e = new ChatLieu();
        e.setTenChatLieu(dto.getTen());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        e = repo.save(e);
        e.setMaChatLieu("CL" + e.getId());
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeDto update(AttributeDto dto) {
        ChatLieu e = repo.findById(dto.getId()).orElseThrow();
        e.setTenChatLieu(dto.getTen());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
