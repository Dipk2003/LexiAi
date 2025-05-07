package com.lexiai.service;

import com.lexiai.model.CaseDetail;
import com.lexiai.model.LawFirm;
import com.lexiai.model.User;
import com.lexiai.repository.CaseDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseDetailService {
    private final CaseDetailRepository caseDetailRepository;

    public CaseDetailService(CaseDetailRepository caseDetailRepository) {
        this.caseDetailRepository = caseDetailRepository;
    }

    public CaseDetail saveCaseDetail(CaseDetail caseDetail) {
        return caseDetailRepository.save(caseDetail);
    }

    public List<CaseDetail> getCasesByUser(User user) {
        return caseDetailRepository.findBySearchedBy(user);
    }

    public List<CaseDetail> getCasesByFirm(LawFirm firm) {
        return caseDetailRepository.findByFirm(firm);
    }

    public List<CaseDetail> searchCases(String keyword) {
        return caseDetailRepository.findByCaseTitleContainingIgnoreCase(keyword);
    }
}
