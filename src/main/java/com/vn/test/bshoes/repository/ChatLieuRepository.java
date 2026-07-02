package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {

    @Query(value = "select * from chat_lieu where ten_chat_lieu = ?1", nativeQuery = true)
    ChatLieu findByTen(String ten);

    @Modifying
    @Transactional
    @Query(value = "UPDATE chat_lieu SET ma_chat_lieu = ?1 WHERE id_chat_lieu = ?2", nativeQuery = true)
    void updateMa(String ma, int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE chat_lieu SET ten_chat_lieu = ?1 WHERE ma_chat_lieu = ?2", nativeQuery = true)
    void updateByMa(String ten, String ma);
}
