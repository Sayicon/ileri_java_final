package com.tbl324.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketDTO(Long id, Long eventId, Long seatId, Long userId, String status) {}
