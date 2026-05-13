package com.tbl324.ticket.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class ConfirmRequest {

    @NotBlank private final String paymentType;

    @JsonCreator
    public ConfirmRequest(@JsonProperty("paymentType") String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentType() { return paymentType; }
}
