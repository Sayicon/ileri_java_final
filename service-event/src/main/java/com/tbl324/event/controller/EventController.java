package com.tbl324.event.controller;

import com.tbl324.event.dto.CreateEventRequest;
import com.tbl324.event.dto.EventDTO;
import com.tbl324.event.dto.SeatDTO;
import com.tbl324.event.service.EventService;
import com.tbl324.shared.api.ApiResponse;
import com.tbl324.shared.api.PagedResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<PagedResult<EventDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(eventService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<EventDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(eventService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EventDTO> create(@Valid @RequestBody CreateEventRequest request) {
        return ApiResponse.success(eventService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<EventDTO> update(@PathVariable Long id,
                                        @Valid @RequestBody CreateEventRequest request) {
        return ApiResponse.success(eventService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        eventService.delete(id);
    }

    @GetMapping("/{id}/seats")
    public ApiResponse<List<SeatDTO>> getSeats(@PathVariable Long id) {
        return ApiResponse.success(eventService.findSeatsByEventId(id));
    }
}
