package com.tbl324.mobile.viewmodel;

import com.tbl324.mobile.model.SeatItem;

import java.util.ArrayList;
import java.util.List;

public class SeatViewModel {

    private static final int MAX_SEATS = 5;

    private final List<SeatItem> selected = new ArrayList<>();

    public boolean toggleSeat(SeatItem seat) {
        if (selected.contains(seat)) {
            selected.remove(seat);
            return false;
        }
        if (selected.size() >= MAX_SEATS) {
            return false;
        }
        selected.add(seat);
        return true;
    }

    public boolean isSelected(SeatItem seat) { return selected.contains(seat); }
    public int getSelectedCount()            { return selected.size(); }
    public List<SeatItem> getSelected()      { return new ArrayList<>(selected); }
    public void clearSelection()             { selected.clear(); }
}
