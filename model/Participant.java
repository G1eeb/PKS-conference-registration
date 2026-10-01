package ru.mirea.conference.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Participant {
    private Long id;
    private String fullName;
    private String email;
    private String organization;
    private LocalDateTime createdAt;

    public Participant() { }

    public Participant(Long id, String fullName, String email, String organization, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.organization = organization;
        this.createdAt = createdAt;
    }

    public Participant(String fullName, String email, String organization) {
        this(null, fullName, email, organization, null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Participant{id=%d, fullName='%s', email='%s', organization='%s'}",
                id, fullName, email, organization);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        Participant that = (Participant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
