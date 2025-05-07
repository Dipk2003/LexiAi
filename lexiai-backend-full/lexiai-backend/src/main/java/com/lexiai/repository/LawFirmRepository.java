package com.lexiai.repository;

import com.lexiai.model.LawFirm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LawFirmRepository extends JpaRepository<LawFirm, Long> {
    Optional<LawFirm> findByFirmName(String firmName);
}
