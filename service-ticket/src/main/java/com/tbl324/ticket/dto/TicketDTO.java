package com.tbl324.ticket.dto;

import com.tbl324.ticket.domain.TicketStatus;

public record TicketDTO(
        Long id,
        Long eventId,
        Long seatId,
        Long userId,
        TicketStatus status
) {}
