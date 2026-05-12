package com.tbl324.event.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class CreateEventRequest {

    @NotBlank
    private final String title;

    private final String description;

    @NotNull
    private final Long venueId;

    @NotNull
    @Future
    private final LocalDateTime startTime;

    @NotNull
    private final LocalDateTime endTime;

    @JsonCreator
    public CreateEventRequest(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("venueId") Long venueId,
            @JsonProperty("startTime") LocalDateTime startTime,
            @JsonProperty("endTime") LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.venueId = venueId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTitle()            { return title; }
    public String getDescription()      { return description; }
    public Long getVenueId()            { return venueId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime()   { return endTime; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String title;
        private String description;
        private Long venueId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public Builder title(String t)           { this.title = t; return this; }
        public Builder description(String d)     { this.description = d; return this; }
        public Builder venueId(Long v)           { this.venueId = v; return this; }
        public Builder startTime(LocalDateTime t){ this.startTime = t; return this; }
        public Builder endTime(LocalDateTime t)  { this.endTime = t; return this; }
        public CreateEventRequest build() {
            return new CreateEventRequest(title, description, venueId, startTime, endTime);
        }
    }
}
