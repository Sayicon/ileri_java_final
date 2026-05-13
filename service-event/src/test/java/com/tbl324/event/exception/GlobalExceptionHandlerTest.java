package com.tbl324.event.exception;

import com.tbl324.event.controller.EventController;
import com.tbl324.event.service.EventService;
import com.tbl324.shared.exception.ConflictException;
import com.tbl324.shared.exception.NotFoundException;
import com.tbl324.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void notFoundException_mapsTo404WithProblemDetail() throws Exception {
        when(eventService.findById(1L)).thenThrow(new NotFoundException("Etkinlik bulunamadı"));

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void conflictException_mapsTo409WithProblemDetail() throws Exception {
        when(eventService.findById(1L)).thenThrow(new ConflictException("Kayıt zaten mevcut"));

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"));
    }

    @Test
    void validationException_mapsTo400WithProblemDetail() throws Exception {
        when(eventService.findById(1L)).thenThrow(new ValidationException("Geçersiz veri"));

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void unexpectedException_mapsTo500WithProblemDetail() throws Exception {
        when(eventService.findById(1L)).thenThrow(new RuntimeException("Beklenmedik hata"));

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}
