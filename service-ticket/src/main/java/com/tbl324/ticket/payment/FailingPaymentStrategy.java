package com.tbl324.ticket.payment;

public class FailingPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean process(Long ticketId) { return false; }
}
