package com.tbl324.event.service;

import com.tbl324.event.dto.CreateEventRequest;
import com.tbl324.event.dto.EventDTO;
import com.tbl324.event.dto.SeatDTO;
import com.tbl324.event.dto.VenueDTO;
import com.tbl324.event.mapper.EventMapper;
import com.tbl324.event.repository.EventJdbcRepository;
import com.tbl324.event.repository.SeatJdbcRepository;
import com.tbl324.event.repository.VenueJdbcRepository;
import com.tbl324.shared.api.PagedResult;
import com.tbl324.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventJdbcRepository eventRepository;
    private final VenueJdbcRepository venueRepository;
    private final SeatJdbcRepository seatRepository;

    public PagedResult<EventDTO> findAll(int page, int size) {
        PagedResult<com.tbl324.event.domain.Event> paged = eventRepository.findAll(page, size);
        List<EventDTO> dtos = paged.getContent().stream().map(EventMapper::toDTO).toList();
        return PagedResult.of(dtos, paged.getPage(), paged.getSize(), paged.getTotal());
    }

    public EventDTO findById(Long id) {
        return eventRepository.findById(id)
                .map(EventMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Etkinlik bulunamadı: " + id));
    }

    public EventDTO create(CreateEventRequest req) {
        var venue = venueRepository.findById(req.getVenueId())
                .orElseThrow(() -> new NotFoundException("Salon bulunamadı: " + req.getVenueId()));
        var event = EventMapper.toEntity(req, venue.getCapacity());
        return EventMapper.toDTO(eventRepository.save(event));
    }

    public EventDTO update(Long id, CreateEventRequest req) {
        eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Etkinlik bulunamadı: " + id));
        var venue = venueRepository.findById(req.getVenueId())
                .orElseThrow(() -> new NotFoundException("Salon bulunamadı: " + req.getVenueId()));
        var updated = EventMapper.toEntity(req, venue.getCapacity())
                .toBuilder().id(id).build();
        return EventMapper.toDTO(eventRepository.save(updated));
    }

    public void delete(Long id) {
        eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Etkinlik bulunamadı: " + id));
        eventRepository.delete(id);
    }

    public List<SeatDTO> findSeatsByEventId(Long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Etkinlik bulunamadı: " + eventId));
        return seatRepository.findByVenueId(event.getVenueId())
                .stream().map(EventMapper::toDTO).toList();
    }

    public void updateSeatStatus(Long seatId, String status) {
        seatRepository.updateStatus(seatId, status);
    }

    public List<VenueDTO> findAllVenues() {
        return venueRepository.findList(0, 100)
                .stream().map(EventMapper::toDTO).toList();
    }

    public List<Long> findEndedEventIds() {
        return eventRepository.findEndedIds();
    }
}
