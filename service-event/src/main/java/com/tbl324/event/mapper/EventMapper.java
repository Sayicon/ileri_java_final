package com.tbl324.event.mapper;

import com.tbl324.event.domain.Event;
import com.tbl324.event.domain.EventStatus;
import com.tbl324.event.domain.Seat;
import com.tbl324.event.domain.Venue;
import com.tbl324.event.dto.CreateEventRequest;
import com.tbl324.event.dto.EventDTO;
import com.tbl324.event.dto.SeatDTO;
import com.tbl324.event.dto.VenueDTO;

public class EventMapper {

    private EventMapper() {}

    public static EventDTO toDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .venueId(event.getVenueId())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .totalSeats(event.getTotalSeats())
                .availableSeats(event.getAvailableSeats())
                .status(event.getStatus().name())
                .build();
    }

    public static SeatDTO toDTO(Seat seat) {
        return SeatDTO.builder()
                .id(seat.getId())
                .venueId(seat.getVenueId())
                .rowLabel(seat.getRowLabel())
                .seatNumber(seat.getSeatNumber())
                .status(seat.getStatus().name())
                .build();
    }

    public static VenueDTO toDTO(Venue venue) {
        return VenueDTO.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .capacity(venue.getCapacity())
                .build();
    }

    public static Event toEntity(CreateEventRequest req, int totalSeats) {
        return Event.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .venueId(req.getVenueId())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .totalSeats(totalSeats)
                .availableSeats(totalSeats)
                .status(EventStatus.ACTIVE)
                .build();
    }
}
