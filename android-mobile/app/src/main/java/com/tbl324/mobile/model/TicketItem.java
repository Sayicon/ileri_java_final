package com.tbl324.mobile.model;

public class TicketItem {
    private Long id;
    private Long eventId;
    private Long seatId;
    private Long userId;
    private String status;

    public Long getId()      { return id; }
    public Long getEventId() { return eventId; }
    public Long getSeatId()  { return seatId; }
    public Long getUserId()  { return userId; }
    public String getStatus(){ return status; }
}
