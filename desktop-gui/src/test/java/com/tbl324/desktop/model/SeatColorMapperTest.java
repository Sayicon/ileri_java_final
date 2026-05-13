package com.tbl324.desktop.model;

import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.assertj.core.api.Assertions.assertThat;

class SeatColorMapperTest {

    @Test
    void colorFor_available_returnsNonNull() {
        Color color = SeatColorMapper.colorFor(SeatStatus.AVAILABLE);
        assertThat(color).isNotNull();
    }

    @Test
    void colorFor_locked_returnsNonNull() {
        Color color = SeatColorMapper.colorFor(SeatStatus.LOCKED);
        assertThat(color).isNotNull();
    }

    @Test
    void colorFor_sold_returnsNonNull() {
        Color color = SeatColorMapper.colorFor(SeatStatus.SOLD);
        assertThat(color).isNotNull();
    }

    @Test
    void colorFor_selected_returnsNonNull() {
        Color color = SeatColorMapper.colorFor(SeatStatus.SELECTED);
        assertThat(color).isNotNull();
    }

    @Test
    void colorFor_allStates_distinctColors() {
        Color available = SeatColorMapper.colorFor(SeatStatus.AVAILABLE);
        Color locked    = SeatColorMapper.colorFor(SeatStatus.LOCKED);
        Color sold      = SeatColorMapper.colorFor(SeatStatus.SOLD);
        Color selected  = SeatColorMapper.colorFor(SeatStatus.SELECTED);

        assertThat(available).isNotEqualTo(locked);
        assertThat(available).isNotEqualTo(sold);
        assertThat(available).isNotEqualTo(selected);
        assertThat(locked).isNotEqualTo(sold);
        assertThat(locked).isNotEqualTo(selected);
        assertThat(sold).isNotEqualTo(selected);
    }
}
