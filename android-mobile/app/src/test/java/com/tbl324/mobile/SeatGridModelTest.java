package com.tbl324.mobile;

import com.tbl324.mobile.model.SeatGridModel;
import com.tbl324.mobile.model.SeatItem;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SeatGridModelTest {

    @Test
    public void fromSeats_empty_returnsEmptyGrid() {
        SeatGridModel grid = SeatGridModel.fromSeats(Collections.emptyList(), 5);
        assertEquals(0, grid.getRowCount());
        assertEquals(5, grid.getColCount());
    }

    @Test
    public void fromSeats_singleSeat_correctCell() {
        SeatItem seat = new SeatItem(1L, 0, 0, "AVAILABLE");
        SeatGridModel grid = SeatGridModel.fromSeats(Collections.singletonList(seat), 5);
        assertEquals(1, grid.getRowCount());
        assertEquals(seat, grid.getSeatAt(0, 0));
    }

    @Test
    public void fromSeats_multipleSeats_correctLayout() {
        List<SeatItem> seats = Arrays.asList(
                new SeatItem(1L, 0, 0, "AVAILABLE"),
                new SeatItem(2L, 0, 1, "SOLD"),
                new SeatItem(3L, 1, 0, "LOCKED")
        );
        SeatGridModel grid = SeatGridModel.fromSeats(seats, 2);
        assertEquals(2, grid.getRowCount());
        assertEquals("SOLD",   grid.getSeatAt(0, 1).getStatus());
        assertEquals("LOCKED", grid.getSeatAt(1, 0).getStatus());
    }

    @Test
    public void getSeatAt_spacerCell_returnsNull() {
        SeatItem seat = new SeatItem(1L, 0, 2, "AVAILABLE");
        SeatGridModel grid = SeatGridModel.fromSeats(Collections.singletonList(seat), 5);
        assertNull(grid.getSeatAt(0, 0));
        assertNull(grid.getSeatAt(0, 1));
        assertEquals(seat, grid.getSeatAt(0, 2));
    }

    @Test
    public void getSeatByPixel_validCoords_returnsSeat() {
        SeatItem seat = new SeatItem(1L, 0, 0, "AVAILABLE");
        SeatGridModel grid = SeatGridModel.fromSeats(Collections.singletonList(seat), 5);
        assertEquals(seat, grid.getSeatByPixel(10f, 10f, 48f));
    }

    @Test
    public void getSeatByPixel_outOfBounds_returnsNull() {
        SeatItem seat = new SeatItem(1L, 0, 0, "AVAILABLE");
        SeatGridModel grid = SeatGridModel.fromSeats(Collections.singletonList(seat), 5);
        assertNull(grid.getSeatByPixel(9999f, 9999f, 48f));
    }
}
