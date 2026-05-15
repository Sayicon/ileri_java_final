package com.tbl324.mobile.api;

import com.tbl324.mobile.model.TicketItem;

public class TicketResponse {
    private TicketItem data;
    public TicketItem getData() { return data; }
    public void setData(TicketItem data) { this.data = data; }
}
