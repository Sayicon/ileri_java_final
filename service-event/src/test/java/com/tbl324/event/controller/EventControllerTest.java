package com.tbl324.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbl324.event.dto.CreateEventRequest;
import com.tbl324.event.dto.EventDTO;
import com.tbl324.event.dto.SeatDTO;
import com.tbl324.event.service.EventService;
import com.tbl324.shared.api.ApiResponse;
import com.tbl324.shared.api.PagedResult;
import com.tbl324.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    void getEvents_returnsApiResponseWrappedPagedResult() throws Exception {
        EventDTO dto = buildEventDTO(1L, "Java Conference");
        PagedResult<EventDTO> paged = PagedResult.of(List.of(dto), 0, 10, 1);
        when(eventService.findAll(0, 10)).thenReturn(paged);

        mockMvc.perform(get("/events").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Java Conference"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void getEventById_existingId_returnsEvent() throws Exception {
        EventDTO dto = buildEventDTO(1L, "Kotlin Day");
        when(eventService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Kotlin Day"));
    }

    @Test
    void getEventById_nonExistentId_returns404ProblemDetail() throws Exception {
        when(eventService.findById(999L)).thenThrow(new NotFoundException("Etkinlik bulunamadı: 999"));

        mockMvc.perform(get("/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void createEvent_validRequest_returns201() throws Exception {
        CreateEventRequest req = buildCreateRequest("Yeni Etkinlik");
        EventDTO dto = buildEventDTO(5L, "Yeni Etkinlik");
        when(eventService.create(any(CreateEventRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(5));
    }

    @Test
    void createEvent_blankTitle_returns400() throws Exception {
        CreateEventRequest req = buildCreateRequest("");

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createEvent_nullStartTime_returns400() throws Exception {
        CreateEventRequest req = CreateEventRequest.builder()
                .title("Etkinlik")
                .venueId(1L)
                .startTime(null)
                .endTime(LocalDateTime.now().plusDays(1))
                .build();

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEventSeats_existingEvent_returnsSeatList() throws Exception {
        List<SeatDTO> seats = List.of(
                SeatDTO.builder().id(1L).rowLabel("A").seatNumber(1).status("AVAILABLE").build(),
                SeatDTO.builder().id(2L).rowLabel("A").seatNumber(2).status("SOLD").build()
        );
        when(eventService.findSeatsByEventId(1L)).thenReturn(seats);

        mockMvc.perform(get("/events/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].rowLabel").value("A"))
                .andExpect(jsonPath("$.data[1].status").value("SOLD"));
    }

    @Test
    void updateEvent_nonExistentId_returns404() throws Exception {
        CreateEventRequest req = buildCreateRequest("Güncelleme");
        when(eventService.update(eq(999L), any(CreateEventRequest.class)))
                .thenThrow(new NotFoundException("Etkinlik bulunamadı: 999"));

        mockMvc.perform(put("/events/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    private EventDTO buildEventDTO(Long id, String title) {
        return EventDTO.builder()
                .id(id)
                .title(title)
                .description("Açıklama")
                .venueId(1L)
                .startTime(LocalDateTime.now().plusDays(7))
                .endTime(LocalDateTime.now().plusDays(7).plusHours(3))
                .totalSeats(100)
                .availableSeats(100)
                .status("ACTIVE")
                .build();
    }

    private CreateEventRequest buildCreateRequest(String title) {
        return CreateEventRequest.builder()
                .title(title)
                .description("Açıklama")
                .venueId(1L)
                .startTime(LocalDateTime.now().plusDays(7))
                .endTime(LocalDateTime.now().plusDays(7).plusHours(3))
                .build();
    }
}
