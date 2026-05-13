package com.tbl324.ticket.payment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStrategyTest {

    @Test
    void mockPaymentStrategy_alwaysSucceeds() {
        PaymentStrategy strategy = new MockPaymentStrategy();
        assertThat(strategy.process(1L)).isTrue();
        assertThat(strategy.process(999L)).isTrue();
    }

    @Test
    void failingPaymentStrategy_alwaysFails() {
        PaymentStrategy strategy = new FailingPaymentStrategy();
        assertThat(strategy.process(1L)).isFalse();
    }

    @Test
    void strategySwap_doesNotAffectCaller() {
        PaymentStrategy ok   = new MockPaymentStrategy();
        PaymentStrategy fail = new FailingPaymentStrategy();

        assertThat(ok.process(1L)).isTrue();
        assertThat(fail.process(1L)).isFalse();
    }
}
