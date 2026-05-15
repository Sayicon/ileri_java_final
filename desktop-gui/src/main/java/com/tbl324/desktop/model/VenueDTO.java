package com.tbl324.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VenueDTO(Long id, String name, String address, int capacity) {}
