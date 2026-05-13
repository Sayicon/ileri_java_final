package com.tbl324.ticket.payment;

import org.springframework.stereotype.Component;

@Component("MOCK")
public class MockPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean process(Long ticketId) { return true; }
}
