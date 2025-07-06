package com.lexiai.controller;

import com.lexiai.model.Lawyer;
import com.lexiai.model.SearchHistory;
import com.lexiai.repository.LawyerRepository;
import com.lexiai.repository.SearchHistoryRepository;
import com.lexiai.security.UserPrincipal;
import com.lexiai.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private LawyerRepository lawyerRepository;
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<Lawyer> getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        
        Optional<Lawyer> lawyer = lawyerRepository.findByEmail(userPrincipal.getEmail());
        if (lawyer.isPresent()) {
            return ResponseEntity.ok(lawyer.get());
        } else {
            throw new ResourceNotFoundException("Lawyer", "email", userPrincipal.getEmail());
        }
    }
    
    @PutMapping("/profile")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<Lawyer> updateProfile(@RequestBody Lawyer updatedLawyer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        
        Optional<Lawyer> lawyerOpt = lawyerRepository.findByEmail(userPrincipal.getEmail());
        if (lawyerOpt.isPresent()) {
            Lawyer lawyer = lawyerOpt.get();
            
            // Update allowed fields (don't allow changing email, password, or firm)
            lawyer.setFirstName(updatedLawyer.getFirstName());
            lawyer.setLastName(updatedLawyer.getLastName());
            lawyer.setPhoneNumber(updatedLawyer.getPhoneNumber());
            lawyer.setSpecialization(updatedLawyer.getSpecialization());
            lawyer.setBarNumber(updatedLawyer.getBarNumber());
            lawyer.setYearsOfExperience(updatedLawyer.getYearsOfExperience());
            
            Lawyer savedLawyer = lawyerRepository.save(lawyer);
            return ResponseEntity.ok(savedLawyer);
        } else {
            throw new ResourceNotFoundException("Lawyer", "email", userPrincipal.getEmail());
        }
    }
    
    @GetMapping("/search-history")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<List<SearchHistory>> getSearchHistory(
            @RequestParam(required = false, defaultValue = "50") int limit) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        
        Optional<Lawyer> lawyer = lawyerRepository.findByEmail(userPrincipal.getEmail());
        if (lawyer.isPresent()) {
            List<SearchHistory> history = searchHistoryRepository
                .findByLawyerIdOrderBySearchDateDesc(lawyer.get().getId())
                .stream()
                .limit(limit)
                .toList();
            return ResponseEntity.ok(history);
        } else {
            throw new ResourceNotFoundException("Lawyer", "email", userPrincipal.getEmail());
        }
    }
    
    @GetMapping("/search-stats")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<SearchStats> getSearchStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        
        Optional<Lawyer> lawyer = lawyerRepository.findByEmail(userPrincipal.getEmail());
        if (lawyer.isPresent()) {
            Long totalSearches = searchHistoryRepository.countByLawyerId(lawyer.get().getId());
            List<Object[]> topQueries = searchHistoryRepository
                .findTopSearchQueriesByLawyer(lawyer.get().getId())
                .stream()
                .limit(5)
                .toList();
            
            SearchStats stats = new SearchStats();
            stats.setTotalSearches(totalSearches);
            stats.setTopQueries(topQueries);
            
            return ResponseEntity.ok(stats);
        } else {
            throw new ResourceNotFoundException("Lawyer", "email", userPrincipal.getEmail());
        }
    }
    
    // Inner class for search statistics
    public static class SearchStats {
        private Long totalSearches;
        private List<Object[]> topQueries;
        
        public Long getTotalSearches() { return totalSearches; }
        public void setTotalSearches(Long totalSearches) { this.totalSearches = totalSearches; }
        
        public List<Object[]> getTopQueries() { return topQueries; }
        public void setTopQueries(List<Object[]> topQueries) { this.topQueries = topQueries; }
    }
}
