package com.tbl324.shared.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProblemDetailTest {

    @Test
    void rfc7807FieldsPresent() {
        ProblemDetail detail = ProblemDetail.builder()
                .type("https://tbl324.com/errors/not-found")
                .title("Not Found")
                .status(404)
                .detail("İstenen kayıt bulunamadı")
                .instance("/api/events/99")
                .build();

        assertEquals("https://tbl324.com/errors/not-found", detail.getType());
        assertEquals("Not Found", detail.getTitle());
        assertEquals(404, detail.getStatus());
        assertEquals("İstenen kayıt bulunamadı", detail.getDetail());
        assertEquals("/api/events/99", detail.getInstance());
    }

    @Test
    void errorsMapForValidation() {
        ProblemDetail detail = ProblemDetail.builder()
                .type("https://tbl324.com/errors/validation")
                .title("Validation Error")
                .status(400)
                .errors(Map.of("email", List.of("geçersiz format"), "name", List.of("boş olamaz")))
                .build();

        assertNotNull(detail.getErrors());
        assertTrue(detail.getErrors().containsKey("email"));
    }

    @Test
    void serializesToJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ProblemDetail detail = ProblemDetail.builder()
                .type("https://tbl324.com/errors/conflict")
                .title("Conflict")
                .status(409)
                .build();

        String json = mapper.writeValueAsString(detail);

        assertTrue(json.contains("type"));
        assertTrue(json.contains("status"));
        assertTrue(json.contains("409"));
    }
}
