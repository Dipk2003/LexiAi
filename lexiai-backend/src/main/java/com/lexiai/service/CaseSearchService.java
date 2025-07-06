package com.lexiai.service;

import com.lexiai.dto.CaseSearchRequest;
import com.lexiai.dto.CaseSearchResponse;
import com.lexiai.model.LegalCase;
import com.lexiai.model.SearchHistory;
import com.lexiai.model.Lawyer;
import com.lexiai.repository.LegalCaseRepository;
import com.lexiai.repository.SearchHistoryRepository;
import com.lexiai.repository.LawyerRepository;
import com.lexiai.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CaseSearchService {

    @Autowired
    private LegalCaseRepository legalCaseRepository;
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    @Autowired
    private LawyerRepository lawyerRepository;
    
    @Autowired
    private IndianCourtScraperService scraperService;

    @Transactional
    public CaseSearchResponse searchCases(CaseSearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        // First search in database
        List<LegalCase> databaseResults = searchInDatabase(request);
        
        List<LegalCase> allResults = new ArrayList<>(databaseResults);
        String dataSource = "database";
        
        // If no results found in database, try web scraping
        if (databaseResults.isEmpty()) {
            List<LegalCase> scrapedResults = searchExternal(request);
            if (!scrapedResults.isEmpty()) {
                // Save scraped results to database
                for (LegalCase legalCase : scrapedResults) {
                    legalCaseRepository.save(legalCase);
                }
                allResults.addAll(scrapedResults);
                dataSource = "web_scraping";
            }
        } else if (databaseResults.size() < 5) {
            // If we have some results but not many, supplement with web scraping
            List<LegalCase> scrapedResults = searchExternal(request);
            allResults.addAll(scrapedResults);
            dataSource = "mixed";
        }
        
        // Update search count for found cases
        allResults.forEach(LegalCase::incrementSearchCount);
        legalCaseRepository.saveAll(allResults);
        
        // Record search history
        recordSearchHistory(request, allResults.size(), dataSource, System.currentTimeMillis() - startTime);
        
        // Prepare paginated response
        int totalResults = allResults.size();
        int fromIndex = request.getPage() * request.getSize();
        int toIndex = Math.min(fromIndex + request.getSize(), totalResults);
        
        List<LegalCase> paginatedResults = fromIndex < totalResults ? 
            allResults.subList(fromIndex, toIndex) : new ArrayList<>();
        
        CaseSearchResponse response = new CaseSearchResponse();
        response.setCases(paginatedResults);
        response.setTotalResults(totalResults);
        response.setCurrentPage(request.getPage());
        response.setTotalPages((int) Math.ceil((double) totalResults / request.getSize()));
        response.setSearchQuery(request.getQuery());
        response.setDataSource(dataSource);
        response.setResponseTimeMs(System.currentTimeMillis() - startTime);
        response.setHasMoreResults(toIndex < totalResults);
        
        return response;
    }
    
    public LegalCase getCaseById(Long id) {
        Optional<LegalCase> caseOpt = legalCaseRepository.findById(id);
        if (caseOpt.isPresent()) {
            LegalCase legalCase = caseOpt.get();
            legalCase.incrementSearchCount();
            return legalCaseRepository.save(legalCase);
        }
        return null;
    }
    
    public LegalCase getCaseByCaseNumber(String caseNumber) {
        Optional<LegalCase> caseOpt = legalCaseRepository.findByCaseNumber(caseNumber);
        if (caseOpt.isPresent()) {
            LegalCase legalCase = caseOpt.get();
            legalCase.incrementSearchCount();
            return legalCaseRepository.save(legalCase);
        }
        return null;
    }
    
    @Cacheable(value = "popularCases", key = "#limit")
    public List<LegalCase> getPopularCases(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return legalCaseRepository.findMostSearchedCases().stream()
            .limit(limit)
            .toList();
    }
    
    @Cacheable(value = "recentCases", key = "#limit")
    public List<LegalCase> getRecentCases(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return legalCaseRepository.findRecentCases().stream()
            .limit(limit)
            .toList();
    }

    private List<LegalCase> searchInDatabase(CaseSearchRequest request) {
        String searchType = request.getSearchType();
        String query = request.getQuery();
        
        if ("case_number".equals(searchType)) {
            Optional<LegalCase> caseOpt = legalCaseRepository.findByCaseNumber(query);
            return caseOpt.map(List::of).orElse(new ArrayList<>());
        } else if ("party".equals(searchType)) {
            return legalCaseRepository.findByParty(query);
        } else if ("title".equals(searchType)) {
            return legalCaseRepository.findByTitleContainingIgnoreCase(query);
        } else {
            // Default to keyword search
            if (request.getCaseType() != null || request.getCourtName() != null || request.getJurisdiction() != null) {
                return legalCaseRepository.findByMultipleCriteria(
                    request.getCaseType(),
                    request.getJurisdiction(),
                    request.getCourtName(),
                    query
                );
            } else {
                return legalCaseRepository.searchByKeyword(query);
            }
        }
    }

    private List<LegalCase> searchExternal(CaseSearchRequest request) {
        List<LegalCase> results = new ArrayList<>();
        String query = request.getQuery();
        String courtName = request.getCourtName();
        
        try {
            // Try different scraping strategies based on search type
            if ("case_number".equals(request.getSearchType())) {
                // If it looks like a case number, try specific court scrapers
                if (courtName != null) {
                    if (courtName.toLowerCase().contains("supreme")) {
                        results.addAll(scraperService.scrapeSupremeCourt(query));
                    } else {
                        results.addAll(scraperService.scrapeHighCourt(query, courtName));
                    }
                } else {
                    // Try eCourts first
                    results.addAll(scraperService.scrapeECourts(query, "Generic"));
                }
            } else {
                // For other search types, try eCourts
                results.addAll(scraperService.scrapeECourts(query, courtName != null ? courtName : "Generic"));
            }
        } catch (Exception e) {
            // Log error but don't fail the entire search
            System.err.println("Web scraping failed: " + e.getMessage());
        }
        
        return results;
    }
    
    private void recordSearchHistory(CaseSearchRequest request, int resultsCount, String dataSource, long responseTime) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
                Optional<Lawyer> lawyerOpt = lawyerRepository.findByEmail(userPrincipal.getEmail());
                
                if (lawyerOpt.isPresent()) {
                    SearchHistory history = new SearchHistory();
                    history.setSearchQuery(request.getQuery());
                    history.setSearchType(request.getSearchType());
                    history.setResultsCount(resultsCount);
                    history.setDataSource(dataSource);
                    history.setResponseTimeMs(responseTime);
                    history.setLawyer(lawyerOpt.get());
                    
                    searchHistoryRepository.save(history);
                }
            }
        } catch (Exception e) {
            // Don't fail the search if history recording fails
            System.err.println("Failed to record search history: " + e.getMessage());
        }
    }
}
