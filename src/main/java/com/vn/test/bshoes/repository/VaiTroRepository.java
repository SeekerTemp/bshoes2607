package com.vn.test.bshoes.repository;

import com.vn.test.bshoes.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

// NOTE: legacy VaiTro DAO was all stubs; inherited JpaRepository CRUD is sufficient.
public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {
}
