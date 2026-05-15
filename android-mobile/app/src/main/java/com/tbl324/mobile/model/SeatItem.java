package com.tbl324.mobile.model;

import java.util.Objects;

public class SeatItem {
    private long id;
    private String rowLabel;
    private int seatNumber;
    private String status;

    public SeatItem() {}

    public SeatItem(long id, String rowLabel, int seatNumber, String status) {
        this.id         = id;
        this.rowLabel   = rowLabel;
        this.seatNumber = seatNumber;
        this.status     = status;
    }

    public long getId()           { return id; }
    public String getRowLabel()   { return rowLabel; }
    public int getSeatNumber()    { return seatNumber; }
    public String getStatus()     { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRow() {
        if (rowLabel == null || rowLabel.isEmpty()) return 0;
        return rowLabel.charAt(0) - 'A';
    }

    public int getCol() {
        return Math.max(0, seatNumber - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatItem)) return false;
        return id == ((SeatItem) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
