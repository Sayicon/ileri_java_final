package com.tbl324.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EventDTO(Long id, String title, String description, Long venueId, String status) {
    @Override public String toString() { return title; }
}
