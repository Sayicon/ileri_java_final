package com.tbl324.event.dto;

public final class SeatDTO {
    private final Long id;
    private final Long venueId;
    private final String rowLabel;
    private final int seatNumber;
    private final String status;

    private SeatDTO(Builder b) {
        this.id = b.id;
        this.venueId = b.venueId;
        this.rowLabel = b.rowLabel;
        this.seatNumber = b.seatNumber;
        this.status = b.status;
    }

    public Long getId()          { return id; }
    public Long getVenueId()     { return venueId; }
    public String getRowLabel()  { return rowLabel; }
    public int getSeatNumber()   { return seatNumber; }
    public String getStatus()    { return status; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long id;
        private Long venueId;
        private String rowLabel;
        private int seatNumber;
        private String status;

        public Builder id(Long id)          { this.id = id; return this; }
        public Builder venueId(Long v)      { this.venueId = v; return this; }
        public Builder rowLabel(String r)   { this.rowLabel = r; return this; }
        public Builder seatNumber(int n)    { this.seatNumber = n; return this; }
        public Builder status(String s)     { this.status = s; return this; }
        public SeatDTO build()              { return new SeatDTO(this); }
    }
}
