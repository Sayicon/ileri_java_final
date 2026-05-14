package com.tbl324.mobile.model;

public class EventItem {
    private long id;
    private String name;
    private String venue;

    public EventItem() {}

    public EventItem(long id, String name, String venue) {
        this.id    = id;
        this.name  = name;
        this.venue = venue;
    }

    public long getId()      { return id; }
    public String getName()  { return name; }
    public String getVenue() { return venue; }
}
