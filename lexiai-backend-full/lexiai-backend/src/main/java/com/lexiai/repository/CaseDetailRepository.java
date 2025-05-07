package com.lexiai.repository;

import com.lexiai.model.CaseDetail;
import com.lexiai.model.LawFirm;
import com.lexiai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseDetailRepository extends JpaRepository<CaseDetail, Long> {
    List<CaseDetail> findBySearchedBy(User user);
    List<CaseDetail> findByFirm(LawFirm firm);
    List<CaseDetail> findByCaseTitleContainingIgnoreCase(String keyword);
}
