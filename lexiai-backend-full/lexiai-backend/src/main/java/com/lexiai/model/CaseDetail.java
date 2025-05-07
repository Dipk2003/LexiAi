package com.lexiai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_details")
public class CaseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String caseTitle;

    private String summary;

    private String outcome;

    private String proof; // e.g., URL or text summary

    private LocalDateTime searchedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User searchedBy;

    @ManyToOne
    @JoinColumn(name = "firm_id")
    private LawFirm firm;

    // Constructors
    public CaseDetail() {}

    public CaseDetail(String caseTitle, String summary, String outcome, String proof, User searchedBy, LawFirm firm) {
        this.caseTitle = caseTitle;
        this.summary = summary;
        this.outcome = outcome;
        this.proof = proof;
        this.searchedBy = searchedBy;
        this.firm = firm;
        this.searchedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getCaseTitle() { return caseTitle; }

    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }

    public String getSummary() { return summary; }

    public void setSummary(String summary) { this.summary = summary; }

    public String getOutcome() { return outcome; }

    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getProof() { return proof; }

    public void setProof(String proof) { this.proof = proof; }

    public LocalDateTime getSearchedAt() { return searchedAt; }

    public void setSearchedAt(LocalDateTime searchedAt) { this.searchedAt = searchedAt; }

    public User getSearchedBy() { return searchedBy; }

    public void setSearchedBy(User searchedBy) { this.searchedBy = searchedBy; }

    public LawFirm getFirm() { return firm; }

    public void setFirm(LawFirm firm) { this.firm = firm; }
}
