package com.tbl324.mobile.model;

import java.util.List;

public class SeatGridModel {

    private final SeatItem[][] grid;
    private final int cols;

    private SeatGridModel(SeatItem[][] grid, int cols) {
        this.grid = grid;
        this.cols = cols;
    }

    public static SeatGridModel fromSeats(List<SeatItem> seats) {
        if (seats.isEmpty()) {
            return new SeatGridModel(new SeatItem[0][0], 0);
        }
        int maxRow = 0;
        int maxCol = 0;
        for (SeatItem s : seats) {
            int r = s.getRow();
            int c = s.getCol();
            if (r > maxRow) maxRow = r;
            if (c > maxCol) maxCol = c;
        }
        int rows = maxRow + 1;
        int cols = maxCol + 1;
        SeatItem[][] grid = new SeatItem[rows][cols];
        for (SeatItem s : seats) {
            grid[s.getRow()][s.getCol()] = s;
        }
        return new SeatGridModel(grid, cols);
    }

    public int getRowCount() { return grid.length; }
    public int getColCount() { return cols; }

    public SeatItem getSeatAt(int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= cols) return null;
        return grid[row][col];
    }

    public SeatItem getSeatByPixel(float x, float y, float cellStep) {
        int col = (int) (x / cellStep);
        int row = (int) (y / cellStep);
        return getSeatAt(row, col);
    }
}
