package ru.mirea.conference.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Registration {
    private Long id;
    private Long participantId;
    private Long conferenceId;
    private RegistrationStatus status;
    private LocalDateTime registeredAt;

    private String participantName;
    private String conferenceTitle;

    public Registration() { }

    public Registration(Long id, Long participantId, Long conferenceId,
                        RegistrationStatus status, LocalDateTime registeredAt) {
        this.id = id;
        this.participantId = participantId;
        this.conferenceId = conferenceId;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    public Registration(Long participantId, Long conferenceId, RegistrationStatus status) {
        this(null, participantId, conferenceId, status, null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public Long getConferenceId() { return conferenceId; }
    public void setConferenceId(Long conferenceId) { this.conferenceId = conferenceId; }

    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public String getConferenceTitle() { return conferenceTitle; }
    public void setConferenceTitle(String conferenceTitle) { this.conferenceTitle = conferenceTitle; }

    @Override
    public String toString() {
        return String.format("Registration{id=%d, participant=%s, conference=%s, status=%s, at=%s}",
                id,
                participantName != null ? participantName : participantId,
                conferenceTitle != null ? conferenceTitle : conferenceId,
                status, registeredAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Registration)) return false;
        Registration that = (Registration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
