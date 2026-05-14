package com.tbl324.mobile;

import com.tbl324.mobile.model.SeatItem;
import com.tbl324.mobile.viewmodel.SeatViewModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SeatViewModelTest {

    private static final int MAX_SEATS = 5;
    private SeatViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new SeatViewModel();
    }

    @Test
    public void selectSeat_underLimit_succeeds() {
        SeatItem seat = new SeatItem(1L, 0, 0, "AVAILABLE");
        boolean result = viewModel.toggleSeat(seat);
        assertTrue(result);
        assertEquals(1, viewModel.getSelectedCount());
    }

    @Test
    public void selectSeat_overMaxFive_rejected() {
        for (int i = 0; i < MAX_SEATS; i++) {
            viewModel.toggleSeat(new SeatItem((long) i, 0, i, "AVAILABLE"));
        }
        SeatItem extra = new SeatItem(99L, 1, 0, "AVAILABLE");
        boolean result = viewModel.toggleSeat(extra);
        assertFalse(result);
        assertEquals(MAX_SEATS, viewModel.getSelectedCount());
    }

    @Test
    public void deselectSeat_removesFromSelection() {
        SeatItem seat = new SeatItem(1L, 0, 0, "AVAILABLE");
        viewModel.toggleSeat(seat);
        viewModel.toggleSeat(seat);
        assertEquals(0, viewModel.getSelectedCount());
    }

    @Test
    public void clearSelection_emptiesAll() {
        viewModel.toggleSeat(new SeatItem(1L, 0, 0, "AVAILABLE"));
        viewModel.toggleSeat(new SeatItem(2L, 0, 1, "AVAILABLE"));
        viewModel.clearSelection();
        assertEquals(0, viewModel.getSelectedCount());
    }

    @Test
    public void isSelected_returnsTrueForSelected() {
        SeatItem seat = new SeatItem(1L, 0, 0, "AVAILABLE");
        viewModel.toggleSeat(seat);
        assertTrue(viewModel.isSelected(seat));
    }
}
