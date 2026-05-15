package com.tbl324.desktop.model;

import java.util.List;
import java.util.Optional;

public class SeatGrid {

    private final SeatDTO[][] grid;
    private final int cols;

    private SeatGrid(SeatDTO[][] grid, int cols) {
        this.grid = grid;
        this.cols = cols;
    }

    // rowLabel "A","B",... → 0,1,...  seatNumber 1,2,... → col 0,1,...
    private static int rowIndex(String rowLabel) {
        if (rowLabel == null || rowLabel.isEmpty()) return 0;
        int idx = 0;
        for (char c : rowLabel.toUpperCase().toCharArray())
            idx = idx * 26 + (c - 'A' + 1);
        return idx - 1;
    }

    public static SeatGrid fromSeats(List<SeatDTO> seats, int cols) {
        if (seats.isEmpty()) {
            return new SeatGrid(new SeatDTO[0][cols], cols);
        }
        int maxRow = seats.stream().mapToInt(s -> rowIndex(s.rowLabel())).max().orElse(0);
        SeatDTO[][] grid = new SeatDTO[maxRow + 1][cols];
        for (SeatDTO seat : seats) {
            int r = rowIndex(seat.rowLabel());
            int c = seat.seatNumber() - 1;
            if (c >= 0 && c < cols) {
                grid[r][c] = seat;
            }
        }
        return new SeatGrid(grid, cols);
    }

    public int rows() { return grid.length; }
    public int cols() { return cols; }

    public Optional<SeatDTO> at(int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= cols) {
            return Optional.empty();
        }
        return Optional.ofNullable(grid[row][col]);
    }

    public Optional<SeatDTO> atPixel(double x, double y, double cellSize) {
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);
        return at(row, col);
    }
}
