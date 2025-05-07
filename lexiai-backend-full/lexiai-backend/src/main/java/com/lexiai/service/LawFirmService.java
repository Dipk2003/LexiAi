package com.lexiai.service;

import com.lexiai.model.LawFirm;
import com.lexiai.repository.LawFirmRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LawFirmService {
    private final LawFirmRepository firmRepository;

    public LawFirmService(LawFirmRepository firmRepository) {
        this.firmRepository = firmRepository;
    }

    public Optional<LawFirm> getFirmByName(String name) {
        return firmRepository.findByFirmName(name);
    }

    public LawFirm saveFirm(LawFirm firm) {
        return firmRepository.save(firm);
    }
}
