package com.lexiai.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "law_firms")
public class LawFirm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firmName;

    @OneToMany(mappedBy = "lawFirm", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> lawyers = new HashSet<>();

    // Constructors
    public LawFirm() {}

    public LawFirm(String firmName) {
        this.firmName = firmName;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getFirmName() { return firmName; }

    public void setFirmName(String firmName) { this.firmName = firmName; }

    public Set<User> getLawyers() { return lawyers; }

    public void setLawyers(Set<User> lawyers) { this.lawyers = lawyers; }
}
