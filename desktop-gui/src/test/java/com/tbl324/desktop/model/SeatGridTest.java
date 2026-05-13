package com.tbl324.desktop.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SeatGridTest {

    @Test
    void fromSeats_empty_returnsEmptyGrid() {
        SeatGrid grid = SeatGrid.fromSeats(List.of(), 5);
        assertThat(grid.rows()).isZero();
        assertThat(grid.cols()).isEqualTo(5);
    }

    @Test
    void fromSeats_singleSeat_placedAtCorrectCell() {
        SeatDTO seat = new SeatDTO(1L, 0, 0, SeatStatus.AVAILABLE);
        SeatGrid grid = SeatGrid.fromSeats(List.of(seat), 5);

        assertThat(grid.rows()).isEqualTo(1);
        assertThat(grid.at(0, 0)).contains(seat);
    }

    @Test
    void fromSeats_multipleSeats_correctLayout() {
        List<SeatDTO> seats = List.of(
                new SeatDTO(1L, 0, 0, SeatStatus.AVAILABLE),
                new SeatDTO(2L, 0, 1, SeatStatus.SOLD),
                new SeatDTO(3L, 1, 0, SeatStatus.LOCKED)
        );
        SeatGrid grid = SeatGrid.fromSeats(seats, 2);

        assertThat(grid.rows()).isEqualTo(2);
        assertThat(grid.at(0, 1).map(SeatDTO::status)).contains(SeatStatus.SOLD);
        assertThat(grid.at(1, 0).map(SeatDTO::status)).contains(SeatStatus.LOCKED);
    }

    @Test
    void atPixel_validCoords_returnsSeat() {
        SeatDTO seat = new SeatDTO(1L, 0, 0, SeatStatus.AVAILABLE);
        SeatGrid grid = SeatGrid.fromSeats(List.of(seat), 5);
        double cellSize = 40.0;

        Optional<SeatDTO> found = grid.atPixel(10.0, 10.0, cellSize);
        assertThat(found).contains(seat);
    }

    @Test
    void atPixel_outOfBounds_returnsEmpty() {
        SeatDTO seat = new SeatDTO(1L, 0, 0, SeatStatus.AVAILABLE);
        SeatGrid grid = SeatGrid.fromSeats(List.of(seat), 5);
        double cellSize = 40.0;

        Optional<SeatDTO> found = grid.atPixel(999.0, 999.0, cellSize);
        assertThat(found).isEmpty();
    }

    @Test
    void spacerCell_returnsEmpty() {
        SeatDTO seat = new SeatDTO(1L, 0, 2, SeatStatus.AVAILABLE);
        SeatGrid grid = SeatGrid.fromSeats(List.of(seat), 5);

        assertThat(grid.at(0, 0)).isEmpty();
        assertThat(grid.at(0, 1)).isEmpty();
        assertThat(grid.at(0, 2)).contains(seat);
    }
}
