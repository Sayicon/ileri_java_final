package com.tbl324.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeatDTO(Long id, String rowLabel, int seatNumber, SeatStatus status) {}
