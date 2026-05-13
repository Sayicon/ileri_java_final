package com.tbl324.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbl324.notification.domain.NotificationType;
import com.tbl324.notification.dto.SendNotificationRequest;
import com.tbl324.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  NotificationService notificationService;

    @Test
    void send_validEmailRequest_returns202() throws Exception {
        doNothing().when(notificationService).send(any());

        SendNotificationRequest req = new SendNotificationRequest(
                NotificationType.EMAIL, "test@example.com", "Konu", "Mesaj içeriği");

        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted());
    }

    @Test
    void send_validSmsRequest_returns202() throws Exception {
        doNothing().when(notificationService).send(any());

        SendNotificationRequest req = new SendNotificationRequest(
                NotificationType.SMS, "+905551234567", null, "SMS metni");

        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted());
    }

    @Test
    void send_missingRecipient_returns400() throws Exception {
        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"EMAIL\",\"body\":\"test\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void send_missingType_returns400() throws Exception {
        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"recipient\":\"x@x.com\",\"body\":\"test\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void send_serviceThrows_returns500() throws Exception {
        doThrow(new RuntimeException("gönderim hatası")).when(notificationService).send(any());

        SendNotificationRequest req = new SendNotificationRequest(
                NotificationType.PUSH, "device-token-abc", null, "Push bildirimi");

        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }
}
