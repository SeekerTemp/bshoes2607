package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.XuatSu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XuatSuRepository extends JpaRepository<XuatSu, Integer> {
}
