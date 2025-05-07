package com.lexiai.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String role; // Values: "LAWYER" or "FIRM_ADMIN"

    @ManyToOne
    @JoinColumn(name = "firm_id")
    private LawFirm lawFirm;

    // Constructors
    public User() {}

    public User(String name, String email, String password, String role, LawFirm lawFirm) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.lawFirm = lawFirm;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public LawFirm getLawFirm() { return lawFirm; }

    public void setLawFirm(LawFirm lawFirm) { this.lawFirm = lawFirm; }
}
