package com.tbl324.ticket.payment;

import org.springframework.stereotype.Component;

@Component("WALLET")
public class WalletPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean process(Long ticketId) {
        // Cüzdan bakiyesi kontrolü burada yapılır (mock: her zaman başarılı)
        return true;
    }
}
