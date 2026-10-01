package ru.mirea.conference.model;

import java.time.LocalDate;
import java.util.Objects;

public class Conference {
    private Long id;
    private String title;
    private ConferenceTrack track;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;

    public Conference() { }

    public Conference(Long id, String title, ConferenceTrack track,
                      LocalDate startDate, LocalDate endDate, String location) {
        this.id = id;
        this.title = title;
        this.track = track;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    public Conference(String title, ConferenceTrack track,
                      LocalDate startDate, LocalDate endDate, String location) {
        this(null, title, track, startDate, endDate, location);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ConferenceTrack getTrack() { return track; }
    public void setTrack(ConferenceTrack track) { this.track = track; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return String.format("Conference{id=%d, title='%s', track=%s, %s..%s, location='%s'}",
                id, title, track, startDate, endDate, location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conference)) return false;
        Conference that = (Conference) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
