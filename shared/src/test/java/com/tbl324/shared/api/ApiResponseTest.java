package com.tbl324.shared.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void successSetsFieldsCorrectly() {
        ApiResponse<String> response = ApiResponse.success("hello");

        assertTrue(response.isSuccess());
        assertEquals("hello", response.getData());
        assertNull(response.getError());
    }

    @Test
    void errorSetsFieldsCorrectly() {
        ApiResponse<String> response = ApiResponse.error("NOT_FOUND", "kayıt bulunamadı");

        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("NOT_FOUND", response.getError().getCode());
        assertEquals("kayıt bulunamadı", response.getError().getMessage());
    }

    @Test
    void jsonRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ApiResponse<String> original = ApiResponse.success("test-data");

        String json = mapper.writeValueAsString(original);
        ApiResponse<String> deserialized = mapper.readValue(json, new TypeReference<>() {});

        assertTrue(deserialized.isSuccess());
        assertEquals("test-data", deserialized.getData());
    }

    @Test
    void errorResponseJsonRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ApiResponse<Void> original = ApiResponse.error("CONFLICT", "zaten mevcut");

        String json = mapper.writeValueAsString(original);
        ApiResponse<Void> deserialized = mapper.readValue(json, new TypeReference<>() {});

        assertFalse(deserialized.isSuccess());
        assertEquals("CONFLICT", deserialized.getError().getCode());
    }
}
