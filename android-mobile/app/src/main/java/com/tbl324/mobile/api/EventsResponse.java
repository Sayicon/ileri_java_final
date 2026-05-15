package com.tbl324.mobile.api;

import com.tbl324.mobile.model.EventItem;

import java.util.List;

public class EventsResponse {
    private EventsData data;
    public EventsData getData() { return data; }

    public static class EventsData {
        private List<EventItem> content;
        public List<EventItem> getContent() { return content; }
    }
}
