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

    public static SeatGrid fromSeats(List<SeatDTO> seats, int cols) {
        if (seats.isEmpty()) {
            return new SeatGrid(new SeatDTO[0][cols], cols);
        }
        int maxRow = seats.stream().mapToInt(SeatDTO::row).max().orElse(0);
        SeatDTO[][] grid = new SeatDTO[maxRow + 1][cols];
        for (SeatDTO seat : seats) {
            if (seat.col() < cols) {
                grid[seat.row()][seat.col()] = seat;
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
