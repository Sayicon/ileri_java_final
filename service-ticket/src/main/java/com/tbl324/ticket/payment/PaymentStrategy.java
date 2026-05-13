package com.tbl324.ticket.payment;

@FunctionalInterface
public interface PaymentStrategy {
    boolean process(Long ticketId);
}
