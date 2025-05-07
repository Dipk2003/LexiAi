package com.lexiai.controller;

import com.lexiai.model.CaseDetail;
import com.lexiai.model.LawFirm;
import com.lexiai.model.User;
import com.lexiai.service.CaseDetailService;
import com.lexiai.service.LawFirmService;
import com.lexiai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin
public class CaseDetailController {
    private final CaseDetailService caseService;
    private final UserService userService;
    private final LawFirmService firmService;

    public CaseDetailController(CaseDetailService caseService, UserService userService, LawFirmService firmService) {
        this.caseService = caseService;
        this.userService = userService;
        this.firmService = firmService;
    }

    @PostMapping("/search")
    public ResponseEntity<CaseDetail> saveSearch(@RequestBody CaseDetail caseDetail) {
        return ResponseEntity.ok(caseService.saveCaseDetail(caseDetail));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<CaseDetail>> getCasesByUser(@PathVariable String email) {
        User user = userService.getUserByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(caseService.getCasesByUser(user));
    }

    @GetMapping("/firm/{firmName}")
    public ResponseEntity<List<CaseDetail>> getCasesByFirm(@PathVariable String firmName) {
        LawFirm firm = firmService.getFirmByName(firmName).orElse(null);
        if (firm == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(caseService.getCasesByFirm(firm));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<CaseDetail>> searchCases(@PathVariable String keyword) {
        return ResponseEntity.ok(caseService.searchCases(keyword));
    }
}
