package com.tbl324.ticket.domain;

import java.time.LocalDateTime;

public class Ticket {

    private final Long id;
    private final Long eventId;
    private final Long seatId;
    private final Long userId;
    private final TicketStatus status;
    private final LocalDateTime reservedAt;
    private final LocalDateTime expiresAt;

    private Ticket(Builder b) {
        this.id         = b.id;
        this.eventId    = b.eventId;
        this.seatId     = b.seatId;
        this.userId     = b.userId;
        this.status     = b.status;
        this.reservedAt = b.reservedAt;
        this.expiresAt  = b.expiresAt;
    }

    public Long getId()                  { return id; }
    public Long getEventId()             { return eventId; }
    public Long getSeatId()              { return seatId; }
    public Long getUserId()              { return userId; }
    public TicketStatus getStatus()      { return status; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public LocalDateTime getExpiresAt()  { return expiresAt; }

    public Ticket withStatus(TicketStatus newStatus) {
        return builder()
                .id(this.id).eventId(this.eventId).seatId(this.seatId).userId(this.userId)
                .status(newStatus).reservedAt(this.reservedAt).expiresAt(this.expiresAt)
                .build();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long eventId;
        private Long seatId;
        private Long userId;
        private TicketStatus status;
        private LocalDateTime reservedAt;
        private LocalDateTime expiresAt;

        public Builder id(Long id)                        { this.id = id; return this; }
        public Builder eventId(Long eventId)              { this.eventId = eventId; return this; }
        public Builder seatId(Long seatId)                { this.seatId = seatId; return this; }
        public Builder userId(Long userId)                { this.userId = userId; return this; }
        public Builder status(TicketStatus status)        { this.status = status; return this; }
        public Builder reservedAt(LocalDateTime t)        { this.reservedAt = t; return this; }
        public Builder expiresAt(LocalDateTime t)         { this.expiresAt = t; return this; }
        public Ticket build()                             { return new Ticket(this); }
    }
}
