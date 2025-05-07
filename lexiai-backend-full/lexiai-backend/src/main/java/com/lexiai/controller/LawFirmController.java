package com.lexiai.controller;

import com.lexiai.model.LawFirm;
import com.lexiai.service.LawFirmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/firms")
@CrossOrigin
public class LawFirmController {
    private final LawFirmService firmService;

    public LawFirmController(LawFirmService firmService) {
        this.firmService = firmService;
    }

    @PostMapping("/register")
    public ResponseEntity<LawFirm> registerFirm(@RequestBody LawFirm firm) {
        return ResponseEntity.ok(firmService.saveFirm(firm));
    }
}

