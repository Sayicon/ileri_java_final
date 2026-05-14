package com.tbl324.mobile.model;

import java.util.List;

public class SeatGridModel {

    private final SeatItem[][] grid;
    private final int cols;

    private SeatGridModel(SeatItem[][] grid, int cols) {
        this.grid = grid;
        this.cols = cols;
    }

    public static SeatGridModel fromSeats(List<SeatItem> seats, int cols) {
        if (seats.isEmpty()) {
            return new SeatGridModel(new SeatItem[0][cols], cols);
        }
        int maxRow = 0;
        for (SeatItem s : seats) if (s.getRow() > maxRow) maxRow = s.getRow();
        SeatItem[][] grid = new SeatItem[maxRow + 1][cols];
        for (SeatItem s : seats) {
            if (s.getCol() < cols) grid[s.getRow()][s.getCol()] = s;
        }
        return new SeatGridModel(grid, cols);
    }

    public int getRowCount() { return grid.length; }
    public int getColCount() { return cols; }

    public SeatItem getSeatAt(int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= cols) return null;
        return grid[row][col];
    }

    public SeatItem getSeatByPixel(float x, float y, float cellSize) {
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);
        return getSeatAt(row, col);
    }
}
