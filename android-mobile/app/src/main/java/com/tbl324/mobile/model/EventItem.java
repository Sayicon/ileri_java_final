package com.tbl324.mobile.model;

public class EventItem {
    private long id;
    private String title;
    private String description;
    private Long venueId;
    private String status;

    public EventItem() {}

    public long getId()          { return id; }
    public String getTitle()     { return title; }
    public String getName()      { return title; }
    public String getDescription() { return description; }
    public Long getVenueId()     { return venueId; }
    public String getStatus()    { return status; }

    @Override
    public String toString() { return title != null ? title : ""; }
}
