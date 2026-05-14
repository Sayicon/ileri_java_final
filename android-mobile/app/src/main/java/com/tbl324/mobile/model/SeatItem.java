package com.tbl324.mobile.model;

import java.util.Objects;

public class SeatItem {
    private final long id;
    private final int row;
    private final int col;
    private String status;

    public SeatItem(long id, int row, int col, String status) {
        this.id     = id;
        this.row    = row;
        this.col    = col;
        this.status = status;
    }

    public long getId()     { return id; }
    public int  getRow()    { return row; }
    public int  getCol()    { return col; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatItem)) return false;
        return id == ((SeatItem) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
