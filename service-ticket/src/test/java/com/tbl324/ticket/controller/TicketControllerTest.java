package com.tbl324.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbl324.ticket.domain.TicketStatus;
import com.tbl324.ticket.dto.ReserveRequest;
import com.tbl324.ticket.dto.TicketDTO;
import com.tbl324.ticket.service.TicketService;
import com.tbl324.shared.exception.ConflictException;
import com.tbl324.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  TicketService ticketService;

    @Test
    void getMyTickets_returnsTicketList() throws Exception {
        TicketDTO dto = new TicketDTO(1L, 1L, 5L, 10L, TicketStatus.CONFIRMED);
        when(ticketService.getMyTickets(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/tickets/my").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    @Test
    void getMyTickets_emptyList_returnsEmptyArray() throws Exception {
        when(ticketService.getMyTickets(99L)).thenReturn(List.of());

        mockMvc.perform(get("/tickets/my").param("userId", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void reserve_validRequest_returns201() throws Exception {
        TicketDTO dto = new TicketDTO(1L, 1L, 5L, 10L, TicketStatus.PENDING);
        when(ticketService.reserve(any())).thenReturn(dto);

        mockMvc.perform(post("/tickets/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ReserveRequest(1L, 5L, 10L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void reserve_blankFields_returns400() throws Exception {
        mockMvc.perform(post("/tickets/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserve_seatLocked_returns409() throws Exception {
        when(ticketService.reserve(any())).thenThrow(new ConflictException("Koltuk kilitli"));

        mockMvc.perform(post("/tickets/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ReserveRequest(1L, 5L, 10L))))
                .andExpect(status().isConflict());
    }

    @Test
    void confirm_validTicket_returns200() throws Exception {
        TicketDTO dto = new TicketDTO(1L, 1L, 5L, 10L, TicketStatus.CONFIRMED);
        when(ticketService.confirm(anyLong(), any())).thenReturn(dto);

        mockMvc.perform(post("/tickets/1/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentType\":\"MOCK\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirm_ticketNotFound_returns404() throws Exception {
        when(ticketService.confirm(anyLong(), any())).thenThrow(new NotFoundException("Bilet bulunamadı"));

        mockMvc.perform(post("/tickets/999/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentType\":\"MOCK\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancel_existingTicket_returns204() throws Exception {
        mockMvc.perform(delete("/tickets/1"))
                .andExpect(status().isNoContent());
    }
}
