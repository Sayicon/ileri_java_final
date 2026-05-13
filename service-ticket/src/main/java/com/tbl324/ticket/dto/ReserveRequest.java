package com.tbl324.ticket.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class ReserveRequest {

    @NotNull private final Long eventId;
    @NotNull private final Long seatId;
    @NotNull private final Long userId;

    @JsonCreator
    public ReserveRequest(
            @JsonProperty("eventId") Long eventId,
            @JsonProperty("seatId")  Long seatId,
            @JsonProperty("userId")  Long userId) {
        this.eventId = eventId;
        this.seatId  = seatId;
        this.userId  = userId;
    }

    public Long getEventId() { return eventId; }
    public Long getSeatId()  { return seatId; }
    public Long getUserId()  { return userId; }
}
