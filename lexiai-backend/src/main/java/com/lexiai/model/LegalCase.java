package com.lexiai.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "legal_cases")
public class LegalCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_number", unique = true)
    private String caseNumber;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "court_name")
    private String courtName;

    @Column(name = "case_type")
    private String caseType; // e.g., "Civil", "Criminal", "Constitutional"

    @Column(name = "case_status")
    private String caseStatus; // e.g., "Active", "Closed", "Pending"

    @Column(name = "filing_date")
    private LocalDate filingDate;

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    @Column(name = "judge_name")
    private String judgeName;

    @Column(name = "plaintiff")
    private String plaintiff;

    @Column(name = "defendant")
    private String defendant;

    @Column(name = "legal_citation")
    private String legalCitation;

    @Column(name = "jurisdiction")
    private String jurisdiction;

    @Column(name = "case_summary", columnDefinition = "TEXT")
    private String caseSummary;

    @Column(name = "key_issues", columnDefinition = "TEXT")
    private String keyIssues;

    @Column(name = "legal_precedents", columnDefinition = "TEXT")
    private String legalPrecedents;

    @Column(name = "outcome")
    private String outcome;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "source_type")
    private String sourceType; // e.g., "API", "Web Scraping", "Manual Entry"

    @Column(name = "keywords")
    private String keywords; // Comma-separated keywords for searching

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "search_count")
    private Integer searchCount = 0; // Track how many times this case has been accessed

    // Constructors
    public LegalCase() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }

    public String getCaseStatus() { return caseStatus; }
    public void setCaseStatus(String caseStatus) { this.caseStatus = caseStatus; }

    public LocalDate getFilingDate() { return filingDate; }
    public void setFilingDate(LocalDate filingDate) { this.filingDate = filingDate; }

    public LocalDate getDecisionDate() { return decisionDate; }
    public void setDecisionDate(LocalDate decisionDate) { this.decisionDate = decisionDate; }

    public String getJudgeName() { return judgeName; }
    public void setJudgeName(String judgeName) { this.judgeName = judgeName; }

    public String getPlaintiff() { return plaintiff; }
    public void setPlaintiff(String plaintiff) { this.plaintiff = plaintiff; }

    public String getDefendant() { return defendant; }
    public void setDefendant(String defendant) { this.defendant = defendant; }

    public String getLegalCitation() { return legalCitation; }
    public void setLegalCitation(String legalCitation) { this.legalCitation = legalCitation; }

    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }

    public String getCaseSummary() { return caseSummary; }
    public void setCaseSummary(String caseSummary) { this.caseSummary = caseSummary; }

    public String getKeyIssues() { return keyIssues; }
    public void setKeyIssues(String keyIssues) { this.keyIssues = keyIssues; }

    public String getLegalPrecedents() { return legalPrecedents; }
    public void setLegalPrecedents(String legalPrecedents) { this.legalPrecedents = legalPrecedents; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getSearchCount() { return searchCount; }
    public void setSearchCount(Integer searchCount) { this.searchCount = searchCount; }

    public void incrementSearchCount() {
        this.searchCount = (this.searchCount == null) ? 1 : this.searchCount + 1;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
