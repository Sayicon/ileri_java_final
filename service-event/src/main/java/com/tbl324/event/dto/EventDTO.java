package com.tbl324.event.dto;

import java.time.LocalDateTime;

public final class EventDTO {
    private final Long id;
    private final String title;
    private final String description;
    private final Long venueId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int totalSeats;
    private final int availableSeats;
    private final String status;

    private EventDTO(Builder b) {
        this.id = b.id;
        this.title = b.title;
        this.description = b.description;
        this.venueId = b.venueId;
        this.startTime = b.startTime;
        this.endTime = b.endTime;
        this.totalSeats = b.totalSeats;
        this.availableSeats = b.availableSeats;
        this.status = b.status;
    }

    public Long getId()               { return id; }
    public String getTitle()          { return title; }
    public String getDescription()    { return description; }
    public Long getVenueId()          { return venueId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime()   { return endTime; }
    public int getTotalSeats()        { return totalSeats; }
    public int getAvailableSeats()    { return availableSeats; }
    public String getStatus()         { return status; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long id;
        private String title;
        private String description;
        private Long venueId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private int totalSeats;
        private int availableSeats;
        private String status;

        public Builder id(Long id)                      { this.id = id; return this; }
        public Builder title(String title)              { this.title = title; return this; }
        public Builder description(String description)  { this.description = description; return this; }
        public Builder venueId(Long venueId)            { this.venueId = venueId; return this; }
        public Builder startTime(LocalDateTime t)       { this.startTime = t; return this; }
        public Builder endTime(LocalDateTime t)         { this.endTime = t; return this; }
        public Builder totalSeats(int n)                { this.totalSeats = n; return this; }
        public Builder availableSeats(int n)            { this.availableSeats = n; return this; }
        public Builder status(String s)                 { this.status = s; return this; }
        public EventDTO build()                         { return new EventDTO(this); }
    }
}
